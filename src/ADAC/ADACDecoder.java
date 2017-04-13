package ADAC;

import ij.io.FileInfo;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ADACDecoder {

	private String directory, fileName;
	private ByteBuffer keyBuffer;
	private ByteBuffer valBuffer;
	private ADACDictionary dict = new ADACDictionary();
	private BufferedInputStream inputStream;
	private BufferedInputStream f;
	private byte[] valHeaders;
	private boolean isGated = false;

	public String header, AD_Type, AD_ex_objs;
	public String[] values = new String[ADACDictionary.NUM_KEYS + 1];
	public int xdim, ydim, bitDepth;
	public int zdim = 1;
	public int slices = 1;
	public int intervals = 1;
	public double slice_t, frameTime;

	public ADACDecoder(String directory, String fileName) {
		this.directory = directory;
		this.fileName = fileName;
	}

	public BufferedInputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(BufferedInputStream bis) {
		inputStream = bis;
	}

	public FileInfo getFileInfo() throws IOException {

		FileInfo fi = new FileInfo();
		fi.fileFormat = FileInfo.RAW;
		fi.fileName = fileName;
		fi.intelByteOrder = false;

		if (directory.indexOf("://") > 0) { // is URL

			URL u = new URL(directory + fileName);
			inputStream = new BufferedInputStream(u.openStream());
			fi.inputStream = inputStream;

		} else if (inputStream != null) {

			fi.inputStream = inputStream;

		} else {

			fi.directory = directory;

		}

		if (inputStream != null) {
			f = inputStream;
		} else {
			f = new BufferedInputStream(new FileInputStream(directory + fileName));
		}

		Log.log("\nADACDecoder: decoding " + fileName);

		// Copy header into a byteBuffer for parsing forwards and backwards
		byte[] bytHeader = new byte[ADACDictionary.LABEL_OFFSET];
		valHeaders = new byte[ADACDictionary.IM_OFFSET];
		f.read(bytHeader, 0, bytHeader.length);
		f.read(valHeaders, ADACDictionary.LABEL_OFFSET, valHeaders.length - ADACDictionary.LABEL_OFFSET);

		if (fi.intelByteOrder) {
			keyBuffer.order(ByteOrder.LITTLE_ENDIAN);
			valBuffer.order(ByteOrder.LITTLE_ENDIAN);
		}

		keyBuffer = ByteBuffer.wrap(bytHeader);
		valBuffer = ByteBuffer.wrap(valHeaders);

		// Parse the header
		header = getHeader();

		// Set values for image display
		fi.width = xdim;
		fi.height = ydim;
		fi.pixelDepth = slice_t;
		fi.fileType = bitDepth;
		fi.frameInterval = frameTime;

		// Gated or non-gated
		if (isGated) {
			// The GE data type represents gated objects...
			// must have one of the following objects:
			// - Gated SPECT projections
			// - Gated reconstruction
			// - Gated planar (although all examples of these I have seen just
			// use the dynamic planar (DP) data type)
			if (slices > 0) {
				// Must have a gated reconstruction, which has some number
				// (usually 16) intervals per reconstructed slice
				fi.nImages = zdim * slices * intervals;

				// For each gated interval there is an extra 128 byte header
				// (beginning "adac01") block starting at the normal image
				// offset location. Add this to the offset:
				fi.offset = ADACDictionary.IM_OFFSET + intervals * 128;
				
			} else {
				// Gated SPECT data set, which has some number (usually 16)
				// intervals per azimuthal projection.
				fi.nImages = zdim * intervals;

				// For each azimuth there is an additional 1664 byte header
				// (beginning "adac01") at the normal image offset location. Add
				// this to the image offset.
				fi.offset = ADACDictionary.IM_OFFSET + intervals * 1664;
				
			}

		} else {
			// Non gated data - simplest case
			fi.nImages = zdim;
			fi.offset = ADACDictionary.IM_OFFSET;
		}

		Log.log("Image offset: " + fi.offset);
		fi = parseADACExtras(fi);

		return fi;

	}

	private String getHeader() throws IOException {

		String hdr;

		// ////////////////////////////////////////////////////////////
		// Administrative header info
		// ////////////////////////////////////////////////////////////

		// First 10 bytes reserved for preamble
		byte[] sixBytes = new byte[6];
		keyBuffer.get(sixBytes, 0, 6);
		hdr = new String(sixBytes) + "\n";
		Log.log(hdr); // says adac01

		try {

			short labels = keyBuffer.getShort();
			Log.log(Integer.toString(labels)); // Number of labels in header
			Log.log(Integer.toString(keyBuffer.get())); // Number of sub-headers
			Log.log(Integer.toString(keyBuffer.get())); // Unused byte

			// For each header field available.. get them
			for (short i = 0; i < labels; i++) {

				// Attempt to find the next key...
				// ...the keynum (description)
				// ...the offset to the value
				ADACKey key = getKeys();
				short fieldOffset = key.getFieldOffset();
				short keynum = key.getKeyNum();

				switch (key.getDataType()) {

				case ADACDictionary.BYTE:

					// How long is this byte[]?
					int len = dict.valLength[keynum];
					byte[] bytes = new byte[len];

					// Move the value buffer to the correct location
					valBuffer.position(fieldOffset);
					valBuffer.get(bytes, 0, len);
					String string = new String(bytes);
					values[keynum] = string;

					switch (keynum) {
					case ADACDictionary.PROGRAM_SPECIFIC:
						// ADAC "extras"
						AD_ex_objs = string;
						values[keynum] = AD_ex_objs;
						break;
					case ADACDictionary.DATA_TYPE:
						AD_Type = string;
						if (AD_Type != null && AD_Type.equals("GE")) {
							// The GE data type represents gated objects...
							// must have one of the following objects:
							// - Gated SPECT projections
							// - Gated reconstruction
							// - Gated planar
							isGated = true;
						}
						break;
					}

					break;

				case ADACDictionary.SHORT:

					short shortValue = valBuffer.getShort(fieldOffset);

					switch (keynum) {

					case ADACDictionary.X_DIMENSIONS: // X-dimension
						xdim = shortValue;
						break;

					case ADACDictionary.Y_DIMENSIONS: // Y-dimension
						ydim = shortValue;
						break;

					case ADACDictionary.Z_DIMENSIONS: // Z dimension
						zdim = shortValue;
						break;

					case ADACDictionary.PIXEL_BIT_DEPTH: // Pixel depth

						switch (shortValue) {

						case 8:
							bitDepth = FileInfo.GRAY8;
							break;

						case 16:
							bitDepth = FileInfo.GRAY16_SIGNED;
							break;

						case 32:
							bitDepth = FileInfo.GRAY32_FLOAT;
							break;

						default:
							bitDepth = FileInfo.GRAY16_UNSIGNED;
						}
						;

						break;

					case ADACDictionary.NUMBER_OF_IMAGE_SETS:
						intervals = shortValue;
						Log.log("" + intervals);
						break;

					case ADACDictionary.RECONSTRUCTED_SLICES:
						slices = shortValue;
						Log.log("" + slices);
						break;

					}

					values[keynum] = "" + shortValue;
					break;

				case ADACDictionary.INT:

					int m_Int = valBuffer.getInt(fieldOffset);

					switch (keynum) {

					case ADACDictionary.FRAME_TIME:
						// Time per frame
						frameTime = ((double) m_Int) / 1000d;
						break;

					}
					values[keynum] = "" + m_Int;
					break;

				case ADACDictionary.FLOAT:

					float floatValue = valBuffer.getFloat(fieldOffset);

					switch (keynum) {

					case ADACDictionary.SLICE_THICKNESS:
						slice_t = floatValue;

					}

					values[keynum] = "" + floatValue;
					break;

				}

				hdr += dict.descriptions[keynum] + " = " + values[keynum] + "\n";

				Log.log(keynum + ", " + key.getDataType() + ", " + fieldOffset + ", " + values[keynum]);

			}

			Log.log("" + values.length);

			return hdr;

		} catch (IOException e) {
			Log.error("ADAC Decoder", "Failed to retrieve ADAC image file header. " + "Is this an ADAC image file?");
			return null;
		}
	}

	private FileInfo parseADACExtras(FileInfo fi) {

		final int INDX_CALB = 0;

		// Define ADAC extras that we will parse
		String[] extras = { "CALB", "WLAA" };

		String anExtra;

		for (int j = 0; j < extras.length; j++) {

			int indxExtra = AD_ex_objs.indexOf(extras[j]);
			if (indxExtra != -1 && !(extras[j].equals(null))) {
				Log.log("CALB at " + indxExtra);

				byte[] someBytes;
				// Check we've got readable ASCII chars - 32 is the first
				// (space) and 126 is the last (~);
				int i = 0;
				do {
					int index = indxExtra + extras[j].length() + i;
					anExtra = AD_ex_objs.substring(index, index + 1);
					someBytes = anExtra.getBytes();
					Log.log("" + (int) someBytes[0]);
					i++;
				} while ((int) someBytes[0] < 32 && (int) someBytes[0] > 126);
				// We should have skipped any rubbish preamble, like a shift or
				// STX

				String message = "";
				boolean continu = false;
				do {
					int index = indxExtra + extras[j].length() + i;
					anExtra = AD_ex_objs.substring(index, index + 1);
					someBytes = anExtra.getBytes();
					if ((int) someBytes[0] > 31 && (int) someBytes[0] < 127) {
						continu = true;
						message += anExtra;
					} else {
						continu = false;
					}
					Log.log("" + (int) someBytes[0]);
					i++;
				} while (continu);

				// Assuming the object is terminated by a non-printingcharacter,
				// we have the full "extra." Now use information in some extras:
				switch (j) {
				case INDX_CALB:
					// Calibration factor is size (mm) of a pixel if the image
					// were scaled to 1024 pixels wide or high
					double calibF;
					try {
						calibF = Float.parseFloat(message);
						if (calibF != 0) {
							fi.pixelWidth = calibF * 1024 / xdim;
							// ADAC only does square pixels
							fi.pixelHeight = fi.pixelWidth;
							fi.unit = "mm";
						}
					} catch (NumberFormatException e) {
						Log.log("Unable to parse calibration factor");
					}
					break;
				case 1:

				}
				Log.log(message);
				message = "";

			}
		}

		return fi;
	}

	private ADACKey getKeys() throws IOException {

		short num = keyBuffer.getShort();
		byte datTyp = keyBuffer.get();
		keyBuffer.get(); // unused byte
		short fieldOffset = keyBuffer.getShort();

		return new ADACKey(num, datTyp, fieldOffset);

	}

	public boolean isGated() {
		return isGated;
	}

}
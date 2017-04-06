package ADAC;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import ij.io.FileInfo;

public class ADACDecoder implements KvpListener {

	private String directory, fileName;
	private ByteBuffer keyBuffer;
	private ByteBuffer valBuffer;
	private ADACDictionary dict = new ADACDictionary();
	private BufferedInputStream inputStream;
	private BufferedInputStream f;
	private byte[] valHeaders;

	private final Map<Short, String> stringsMap;
	private final Map<Short, Short> shortsMap;
	private final Map<Short, Integer> intsMap;
	private final Map<Short, Float> floatsMap;
	private final Map<Short, Integer> bitDepthMap;
	private final ArrayList<ADACKvp> keyList;
	
	public String AD_Type, AD_ex_objs;
	public int xdim, ydim, bitDepth;
	public int zdim = 1;
	public int slices = 1;
	public int intervals = 1;
	public double slice_t, frameTime;
	private FileInfo fi;

	public ADACDecoder(String directory, String fileName) {

		this.directory = directory;
		this.fileName = fileName;

		stringsMap = new HashMap<Short, String>();
		shortsMap = new HashMap<Short, Short>();
		intsMap = new HashMap<Short, Integer>();
		floatsMap = new HashMap<Short, Float>();

		keyList = new ArrayList<ADACKvp>();
		
		fi = new FileInfo();

		// Bit depth and set the default bit depth
		bitDepthMap = new HashMap<Short, Integer>();
		bitDepthMap.put(null, FileInfo.GRAY16_SIGNED);
		bitDepthMap.put((short) 8, FileInfo.GRAY8);
		bitDepthMap.put((short) 16, FileInfo.GRAY16_SIGNED);
		bitDepthMap.put((short) 32, FileInfo.GRAY32_FLOAT);

	}

	public BufferedInputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(BufferedInputStream bis) {
		inputStream = bis;
	}

	public FileInfo getFileInfo() throws IOException {

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
		parseHeader();
		setValues();
		fi = parseADACExtras(fi);
		
		return fi;

	}

	private void parseHeader() throws IOException {

		// ////////////////////////////////////////////////////////////
		// Administrative header info
		// ////////////////////////////////////////////////////////////

		// First 10 bytes reserved for preamble
		byte[] sixBytes = new byte[6];
		keyBuffer.get(sixBytes, 0, 6);
		Log.log(new String(sixBytes) + "\n"); // says adac01

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
				short num = key.getKeyNum();
				switch (key.getDataType()) {

				case ADACDictionary.BYTE:

					keyList.add(new ByteKvp(this, key));
					break;

				case ADACDictionary.SHORT:

					keyList.add(new ShortKvp(this, key));
					break;

				case ADACDictionary.INT:

					keyList.add(new IntKvp(this, key));
					break;

				case ADACDictionary.FLOAT:

					keyList.add(new FloatKvp(this, key));
					break;

				}

			}

		} catch (IOException e) {
			Log.error("ADAC Decoder", "Failed to retrieve ADAC image file header. " + "Is this an ADAC image file?");
		}
	}

	private void setValues() {

		// Strings
		AD_ex_objs = stringsMap.get(ADACDictionary.PROGRAM_SPECIFIC);
		AD_Type = stringsMap.get(ADACDictionary.DATA_TYPE);

		// Shorts
		fi.width = shortsMap.get(ADACDictionary.X_DIMENSIONS);
		fi.height = shortsMap.get(ADACDictionary.Y_DIMENSIONS);
		zdim = shortsMap.get(ADACDictionary.Z_DIMENSIONS);
		slices = shortsMap.get(ADACDictionary.RECONSTRUCTED_SLICES);
		intervals = shortsMap.get(ADACDictionary.NUMBER_OF_IMAGE_SETS);

		short adBitDepth = shortsMap.get(ADACDictionary.PIXEL_BIT_DEPTH);
		fi.fileType = bitDepthMap.get(adBitDepth);

		// Ints
		// Convert from milliseconds to seconds
		fi.frameInterval = intsMap.get(ADACDictionary.FRAME_TIME) / 1000;

		// Gated or non-gated
		if (isGated()) {
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
				fi.offset = ADACDictionary.IM_OFFSET;
			} else {
				// Gated SPECT projections, which has some number (usually 16)
				// intervals per azimuthal projection
				fi.nImages = zdim * intervals;
				fi.offset = ADACDictionary.GATED_SPECT_OFFSET;
			}

		} else {
			// Non gated data - simplest case
			fi.nImages = zdim;
			fi.offset = ADACDictionary.IM_OFFSET;
		}

	}

	private FileInfo parseADACExtras(FileInfo fi) {

		ADACExtras adacExtras = new ADACExtras(AD_ex_objs);

		// Calculate pixel dimensions from the calibration factor.
		// Calibration factor is the pixel size of a 1024x1024 pixel
		// image acquired with the full field of view.
		float cal = adacExtras.getCalibrationFactor();
		if (cal != 0) {
			fi.pixelWidth = cal * 1024 / xdim;
			// ADAC only does square pixels
			fi.pixelHeight = fi.pixelWidth;
			fi.unit = "mm";
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

		if (AD_Type == null) {

			return false;

		} else if (AD_Type.equals("GE") || AD_Type.equals("GP")) {

			// GE - Gated ECT
			// GP - Gated planar (although these usually just get given DP)
			return true;

		} else {

			return false;
		}

	}

	public void read(ByteKvp byteKvp) {

		// How long is this byte[]?
		int len = dict.valLength[byteKvp.getKeyNum()];
		byte[] bytes = new byte[len];

		// Move the value buffer to the correct location
		valBuffer.position(byteKvp.getFieldOffset());
		valBuffer.get(bytes, 0, len);
		byteKvp.setString(bytes);

		stringsMap.put(byteKvp.getKeyNum(), byteKvp.getString());
		Log.log(byteKvp.getLogString());

	}

	public void read(ShortKvp shortKvp) {

		short shortValue = valBuffer.getShort(shortKvp.getFieldOffset());
		shortKvp.setValue(shortValue);
		shortsMap.put(shortKvp.getKeyNum(), shortKvp.getValue());
		Log.log(shortKvp.getLogString());

	}

	public void read(IntKvp intKvp) {

		int m_Int = valBuffer.getInt(intKvp.getFieldOffset());
		intKvp.setValue(m_Int);
		intsMap.put(intKvp.getKeyNum(), intKvp.getValue());
		Log.log(intKvp.getLogString());

	}

	public void read(FloatKvp floatKvp) {

		float floatValue = valBuffer.getFloat(floatKvp.getFieldOffset());
		floatKvp.setValue(floatValue);
		floatsMap.put(floatKvp.getKeyNum(), floatKvp.getValue());
		Log.log(floatKvp.getLogString());

	}

	public Object getImageInfo() {

		StringBuffer header = new StringBuffer();
		
		Iterator<ADACKvp> it = keyList.iterator();
		while(it.hasNext()){
			ADACKvp ak = it.next();
			header.append(dict.descriptions[ak.getKeyNum()]);
			header.append(" = ");
			header.append(ak.getString());
			header.append("\n");
		}
		
		return header.toString().trim();
	}

}
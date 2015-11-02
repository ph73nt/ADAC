package ADAC;

import ij.IJ;
import ij.ImagePlus;
import ij.io.FileInfo;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ADACEncoder {

	private FileInfo fi;
	private int bitsPerSample, photoInterp, imageSize;
	private long stackSize;
	private byte[] bytes = new byte[ADACDictionary.IM_OFFSET];
	private ByteBuffer keyBuffer; // = ByteBuffer.allocate(ADACDictionary.LABEL_OFFSET);
	private ByteBuffer valBuffer; // = ByteBuffer.allocate(ADACDictionary.IM_OFFSET);
	private final boolean isLittleEndian = false; // bigendian
	private ImagePlus imp;
	private ADACDictionary dict = new ADACDictionary();

	public ADACEncoder(FileInfo fi, ImagePlus imp) {
		this.fi = fi;
		this.imp = imp;
		fi.intelByteOrder = isLittleEndian;
		int bytesPerPixel = 1;

		bitsPerSample = 8;
		switch (fi.fileType) {
		case FileInfo.GRAY8:
			photoInterp = fi.whiteIsZero ? 0 : 1;
			break;
		case FileInfo.GRAY16_UNSIGNED:
		case FileInfo.GRAY16_SIGNED:
			bitsPerSample = 16;
			photoInterp = fi.whiteIsZero ? 0 : 1;
			bytesPerPixel = 2;
			break;
		case FileInfo.GRAY32_FLOAT:
			bitsPerSample = 32;
			photoInterp = fi.whiteIsZero ? 0 : 1;
			bytesPerPixel = 4;
			break;
		default:
			photoInterp = 0;
		}

		imageSize = fi.width * fi.height * bytesPerPixel;
		stackSize = (long) imageSize * fi.nImages;
		fi.offset = ADACDictionary.IM_OFFSET;
		
	}

	public void write(OutputStream out) throws IOException {
		writeHeader(out);
	}

	public void write(DataOutputStream out) throws IOException {
		write((OutputStream) out);
	}

	void buildHeader() {

		// First 10 bytes are reserved for preamble...
		// ...must begin with adac01
		keyBuffer.put("adac01".getBytes());

		// Next char null and then add number of "labels" - but do that later
		byte noLabels = 0;
		keyBuffer.position(8);

		// ... Number of sub-headers - usually 2 for normal images
		keyBuffer.put( (byte) 2);

		// ... unused byte
		keyBuffer.position(10);

		// ... first parse the image info and save any ADAC tags
		// these may be overwritten later (for tags like dimensions)
		Object obj = imp.getProperty("Info");
		String strInfo = obj.toString();
		int labelOffset = 0;
		int numLabels = 0;
		valBuffer.position(ADACDictionary.LABEL_OFFSET);

		for (int i = 0; i < ADACDictionary.NUM_KEYS; i++) {

			int offset;
			numLabels++;
			
			// Terminate the label with a null... obviously do not put
			// one in the zeroth position
			if( i != 0) {
				labelOffset = valBuffer.position() + 1;
				valBuffer.position(labelOffset);
			}

			// Look for occurences of Key descriptions
			int intIndex = -1;
			String description = null;

			if (dict.descriptions.length > i
					&& (description = dict.descriptions[i + 1]) != null
					&& !description.equals("")) {

				intIndex = strInfo.indexOf(description + " = ");

			}

			// -1 returned if description not found
			if (intIndex > -1) {

				IJ.log("ADAC header label " + ++noLabels);
				IJ.log("Found " + dict.descriptions[i + 1]);
				int len = dict.valLength[i + 1];

				// Calculate the final character position in the string -
				// remember " = "
				int from = intIndex + dict.descriptions[i + 1].length() + 3;
				offset = strInfo.indexOf("\n", from);

				// String that holds the info to be written into the header.
				// This may need to be converted to numerical data.
				String strTemp;
				if (strInfo.length() - intIndex >= offset) {
					strTemp = strInfo.substring(from, offset);
				} else {
					strTemp = strInfo.substring(from);
				}

				IJ.log(strTemp);

				// Decide what sort of data this is for writing into the header.
				byte[] labType = new byte[1];

				// The dictionary knows...
				switch (dict.type[i + 1]) {

				  case 4: // variable

					// I haven't seen a use case for this
					labType[0] = (byte) 4;
					break;

				  case 3: // Float

					labType[0] = (byte) 3;

					try {

						float num = Float.parseFloat(strTemp);
						valBuffer.putFloat(num);

					} catch (NumberFormatException e) {
						IJ.log("Unable to parse floating point data\n"
								+ strTemp);
					}

					break;

				  case 2: // Integer

					labType[0] = (byte) 2;

					try {

						int num = Integer.parseInt(strTemp);
						valBuffer.putInt(num);

					} catch (NumberFormatException e) {
						IJ.log("Unable to parse integer data\n" + strTemp);
					}

					break;

				  case 1: // Short

					labType[0] = (byte) 1;

					try {
						
						short num = Short.parseShort(strTemp);
						valBuffer.putShort(num);
					
					} catch (NumberFormatException e) {
						IJ.log("Unable to parse short data\n" + strTemp);
					}

					break;

				  case 5:
					// ADACDictionary class uses this to differentiate string
					// from byte, but in ADAC images does not exist - just byte
				  case 0: // Byte
				  default:
					// default to byte
					labType[0] = (byte) 0;

					// Make a mutable buffer for manipulating the fixed-length
					// string fields in the header
					StringBuffer buffer = new StringBuffer(strTemp);

					// Make sure lenth padded with nulls
					while (buffer.length() < len) {
						buffer.append('\u0000');
					}

					// Not greater than it should be!
					while (buffer.length() > len) {
						buffer.deleteCharAt(buffer.length() - 1);
					}

					// Write the data into the header at the correct offset
					valBuffer.put(buffer.toString().getBytes());

				}

				// Write the key value that refers to this header item in
				// form:
				// AA#!&&;
				
				// AA = label (or key) number;
				keyBuffer.putShort( (short) (i + 1));

				// # = byte to define label type
				keyBuffer.put(labType);

				// ! = unused byte
				keyBuffer.put( (byte) 0);

				// && = short offset in the file to the data
				keyBuffer.putShort( (short) (labelOffset + ADACDictionary.LABEL_OFFSET));

			}
		}

		// Fill in the number of labels
		keyBuffer.position(7);
		keyBuffer.put(noLabels);

	}

	void writeHeader(OutputStream out) throws IOException {
		
		keyBuffer = ByteBuffer.wrap(bytes, 0, ADACDictionary.LABEL_OFFSET);
		
		int len = ADACDictionary.IM_OFFSET - ADACDictionary.LABEL_OFFSET;
		valBuffer = ByteBuffer.wrap(bytes, ADACDictionary.LABEL_OFFSET, len);
		
		buildHeader();
		
		out.write(bytes);
	}

}
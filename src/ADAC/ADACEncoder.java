package ADAC;

import ij.IJ;
import ij.ImagePlus;
import ij.io.FileInfo;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class ADACEncoder {

	private FileInfo fi;
	private int bitsPerSample, photoInterp, imageSize;
	private long stackSize;
	private byte[] imHdr = new byte[ADACDictionary.IM_OFFSET];
	private final boolean isLittleEndian = false;  // bigendian
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

		// Initialise everything to zero (nulls in terms of chars)
		for (int i = 0; i < ADACDictionary.IM_OFFSET; i++) {
			imHdr[i] = 0;
		}
		String headerData = ""; // Holds demographic (etc) data NOT keys and
								// locations

		// First 10 bytes are reserved for preamble...
		// ...must begin with adac01
		charsToHeader("adac01", 0);

		// Null char
		imHdr[6] = 0;

		// ...must then know how many "labels" - do this later, but generally
		// this
		// will be 88 including the "extras"
		byte noLabels = 0;
		imHdr[7] = 88;

		// ... Number of sub-headers - usually 2 for normal images
		imHdr[8] = 2;

		// ... unused byte - set to zero
		imHdr[9] = 0;

		// Now begins the header proper...

		int keyOffset = 10;
		short lblOffset = ADACDictionary.LABEL_OFFSET;

		// ... first parse the image info and save any ADAC tags
		// these may be overwritten later (for tags like dimensions)

		Object obj = imp.getProperty("Info");
		String strInfo = obj.toString();

		for (int i = 0; i < ADACDictionary.NUM_KEYS; i++) {

			int offset;

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
						
						// Make floating point number from string
						float num = Float.parseFloat(strTemp);
						// Convert to array of bytes
						byte[] bytes = ByteBuffer.allocate(4).putFloat(num).array();
						IJ.log("Order = " + ByteBuffer.allocate(4).putFloat(num).order());
						
					} catch (NumberFormatException e){
					
						IJ.log("Unable to parse floating point data\n" + strTemp);
					
					}
					
					break;
				case 2: // Integer
					labType[0] = (byte) 2;
					break;
				case 1: // Short
					labType[0] = (byte) 1;
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
					charsToHeader(buffer.toString(), lblOffset);

				}
				
				// Write the key value that refers to this header item in
				// form:
				// AA#!&&;
				// AA = label (or key) number;
				shortToHeader((short) (i + 1), keyOffset);
				
				// ...and calculate the location for the next key
				keyOffset += 2;
				
				// # = byte to define label type
				bytesToHeader(labType, keyOffset);
				
				// ! = unused byte
				keyOffset += 2;
				
				// && = short offset in the file to the data
				shortToHeader(lblOffset, keyOffset);
				keyOffset += 2;

				IJ.log(strTemp);
				IJ.log("Done");
				
				// Recalculate the offset to the next label:
				lblOffset += (short) len;
			}
		}

		// Fill in the number of labels
		imHdr[7] = noLabels;
		IJ.log("hdr7 = " + noLabels);

	}

	void charsToHeader(String s, int loc) {
		int len = s.length();

		for (int i = 0; i < len; i++) {
			byte m_char = (byte) s.charAt(i);
			imHdr[loc + i] = m_char;
		}
	}

	void bytesToHeader(byte[] m_Byte, int loc) {
		int len = m_Byte.length;
		for (int i = 0; i < len; i++) {
			imHdr[loc + i] = m_Byte[i];
		}
	}

	void shortToHeader(short m_Short, int loc) {
		bytesToHeader(shortToBytes(m_Short), loc);
	}

	void writeHeader(OutputStream out) throws IOException {
		buildHeader();
		out.write(imHdr);
	}

	byte[] shortToBytes(short m_Short) {
		byte[] m_Byte = new byte[2];
		m_Byte[isLittleEndian ? 0 : 1] = (byte) (0xff & m_Short);
		m_Byte[isLittleEndian ? 1 : 0] = (byte) (((0xff << 8) & m_Short) >> 8);
		return m_Byte;
	}
}
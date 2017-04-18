package ADAC;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import ij.io.FileInfo;

public class ADACDecoder implements KvpListener {

	private String directory, fileName;
	private Map<String, String> extrasMap = new HashMap<String, String>();
	private BufferedInputStream f;
	private FileInfo fi;
	private final Map<Short, Float> floatsMap;

	private BufferedInputStream inputStream;
	public int intervals = 1;
	private final Map<Short, Integer> intsMap;
	private Boolean isGated = null;
	private ByteBuffer keyBuffer;
	private final ArrayList<ADACKvp> keyList;
	private final Map<Short, Short> shortsMap;
	public double slice_t, frameTime;

	public int slices = 1;
	private final Map<Short, String> stringsMap;
	private ByteBuffer valBuffer;
	private byte[] valHeaders;
	public int ydim, bitDepth;
	public int zdim = 1;

	public ADACDecoder(String directory, String fileName) {

		this.directory = directory;
		this.fileName = fileName;

		stringsMap = new HashMap<Short, String>();
		shortsMap = new HashMap<Short, Short>();
		intsMap = new HashMap<Short, Integer>();
		floatsMap = new HashMap<Short, Float>();

		keyList = new ArrayList<ADACKvp>();

	}
	
	/**
	 * Get the bit depth of the image
	 * @return
	 */
	public short getBitDepth(){

		return getShort(ADACDictionary.PIXEL_BIT_DEPTH);
		
	}

	public FileInfo getFileInfo(FileInfo fileinfo) throws IOException {

		fi = fileinfo;
		
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

		return fi;

	}
	
	/**
	 * Return a floating point value represented by the key argument (a
	 * near-definitive list is given in the dictionary class).
	 * 
	 * @param dictionaryKey
	 * @return
	 */
	public float getFloat(short dictionaryKey) {
		return floatsMap.get(dictionaryKey);
	}


	public Object getImageInfo() {

		StringBuffer header = new StringBuffer();

		Iterator<ADACKvp> it = keyList.iterator();
		while (it.hasNext()) {
			ADACKvp ak = it.next();
			header.append(ADACDictionary.descriptions[ak.getKeyNum()]);
			header.append(" = ");
			header.append(ak.getString());
			header.append("\n");
		}

		return header.toString().trim();
	}
	
	/**
	 * Get the length of time of the acquisition of each frame.
	 * @return
	 */
	public double getFrameTime(){
	
		// Convert from milliseconds to seconds
		return getInteger(ADACDictionary.FRAME_TIME) / 1000;
		
	}

	public BufferedInputStream getInputStream() {
		return inputStream;
	}

	private ADACKey getKeys() throws IOException {

		// Get the key number for cross-referencing with the dictionary
		short num = keyBuffer.getShort();

		// The next byte is the data type. This explicit declaration
		// is redundant as we have the information in the dictionary.
		// Dictionary definition is preferred so that we can override
		// special items like "extras".
		keyBuffer.get();

		// The next byte is unused by definition
		keyBuffer.get();

		// Get the offset in bytes to the field value
		short fieldOffset = keyBuffer.getShort();

		return new ADACKey(num, fieldOffset);

	}
	
	/**
	 * Get the image height in pixel units
	 * @return
	 */
	public short getHeight(){
		
		return getShort(ADACDictionary.Y_DIMENSIONS);
	}
	
	/**
	 * Get the offset, in bytes, to the image data within the file
	 * @return
	 */
	public int getImageOffset(){

		if (isGated()) {

			if (isReconstruction()) {

				// Must have a gated reconstruction. For each gated interval
				// there is an extra 128 byte header (beginning "adac01") block
				// starting at the normal image offset location. Add this to the
				// offset:
				return ADACDictionary.IM_OFFSET + intervals * 128;

			} else {

				// Gated SPECT data set. For each azimuth there is an additional
				// 1664 byte header (beginning "adac01") at the normal image
				// offset location. Add this to the image offset.
				return ADACDictionary.IM_OFFSET + intervals * 1664;

			}

		} else {
			// Non gated data - simplest case
			return ADACDictionary.IM_OFFSET;
		}

	}

	/**
	 * Return an integer value represented by the key argument (a
	 * near-definitive list is given in the dictionary class).
	 * 
	 * @param dictionaryKey
	 * @return
	 */
	public int getInteger(short dictionaryKey) {
		return intsMap.get(dictionaryKey);
	}

	public int getNumberOfImages(){
		return 0;
	}
	
	/**
	 * Get the pixel size in mm.
	 * 
	 * The pixel pixel dimensions are calculated from the calibration factor.
	 * Calibration factor is the pixel size of a 1024x1024 pixel image acquired
	 * with the full field of view. Pixel size is calculated with the following
	 * equation:
	 * 
	 * (1024 * CALB)/(dim * zoom) mm/pixel
	 * 
	 * @return
	 */
	public float getPixelSize() {

		float pixelSize = 0;
		float zoom = getFloat(ADACDictionary.ZOOM);

		// Get calibration factor (CALB)
		String calString = extrasMap.get(ExtrasKvp.CALIB_KEY);

		// Some wholebody images have height > width. Typically 1024x512.
		// Crocodile eats the biggest.
		short height = getHeight();
		short width = getWidth();
		short dim = height > width ? height : width;

		if (dim > 0 && calString != null) {

			try {

				float cal = Float.parseFloat(calString);

				// Now calculate the pixel size
				pixelSize = (1024 * cal) / (dim * zoom);

			} catch (NumberFormatException e) {

				Log.log("Unable to parse calibration factor");
				pixelSize = getRoughPixelSize();

			}
		} else {
			pixelSize = getRoughPixelSize();
		}

		return pixelSize;

	}
	
	private float getRoughPixelSize(){
		
		// Fall back on a-priori knowledge of useful field of view size
		// (520mm x 380 mm), which gives a rough approximation.
		float size = 380; // mm
		float pixels = getHeight();
		return pixels > 0 ? size/pixels : 0;
		
	}
	
	/**
	 * Return a short integer value represented by the key argument (a
	 * near-definitive list is given in the dictionary class).
	 * 
	 * @param dictionaryKey
	 * @return
	 */
	public short getShort(short dictionaryKey) {
		return shortsMap.get(dictionaryKey);
	}

	/**
	 * Return a string value represented by the key argument (a
	 * near-definitive list is given in the dictionary class).
	 * 
	 * @param dictionaryKey
	 * @return
	 */
	public String getString(short dictionaryKey) {
		return stringsMap.get(dictionaryKey);
	}

	/**
	 * Get the image width in pixel units
	 * @return
	 */
	public short getWidth(){
		
		return getShort(ADACDictionary.X_DIMENSIONS);
	}

	/**
	 * Gated data types will often require a 4D presentation.
	 * 
	 * @return True if the image type is gated, regardless of being tomographic
	 *         or planar.
	 */
	public boolean isGated() {

		if (isGated == null) {
			
			String AD_Type = getString(ADACDictionary.DATA_TYPE);

			if (AD_Type != null && AD_Type.startsWith("G")) {

				// GE - Gated ECT
				// GP - Gated planar (although these usually just get given DP)
				isGated = true;

			} else {

				isGated = false;
			}
		}

		return isGated;

	}
	
	/**
	 * Check if the image is a tomographic reconstruction.
	 * 
	 * @return true if the number of reconstructed slices is greater than 0,
	 *         false otherwise.
	 */
	public boolean isReconstruction() {
		return getShort(ADACDictionary.RECONSTRUCTED_SLICES) > 0;
	}	

	/**
	 * Parse the header information for key-value pairs.
	 * 
	 * @throws IOException
	 */
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

				case ADACDictionary.EXTRAS:

					keyList.add(new ExtrasKvp(this, key));
					break;

				}

			}

		} catch (IOException e) {
			Log.error("ADAC Decoder", "Failed to retrieve ADAC image file header. " + "Is this an ADAC image file?");
		}
	}

	/**
	 * Read a byte key-value pair.
	 */
	public void read(ByteKvp byteKvp) {

		// How long is this byte[]?
		int len = ADACDictionary.valLength[byteKvp.getKeyNum()];
		byte[] bytes = new byte[len];

		// Move the value buffer to the correct location
		valBuffer.position(byteKvp.getFieldOffset());
		valBuffer.get(bytes, 0, len);
		byteKvp.setString(bytes);

		stringsMap.put(byteKvp.getKeyNum(), byteKvp.getString());
		Log.log(byteKvp.getLogString());

	}

	/**
	 * Read the "extra" key-value pair.
	 */
	public void read(ExtrasKvp extraKvp) {

		byte[] bytes = new byte[ExtrasKvp.LENGTH];

		// Move the value buffer to the correct location
		valBuffer.position(extraKvp.getFieldOffset());
		valBuffer.get(bytes, 0, ExtrasKvp.LENGTH);

		// Set the bytes string of the extras object
		extraKvp.setData(bytes);
		extrasMap = extraKvp.getMap();

	}

	/**
	 * Read a floating point key-value pair.
	 */
	public void read(FloatKvp floatKvp) {

		float floatValue = valBuffer.getFloat(floatKvp.getFieldOffset());
		floatKvp.setValue(floatValue);
		floatsMap.put(floatKvp.getKeyNum(), floatKvp.getValue());
		Log.log(floatKvp.getLogString());

	}

	/**
	 * Read an integer key-value pair.
	 */
	public void read(IntKvp intKvp) {

		int m_Int = valBuffer.getInt(intKvp.getFieldOffset());
		intKvp.setValue(m_Int);
		intsMap.put(intKvp.getKeyNum(), intKvp.getValue());
		Log.log(intKvp.getLogString());

	}

	/**
	 * Read a short integer key-value pair.
	 */
	public void read(ShortKvp shortKvp) {

		short shortValue = valBuffer.getShort(shortKvp.getFieldOffset());
		shortKvp.setValue(shortValue);
		shortsMap.put(shortKvp.getKeyNum(), shortKvp.getValue());
		Log.log(shortKvp.getLogString());

	}

	/**
	 * Set the buffered inputstream object containing the data.
	 * @param bis
	 */
	public void setInputStream(BufferedInputStream bis) {
		inputStream = bis;
	}

	private void setValues() {

		// Shorts
		zdim = getShort(ADACDictionary.Z_DIMENSIONS);
		slices = getShort(ADACDictionary.RECONSTRUCTED_SLICES);
		intervals = getShort(ADACDictionary.NUMBER_OF_IMAGE_SETS);
		
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

			} else {

				// Gated SPECT data set, which has some number (usually 16)
				// intervals per azimuthal projection.
				fi.nImages = zdim * intervals;

			}

		} else {
			// Non gated data - simplest case
			fi.nImages = zdim;
		}

	}

}
package ADAC;

/**
 * Dictionary for the ADAC image object, including offsets and key-value pair
 * information.
 * 
 * @author NTHOMSON
 */
public class ADACDictionary {

	/**
	 * Offset to the information ADAC refers to as "labels". One would call
	 * these the values of key-value pairs.
	 */
	public static final int LABEL_OFFSET = 540;
	/**
	 * The offset in bytes to the image data.
	 */
	public static final int IM_OFFSET = 2048;
	/**
	 * The normal image offset of 2048 bytes does not seem to apply to gated
	 * data sets. There must be some extra data, possibly pertaining to recorded
	 * beats held here for 106496 bytes. This takes up exactly 26 frames of
	 * 64x64x8.
	 */
	public static final int GATED_SPECT_OFFSET = 108544;
	/**
	 * The maximum number of keys stored in an ADAC image object
	 */
	public static final int NUM_KEYS = 114;
	/**
	 * The byte data type of a key value pair.
	 */
	public static final int BYTE = 0;
	/**
	 * The short data type of a key value pair.
	 */
	public static final int SHORT = 1;
	/**
	 * The integer data type of a key value pair.
	 */
	public static final int INT = 2;
	/**
	 * The float data type of a key value pair.
	 */
	public static final int FLOAT = 3;
	/**
	 * The miscellaneous data type of a key value pair.
	 */
	public static final int VAR = 4;
	/**
	 * Descriptions of ADAC key-value pairs that appear in the IJ image info
	 */
	public final String[] descriptions;
	/**
	 * Key-value pair data types
	 */
	public final int[] type;
	/**
	 * Each value of a key-value pair is restricted to a set length and
	 * null-padded if necessary. This often leads to truncation of long fields
	 * such as patient name, which can be found in non-truncated format in the
	 * ADAC Extra Objects field.
	 */
	public final int[] valLength;

	public static final int PATIENT_NAME = 1;
	public static final int PATIENT_ID = 2;
	public static final int PATIENT_SEX = 3;
	public static final int PATIENT_AGE = 4;
	public static final int PATIENT_HEIGHT = 5; 
	public static final int PATIENT_WEIGHT = 6;
	public static final int UNIQUE_PATIENT_KEY = 76;	
	public static final int DATE_OF_BIRTH = 109;
	public static final int ACQUISITION_DATE = 7;
	public static final int DOSE_ADMIN_TIME = 8;
	public static final int UNIQUE_EXAM_KEY = 9;
	public static final int EXAM_PROCEDURE = 10;
	public static final int REFFERING_PHYSICIAN = 11;
	public static final int ATTENDING_PHYSICIAN = 12;
	public static final int IMAGING_MODALITY = 13;
	public static final int INSTITUTION_NAME = 14;
	public static final int NRML_CRV_FILE_NAME = 82;
	public static final int HISTOG_CRV_FILE_NAME = 15;
	public static final int ACQUISITION_START_TIME = 16;
	public static final int DATA_TYPE = 17;
	public static final int IMAGE_VIEW_ID = 18;
	public static final int UNIQUE_OBJECT_KEY = 83;
	public static final int ASSOCIATED_PARENT_FILE = 75;
	public static final int IMAGING_DEVICE_NAME = 19;
	public static final int DEVICE_SERIAL_NUMBER = 20;
	public static final int COLLIMATOR_USED = 21;
	public static final int SOFTWARE_VERSION_NUMBER = 22;
	public static final int RADIOPHARMACEUTICAL_1 = 23;
	public static final int DOSAGE_1 = 24;
	public static final int RADIOPHARMACEUTICAL_2 = 25;
	public static final int DOSAGE_2 = 26;
	public static final int ISOTOPE_IMAGING_MODE = 27;
	public static final int ENERGY_WINDOW_1_CENTER = 28;
	public static final int ENERGY_WINDOW_1_WIDTH = 29;
	public static final int ENERGY_WINDOW_2_CENTER = 30;
	public static final int ENERGY_WINDOW_2_WIDTH = 31;
	public static final int ENERGY_WINDOW_3_CENTER = 32;
	public static final int ENERGY_WINDOW_3_WIDTH = 33;
	public static final int ENERGY_WINDOW_4_CENTER = 34;
	public static final int ENERGY_WINDOW_4_WIDTH = 35;
	public static final int PATIENT_ORIENTATION = 36;
	public static final int DIRECTIONAL_ORIENTATION = 110;
	public static final int SPATIAL_RESOLUTION = 37;
	public static final int SLICE_THICKNESS = 38;
	public static final int X_DIMENSIONS = 39;
	public static final int Y_DIMENSIONS = 40;
	public static final int Z_DIMENSIONS = 41;
	public static final int NUMBER_OF_IMAGE_SETS = 86;
	public static final int PIXEL_BIT_DEPTH = 42;
	public static final int TRUE_COLOUR_FLAG = 85;
	public static final int UNIFORMITY_CORRECTION = 43;
	public static final int ZOOM = 44;
	public static final int VFR_STRUCT = 111;
	public static final int TOTAL_COUNTS_IN_FRAME = 45;
	public static final int FRAME_TIME = 46;
	public static final int ACQUISITION_TIME = 47;
	public static final int UNUSED = 84;
	public static final int MAX_VALUE_IN_SET = 48;
	public static final int MIN_VALUE_IN_SET = 49;
	public static final int SCALE_FACTOR = 87;
	public static final int R_R_INTERVAL_TIME = 50;
	public static final int R_R_LOW_TOLERANCE_TIME = 112;
	public static final int R_R_HIGH_TOLERANCE_TIME = 113;
	public static final int CYCLES_IMAGED_PC = 51;
	public static final int CYCLES_ACCEPTED_PC = 52;
	public static final int CYCLES_REJECTED_PC = 53;
	public static final int END_DIASTOLIC_FRAME = 54;
	public static final int END_SYSTOLIC_FRAME = 55;
	public static final int EJECTION_FRACTION = 56;
	public static final int STARTING_ANGLE = 57;
	public static final int DEGREES_OF_ROTATION = 58;
	public static final int DIRECTION_OF_ROTATION = 59;
	public static final int REORIENTATION_TYPE = 60;
	public static final int RECONSTRUCTED_SLICES = 61;
	public static final int UPPER_WINDOW_GRAY_LEVEL = 62;
	public static final int LOWER_LEVEL_GRAY_LEVEL = 63;
	public static final int ASSOCIATED_COLOUR_MAP = 64;
	public static final int CUSTOMISED_COLOUR_MAP = 65;
	public static final int MANIPULATED_IMAGE = 66;
	public static final int AXIS_OF_ROTATION_CORR = 67;
	public static final int REORIENTATION_AZIMUTH = 68;
	public static final int REORIENTATION_ELEVATION = 69;
	public static final int FILTER_TYPE = 70;
	public static final int FILTER_ORDER = 71;
	public static final int CUTOFF_FREQUENCY = 72;
	public static final int RECONSTRUCTION_TYPE = 73;
	public static final int ATTENUATION_COEFFICIENT = 74;
	public static final int PROGRAM_SPECIFIC = 114;	
	
	/**
	 * Populates the ADAC key-value pairs information. There are arrays for KVP
	 * descriptions, data type and value lengths.
	 */
	public ADACDictionary() {

		int x;
		descriptions = new String[NUM_KEYS + 1];
		type = new int[NUM_KEYS + 1];
		valLength = new int[NUM_KEYS + 1];
		// Set array values to blanks strings or zeros
		for (int i = 0; i < NUM_KEYS; i++) {
			descriptions[i] = "";
			type[i] = 0;
			valLength[i] = 0;
		}

		// Start with a null entry so that key number matches entry
		descriptions[0] = null;
		type[0] = 0;
		valLength[0] = 0;

		// //////////////////////////////////////////////////////////
		// Patient demographics
		// //////////////////////////////////////////////////////////

		descriptions[PATIENT_NAME] = "Patient name";
		type[PATIENT_NAME] = BYTE;
		valLength[PATIENT_NAME] = 20;

		descriptions[PATIENT_ID] = "Patient ID";
		type[PATIENT_ID] = BYTE;
		valLength[PATIENT_ID] = 11;

		descriptions[PATIENT_SEX] = "Patient sex";
		type[PATIENT_SEX] = BYTE;
		valLength[PATIENT_SEX] = 1;
		
		descriptions[PATIENT_AGE] = "Patient age";
		type[PATIENT_AGE] = SHORT;
		valLength[PATIENT_AGE] = 2;

		descriptions[PATIENT_HEIGHT] = "Patient height";
		type[PATIENT_HEIGHT] = SHORT;
		valLength[PATIENT_HEIGHT] = 2;

		descriptions[PATIENT_WEIGHT] = "Patient weight";
		type[PATIENT_WEIGHT] = SHORT;
		valLength[PATIENT_WEIGHT] = 2;

		descriptions[UNIQUE_PATIENT_KEY] = "Unique patient key";
		type[UNIQUE_PATIENT_KEY] = BYTE;
		valLength[UNIQUE_PATIENT_KEY] = 6;

		descriptions[DATE_OF_BIRTH] = "Date of birth";
		type[DATE_OF_BIRTH] = BYTE;
		valLength[DATE_OF_BIRTH] = 8; // YYYYMMDD

		// //////////////////////////////////////////////////////////
		// Exam information
		// //////////////////////////////////////////////////////////
		x = 7;
		descriptions[x] = "Acquisition date";
		type[x] = BYTE;
		valLength[x] = 8; // YYYYMMDD

		x = 8;
		descriptions[x] = "Dose admin. time";
		type[x] = BYTE;
		valLength[x] = 8; // HH:MM:SS

		x = 9;
		descriptions[x] = "Unique exam key";
		type[x] = BYTE;
		valLength[x] = 8;

		x = 10;
		descriptions[x] = "Exam procedure";
		type[x] = BYTE;
		valLength[x] = 36;

		x = 11;
		descriptions[x] = "Reffering physician";
		type[x] = BYTE;
		valLength[x] = 20;

		x = 12;
		descriptions[x] = "Attending physician";
		type[x] = BYTE;
		valLength[x] = 20;

		x = 13;
		descriptions[x] = "Imaging modality";
		type[x] = BYTE;
		valLength[x] = 2;

		x = 14;
		descriptions[x] = "Institution name";
		type[x] = BYTE;
		valLength[x] = 20;

		x = 82;
		descriptions[x] = "Nrml crv file name";
		type[x] = BYTE;
		valLength[x] = 10;

		x = 15;
		descriptions[x] = "Histog. crv file name";
		type[x] = BYTE;
		valLength[x] = 20;

		x = 16;
		descriptions[x] = "Acquisition start time";
		type[x] = BYTE;
		valLength[x] = 10;

		// //////////////////////////////////////////////////////////
		// Relational information
		// //////////////////////////////////////////////////////////
		x = 17;
		descriptions[x] = "Data type";
		type[x] = BYTE;
		valLength[x] = 2;

		x = 18;
		descriptions[x] = "Image view ID";
		type[x] = BYTE;
		valLength[x] = 16;

		x = 83;
		descriptions[x] = "Unique object key";
		type[x] = BYTE;
		valLength[x] = 3;

		x = 75;
		descriptions[x] = "Associated parent file";
		type[x] = BYTE;
		valLength[x] = 20;

		// //////////////////////////////////////////////////////////
		// Acquisition information - general
		// //////////////////////////////////////////////////////////
		x = 19;
		descriptions[x] = "Imaging device name";
		type[x] = BYTE;
		valLength[x] = 10;

		x = 20;
		descriptions[x] = "Device serial number";
		type[x] = BYTE;
		valLength[x] = 12;

		x = 21;
		descriptions[x] = "Collimator used";
		type[x] = BYTE;
		valLength[x] = 6;

		x = 22;
		descriptions[x] = "Software version number";
		type[x] = BYTE;
		valLength[x] = 8;

		x = 23;
		descriptions[x] = "Radiopharmaceutical 1";
		type[x] = BYTE;
		valLength[x] = 16;

		x = 24;
		descriptions[x] = "Dosage 1";
		type[x] = SHORT;
		valLength[x] = 2;

		x = 25;
		descriptions[x] = "Radiopharmaceutical 2";
		type[x] = BYTE;
		valLength[x] = 16;

		x = 26;
		descriptions[x] = "Dosage 2";
		type[x] = SHORT;
		valLength[x] = 2;

		x = 27;
		descriptions[x] = "Isotope imaging mode";
		type[x] = BYTE;
		valLength[x] = 1;

		x = 28;
		descriptions[x] = "Energy window 1 (center)";
		type[x] = SHORT;
		valLength[x] = 2;

		x = 29;
		descriptions[x] = "Energy window 1 (width)";
		type[x] = SHORT;
		valLength[x] = 2;

		x = 30;
		descriptions[x] = "Energy window 2 (center)";
		type[x] = SHORT;
		valLength[x] = 2;

		x = 31;
		descriptions[x] = "Energy window 2 (width)";
		type[x] = SHORT;
		valLength[x] = 2;

		x = 32;
		descriptions[x] = "Energy window 3 (center)";
		type[x] = SHORT;
		valLength[x] = 2;

		x = 33;
		descriptions[x] = "Energy window 3 (width)";
		type[x] = SHORT;
		valLength[x] = 2;

		x = 34;
		descriptions[x] = "Energy window 4 (center)";
		type[x] = SHORT;
		valLength[x] = 2;

		x = 35;
		descriptions[x] = "Energy window 4 (width)";
		type[x] = SHORT;
		valLength[x] = 2;

		x = 36;
		descriptions[x] = "Patient orientation";
		type[x] = BYTE;
		valLength[x] = 6;

		x = 110;
		descriptions[x] = "Directional orientation";
		type[x] = BYTE;
		valLength[x] = 1;

		x = 37;
		descriptions[x] = "Spatial resolution (mm)";
		type[x] = FLOAT; // in mm
		valLength[x] = 4;

		x = 38;
		descriptions[x] = "Slice thickness (mm)";
		type[x] = FLOAT; // in mm
		valLength[x] = 4;

		x = 39;
		descriptions[x] = "X-dimensions";
		type[x] = SHORT;
		valLength[x] = 2;

		x = 40;
		descriptions[x] = "Y-dimensions";
		type[x] = SHORT;
		valLength[x] = 2;

		x = 41;
		descriptions[x] = "Z-dimensions";
		type[x] = SHORT;
		valLength[x] = 2;

		x = 86;
		descriptions[x] = "Number of image sets";
		type[x] = SHORT;
		valLength[x] = 2;

		x = 42;
		descriptions[x] = "Pixel bit-depth";
		type[x] = SHORT;
		valLength[x] = 2;

		x = 85;
		descriptions[x] = "True colour flag";
		type[x] = BYTE;
		valLength[x] = 1;

		x = 43;
		descriptions[x] = "Uniformity correction";
		type[x] = BYTE;
		valLength[x] = 20;

		x = 44;
		descriptions[x] = "Zoom";
		type[x] = FLOAT;
		valLength[x] = 4;

		x = 111;
		descriptions[x] = "VFR struct";
		type[x] = BYTE;
		valLength[x] = 64;

		// //////////////////////////////////////////////////////////
		// Acquisition information - multiframe specific
		// //////////////////////////////////////////////////////////

		x = 45;
		descriptions[x] = "Total counts in frame";
		type[x] = FLOAT;
		valLength[x] = 4;

		x = 46;
		descriptions[x] = "Frame time (ms)";
		type[x] = INT; // in ms
		valLength[x] = 4;

		x = 47;
		descriptions[x] = "Acquisition time (ms)";
		type[x] = INT; // in ms
		valLength[x] = 4;

		x = 84;
		descriptions[x] = "Unused";
		type[x] = BYTE;
		valLength[x] = 8;

		x = 48;
		descriptions[x] = "Max value in frame/set";
		type[x] = FLOAT;
		valLength[x] = 4;

		x = 49;
		descriptions[x] = "Min value in frame/set";
		type[x] = FLOAT;
		valLength[x] = 4;

		x = 87;
		descriptions[x] = "Scale factor";
		type[x] = FLOAT;
		valLength[x] = 4;

		// //////////////////////////////////////////////////////////
		// Acquisition information - gated specific
		// //////////////////////////////////////////////////////////

		x = 50;
		descriptions[x] = "R-R interval time";
		type[x] = SHORT;
		valLength[x] = 2;

		x = 112;
		descriptions[x] = "R-R low tolerance time";
		type[x] = SHORT;
		valLength[x] = 2;

		x = 113;
		descriptions[x] = "R-R high tolerance time";
		type[x] = SHORT;
		valLength[x] = 2;

		x = 51;
		descriptions[x] = "Cycles imaged (%)";
		type[x] = SHORT;
		valLength[x] = 2;

		x = 52;
		descriptions[x] = "Cycles accepted (%)";
		type[x] = SHORT;
		valLength[x] = 2;

		x = 53;
		descriptions[x] = "Cycles rejected (%)";
		type[x] = SHORT;
		valLength[x] = 2;

		x = 54;
		descriptions[x] = "End diastolic frame (approx)";
		type[x] = SHORT;
		valLength[x] = 2;

		x = 55;
		descriptions[x] = "End systolic frame (approx)";
		type[x] = SHORT;
		valLength[x] = 2;

		x = 56;
		descriptions[x] = "Ejection fraction (approx)";
		type[x] = FLOAT;
		valLength[x] = 4;

		// //////////////////////////////////////////////////////////
		// Acquisition information - SPECT specific
		// //////////////////////////////////////////////////////////

		x = 57;
		descriptions[x] = "Starting angle";
		type[x] = SHORT;
		valLength[x] = 2;

		x = 58;
		descriptions[x] = "Degrees of rotation";
		type[x] = SHORT;
		valLength[x] = 2;

		x = 59;
		descriptions[x] = "Direction of rotation";
		type[x] = BYTE; // "+" or "-"
		valLength[x] = 1;

		x = 60;
		descriptions[x] = "Reorientation type";
		type[x] = BYTE; // C for cardiac, B for brain, N for normal
		valLength[x] = 1;

		// ADAC description: Start frame, reconstruction limit
		x = 61;
		descriptions[x] = "Reconstructed slices";
		type[x] = SHORT;
		valLength[x] = 2;

		// //////////////////////////////////////////////////////////
		// Image display information
		// //////////////////////////////////////////////////////////

		x = 62;
		descriptions[x] = "Upper window gray level";
		type[x] = SHORT;
		valLength[x] = 2;

		x = 63;
		descriptions[x] = "Lower level gray level";
		type[x] = SHORT;
		valLength[x] = 2;

		x = 64;
		descriptions[x] = "Associated colour map";
		type[x] = SHORT;
		valLength[x] = 2;

		x = 65;
		descriptions[x] = "Customised colour map";
		type[x] = BYTE;
		valLength[x] = 20; // Filename (very short, should be in same dir)

		// //////////////////////////////////////////////////////////
		// Image processing information
		// //////////////////////////////////////////////////////////

		x = 66;
		descriptions[x] = "Manipulated image";
		type[x] = BYTE;
		valLength[x] = 1; // Y/N

		x = 67;
		descriptions[x] = "Axis of rotation corr.";
		type[x] = SHORT;
		valLength[x] = 2;

		x = 68;
		descriptions[x] = "Reorientation azimuth";
		type[x] = SHORT;
		valLength[x] = 2;

		x = 69;
		descriptions[x] = "Reorientation elevation";
		type[x] = SHORT;
		valLength[x] = 2;

		x = 70;
		descriptions[x] = "Filter type";
		type[x] = BYTE;
		valLength[x] = 16;

		x = 71;
		descriptions[x] = "Filter order";
		type[x] = SHORT;
		valLength[x] = 2;

		x = 72;
		descriptions[x] = "Cutoff frequency";
		type[x] = FLOAT;
		valLength[x] = 4;

		x = 73;
		descriptions[x] = "Reconstruction type";
		type[x] = BYTE;
		valLength[x] = 4; // eg OSA = oblique short axis

		x = 74;
		descriptions[x] = "Attenuation coefficient";
		type[x] = FLOAT;
		valLength[x] = 4;

		// //////////////////////////////////////////////////////////
		// Program specific and "extra" object information
		// //////////////////////////////////////////////////////////

		x = 114;
		descriptions[x] = "Program specific";
		type[x] = BYTE;
		valLength[x] = 800;

	}
}

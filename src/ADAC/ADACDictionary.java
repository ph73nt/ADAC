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
	 * The miscellaneous data type of a key value pair. Does not seem to be used
	 * anywhere I have seen.
	 */
	public static final int VAR = 4;
	/**
	 * Additional type to separate Program Specific (or "Extras") objects
	 */
	public static final int EXTRAS = 5;
	/**
	 * Descriptions of ADAC key-value pairs that appear in the IJ image info
	 */
	public final static String[] descriptions;
	/**
	 * Key-value pair data types
	 */
	public final static int[] type;
	/**
	 * Each value of a key-value pair is restricted to a set length and
	 * null-padded if necessary. This often leads to truncation of long fields
	 * such as patient name, which can be found in non-truncated format in the
	 * ADAC Extra Objects field.
	 */
	public final static int[] valLength;

	// //////////////////////////////////////////////////////////
	// Patient demographics
	// //////////////////////////////////////////////////////////
	public static final short PATIENT_NAME = 1;
	public static final short PATIENT_ID = 2;
	public static final short PATIENT_SEX = 3;
	public static final short PATIENT_AGE = 4;
	public static final short PATIENT_HEIGHT = 5;
	public static final short PATIENT_WEIGHT = 6;
	public static final short UNIQUE_PATIENT_KEY = 76;
	public static final short DATE_OF_BIRTH = 109;

	// //////////////////////////////////////////////////////////
	// Exam information
	// //////////////////////////////////////////////////////////
	public static final short ACQUISITION_DATE = 7;
	public static final short DOSE_ADMIN_TIME = 8;
	public static final short UNIQUE_EXAM_KEY = 9;
	public static final short EXAM_PROCEDURE = 10;
	public static final short REFFERING_PHYSICIAN = 11;
	public static final short ATTENDING_PHYSICIAN = 12;
	public static final short IMAGING_MODALITY = 13;
	public static final short INSTITUTION_NAME = 14;
	public static final short NRML_CRV_FILE_NAME = 82;
	public static final short HISTOG_CRV_FILE_NAME = 15;
	public static final short ACQUISITION_START_TIME = 16;

	// //////////////////////////////////////////////////////////
	// Relational information
	// //////////////////////////////////////////////////////////
	public static final short DATA_TYPE = 17;
	public static final short IMAGE_VIEW_ID = 18;
	public static final short UNIQUE_OBJECT_KEY = 83;
	public static final short ASSOCIATED_PARENT_FILE = 75;

	// //////////////////////////////////////////////////////////
	// Acquisition information - general
	// //////////////////////////////////////////////////////////
	public static final short IMAGING_DEVICE_NAME = 19;
	public static final short DEVICE_SERIAL_NUMBER = 20;
	public static final short COLLIMATOR_USED = 21;
	public static final short SOFTWARE_VERSION_NUMBER = 22;
	public static final short RADIOPHARMACEUTICAL_1 = 23;
	public static final short DOSAGE_1 = 24;
	public static final short RADIOPHARMACEUTICAL_2 = 25;
	public static final short DOSAGE_2 = 26;
	public static final short ISOTOPE_IMAGING_MODE = 27;
	public static final short ENERGY_WINDOW_1_CENTER = 28;
	public static final short ENERGY_WINDOW_1_WIDTH = 29;
	public static final short ENERGY_WINDOW_2_CENTER = 30;
	public static final short ENERGY_WINDOW_2_WIDTH = 31;
	public static final short ENERGY_WINDOW_3_CENTER = 32;
	public static final short ENERGY_WINDOW_3_WIDTH = 33;
	public static final short ENERGY_WINDOW_4_CENTER = 34;
	public static final short ENERGY_WINDOW_4_WIDTH = 35;
	public static final short PATIENT_ORIENTATION = 36;
	public static final short DIRECTIONAL_ORIENTATION = 110;
	public static final short SPATIAL_RESOLUTION = 37;
	public static final short SLICE_THICKNESS = 38;
	public static final short X_DIMENSIONS = 39;
	public static final short Y_DIMENSIONS = 40;
	public static final short Z_DIMENSIONS = 41;
	public static final short NUMBER_OF_IMAGE_SETS = 86;
	public static final short PIXEL_BIT_DEPTH = 42;
	public static final short TRUE_COLOUR_FLAG = 85;
	public static final short UNIFORMITY_CORRECTION = 43;
	public static final short ZOOM = 44;
	public static final short VFR_STRUCT = 111;

	// //////////////////////////////////////////////////////////
	// Acquisition information - multiframe specific
	// //////////////////////////////////////////////////////////
	public static final short TOTAL_COUNTS_IN_FRAME = 45;
	public static final short FRAME_TIME = 46;
	public static final short ACQUISITION_TIME = 47;
	public static final short UNUSED = 84;
	public static final short MAX_VALUE_IN_SET = 48;
	public static final short MIN_VALUE_IN_SET = 49;
	public static final short SCALE_FACTOR = 87;

	// //////////////////////////////////////////////////////////
	// Acquisition information - gated specific
	// //////////////////////////////////////////////////////////
	public static final short R_R_INTERVAL_TIME = 50;
	public static final short R_R_LOW_TOLERANCE_TIME = 112;
	public static final short R_R_HIGH_TOLERANCE_TIME = 113;
	public static final short CYCLES_IMAGED_PC = 51;
	public static final short CYCLES_ACCEPTED_PC = 52;
	public static final short CYCLES_REJECTED_PC = 53;
	public static final short END_DIASTOLIC_FRAME = 54;
	public static final short END_SYSTOLIC_FRAME = 55;
	public static final short EJECTION_FRACTION = 56;

	// //////////////////////////////////////////////////////////
	// Acquisition information - SPECT specific
	// //////////////////////////////////////////////////////////
	public static final short STARTING_ANGLE = 57;
	public static final short DEGREES_OF_ROTATION = 58;
	public static final short DIRECTION_OF_ROTATION = 59;
	public static final short REORIENTATION_TYPE = 60;
	public static final short RECONSTRUCTED_SLICES = 61;

	// //////////////////////////////////////////////////////////
	// Image display information
	// //////////////////////////////////////////////////////////
	public static final short UPPER_WINDOW_GRAY_LEVEL = 62;
	public static final short LOWER_LEVEL_GRAY_LEVEL = 63;
	public static final short ASSOCIATED_COLOUR_MAP = 64;
	public static final short CUSTOMISED_COLOUR_MAP = 65;

	// //////////////////////////////////////////////////////////
	// Image processing information
	// //////////////////////////////////////////////////////////
	public static final short MANIPULATED_IMAGE = 66;
	public static final short AXIS_OF_ROTATION_CORR = 67;
	public static final short REORIENTATION_AZIMUTH = 68;
	public static final short REORIENTATION_ELEVATION = 69;
	public static final short FILTER_TYPE = 70;
	public static final short FILTER_ORDER = 71;
	public static final short CUTOFF_FREQUENCY = 72;
	public static final short RECONSTRUCTION_TYPE = 73;
	public static final short ATTENUATION_COEFFICIENT = 74;

	// //////////////////////////////////////////////////////////
	// Program specific and "extra" object information
	// //////////////////////////////////////////////////////////
	public static final short PROGRAM_SPECIFIC = 114;

	static {

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

		// Populate patient demographics

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

		// Populate Exam Information
		descriptions[ACQUISITION_DATE] = "Acquisition date";
		type[ACQUISITION_DATE] = BYTE;
		valLength[ACQUISITION_DATE] = 8; // YYYYMMDD

		descriptions[DOSE_ADMIN_TIME] = "Dose admin. time";
		type[DOSE_ADMIN_TIME] = BYTE;
		valLength[DOSE_ADMIN_TIME] = 8; // HH:MM:SS

		descriptions[UNIQUE_EXAM_KEY] = "Unique exam key";
		type[UNIQUE_EXAM_KEY] = BYTE;
		valLength[UNIQUE_EXAM_KEY] = 8;

		descriptions[EXAM_PROCEDURE] = "Exam procedure";
		type[EXAM_PROCEDURE] = BYTE;
		valLength[EXAM_PROCEDURE] = 36;

		descriptions[REFFERING_PHYSICIAN] = "Reffering physician";
		type[REFFERING_PHYSICIAN] = BYTE;
		valLength[REFFERING_PHYSICIAN] = 20;

		descriptions[ATTENDING_PHYSICIAN] = "Attending physician";
		type[ATTENDING_PHYSICIAN] = BYTE;
		valLength[ATTENDING_PHYSICIAN] = 20;

		descriptions[IMAGING_MODALITY] = "Imaging modality";
		type[IMAGING_MODALITY] = BYTE;
		valLength[IMAGING_MODALITY] = 2;

		descriptions[INSTITUTION_NAME] = "Institution name";
		type[INSTITUTION_NAME] = BYTE;
		valLength[INSTITUTION_NAME] = 20;

		descriptions[NRML_CRV_FILE_NAME] = "Nrml crv file name";
		type[NRML_CRV_FILE_NAME] = BYTE;
		valLength[NRML_CRV_FILE_NAME] = 10;

		descriptions[HISTOG_CRV_FILE_NAME] = "Histog. crv file name";
		type[HISTOG_CRV_FILE_NAME] = BYTE;
		valLength[HISTOG_CRV_FILE_NAME] = 20;

		descriptions[ACQUISITION_START_TIME] = "Acquisition start time";
		type[ACQUISITION_START_TIME] = BYTE;
		valLength[ACQUISITION_START_TIME] = 10;

		// Populate relational information

		descriptions[DATA_TYPE] = "Data type";
		type[DATA_TYPE] = BYTE;
		valLength[DATA_TYPE] = 2;

		descriptions[IMAGE_VIEW_ID] = "Image view ID";
		type[IMAGE_VIEW_ID] = BYTE;
		valLength[IMAGE_VIEW_ID] = 16;

		descriptions[UNIQUE_OBJECT_KEY] = "Unique object key";
		type[UNIQUE_OBJECT_KEY] = BYTE;
		valLength[UNIQUE_OBJECT_KEY] = 3;

		descriptions[ASSOCIATED_PARENT_FILE] = "Associated parent file";
		type[ASSOCIATED_PARENT_FILE] = BYTE;
		valLength[ASSOCIATED_PARENT_FILE] = 20;

		// Populate general acquisition information
		descriptions[IMAGING_DEVICE_NAME] = "Imaging device name";
		type[IMAGING_DEVICE_NAME] = BYTE;
		valLength[IMAGING_DEVICE_NAME] = 10;

		descriptions[DEVICE_SERIAL_NUMBER] = "Device serial number";
		type[DEVICE_SERIAL_NUMBER] = BYTE;
		valLength[DEVICE_SERIAL_NUMBER] = 12;

		descriptions[COLLIMATOR_USED] = "Collimator used";
		type[COLLIMATOR_USED] = BYTE;
		valLength[COLLIMATOR_USED] = 6;

		descriptions[SOFTWARE_VERSION_NUMBER] = "Software version number";
		type[SOFTWARE_VERSION_NUMBER] = BYTE;
		valLength[SOFTWARE_VERSION_NUMBER] = 8;

		descriptions[RADIOPHARMACEUTICAL_1] = "Radiopharmaceutical 1";
		type[RADIOPHARMACEUTICAL_1] = BYTE;
		valLength[RADIOPHARMACEUTICAL_1] = 16;

		descriptions[DOSAGE_1] = "Dosage 1";
		type[DOSAGE_1] = SHORT;
		valLength[DOSAGE_1] = 2;

		descriptions[RADIOPHARMACEUTICAL_2] = "Radiopharmaceutical 2";
		type[RADIOPHARMACEUTICAL_2] = BYTE;
		valLength[RADIOPHARMACEUTICAL_2] = 16;

		descriptions[DOSAGE_2] = "Dosage 2";
		type[DOSAGE_2] = SHORT;
		valLength[DOSAGE_2] = 2;

		descriptions[ISOTOPE_IMAGING_MODE] = "Isotope imaging mode";
		type[ISOTOPE_IMAGING_MODE] = BYTE;
		valLength[ISOTOPE_IMAGING_MODE] = 1;

		descriptions[ENERGY_WINDOW_1_CENTER] = "Energy window 1 (center)";
		type[ENERGY_WINDOW_1_CENTER] = SHORT;
		valLength[ENERGY_WINDOW_1_CENTER] = 2;

		descriptions[ENERGY_WINDOW_1_WIDTH] = "Energy window 1 (width)";
		type[ENERGY_WINDOW_1_WIDTH] = SHORT;
		valLength[ENERGY_WINDOW_1_WIDTH] = 2;

		descriptions[ENERGY_WINDOW_2_CENTER] = "Energy window 2 (center)";
		type[ENERGY_WINDOW_2_CENTER] = SHORT;
		valLength[ENERGY_WINDOW_2_CENTER] = 2;

		descriptions[ENERGY_WINDOW_2_WIDTH] = "Energy window 2 (width)";
		type[ENERGY_WINDOW_2_WIDTH] = SHORT;
		valLength[ENERGY_WINDOW_2_WIDTH] = 2;

		descriptions[ENERGY_WINDOW_3_CENTER] = "Energy window 3 (center)";
		type[ENERGY_WINDOW_3_CENTER] = SHORT;
		valLength[ENERGY_WINDOW_3_CENTER] = 2;

		descriptions[ENERGY_WINDOW_3_WIDTH] = "Energy window 3 (width)";
		type[ENERGY_WINDOW_3_WIDTH] = SHORT;
		valLength[ENERGY_WINDOW_3_WIDTH] = 2;

		descriptions[ENERGY_WINDOW_4_CENTER] = "Energy window 4 (center)";
		type[ENERGY_WINDOW_4_CENTER] = SHORT;
		valLength[ENERGY_WINDOW_4_CENTER] = 2;

		descriptions[ENERGY_WINDOW_4_WIDTH] = "Energy window 4 (width)";
		type[ENERGY_WINDOW_4_WIDTH] = SHORT;
		valLength[ENERGY_WINDOW_4_WIDTH] = 2;

		descriptions[PATIENT_ORIENTATION] = "Patient orientation";
		type[PATIENT_ORIENTATION] = BYTE;
		valLength[PATIENT_ORIENTATION] = 6;

		descriptions[DIRECTIONAL_ORIENTATION] = "Directional orientation";
		type[DIRECTIONAL_ORIENTATION] = BYTE;
		valLength[DIRECTIONAL_ORIENTATION] = 1;

		descriptions[SPATIAL_RESOLUTION] = "Spatial resolution (mm)";
		type[SPATIAL_RESOLUTION] = FLOAT; // in mm
		valLength[SPATIAL_RESOLUTION] = 4;

		descriptions[SLICE_THICKNESS] = "Slice thickness (mm)";
		type[SLICE_THICKNESS] = FLOAT; // in mm
		valLength[SLICE_THICKNESS] = 4;

		descriptions[X_DIMENSIONS] = "X-dimensions";
		type[X_DIMENSIONS] = SHORT;
		valLength[X_DIMENSIONS] = 2;

		descriptions[Y_DIMENSIONS] = "Y-dimensions";
		type[Y_DIMENSIONS] = SHORT;
		valLength[Y_DIMENSIONS] = 2;

		descriptions[Z_DIMENSIONS] = "Z-dimensions";
		type[Z_DIMENSIONS] = SHORT;
		valLength[Z_DIMENSIONS] = 2;

		descriptions[NUMBER_OF_IMAGE_SETS] = "Number of image sets";
		type[NUMBER_OF_IMAGE_SETS] = SHORT;
		valLength[NUMBER_OF_IMAGE_SETS] = 2;

		descriptions[PIXEL_BIT_DEPTH] = "Pixel bit-depth";
		type[PIXEL_BIT_DEPTH] = SHORT;
		valLength[PIXEL_BIT_DEPTH] = 2;

		descriptions[TRUE_COLOUR_FLAG] = "True colour flag";
		type[TRUE_COLOUR_FLAG] = BYTE;
		valLength[TRUE_COLOUR_FLAG] = 1;

		descriptions[UNIFORMITY_CORRECTION] = "Uniformity correction";
		type[UNIFORMITY_CORRECTION] = BYTE;
		valLength[UNIFORMITY_CORRECTION] = 20;

		descriptions[ZOOM] = "Zoom";
		type[ZOOM] = FLOAT;
		valLength[ZOOM] = 4;

		descriptions[VFR_STRUCT] = "VFR struct";
		type[VFR_STRUCT] = BYTE;
		valLength[VFR_STRUCT] = 64;

		// Populate Multiframe-specific acquisition information

	    ////////////////////////////////////////////////////////////
	    // Acquisition information - multiframe specific
	    ////////////////////////////////////////////////////////////

		descriptions[TOTAL_COUNTS_IN_FRAME] = "Total counts in frame";
		type[TOTAL_COUNTS_IN_FRAME] = INT; // in ms
		valLength[TOTAL_COUNTS_IN_FRAME] = 4;

		descriptions[FRAME_TIME] = "Frame time (ms)";
		type[FRAME_TIME] = INT; // in ms
		valLength[FRAME_TIME] = 4;
		
		descriptions[ACQUISITION_TIME] = "Acquisition time (ms)";
		type[ACQUISITION_TIME] = INT; // in ms
		valLength[ACQUISITION_TIME] = 4;

		descriptions[UNUSED] = "Unused";
		type[UNUSED] = BYTE;
		valLength[UNUSED] = 8;

		descriptions[MAX_VALUE_IN_SET] = "Max value in frame/set";
		type[MAX_VALUE_IN_SET] = FLOAT;
		valLength[MAX_VALUE_IN_SET] = 4;

		descriptions[MIN_VALUE_IN_SET] = "Min value in frame/set";
		type[MIN_VALUE_IN_SET] = FLOAT;
		valLength[MIN_VALUE_IN_SET] = 4;

		descriptions[SCALE_FACTOR] = "Scale factor";
		type[SCALE_FACTOR] = FLOAT;
		valLength[SCALE_FACTOR] = 4;

		// Populate Gated-specific acquisition information

		descriptions[R_R_INTERVAL_TIME] = "R-R interval time";
		type[R_R_INTERVAL_TIME] = SHORT;
		valLength[R_R_INTERVAL_TIME] = 2;

		descriptions[R_R_LOW_TOLERANCE_TIME] = "R-R low tolerance time";
		type[R_R_LOW_TOLERANCE_TIME] = SHORT;
		valLength[R_R_LOW_TOLERANCE_TIME] = 2;

		descriptions[R_R_HIGH_TOLERANCE_TIME] = "R-R high tolerance time";
		type[R_R_HIGH_TOLERANCE_TIME] = SHORT;
		valLength[R_R_HIGH_TOLERANCE_TIME] = 2;

		descriptions[CYCLES_IMAGED_PC] = "Cycles imaged (%)";
		type[CYCLES_IMAGED_PC] = SHORT;
		valLength[CYCLES_IMAGED_PC] = 2;

		descriptions[CYCLES_ACCEPTED_PC] = "Cycles accepted (%)";
		type[CYCLES_ACCEPTED_PC] = SHORT;
		valLength[CYCLES_ACCEPTED_PC] = 2;

		descriptions[CYCLES_REJECTED_PC] = "Cycles rejected (%)";
		type[CYCLES_REJECTED_PC] = SHORT;
		valLength[CYCLES_REJECTED_PC] = 2;

		descriptions[END_DIASTOLIC_FRAME] = "End diastolic frame (approx)";
		type[END_DIASTOLIC_FRAME] = SHORT;
		valLength[END_DIASTOLIC_FRAME] = 2;

		descriptions[END_SYSTOLIC_FRAME] = "End systolic frame (approx)";
		type[END_SYSTOLIC_FRAME] = SHORT;
		valLength[END_SYSTOLIC_FRAME] = 2;

		descriptions[EJECTION_FRACTION] = "Ejection fraction (approx)";
		type[EJECTION_FRACTION] = FLOAT;
		valLength[EJECTION_FRACTION] = 4;

		// Populate SPECT-specific acquisition information

		descriptions[STARTING_ANGLE] = "Starting angle";
		type[STARTING_ANGLE] = SHORT;
		valLength[STARTING_ANGLE] = 2;

		descriptions[DEGREES_OF_ROTATION] = "Degrees of rotation";
		type[DEGREES_OF_ROTATION] = SHORT;
		valLength[DEGREES_OF_ROTATION] = 2;

		descriptions[DIRECTION_OF_ROTATION] = "Direction of rotation";
		type[DIRECTION_OF_ROTATION] = BYTE; // "+" or "-"
		valLength[DIRECTION_OF_ROTATION] = 1;

		descriptions[REORIENTATION_TYPE] = "Reorientation type";
		type[REORIENTATION_TYPE] = BYTE; // C for cardiac, B for brain, N for
											// normal
		valLength[REORIENTATION_TYPE] = 1;

		// ADAC description: Start frame, reconstruction limit
		descriptions[RECONSTRUCTED_SLICES] = "Reconstructed slices";
		type[RECONSTRUCTED_SLICES] = SHORT;
		valLength[RECONSTRUCTED_SLICES] = 2;

		// Populate Image display information

		descriptions[UPPER_WINDOW_GRAY_LEVEL] = "Upper window gray level";
		type[UPPER_WINDOW_GRAY_LEVEL] = SHORT;
		valLength[UPPER_WINDOW_GRAY_LEVEL] = 2;

		descriptions[LOWER_LEVEL_GRAY_LEVEL] = "Lower level gray level";
		type[LOWER_LEVEL_GRAY_LEVEL] = SHORT;
		valLength[LOWER_LEVEL_GRAY_LEVEL] = 2;

		descriptions[ASSOCIATED_COLOUR_MAP] = "Associated colour map";
		type[ASSOCIATED_COLOUR_MAP] = SHORT;
		valLength[ASSOCIATED_COLOUR_MAP] = 2;

		descriptions[CUSTOMISED_COLOUR_MAP] = "Customised colour map";
		type[CUSTOMISED_COLOUR_MAP] = BYTE;
		valLength[CUSTOMISED_COLOUR_MAP] = 20; // Filename (very short, should

		// Populate image processing information

		descriptions[MANIPULATED_IMAGE] = "Manipulated image";
		type[MANIPULATED_IMAGE] = BYTE;
		valLength[MANIPULATED_IMAGE] = 1; // Y/N

		descriptions[AXIS_OF_ROTATION_CORR] = "Axis of rotation corr.";
		type[AXIS_OF_ROTATION_CORR] = SHORT;
		valLength[AXIS_OF_ROTATION_CORR] = 2;

		descriptions[REORIENTATION_AZIMUTH] = "Reorientation azimuth";
		type[REORIENTATION_AZIMUTH] = SHORT;
		valLength[REORIENTATION_AZIMUTH] = 2;

		descriptions[REORIENTATION_ELEVATION] = "Reorientation elevation";
		type[REORIENTATION_ELEVATION] = SHORT;
		valLength[REORIENTATION_ELEVATION] = 2;

		descriptions[FILTER_TYPE] = "Filter type";
		type[FILTER_TYPE] = BYTE;
		valLength[FILTER_TYPE] = 16;

		descriptions[FILTER_ORDER] = "Filter order";
		type[FILTER_ORDER] = SHORT;
		valLength[FILTER_ORDER] = 2;

		descriptions[CUTOFF_FREQUENCY] = "Cutoff frequency";
		type[CUTOFF_FREQUENCY] = FLOAT;
		valLength[CUTOFF_FREQUENCY] = 4;

		descriptions[RECONSTRUCTION_TYPE] = "Reconstruction type";
		type[RECONSTRUCTION_TYPE] = BYTE;
		valLength[RECONSTRUCTION_TYPE] = 4; // eg OSA = oblique short axis

		descriptions[ATTENUATION_COEFFICIENT] = "Attenuation coefficient";
		type[ATTENUATION_COEFFICIENT] = FLOAT;
		valLength[ATTENUATION_COEFFICIENT] = 4;

		// Populate ADAC EXtras - programme specific information

		descriptions[PROGRAM_SPECIFIC] = "Program specific";
		type[PROGRAM_SPECIFIC] = EXTRAS;
		valLength[PROGRAM_SPECIFIC] = 800;

	}
}

package ADAC;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class ExtrasKvp extends ADACKvp {

	/**
	 * The size (in mm) of a pixel acquired on a 1024 square matrix. Other matrix sizes need scaling to this factor.
	 */
	public static final String CALIB_KEY = "CALB";
	public static final String CALIB_DESC = "Calibration factor";
	public static final String LONG_PATIENT_NAME_KEY = "WLAG";
	public static final String LONG_NAME_DESC = "Patient name (long)";
	public static final String LONG_ID_KEY = "WLAH";
	public static final String LONG_ID_DESC = "Patient ID (long)";
	public static final String STUID_KEY = "WLAS";
	public static final String STUID_DESC = "Study Instance UID";
	public static final String ACCNUM_KEY = "WLAA";
	public static final String ACCNUM_DESC = "Accession number";
	public static final String DOSE_KEY = "DOSE";
	public static final String DOSE_DESC = "Patient dose(s)";
	public static final String DETECTOR_KEY = "HADS";
	public static final String DETECTOR_DESC = "Detector heads";
	public static final String STUDY_DATE_KEY = "WLAY";
	public static final String STUDY_DATE_DESC = "Dicom study date";
	public static final String STUDY_TIME_KEY = "Dicom study time";
	public static final String STUDY_DESCRIPTION_KEY = "WLBB";
	public static final String STUDY_DESCRIPTION_DESC = "Study description";
	public static final String AET_KEY = "WLBD";
	public static final String AET_DESC = "Application entitiy title";
	public static final String SCHED_PROC_KEY = "WLBC";
	public static final String SCHED_PROC_DESC = "Scheduled procedure step ID";
	public static final String REQ_PROC_KEY = "WLBT";
	public static final String REQ_PROC_DESC = "Requested procedure ID";
	
	public static int LENGTH = ADACDictionary.valLength[ADACDictionary.PROGRAM_SPECIFIC];

	private HashMap<String, String> extraMap = null;

	public ExtrasKvp(KvpListener listener, ADACKey key) {
		super(listener, key);
	}

	@Override
	public String getString() {

		StringBuilder sb = new StringBuilder();

		Set<String> keys = extraMap.keySet();
		Iterator<String> it = keys.iterator();
		while (it.hasNext()) {

			String key = it.next();
			sb.append(key);
			sb.append(" = ");
			sb.append(extraMap.get(key));
			sb.append("\n");
		}

		return sb.toString().trim();
	}

	@Override
	protected void read() {
		listener.read(this);
	}

	/**
	 * Set the raw byte data that contains the ADAC Extra object items.
	 * 
	 * @param bytes
	 */
	public void setData(byte[] bytes) {

		makeMap(bytes);

	}

	private HashMap<String, String> makeMap(byte[] bytes) {

		extraMap = new HashMap<String, String>();

		String extras = new String(bytes);
		String[] tokens = extras.trim().split("\\u0000");
		for (int i = 0; i < tokens.length; i++) {

			String tok = tokens[i];

			// The first four characters represent a key
			// Next byte is a non-printing (or some random)
			// character. This is followed by the value string.
			// The minimum number of characters must be six
			if (tok.length() < 6) {
				return extraMap;
			}

			String key = tok.substring(0, 5);
			String value = tok.substring(6);

			extraMap.put(key, value);

		}

		return extraMap;

	}

	/**
	 * Returns a HashMap containing key-value pairs that represent Program
	 * Specific items in the ADAC image objects. These are often referred to as
	 * ADAC "extra" objects. Dicom worklist data is stored here.
	 * 
	 * @return
	 */
	public HashMap<String, String> getMap() {
		return extraMap;
	}

}

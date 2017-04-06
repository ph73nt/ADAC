package ADAC;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class ADACExtras {

	private final Map<String, String> extrasMap;

	public static final String CALIBRATION_FACTOR = "CALB";
	
	public ADACExtras(String extras) {

		// Define a key value pair map to place to extras
		extrasMap = new HashMap<String, String>();

		// Use a buffer for convenience
		ByteBuffer bbuff = ByteBuffer.wrap(extras.getBytes());
		byte[] key = new byte[4];

		// ADAC Extras are key-value pairs headed by a four character
		// header terminated by a null char, followed by the value
		// terminated by a null.
		while (bbuff.position() < extras.length() - 5) {
			
			// Get the 4 byte key
			bbuff.get(key, 0, 4);

			// Check the chars are all valid. If we find non
			// alpha-numeric bytes, then exit.
			if (!(new String(key)).matches("\\w\\w\\w\\w")) {
				break;
			}

			// Skip null char
			bbuff.get();

			// Build the value string
			StringBuffer val = new StringBuffer();
			byte nextChar;
			while ((nextChar = bbuff.get()) != 0) {
				val.append(nextChar);
			}

			// Write into the map
			extrasMap.put(new String(key), val.toString());

		}

	}
	
	public float getCalibrationFactor(){
		
		String strCalFactor = extrasMap.get(CALIBRATION_FACTOR);
		
		try {
			
			return Float.parseFloat(strCalFactor);
			
		} catch (NumberFormatException e){
			
			return 0f;
			
		}
		
	}

}

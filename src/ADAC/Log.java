package ADAC;

import ij.IJ;

public class Log {

	public static void log(String text){
		
		if (IJ.debugMode){
			
			IJ.log(text);
			
		}
		
	}
	
	public static void error(String title, String text){
		IJ.error(title, text);
	}
	
}

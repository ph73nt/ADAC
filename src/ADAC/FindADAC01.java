package ADAC;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FindADAC01 {

	public FindADAC01() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {

		BufferedInputStream bis = null;

		try {

			String fileName = args[0];
			File file = new File(fileName);
			InputStream is = new FileInputStream(file);
			bis = new BufferedInputStream(is);
			int i = 0;
			int lastAdac = 0;
			int thisAdac = 0;
			byte[] bytes = new byte[1];
			StringBuilder sb = new StringBuilder();

			while ((bis.read(bytes)) != -1) {

				sb.append(new String(bytes));
				if (sb.length() > 6) {
					sb.deleteCharAt(0);
				}

				i++;

				String string = sb.toString();
				if (string != null && string.equals("adac01")) {
					
					lastAdac = thisAdac;
					thisAdac = i - string.length();
					int since = thisAdac - lastAdac;
					
					System.out.println("\"adac01\" found at: " + thisAdac + " ; " + since
							+ " bytes since last \"adac01\"");
					
				}

//				if (i > 10) {
//					break;
//				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				bis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

}

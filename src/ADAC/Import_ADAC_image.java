package ADAC;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import ij.IJ;
import ij.ImagePlus;
import ij.io.FileInfo;
import ij.io.FileOpener;
import ij.io.OpenDialog;
import ij.plugin.PlugIn;

/**
 *
 * @author NTHOMSON
 */
public class Import_ADAC_image extends ImagePlus implements PlugIn {

	private BufferedInputStream inputStream;

	public Import_ADAC_image() {
	}

	public Import_ADAC_image(InputStream is) {
		this(new BufferedInputStream(is));
	}

	/** Constructs a DICOM reader that using an BufferredInputStream. */
	public Import_ADAC_image(BufferedInputStream bis) {
		inputStream = bis;
	}

	public void run(String arg) {

		IJ.showStatus("Opening ADAC image file...");

		OpenDialog od = new OpenDialog("Open ADAC image file...", arg);
		String directory = od.getDirectory();
		String fileName = od.getFileName();

		if (fileName == null) {
			return;
		}

		IJ.showStatus("Opening: " + directory + fileName);
		FileInfo fi = null;
		ADACDecoder ad = new ADACDecoder(directory, fileName);
		ad.setInputStream(inputStream);

		try {
			fi = ad.getFileInfo();
		} catch (IOException e) {
			String msg = e.getMessage();
			msg = "This does not appear to be a valid\n" + "ADAC file.";
			Log.error("ADACDecoder", msg);
			return;
		}

		if (fi != null && fi.width > 0 && fi.height > 0 && fi.offset > 0) {

			FileOpener fo = new FileOpener(fi);
			ImagePlus imp = fo.open(false);

			if (imp.getStackSize() > 1) {

				// Gated image set
				if (ad.isGated()) {

					// Is it a reconstruction?
					if (ad.slices > 0 && ad.intervals > 1) {
						// Yes it is a reconstruction
						setDimensions(1, ad.slices, ad.intervals);
					} else {
						setDimensions(1, ad.zdim, ad.intervals);
					}
					setOpenAsHyperStack(true);
				}

				setStack(fileName, imp.getStack());

			} else {
				setProcessor(fileName, imp.getProcessor());
			}

			setCalibration(imp.getCalibration());

			setProperty("Info", ad.getImageInfo());// getHeader());

			setFileInfo(fi); // needed for revert

			if (arg.equals("")) {
				show();
			}

		} else { // if (showErrors)
			Log.error("ADACDecoder", "Unable to decode ADAC header.");
		}

		IJ.showStatus("");

	}
}
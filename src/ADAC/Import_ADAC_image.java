package ADAC;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

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
	private FileInfo fi = new FileInfo();

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
		fi.fileName = od.getFileName();

		if (fi.fileName == null) {
			return;
		}

		IJ.showStatus("Opening: " + directory + fi.fileName);
		setStream(directory);
		
		ADACDecoder ad = new ADACDecoder(directory, fi.fileName);
		ad.setInputStream(inputStream);
		setParameters();

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

				setStack(fi.fileName, imp.getStack());

			} else {
				setProcessor(fi.fileName, imp.getProcessor());
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

	private void setStream(String directory) {

		try {
			
			if (directory.indexOf("://") > 0) { // is URL

				URL u = new URL(directory + fi.fileName);
				inputStream = new BufferedInputStream(u.openStream());
				fi.inputStream = inputStream;

			} else if (inputStream != null) {
				fi.inputStream = inputStream;
			} else {
				fi.directory = directory;
			}
			
		} catch (IOException e) {
			String msg = e.getMessage();
			msg = "This does not appear to be a valid\n" + "ADAC file.";
			Log.error("ADACDecoder", msg);
			return;
		}

	}

	private void setParameters() {

		fi.fileFormat = FileInfo.RAW;
		fi.intelByteOrder = false;

	}
}
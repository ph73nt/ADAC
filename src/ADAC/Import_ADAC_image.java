package ADAC;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

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
public class Import_ADAC_image extends ImagePlus implements PlugIn, ADACLog {

	private BufferedInputStream inputStream;
	private FileInfo fi = new FileInfo();

	// Bit depth and set the default bit depth
	private static final Map<Short, Integer> bitDepthMap = new HashMap<Short, Integer>();
	private ADACDecoder ad;
	static {
		bitDepthMap.put(null, FileInfo.GRAY16_SIGNED);
		bitDepthMap.put((short) 8, FileInfo.GRAY8);
		bitDepthMap.put((short) 16, FileInfo.GRAY16_SIGNED);
		bitDepthMap.put((short) 32, FileInfo.GRAY32_FLOAT);
	}

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

		try {
			ad = new ADACDecoder(directory, fi.fileName, inputStream, this);
		} catch (IOException e) {
			String msg = e.getMessage();
			msg = "This does not appear to be a valid\n" + "ADAC file.";
			error("ADACDecoder", msg);
		}

		setParameters();

		if (fi != null && fi.width > 0 && fi.height > 0 && fi.offset > 0) {

			FileOpener fo = new FileOpener(fi);
			ImagePlus imp = fo.open(false);

			if (imp.getStackSize() > 1) {

				// Gated image set
				if (ad.isGated()) {

					int intervals = ad.getNumberOfGatedIntervals();
					int slices = ad.getNumberOfSlices();
					int zdim = ad.getZDim();

					// Is it a reconstruction?
					if (ad.isReconstruction() && intervals > 1) {
						// Yes it is a reconstruction
						setDimensions(1, slices, intervals);
					} else {
						setDimensions(1, zdim, intervals);
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
			error("ADACDecoder", "Unable to decode ADAC header.");
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
			error("ADACDecoder", msg);
			return;
		}

	}

	private void setParameters() {

		fi.fileFormat = FileInfo.RAW;

		// ADAC objects always big-endian
		fi.intelByteOrder = false;

		fi.width = ad.getWidth();
		fi.height = ad.getHeight();
		fi.frameInterval = ad.getFrameTime();
		fi.nImages = ad.getNumberOfImages();
		fi.offset = ad.getImageOffset();
		log("Image offset: " + fi.offset);

		// Bitdepth
		short adBitDepth = ad.getBitDepth();
		fi.fileType = bitDepthMap.get(adBitDepth);

		// ADAC only does square pixels
		fi.pixelWidth = ad.getPixelSize();
		fi.pixelHeight = fi.pixelWidth;
		fi.unit = "mm";

	}

	public void log(String text) {
		
		if (IJ.debugMode) {

			IJ.log(text);

		}
		
	}

	public void error(String title, String text) {
		
		IJ.error(title, text);

		
	}
}
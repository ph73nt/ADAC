package ADAC;

import ij.IJ;
import ij.ImagePlus;
import ij.io.FileInfo;
import ij.io.FileOpener;
import ij.io.OpenDialog;
import ij.plugin.PlugIn;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

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
	  
    OpenDialog od = new OpenDialog("Open ADAC image file...", arg);
    String directory = od.getDirectory();
    String fileName = od.getFileName();
    
    if (fileName == null) {
      return;
    }
    
    IJ.showStatus("Opening: " + directory + fileName);
    FileInfo fi = null;
    ADACDecoder ad = new ADACDecoder(directory, fileName);
    ad.inputStream = inputStream;
    
    try {
      fi = ad.getFileInfo();
    } catch (IOException e) {
      String msg = e.getMessage();
      msg = "This does not appear to be a valid\n"
              + "ADAC file.";
      IJ.error("ADACDecoder", msg);
      return;
    }
    
    if (fi != null && fi.width > 0 && fi.height > 0 && fi.offset > 0) {
    	
      FileOpener fo = new FileOpener(fi);
      ImagePlus imp = fo.open(false);
//      ImageProcessor ip = imp.getProcessor();

      /*if (fi.fileType == FileInfo.GRAY16_SIGNED) {
      if (ad.rescaleIntercept != 0.0 && dd.rescaleSlope == 1.0) {
      ip.add(ad.rescaleIntercept);
      }
      } else if (dd.rescaleIntercept != 0.0 && (dd.rescaleSlope == 1.0 || fi.fileType == FileInfo.GRAY8)) {
      double[] coeff = new double[2];
      coeff[0] = dd.rescaleIntercept;
      coeff[1] = dd.rescaleSlope;
      imp.getCalibration().setFunction(Calibration.STRAIGHT_LINE, coeff, "gray value");
      }*/

//      if (dd.windowWidth > 0.0) {
//        double min = dd.windowCenter - dd.windowWidth / 2;
//        double max = dd.windowCenter + dd.windowWidth / 2;
//        Calibration cal = imp.getCalibration();
//        min = cal.getRawValue(min);
//        max = cal.getRawValue(max);
//        ip.setMinAndMax(min, max);
//        if (IJ.debugMode) {
//          IJ.log("window: " + min + "-" + max);
//        }
//      }
      
      if (imp.getStackSize() > 1) {
        setStack(fileName, imp.getStack());
      } else {
        setProcessor(fileName, imp.getProcessor());
      }
      
      setCalibration(imp.getCalibration());

      setProperty("Info", ad.header);//getHeader());

      setFileInfo(fi); // needed for revert
      
      if (arg.equals("")) {
        show();
      }
      
    } else { //if (showErrors)
      IJ.error("ADACDecoder", "Unable to decode ADAC header.");
    }
    
    IJ.showStatus("");

  }
}
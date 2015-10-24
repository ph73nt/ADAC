package ADAC;

import ij.IJ;
import ij.ImagePlus;
import ij.io.FileInfo;
import ij.io.FileOpener;
import ij.io.OpenDialog;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Vector;

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
      ImageProcessor ip = imp.getProcessor();

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

class ADACDecoder {

  private String directory, fileName;
  private boolean littleEndian = false;
  private int location = 0, offset = 0;
  private byte[] bytHeader = new byte[ADACDictionary.IM_OFFSET];

  ADACDictionary dict = new ADACDictionary();
  BufferedInputStream inputStream, f;

  public String header, AD_Type, AD_ex_objs;
  public Vector values = new Vector();
  public byte datTyp, unused;
  public short keynum, fieldOffset;
  public int xdim, ydim, zdim, bitDepth, intervals, noSets;
  public double slice_t, frameTime;

  public ADACDecoder(String directory, String fileName) {
    this.directory = directory;
    this.fileName = fileName;
  }

  FileInfo getFileInfo() throws IOException {
    FileInfo fi = new FileInfo();
    fi.fileFormat = fi.RAW;
    fi.fileName = fileName;
    if (directory.indexOf("://") > 0) { // is URL
      URL u = new URL(directory + fileName);
      inputStream = new BufferedInputStream(u.openStream());
      fi.inputStream = inputStream;
    } else if (inputStream != null) {
      fi.inputStream = inputStream;
    } else {
      fi.directory = directory;
    }

    if (inputStream != null) {
      f = inputStream;
    } else {
      f = new BufferedInputStream(new FileInputStream(directory + fileName));
    }
    if (IJ.debugMode) {
      IJ.log("");
      IJ.log("ADACDecoder: decoding " + fileName);
    }

    // Copy header into a byte array for parsing forwards and backwards
    f.read(bytHeader, 0, ADACDictionary.IM_OFFSET);
    header = getHeader();

    // Set some default values for testing
    fi.width = xdim;
    fi.height = ydim;
    fi = setFrames(fi);
    fi.pixelDepth = slice_t;
    fi.fileType = bitDepth;
    fi.frameInterval = frameTime;
    fi.intelByteOrder = false;            // Big endian on Sun Solaris
    fi.offset = ADACDictionary.IM_OFFSET;
    fi = parseADACExtras(fi);

    return fi;
  }

  String getHeader() throws IOException {
    String hdr;
    //////////////////////////////////////////////////////////////
    // Administrative header info
    //////////////////////////////////////////////////////////////

    // First 10 bytes reserved for preamble
    hdr = getString(6) + "\n";
    IJ.log(hdr);                 // says adac01
    try {
      short labels = getShort();
      IJ.log(Integer.toString(labels)); // Number of labels in header
      IJ.log(Integer.toString(getByte()));  // Number of sub-headers
      IJ.log(Integer.toString(getByte()));  // Unused byte

      offset = location;

      IJ.log("location = " + location);

      // For each header field available.. get them
      values.setSize(ADACDictionary.NUM_KEYS + 1);

      for (short i = 0; i < labels; i++) {
        
    	// Attempt to find the next key...
        //   ...the keynum (description)
        //   ...the offset to the value
        getKeys();
        
        // Remember how far through the list of headers we have got
        offset = location;
        //IJ.log("location[" + i + "] = " + location);
        location = fieldOffset;
        //IJ.log("location[" + i + "] = " + location);
        switch (datTyp) {
        
          case ADACDictionary.BYTE:
            // Differentiate between byte proper and a string
            //  (ADAC header does not)
            if (dict.type[keynum] == ADACDictionary.STRING) {
              switch (keynum) {
                case 114:
                  AD_ex_objs = getString(dict.valLength[keynum]);
                  values.setElementAt(AD_ex_objs, keynum);
                  break;
                case 17:
                  AD_Type = getStringLessNull(dict.valLength[keynum]);
                  values.setElementAt(AD_Type, keynum);
                  break;
                default:
                  values.setElementAt(
                          getStringLessNull(dict.valLength[keynum]), keynum);
                  break;
              }
            } else {
              values.setElementAt((byte) getByte(), keynum);
            }
            break;
            
          case ADACDictionary.SHORT:
            short shortValue = (short) getShort();
            switch (keynum) {
              case 39: // X-dimension
                xdim = shortValue;
                break;
              case 40: //Y-dimension
                ydim = shortValue;
                break;
              case 41: //Z dimension
                zdim = shortValue;
                break;
              case 42: // Pixel depth
                switch (shortValue) {
                  case 8:
                    bitDepth = FileInfo.GRAY8;
                    break;
                  case 16:
                    bitDepth = FileInfo.GRAY16_SIGNED;
                    break;
                  case 32:
                    bitDepth = FileInfo.GRAY32_FLOAT;
                    break;
                  default:
                    bitDepth = FileInfo.GRAY16_UNSIGNED;
                }
                ;
                break;
              case 86:
                noSets = shortValue;
                IJ.log("" + noSets);
                break;
              case 61:
                intervals = shortValue;
                IJ.log("" + intervals);
                break;
            }
            values.setElementAt(shortValue, keynum);
            break;
            
          case ADACDictionary.INT:
            int m_Int = getInt();
            switch (keynum) {
              case 46:
                // Time oper frame
                frameTime = ((double) m_Int) / 1000d;
                break;
            }
            values.setElementAt(m_Int, keynum);
            break;
            
          case ADACDictionary.FLOAT:
            float floatValue = getFloat();
            switch (keynum) {
              case 38: // Slice thickness
                slice_t = floatValue;
            }
            values.setElementAt(floatValue, keynum);
            break;
        }
        hdr += dict.descriptions[keynum] + " = "
                + values.elementAt(keynum) + "\n";

        IJ.log(keynum + ", " + datTyp + ", " + unused + ", " + fieldOffset + ", " + values.elementAt(keynum));
        
        location = offset;
      }
      IJ.log("" + values.size());

      /////////////// Get ready for the next code:
      getKeys();
      IJ.log(Integer.toString(keynum));

      return hdr;
    } catch (IOException e) {
      IJ.error("Failed to retrieve ADAC image file header. "
              + "Is this an ADAC image file?");
      return null;
    }
  }

  FileInfo parseADACExtras(FileInfo fi) {
    String anExtra, keyExtra;
    for (int j = 0; j < 2; j++) {

      // Use switch to parse several "extra" parameters
      switch (j) {
        default:
          keyExtra = null;
        case 0:
          keyExtra = "CALB";
        case 1:
          keyExtra = "WLAA";
      }

      int indxExtra = AD_ex_objs.indexOf(keyExtra);
      if (indxExtra != -1 && !(keyExtra.equals(null))) {
        IJ.log("CALB at " + indxExtra);

        byte[] someBytes;
        // Check we've got readable ASCII chars - 32 is the first (space)
        //  and 126 is the last (~);
        int i = 0;
        do {
          int index = indxExtra + keyExtra.length() + i;
          anExtra = AD_ex_objs.substring(index, index + 1);
          someBytes = anExtra.getBytes();
          IJ.log("" + (int) someBytes[0]);
          i++;
        } while ((int) someBytes[0] < 32 && (int) someBytes[0] > 126);
        // We should have skipped any rubbish preamble, like a shift or STX

        String message = "";
        boolean continu = false;
        do {
          int index = indxExtra + keyExtra.length() + i;
          anExtra = AD_ex_objs.substring(index, index + 1);
          someBytes = anExtra.getBytes();
          if ((int) someBytes[0] > 31 && (int) someBytes[0] < 127) {
            continu = true;
            message += anExtra;
          } else {
            continu = false;
          }
          IJ.log("" + (int) someBytes[0]);
          i++;
        } while (continu);

        // Assuming the object is terminated by a non-printingcharacter,
        //  we have the full "extra."  Now use information in some extras:
        switch (j) {
          case 0:
            // Calibration factor is size (mm) of a pixel if the image were
            //   scaled to 1024 pixels wide or high
            double calibF;
            try {
              calibF = Float.parseFloat(message);
              if (calibF != 0) {
                fi.pixelWidth = calibF * 1024 / xdim;
                // ADAC only does square pixels
                fi.pixelHeight = fi.pixelWidth;
                fi.unit = "mm";
              }
            } catch (NumberFormatException e) {
              IJ.log("Unable to parse calibration factor");
            }
            break;
          case 1:
            
        }
        IJ.log(message);
        message = "";

      }
    }

    return fi;
  }

  FileInfo setFrames(FileInfo fi) {
    if (AD_Type.matches("GE")) { // Gated SPECT doesn't yet work!
      fi.nImages = noSets * intervals;
    } else {
      fi.nImages = zdim;
    }
    return fi;
  }

  void getKeys() throws IOException {
    keynum = getShort();
    datTyp = getByte();
    unused = getByte();
    fieldOffset = getShort();
  }

  String getString(int length) throws IOException {
    byte[] buf = new byte[length];
    System.arraycopy(bytHeader, location, buf, 0, length);
    location += length;
    return new String(buf);
  }

  String getStringLessNull(int length) throws IOException {
    String theString = "";
    int i;
    for (i = 0; i
            < length; i++) {
      byte[] buf = new byte[1];
      buf[0] = getByte();
      if (buf[0] != 0) {
        String temp = new String(buf);
        theString += (char) buf[0];
      }
    }
    return theString;
  }

  byte getByte() throws IOException {
    byte b = bytHeader[location];
    ++location;
    return b;
  }

  short getShort() throws IOException {
    byte b0 = getByte();
    byte b1 = getByte();
    // Remember to discard low bits when getting the short a Java
    //  has no byte or short operations, only casting from int
    if (littleEndian) {
      return (short) ((b1 << 8) + (b0 & 0xff));
   } else {
      return (short) ((b0 << 8) + (b1 & 0xff));
    }
  }

  final int getInt() throws IOException {
    int b0 = getByte();
    int b1 = getByte();
    int b2 = getByte();
    int b3 = getByte();
    if (littleEndian) {
      return ((b3 << 24) + (b2 << 16) + (b1 << 8) + b0);
    } else {
      return ((b0 << 24) + (b1 << 16) + (b2 << 8) + b3);
    }
  }

  double getDouble() throws IOException {
    int b0 = getByte();
    int b1 = getByte();
    int b2 = getByte();
    int b3 = getByte();
    int b4 = getByte();
    int b5 = getByte();
    int b6 = getByte();
    int b7 = getByte();
    long res = 0;
    if (littleEndian) {
      res += b0;
      res += (((long) b1) << 8);
      res += (((long) b2) << 16);
      res += (((long) b3) << 24);
      res += (((long) b4) << 32);
      res += (((long) b5) << 40);
      res += (((long) b6) << 48);
      res += (((long) b7) << 56);
    } else {
      res += b7;
      res += (((long) b6) << 8);
      res += (((long) b5) << 16);
      res += (((long) b4) << 24);
      res += (((long) b3) << 32);
      res += (((long) b2) << 40);
      res += (((long) b1) << 48);
      res += (((long) b0) << 56);
    }
    return Double.longBitsToDouble(res);
  }

  float getFloat() throws IOException {
    int b0 = getByte();
    int b1 = getByte();
    int b2 = getByte();
    int b3 = getByte();
    int res = 0;
    if (littleEndian) {
      res += b0;
      res += (((long) b1) << 8);
      res += (((long) b2) << 16);
      res += (((long) b3) << 24);
    } else {
      res += b3;
      res += (((long) b2) << 8);
      res += (((long) b1) << 16);
      res += (((long) b0) << 24);
    }
    return Float.intBitsToFloat(res);
  }

}
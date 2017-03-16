package ADAC;

import ij.IJ;
import ij.ImagePlus;
import ij.io.FileInfo;
import ij.io.FileOpener;
import ij.io.OpenDialog;
import ij.plugin.PlugIn;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
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

class ADACDecoder {

  private String directory, fileName;
  private boolean littleEndian = false;
  private int offset = 0;
  private ByteBuffer keyBuffer;
  private ByteBuffer valBuffer;
  private ADACDictionary dict = new ADACDictionary();
  BufferedInputStream inputStream;
  private BufferedInputStream f;
  private byte[] valHeaders;

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
    fi.fileFormat = FileInfo.RAW;
    fi.fileName = fileName;
    fi.intelByteOrder = false;
    
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

    // Copy header into a byteBuffer for parsing forwards and backwards
    byte[] bytHeader = new byte[ADACDictionary.LABEL_OFFSET];
    valHeaders = new byte[ADACDictionary.IM_OFFSET];
    f.read(bytHeader, 0, bytHeader.length);
    f.read(valHeaders, ADACDictionary.LABEL_OFFSET, valHeaders.length - ADACDictionary.LABEL_OFFSET);

    if(fi.intelByteOrder){
    	keyBuffer.order(ByteOrder.LITTLE_ENDIAN);
    	valBuffer.order(ByteOrder.LITTLE_ENDIAN);
    }
    
    keyBuffer = ByteBuffer.wrap(bytHeader);
    valBuffer = ByteBuffer.wrap(valHeaders);
    
    // Parse the header
    header = getHeader();

    // Set some default values for testing
    fi.width = xdim;
    fi.height = ydim;
    fi = setFrames(fi);
    fi.pixelDepth = slice_t;
    fi.fileType = bitDepth;
    fi.frameInterval = frameTime;
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
    hdr = getKeyString(6) + "\n";
    IJ.log(hdr);                 // says adac01

    try {
    	
      short labels = keyBuffer.getShort();
      IJ.log(Integer.toString(labels)); // Number of labels in header
      IJ.log(Integer.toString(keyBuffer.get()));  // Number of sub-headers
      IJ.log(Integer.toString(keyBuffer.get()));  // Unused byte

      // For each header field available.. get them
      values.setSize(ADACDictionary.NUM_KEYS + 1);

      for (short i = 0; i < labels; i++) {
        
    	// Attempt to find the next key...
        //   ...the keynum (description)
        //   ...the offset to the value
        getKeys();
        
        switch (datTyp) {
        
          case ADACDictionary.BYTE:
            // Differentiate between byte proper and a string
            //  (ADAC header does not)
            if (dict.type[keynum] == ADACDictionary.STRING) {
              switch (keynum) {
                case 114:
                  AD_ex_objs = getValString(dict.valLength[keynum], fieldOffset);
                  values.setElementAt(AD_ex_objs, keynum);
                  break;
                case 17:
                  AD_Type = getValString(dict.valLength[keynum], fieldOffset);
                  values.setElementAt(AD_Type, keynum);
                  break;
                default:
                  values.setElementAt(
                          getValString(dict.valLength[keynum], fieldOffset), keynum);
                  break;
              }
            } else {
              values.setElementAt(keyBuffer.get(fieldOffset), keynum);
            }
            break;
            
          case ADACDictionary.SHORT:
        	  
            short shortValue = valBuffer.getShort(fieldOffset);
            
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
                };
                
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
        	  
            int m_Int = valBuffer.getInt(fieldOffset);
            
            switch (keynum) {
            
              case 46:
                // Time oper frame
                frameTime = ((double) m_Int) / 1000d;
                break;
                
            }
            values.setElementAt(m_Int, keynum);
            break;
            
          case ADACDictionary.FLOAT:
        	  
            float floatValue = valBuffer.getFloat(fieldOffset);
            
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
        
      }
      
      IJ.log("" + values.size());

      /////////////// Get ready for the next code:
//      getKeys();
//      IJ.log(Integer.toString(keynum));

      return hdr;
      
    } catch (IOException e) {
      IJ.error("Failed to retrieve ADAC image file header. "
              + "Is this an ADAC image file?");
      return null;
    }
  }

  FileInfo parseADACExtras(FileInfo fi) {
    
	  final int INDX_CALB = 0;
	  
	  // Define ADAC extras that we will parse
      String[] extras = {"CALB", "WLAA"};
	  
	  String anExtra;
    
    for (int j = 0; j < extras.length; j++) {

      int indxExtra = AD_ex_objs.indexOf(extras[j]);
      if (indxExtra != -1 && !(extras[j].equals(null))) {
        IJ.log("CALB at " + indxExtra);

        byte[] someBytes;
        // Check we've got readable ASCII chars - 32 is the first (space)
        //  and 126 is the last (~);
        int i = 0;
        do {
          int index = indxExtra + extras[j].length() + i;
          anExtra = AD_ex_objs.substring(index, index + 1);
          someBytes = anExtra.getBytes();
          IJ.log("" + (int) someBytes[0]);
          i++;
        } while ((int) someBytes[0] < 32 && (int) someBytes[0] > 126);
        // We should have skipped any rubbish preamble, like a shift or STX

        String message = "";
        boolean continu = false;
        do {
          int index = indxExtra + extras[j].length() + i;
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
          case INDX_CALB:
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
	  
    keynum = keyBuffer.getShort();
    datTyp = keyBuffer.get();
    unused = keyBuffer.get();
    fieldOffset = keyBuffer.getShort();
  
  }

  String getKeyString(int length) throws IOException {
	  
	  byte[] mBytes = new byte[length];
	  keyBuffer.get(mBytes, 0, length);
	  String string = new String(mBytes);
	  return string.trim();
	  
  }

  String getValString(int length, int offset) throws IOException {
	  
	  int theOffset = offset - ADACDictionary.LABEL_OFFSET;
	  
	  byte[] mBytes = new byte[length];
	  System.arraycopy(valHeaders, theOffset, mBytes, 0, length);
	  
	  return new String(valHeaders).trim();
	  
  }
  
}
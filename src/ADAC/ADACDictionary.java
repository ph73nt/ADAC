package ADAC;

/**
 *
 * @author NTHOMSON
 */
public class ADACDictionary {

  public static final int noKeys = 114;
  public static final int aByte = 0, aShort = 1, anInt = 2;
  public static final int aFloat = 3, aVar = 4, aString = 5;
  public String[] descriptions;
  public int[] type, valLength;

  public ADACDictionary() {

    int x;
    descriptions = new String[noKeys + 1];
    type = new int[noKeys + 1];
    valLength = new int[noKeys + 1];
    // Set array values to blanks strings or zeros
    for (int i = 0;
            i < noKeys;
            i++) {
      descriptions[i] = "";
      type[i] = 0;
      valLength[i] = 0;
    }
    
    // Start with a null entry so that key number matches entry
    x = 0;
    descriptions[x] = null;
    type[x] = 0;
    valLength[x] = 0;

    ////////////////////////////////////////////////////////////
    // Patient demographics
    ////////////////////////////////////////////////////////////

    x = 1;
    descriptions[x] = "Patient name";
    type[x] = aString;
    valLength[x] = 20;

    x = 2;
    descriptions[x] = "Patient ID";
    type[x] = aString;
    valLength[x] = 11;

    x = 3;
    descriptions[x] = "Patient sex";
    type[x] = aString;
    valLength[x] = 1;

    x = 4;
    descriptions[x] = "Patient age";
    type[x] = aShort;
    valLength[x] = 2;

    x = 5;
    descriptions[x] = "Patient height";
    type[x] = aShort;
    valLength[x] = 2;

    x = 6;
    descriptions[x] = "Patient weight";
    type[x] = aShort;
    valLength[x] = 2;

    x = 76;
    descriptions[x] = "Unique patient key";
    type[x] = aString;
    valLength[x] = 6;

    x = 109;
    descriptions[x] = "Date of birth";
    type[x] = aString;
    valLength[x] = 8;  // YYYYMMDD

    ////////////////////////////////////////////////////////////
    // Exam information
    ////////////////////////////////////////////////////////////
    x = 7;
    descriptions[x] = "Acquisition date";
    type[x] = aString;
    valLength[x] = 8;  //YYYYMMDD

    x = 8;
    descriptions[x] = "Dose admin. time";
    type[x] = aString;
    valLength[x] = 8;  // HH:MM:SS

    x = 9;
    descriptions[x] = "Unique exam key";
    type[x] = aString;
    valLength[x] = 8;

    x = 10;
    descriptions[x] = "Exam procedure";
    type[x] = aString;
    valLength[x] = 36;

    x = 11;
    descriptions[x] = "Reffering physician";
    type[x] = aString;
    valLength[x] = 20;

    x = 12;
    descriptions[x] = "Attending physician";
    type[x] = aString;
    valLength[x] = 20;

    x = 13;
    descriptions[x] = "Imaging modality";
    type[x] = aString;
    valLength[x] = 2;

    x = 14;
    descriptions[x] = "Institution name";
    type[x] = aString;
    valLength[x] = 20;

    x = 82;
    descriptions[x] = "Nrml crv file name";
    type[x] = aString;
    valLength[x] = 10;

    x = 15;
    descriptions[x] = "Histog. crv file name";
    type[x] = aString;
    valLength[x] = 20;

    x = 16;
    descriptions[x] = "Acquisition start time";
    type[x] = aString;
    valLength[x] = 10;

    ////////////////////////////////////////////////////////////
    // Relational information
    ////////////////////////////////////////////////////////////
    x = 17;
    descriptions[x] = "Data type";
    type[x] = aString;
    valLength[x] = 2;

    x = 18;
    descriptions[x] = "Image view ID";
    type[x] = aString;
    valLength[x] = 16;

    x = 83;
    descriptions[x] = "Unique object key";
    type[x] = aString;
    valLength[x] = 3;

    x = 75;
    descriptions[x] = "Associated parent file";
    type[x] = aString;
    valLength[x] = 20;

    ////////////////////////////////////////////////////////////
    // Acquisition information - general
    ////////////////////////////////////////////////////////////
    x = 19;
    descriptions[x] = "Imaging device name";
    type[x] = aString;
    valLength[x] = 10;

    x = 20;
    descriptions[x] = "Device serial number";
    type[x] = aString;
    valLength[x] = 12;

    x = 21;
    descriptions[x] = "Collimator used";
    type[x] = aString;
    valLength[x] = 6;

    x = 22;
    descriptions[x] = "Software version number";
    type[x] = aString;
    valLength[x] = 8;

    x = 23;
    descriptions[x] = "Radiopharmaceutical 1";
    type[x] = aString;
    valLength[x] = 16;

    x = 24;
    descriptions[x] = "Dosage 1";
    type[x] = aShort;
    valLength[x] = 2;

    x = 25;
    descriptions[x] = "Radiopharmaceutical 2";
    type[x] = aString;
    valLength[x] = 16;

    x = 26;
    descriptions[x] = "Dosage 2";
    type[x] = aShort;
    valLength[x] = 2;

    x = 27;
    descriptions[x] = "Isotope imaging mode";
    type[x] = aString;
    valLength[x] = 1;

    x = 28;
    descriptions[x] = "Enery window 1 (center)";
    type[x] = aShort;
    valLength[x] = 2;

    x = 29;
    descriptions[x] = "Enery window 1 (width)";
    type[x] = aShort;
    valLength[x] = 2;

    x = 30;
    descriptions[x] = "Enery window 2 (center)";
    type[x] = aShort;
    valLength[x] = 2;

    x = 31;
    descriptions[x] = "Enery window 2 (width)";
    type[x] = aShort;
    valLength[x] = 2;

    x = 32;
    descriptions[x] = "Enery window 3 (center)";
    type[x] = aShort;
    valLength[x] = 2;

    x = 33;
    descriptions[x] = "Enery window 3 (width)";
    type[x] = aShort;
    valLength[x] = 2;

    x = 34;
    descriptions[x] = "Enery window 4 (center)";
    type[x] = aShort;
    valLength[x] = 2;

    x = 35;
    descriptions[x] = "Enery window 4 (width)";
    type[x] = aShort;
    valLength[x] = 2;

    x = 36;
    descriptions[x] = "Patient orientation";
    type[x] = aString;
    valLength[x] = 6;

    x = 110;
    descriptions[x] = "Directional orientation";
    type[x] = aString;
    valLength[x] = 1;

    x = 37;
    descriptions[x] = "Spatial resolution";
    type[x] = aFloat; // in mm
    valLength[x] = 4;

    x = 38;
    descriptions[x] = "Slice thickness";
    type[x] = aFloat; // in mm
    valLength[x] = 4;

    x = 39;
    descriptions[x] = "X-dimensions";
    type[x] = aShort;
    valLength[x] = 2;

    x = 40;
    descriptions[x] = "Y-dimensions";
    type[x] = aShort;
    valLength[x] = 2;

    x = 41;
    descriptions[x] = "Z-dimensions";
    type[x] = aShort;
    valLength[x] = 2;

    x = 86;
    descriptions[x] = "Number of image sets";
    type[x] = aShort;
    valLength[x] = 2;

    x = 42;
    descriptions[x] = "Pixel bit-depth";
    type[x] = aShort;
    valLength[x] = 2;

    x = 85;
    descriptions[x] = "True colour flag";
    type[x] = aString;
    valLength[x] = 1;

    x = 43;
    descriptions[x] = "Uniformity correction";
    type[x] = aString;
    valLength[x] = 20;

    x = 44;
    descriptions[x] = "Zoom";
    type[x] = aFloat;
    valLength[x] = 4;

    x = 111;
    descriptions[x] = "VFR struct";
    type[x] = aString;
    valLength[x] = 64;

    ////////////////////////////////////////////////////////////
    // Acquisition information - multiframe specific
    ////////////////////////////////////////////////////////////

    x = 45;
    descriptions[x] = "Total counts in frame";
    type[x] = aFloat;
    valLength[x] = 4;

    x = 46;
    descriptions[x] = "Frame time";
    type[x] = anInt;  // in ms
    valLength[x] = 4;

    x = 47;
    descriptions[x] = "Acquisition time";
    type[x] = anInt;  // in ms
    valLength[x] = 4;

    x = 84;
    descriptions[x] = "Unused";
    type[x] = aString;
    valLength[x] = 8;

    x = 48;
    descriptions[x] = "Max value in frame/set";
    type[x] = aFloat;
    valLength[x] = 4;

    x = 49;
    descriptions[x] = "Min value in frame/set";
    type[x] = aFloat;
    valLength[x] = 4;

    x = 87;
    descriptions[x] = "Scale factor";
    type[x] = aFloat;
    valLength[x] = 4;

    ////////////////////////////////////////////////////////////
    // Acquisition information - gated specific
    ////////////////////////////////////////////////////////////

    x = 50;
    descriptions[x] = "R-R interval time";
    type[x] = aShort;
    valLength[x] = 2;

    x = 112;
    descriptions[x] = "R-R low tolerance time";
    type[x] = aShort;
    valLength[x] = 2;

    x = 113;
    descriptions[x] = "R-R high tolerance time";
    type[x] = aShort;
    valLength[x] = 2;

    x = 51;
    descriptions[x] = "Cycles imaged (%)";
    type[x] = aShort;
    valLength[x] = 2;

    x = 52;
    descriptions[x] = "Cycles accepted (%)";
    type[x] = aShort;
    valLength[x] = 2;

    x = 53;
    descriptions[x] = "Cycles rejected (%)";
    type[x] = aShort;
    valLength[x] = 2;

    x = 54;
    descriptions[x] = "End diastolic frame (approx)";
    type[x] = aShort;
    valLength[x] = 2;

    x = 55;
    descriptions[x] = "End systolic frame (approx)";
    type[x] = aShort;
    valLength[x] = 2;

    x = 56;
    descriptions[x] = "Ejection fraction (approx)";
    type[x] = aFloat;
    valLength[x] = 4;

    ////////////////////////////////////////////////////////////
    // Acquisition information - SPECT specific
    ////////////////////////////////////////////////////////////

    x = 57;
    descriptions[x] = "Starting angle";
    type[x] = aShort;
    valLength[x] = 2;

    x = 58;
    descriptions[x] = "Degrees of rotation";
    type[x] = aShort;
    valLength[x] = 2;

    x = 59;
    descriptions[x] = "Direction of rotation";
    type[x] = aString;  // "+" or "-"
    valLength[x] = 1;

    x = 60;
    descriptions[x] = "Reorientation type";
    type[x] = aString;  // C for cardiac, B for brain, N for normal
    valLength[x] = 1;

    x = 61;
    descriptions[x] = "Start frame, reconstruction limit";
    type[x] = aShort;
    valLength[x] = 2;

    ////////////////////////////////////////////////////////////
    // Image display information
    ////////////////////////////////////////////////////////////

    x = 62;
    descriptions[x] = "Upper window gray level";
    type[x] = aShort;
    valLength[x] = 2;

    x = 63;
    descriptions[x] = "Lower level gray level";
    type[x] = aShort;
    valLength[x] = 2;

    x = 64;
    descriptions[x] = "Associated colour map";
    type[x] = aShort;
    valLength[x] = 2;

    x = 65;
    descriptions[x] = "Customised colour map";
    type[x] = aString;
    valLength[x] = 20;  // Filename (very short, should be in same dir)


    ////////////////////////////////////////////////////////////
    // Image processing information
    ////////////////////////////////////////////////////////////

    x = 66;
    descriptions[x] = "Manipulated image";
    type[x] = aString;
    valLength[x] = 1;   // Y/N

    x = 67;
    descriptions[x] = "Axis of rotation corr.";
    type[x] = aShort;
    valLength[x] = 2;

    x = 68;
    descriptions[x] = "Reorientation azimuth";
    type[x] = aShort;
    valLength[x] = 2;

    x = 69;
    descriptions[x] = "Reorientation elevation";
    type[x] = aShort;
    valLength[x] = 2;

    x = 70;
    descriptions[x] = "Filter type";
    type[x] = aString;
    valLength[x] = 16;

    x = 71;
    descriptions[x] = "Filter order";
    type[x] = aShort;
    valLength[x] = 2;

    x = 72;
    descriptions[x] = "Cutoff frequency";
    type[x] = aFloat;
    valLength[x] = 4;

    x = 73;
    descriptions[x] = "Reconstruction type";
    type[x] = aString;
    valLength[x] = 4; // eg OSA = oblique short axis

    x = 74;
    descriptions[x] = "Attenuation coefficient";
    type[x] = aFloat;
    valLength[x] = 4;

    ////////////////////////////////////////////////////////////
    // Program specific and "extra" object information
    ////////////////////////////////////////////////////////////

    x = 114;
    descriptions[x] = "Program specific";
    type[x] = aString;
    valLength[x] = 800;


  }
}

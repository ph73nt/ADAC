package ADAC;

import ij.*;
import ij.io.*;
import ij.process.*;
import ij.gui.*;
import ij.plugin.filter.*;

import java.io.*;
import java.awt.*;
import java.util.*;
import java.net.URL;

public class Export_ADAC_image implements PlugInFilter {

  ImagePlus imp;
  private FileInfo fi;

  public int setup(String arg, ImagePlus imp) {
    this.imp = imp;
    fi = imp.getFileInfo();
    return DOES_8G + DOES_16 + DOES_32;
  }

  public void run(ImageProcessor ip) {
    String filepath = getPath("ADAC image", ".img");
    IJ.log(filepath);

    try {
      ADACEncoder file = new ADACEncoder(fi, imp);
      DataOutputStream out = new DataOutputStream(
              new BufferedOutputStream(
              new FileOutputStream(filepath)));
      file.write(out);
      out.close();
    } catch (IOException e) {
      showErrorMessage(e);
    }
  }

  String getPath(String type, String extension) {
    String name = imp.getTitle();
    SaveDialog sd = new SaveDialog("Save as " + type, name, extension);
    name = sd.getFileName();
    if (name == null) {
      return null;
    }
    String directory = sd.getDirectory();
    imp.startTiming();
    String path = directory + name;
    return path;
  }

  void showErrorMessage(IOException e) {
    String msg = e.getMessage();
    if (msg.length() > 100) {
      msg = msg.substring(0, 100);
    }
    IJ.error("FileSaver", "An error occured writing the file.\n \n" + msg);
  }
}

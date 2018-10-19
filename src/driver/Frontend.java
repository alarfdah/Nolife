package driver;


import java.io.FileNotFoundException;

/**
 * This class contains the main routine for the Nolife compiler. It
 * the scanner and parser and then processes the input file.
 */

public abstract class Frontend {

  public static String fileName = null; // the name of the input file

  public static boolean generateInterpreterCode = false;

  /**
   * @return the input file name
   */
  static String getFileName() {
    return fileName;
  }

  /**
   * The main routine for compiling and Nolife program
   *
   * @param args the command-line arguments
   */

  public static void main(String[] args) throws java.io.FileNotFoundException,
      java.io.IOException,
      java.lang.InterruptedException {

    Frontend frontend = new NolifeFrontend();
    
    fileName = new String(args[args.length - 1]);
    

    // set up the parser and scanner with the appropriate file
    // name
    //
    // parse the input file 
    //

    frontend.parseFile(fileName);


  }
  
  public abstract void parseFile(String fileName) throws FileNotFoundException;
}

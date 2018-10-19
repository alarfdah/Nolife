package driver;
/**
 * 
 */


import java.io.FileNotFoundException;
import java.io.FileReader;

import parser.NolifeParser;
import parser.ParseException;

/**
 * @author carr
 * 
 */
public class NolifeFrontend extends Frontend {

	@Override
	public void parseFile(String fileName) throws FileNotFoundException {
		
	    FileReader nolifeFile = new FileReader(fileName);
	    NolifeParser parser = new NolifeParser(nolifeFile);
		try {
			NolifeParser.program();
		} catch (ParseException e) {
			System.err.println("Syntax Error in " + fileName + ": " + e);
			System.exit(-1);
		}
		
		System.out.println(fileName + " parsed successfully!");
	}

}

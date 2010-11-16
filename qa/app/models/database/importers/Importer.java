package models.database.importers;

import java.io.CharArrayReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Static collection of methods to import data from external sources, the most
 * prominent being XML.
 * 
 * @author aaron
 * 
 */
public class Importer {

	/**
	 * Imports data from a XML string with the syntax specified by the
	 * {@link XMLParser}.
	 * 
	 * @param string
	 *            XML formated string
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	public static void importXML(String string) throws SAXException,
			IOException, ParserConfigurationException {
		importXML(new InputSource(new CharArrayReader(string.toCharArray())));
	}

	/**
	 * Imports data from a XML file with the syntax specified by the
	 * {@link XMLParser}.
	 * 
	 * @param file
	 *            File object to import
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	public static void importXML(File file) throws SAXException,
			IOException, ParserConfigurationException {
		importXML(new InputSource(new InputStreamReader(new FileInputStream(
				file), "utf-8")));
	}

	/**
	 * Imports data from a XML InputSource with the syntax specified by the
	 * {@link XMLParser}.
	 * 
	 * @param stream
	 *            arbitrary input source
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	public static void importXML(InputSource stream) throws SAXException,
			IOException,
			ParserConfigurationException {
		SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();

		// Now use the parser factory to create a SAXParser object
		SAXParser saxParser = saxParserFactory.newSAXParser();

		// Create an instance of this class; it defines all the handler methods
		DefaultHandler handler = new XMLParser();

		// Finally, tell the parser to parse the input and notify the handler
		saxParser.parse(stream, handler);
	}
}

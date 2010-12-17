package models.database.importers;

import java.io.CharArrayReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import models.database.IDatabase;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * A collection of methods to import data from external sources, the most
 * prominent being XML. The importer is stateless and can thus be reused for
 * efficiency reasons.
 * 
 * @author aaron
 */
public class Importer {

	private final IDatabase db;

	/**
	 * Instantiates a new importer for a given database.
	 * 
	 * @param db
	 *            the database into which to store all the data read
	 */
	public Importer(IDatabase db) {
		this.db = db;
	}

	/**
	 * Imports data from a XML string with the syntax specified by the
	 * {@link XMLParser}.
	 * 
	 * @param db
	 *            the database into which to import the XML data
	 * @param string
	 *            XML formated string
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	public void importXML(String string) throws SAXException, IOException,
			ParserConfigurationException {
		this.importXML(new InputSource(
				new CharArrayReader(string.toCharArray())));
	}

	/**
	 * Imports data from a XML file with the syntax specified by the
	 * {@link XMLParser}.
	 * 
	 * @param db
	 *            the database into which to import the XML data
	 * @param file
	 *            File object to import
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	public void importXML(File file) throws SAXException, IOException,
			ParserConfigurationException {
		this.importXML(new InputSource(new InputStreamReader(
				new FileInputStream(file), "utf-8")));
	}

	/**
	 * Imports data from a XML InputSource with the syntax specified by the.
	 * 
	 * @param db
	 *            the database into which to import the XML data
	 * @param stream
	 *            arbitrary input source
	 * @throws SAXException
	 *             the sAX exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ParserConfigurationException
	 *             the parser configuration exception {@link XMLParser}.
	 */
	public void importXML(InputSource stream) throws SAXException, IOException,
			ParserConfigurationException {
		SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();

		// Now use the parser factory to create a SAXParser object
		SAXParser saxParser = saxParserFactory.newSAXParser();

		// Create an instance of this class; it defines all the handler methods
		DefaultHandler handler = new XMLParser(this.db);

		// Finally, tell the parser to parse the input and notify the handler
		saxParser.parse(stream, handler);
	}
}

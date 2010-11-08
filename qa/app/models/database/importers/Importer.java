package models.database.importers;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class Importer {
	public static void importXML(String string) throws SAXException,
			IOException, ParserConfigurationException {
		ByteArrayInputStream stream = new ByteArrayInputStream(string
				.getBytes());
		importXML(new InputSource(stream));
	}

	public static void importXML(File file) throws SAXException,
			IOException, ParserConfigurationException {
		importXML(new InputSource(new FileReader(file)));
	}

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

package cz.edukomplex.kosilka.client.helper;

import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.XMLParser;

public class MyXMLParser {
	
	/**
	 * Parsuje text na XML
	 * 
	 * @param textToParse 
	 * @return Element root
	 */
	public static Element parseXML(String textToParse){	
		
		Document xmlDoc = XMLParser.parse(textToParse);
		Element root = xmlDoc.getDocumentElement();
		XMLParser.removeWhitespace(xmlDoc);	
		
		return root;
	}
	
	private MyXMLParser(){}
}

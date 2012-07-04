import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class Parser {

	
	public void connectPois(String fullPathGpsies, HashMap<String, ArrayList<Poi>> pois) 
			throws ParserConfigurationException, SAXException, IOException, XMLStreamException, FactoryConfigurationError{
		
		SAXParserFactory saxFac = SAXParserFactory.newInstance();
		SAXParser saxParser = saxFac.newSAXParser();
		
		//write xml document
		XMLStreamWriter writer;
		
		FileOutputStream fos = new FileOutputStream("gpsies_pois.xml");
		
		writer = XMLOutputFactory.newInstance().createXMLStreamWriter(fos, "UTF-8");
		
		writer.writeStartDocument("UTF-8", "1.0");
		writer.writeCharacters("\n");
	    writer.writeStartElement("feed");
	    writer.writeEndElement();
	    writer.writeEndDocument();
		
		writer.close();
		
		//parse via sax, see "PoiHandler"
		DefaultHandler handler = new PoiHandler(pois);
		
		saxParser.parse(fullPathGpsies, handler);
	}
	
	public static void main(String args[]) {
		String[] poiFiles = {"festival", "gebetsstaette", "monuments", "museum", "parks", 
				"ruinen", "see", "sehenswuerdig", "theatreOpera", "towers"};
		HashMap<String, ArrayList<Poi>> pois = null;
	
		Parser parser = new Parser();
		
		//parse poi files from xml into hashmaps --> easier, faster access
		System.out.println("Parsing POI files...");
		
		pois = PoiParser.parsePois(poiFiles);
		
//		//debug: iterate over pois 
//		for(Entry<String, ArrayList<Poi>> poiEntry: pois.entrySet()) {
//			
//			ArrayList<Poi> poiSet = poiEntry.getValue();
//			
//			for(Poi poi: poiSet) {
//				System.out.println("poi: " + poi.title + ", " + poi.lon + ", " + poi.lat);
//			}
//			
//		}

		System.out.println("Parsing POI files: Done"); 
		System.out.println();
		
		//parse file crawled from gpsies and insert pois
		System.out.println("Parsing GPSies file...");
		
		try {
			parser.connectPois("gpsies.xml", pois);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("Parsing GPSies file: Done");
	}
}

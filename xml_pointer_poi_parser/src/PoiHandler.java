import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

//this SAX handler is able to parse our db crawl of gpsies, connect the pois to it and write out a new document
//with help of StAX
public class PoiHandler extends DefaultHandler {
	private HashMap<String, ArrayList<Poi>> pois;
	private ArrayList<Point> trackPoints;
	private FileOutputStream fos;
	private XMLStreamWriter writer;
//	private final String[] mirrorElems = {"fileId", "kmlLink", "trackProperty", 
//			"description", "trackTypes", "trackAttributes", "trackCharacters", "trackRoadbeds",
//				"trackRoads"};
//	private ArrayList<String> mirrElems;
	
	PoiHandler(HashMap<String, ArrayList<Poi>> pois) {
		super();
		
		this.pois = pois;
		
//		mirrElems = (ArrayList<String>) Arrays.asList(mirrorElems);
		
		try {
			fos = new FileOutputStream("gpsies_pois.xml");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			writer = XMLOutputFactory.newInstance().createXMLStreamWriter(fos, "UTF-8");
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FactoryConfigurationError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//start writing the document, insert root <tracks> 
		try {
			writer.writeStartDocument("UTF-8", "1.0");
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void startElement(String uri, String localName,String qName, Attributes attribs) throws SAXException {
		if(qName.equalsIgnoreCase("track")) {
			try {
				writer.writeStartElement(qName);
			} catch (XMLStreamException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if(qName.equalsIgnoreCase("points")) {
			trackPoints = new ArrayList<Point>();
			
			try {
				writer.writeStartElement(qName);
			} catch (XMLStreamException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
//			System.out.println("Start reading points...");
		}
		else if(qName.equalsIgnoreCase("point")) {
			trackPoints.add(new Point(attribs.getValue("lon"), attribs.getValue("lat"), attribs.getValue("ele")));
			
			try {
				writer.writeStartElement(qName);
			} catch (XMLStreamException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else {
			//every other node - write it to target doc
			try {
				writer.writeStartElement(qName);
			} catch (XMLStreamException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		for(int i = 0; i < attribs.getLength(); i++) {
			try {
				writer.writeAttribute(attribs.getLocalName(i), attribs.getValue(i));
			} catch (XMLStreamException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if(qName.equalsIgnoreCase("points")) {		
			System.out.println("Reading points ...");
			
//			long startTime = System.currentTimeMillis();
			
			Rectangle area = new Rectangle();
			
			if(trackPoints.size() > 0) {
				//get some median point of track
				int m = trackPoints.size() / 2;
				
				float mlon = Float.parseFloat(trackPoints.get(m).lon.replace(">", "").replace("<", ""));
				float mlat = Float.parseFloat(trackPoints.get(m).lat.replace(">", "").replace("<", ""));
				
				area.left = mlon;
				area.right = mlon;
				area.top = mlat;
				area.bottom = mlat;
				
//				System.out.println("area: t " + area.top + ", r " + area.right + ", b " + area.bottom + ", l " + area.left);
				
				for(Point point : trackPoints) {
					float lon = Float.parseFloat(point.lon.replace(">", "").replace("<", ""));
					float lat = Float.parseFloat(point.lat.replace(">", "").replace("<", ""));
					
					if (lon < area.left) area.left = lon;
					if (lon > area.right) area.right = lon;
					if (lat < area.bottom) area.bottom = lat;
					if (lat > area.top) area.top = lat;
				}
				
//				System.out.println("area: t " + area.top + ", r " + area.right + ", b " + area.bottom + ", l " + area.left);
				
				//extend area 10% in each direction to get pois in close range to start/end points but outside of
				//inner track area -> will only work like this in certain regions of the world -> germany
				double h_incr = (area.right - area.left) * 0.1;
				double v_incr = (area.top - area.bottom) * 0.1;
				
				area.left -= h_incr;
				area.right += h_incr;
				area.top += v_incr;
				area.bottom -= v_incr;
				
//				System.out.println("area: t " + area.top + ", r " + area.right + ", b " + area.bottom + ", l " + area.left);

//				System.out.println("		Comparing pois ...");
				
				//iterate over pois and see if in track area
				for(Entry<String, ArrayList<Poi>> poiEntry: pois.entrySet()) {
					
					//maybe inefficient, just for comfortable programming and readable code
					ArrayList<Poi> poiSet = poiEntry.getValue();
					
					for(Poi poi: poiSet) {
						
//						System.out.println("poi: " + poi.title + ", " + poi.lon + ", " + poi.lat);
						
						if (poi.lon >= area.left && poi.lon <= area.right && 
								poi.lat >= area.bottom && poi.lat <= area.top) {
							
//							System.out.println("in area: " + poi.title);
							
							try {
								writer.writeStartElement("poi");
								
								if (poi.title != null) {
									writer.writeAttribute("title", poi.title);
								}
								else {
									continue;
								}
								
//								if(poi.wikiLink != null) {
//									
//								}
								writer.writeAttribute("category", poiEntry.getKey());
								writer.writeAttribute("wikiLink", poi.wikiLink);
								writer.writeAttribute("lon", Float.toString(poi.lon));
								writer.writeAttribute("lat", Float.toString(poi.lat));
								
								writer.writeEndElement();
							} catch (XMLStreamException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}
			}
			
//			System.out.println("	Looping through points: " + (System.currentTimeMillis() - startTime) + " ms");
		}

		//no matter which node - write it to target doc
		try {
			writer.writeEndElement();
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void characters(char ch[], int start, int length)
    throws SAXException {
        try {
			writer.writeCharacters(new String(ch, start, length));
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	
}

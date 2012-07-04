import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class PoiParser {
	public static Document getDocument(String fullPath) {
		try {
			//open file and read into dom object ("document")
			File file = new File(fullPath);
			DocumentBuilderFactory dbFac = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFac.newDocumentBuilder();
			Document doc = dBuilder.parse(file);
			
			doc.getDocumentElement().normalize();
			
			return doc;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static HashMap<String, ArrayList<Poi>> parsePois(String files[]) {
		HashMap<String, Document> poiDocs = new HashMap<String, Document>();
		HashMap<String, ArrayList<Poi>> pois = new HashMap<String, ArrayList<Poi>>();
		
		for(String file: files) {
			Document doc = null;
			
			if ((doc = getDocument(file + ".xml")) != null) {
				poiDocs.put(file, doc);
			}
			else {
				System.out.println("Unable to read poi file \"" + file + ".xml\" .");
			}
		}
		
		XPath xpath = XPathFactory.newInstance().newXPath();
		
		ArrayList<Poi> arrPoi = null;
		
		for(Entry<String, Document> poiDoc: poiDocs.entrySet()) {
			try {
				XPathExpression expr = xpath.compile("//result");
				
				NodeList poiElems = (NodeList) expr.evaluate(poiDoc.getValue(), XPathConstants.NODESET);
				
				arrPoi = new ArrayList<Poi>();
				
				for(int i = 0; i < poiElems.getLength(); i++) {
					Node poiElem = poiElems.item(i);
					int lenChilds = poiElem.getChildNodes().getLength();
					
					Poi poi = new Poi();
					
					for(int l = 0; l < lenChilds; l++) {
						Node pChild = poiElem.getChildNodes().item(l);
						
						if (pChild.getNodeType() !=  Node.ELEMENT_NODE) {
							continue;
						}
						
						String attrib = pChild.getAttributes().getNamedItem("name").getNodeValue();
						
//						System.out.println("	attrib: " + attrib);
						
						if (attrib.equals("bezeichnung")) {
							poi.title = pChild.getChildNodes().item(1).getFirstChild().getNodeValue();
						}
						else if (attrib.equals("wikilink")) {
							poi.wikiLink = pChild.getChildNodes().item(1).getFirstChild().getNodeValue();
						}
						else if (attrib.equals("lat")) {
							poi.lat = Float.parseFloat(pChild.getChildNodes().item(1).getFirstChild().getNodeValue());
						}
						else if (attrib.equals("long")) {
							poi.lon = Float.parseFloat(pChild.getChildNodes().item(1).getFirstChild().getNodeValue());
						}
						
//						System.out.println("	poiElem child: " + pChild.getChildNodes().item(1).getNodeName() + 
//								", " + pChild.getChildNodes().item(1).getFirstChild().getNodeValue());
					}
					
					arrPoi.add(poi);
					
					System.out.println("poi: " + poi.title + ", " + poi.lon + ", " + poi.lat);
				}
			} catch (XPathExpressionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (arrPoi != null) {
				pois.put(poiDoc.getKey(), arrPoi);
			}
			else {
				System.out.println("Error: Unable to parse document for \"" + poiDoc.getKey() + "\"");
			}
		}
		
		return pois;
	}
}

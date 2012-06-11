package xmlproject.crawler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.sun.xml.internal.txw2.output.IndentingXMLStreamWriter;

public class Crawler {

	String key = "";
	String path = "";
	
	public Crawler(String key, String path) {
		this.key = key;
		this.path = path;
	}
	
	public void crawl() {

		try {
			File file = new File(this.path+".tmp");
			XMLOutputFactory xmlof = XMLOutputFactory.newInstance();
			
			XMLStreamWriter xmlw = xmlof.createXMLStreamWriter( new FileOutputStream(file), "UTF-8" );
			
			IndentingXMLStreamWriter ixmlw = new IndentingXMLStreamWriter(xmlw);
			
			ixmlw.writeStartDocument();
			
			ixmlw.writeStartElement( "crawl" );
			
				int j = 1;
				for (int i = 1; i <= 100; ++i) {
					long start = System.currentTimeMillis();
					System.out.println("ResultPage: " + i);
					List<String> fileIds = getFileIds(i);
						ixmlw.writeStartElement("query");
							ixmlw.writeAttribute("id", String.valueOf(j++));
							String track = getTracks(fileIds);
							ixmlw.writeCData(track);
						ixmlw.writeEndElement();
					System.out.println("Duration in ms: " + (System.currentTimeMillis() - start));
				}
				
			ixmlw.writeEndDocument();
		
			ixmlw.flush();
			ixmlw.close();
			
			System.out.println("finishing..");
			long start = System.currentTimeMillis();
			File inputFile = new File(this.path+".tmp");
			File outputFile = new File(this.path);
			FileInputStream fis = new FileInputStream(inputFile);
			FileOutputStream fos = new FileOutputStream(outputFile);
			BufferedReader buffi = new BufferedReader(new InputStreamReader(fis));
			BufferedWriter buffo = new  BufferedWriter(new OutputStreamWriter(fos));
			
			String line;
			
			while ((line = buffi.readLine()) != null) { 
				buffo.write(line.replace("<![CDATA[<gpsies>", "<gpsies>")
						.replace("</gpsies>]]>", "</gpsies>"));
			}
			
			buffo.flush();
			buffi.close();
			buffo.close();
			System.out.println("Duration in ms: " + (System.currentTimeMillis() - start));
			System.out.println("finished");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private String getTracks(List<String> fileIds) throws ClientProtocolException, IOException {
		HttpClient httpclient = new DefaultHttpClient();

		HttpPost post = new HttpPost("http://www.gpsies.org/api.do");

		List <NameValuePair> nvps = new ArrayList <NameValuePair>();
		nvps.add(new BasicNameValuePair("key", key));
		nvps.add(new BasicNameValuePair("limit", "100"));
		for (String fileId : fileIds)
			nvps.add(new BasicNameValuePair("fileId", fileId));

		post.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));

		HttpResponse response = httpclient.execute(post);
		HttpEntity entity = response.getEntity();

		BufferedReader rd = new BufferedReader(new InputStreamReader(entity.getContent()));
		StringBuilder sb = new StringBuilder();
		String line = "";

		while ((line = rd.readLine()) != null) { 
			sb.append(line);
		}
		
		return sb.toString().replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "");
	}

	private List<String> getFileIds(int resultPage) throws ClientProtocolException, IOException, ParserConfigurationException, SAXException, XPathExpressionException {
		List<String> fileIds = new ArrayList<String>();
		
		HttpClient httpclient = new DefaultHttpClient();

		HttpPost post = new HttpPost("http://www.gpsies.org/api.do");

		List <NameValuePair> nvps = new ArrayList <NameValuePair>();
		nvps.add(new BasicNameValuePair("key", this.key));
		nvps.add(new BasicNameValuePair("country", "DE"));
		nvps.add(new BasicNameValuePair("limit", "100"));
		nvps.add(new BasicNameValuePair("resultPage", String.valueOf(resultPage)));

		post.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));

		HttpResponse response = httpclient.execute(post);
		HttpEntity entity = response.getEntity();

		BufferedReader rd = new BufferedReader(new InputStreamReader(entity.getContent()));
		StringBuilder sb = new StringBuilder();
		String line = "";

		while ((line = rd.readLine()) != null) { 
			sb.append(line);
		}

		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		domFactory.setNamespaceAware(true); 
		DocumentBuilder builder = domFactory.newDocumentBuilder();
		Document doc = builder.parse(new ByteArrayInputStream(sb.toString().getBytes()));

		XPath xpath = XPathFactory.newInstance().newXPath();
		XPathExpression expr = xpath.compile("//tracks/track/fileId/text()");
		
		Object result = expr.evaluate(doc, XPathConstants.NODESET);
		NodeList nodes = (NodeList) result;
		for (int i = 0; i < nodes.getLength(); i++) {
			fileIds.add(nodes.item(i).getNodeValue());
//			System.out.println(nodes.item(i).getNodeValue()); 
		}
		
		return fileIds;
	}
	
	public static void main(String[] args) {
		Crawler crawler = new Crawler(args[0], args[1]);
		crawler.crawl();
	}
}

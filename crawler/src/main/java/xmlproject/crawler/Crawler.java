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
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPOutputStream;

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

	private String key = "";
	private String path = "";

	private StringBuffer result = new StringBuffer();

	public Crawler(String key, String path) {
		this.key = key;
		this.path = path;
	}

	public void crawl() {

		try {
			long start = System.currentTimeMillis();
			File file = new File(this.path);
			FileOutputStream fos = new FileOutputStream(file);
			// BufferedWriter buffo = new BufferedWriter(new
			// OutputStreamWriter(fos));
			GZIPOutputStream gzipOut = new GZIPOutputStream(fos);
			gzipOut.write("<crawl>".getBytes());
			int j = 1;
			for (int i = 1; i <= 1000; ++i) {
				long start2 = System.currentTimeMillis();
				System.out.println("ResultPage: " + i);
				List<String> fileIds = getFileIds(i);
				String query = "<query id = \"" + (j++) + "\">";
				gzipOut.write(query.getBytes());
				String track = getTracks(fileIds);
				long bla = System.currentTimeMillis();
				gzipOut.write(track.getBytes());
				System.out.println("write time: "
						+ (System.currentTimeMillis() - bla));
				gzipOut.write("</query>".getBytes());
				System.out.println("Duration in ms: "
						+ (System.currentTimeMillis() - start2));
			}

			gzipOut.write("</crawl>".getBytes());
			gzipOut.flush();
			gzipOut.close();

			System.out.println("Duration in ms: "
					+ (System.currentTimeMillis() - start));
			System.out.println("finished");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String getTracks(List<String> fileIds)
			throws ClientProtocolException, IOException, InterruptedException {
		HttpClient httpclient = new DefaultHttpClient();

		HttpPost post = new HttpPost("http://www.gpsies.org/api.do");

		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("key", key));
		nvps.add(new BasicNameValuePair("limit", "100"));
		nvps.add(new BasicNameValuePair("filetype", "kml"));
		for (String fileId : fileIds)
			nvps.add(new BasicNameValuePair("fileId", fileId));

		post.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));

		HttpResponse response = httpclient.execute(post);
		HttpEntity entity = response.getEntity();

		BufferedReader rd = new BufferedReader(new InputStreamReader(
				entity.getContent()));
		StringBuilder sb = new StringBuilder();
		String line = "";

		while ((line = rd.readLine()) != null) {
			sb.append(line);
		}

		String input = sb.toString();
		input = input.replaceAll("<meta>.*</meta>", "");
		input = input.replaceAll("<tracks>|</tracks>", "");
		input = input.replaceAll("<gpsies>|</gpsies>", "");

		List<Thread> tList = new ArrayList<Thread>();
		int counter = 0;
		String pattern = "(<track>.*?</track>)";
		Pattern track = Pattern.compile(pattern);
		Matcher trackMatcher = track.matcher(input);
		while (trackMatcher.find()) {
			Thread t = new Thread(new Worker(++counter, trackMatcher.group(1),
					this));
			tList.add(t);
			t.start();
			if (counter % 10 == 0)
				Thread.sleep(1000);
		}

		for (Thread t : tList) {
			try {
				if (t.isAlive())
					t.join(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		return result.toString().replace(
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "");
	}

	private List<String> getFileIds(int resultPage)
			throws ClientProtocolException, IOException,
			ParserConfigurationException, SAXException,
			XPathExpressionException {
		List<String> fileIds = new ArrayList<String>();

		HttpClient httpclient = new DefaultHttpClient();

		HttpPost post = new HttpPost("http://www.gpsies.org/api.do");

		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("key", this.key));
		nvps.add(new BasicNameValuePair("country", "DE"));
		nvps.add(new BasicNameValuePair("limit", "100"));
		nvps.add(new BasicNameValuePair("resultPage", String
				.valueOf(resultPage)));

		post.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));

		HttpResponse response = httpclient.execute(post);
		HttpEntity entity = response.getEntity();

		BufferedReader rd = new BufferedReader(new InputStreamReader(
				entity.getContent()));
		StringBuilder sb = new StringBuilder();
		String line = "";

		while ((line = rd.readLine()) != null) {
			sb.append(line);
		}

		String pattern = "<fileId>(.*?)</fileId>";
		Pattern fileId = Pattern.compile(pattern);
		Matcher fileIdMatcher = fileId.matcher(sb);

		while (fileIdMatcher.find()) {
			fileIds.add(fileIdMatcher.group(1));
		}

		return fileIds;
	}

	public static void main(String[] args) {
		Crawler crawler = new Crawler(args[0], args[1]);
		crawler.crawl();
	}

	public synchronized void addResult(String res, int id) {
		result.append(res);
	}
}

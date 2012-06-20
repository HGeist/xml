package xmlproject.crawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Worker implements Runnable {

	private final int id;
	private String data;
	private final Crawler crawler;

	Worker(int id, String data, Crawler crawler) {
		this.id = id;
		this.data = data;
		this.crawler = crawler;
	}

	public void run() {
		try {
			long start = System.currentTimeMillis();
			String patternDownload = "<downloadLink>(.*?)</downloadLink>";
			Pattern patDownload = Pattern.compile(patternDownload);
			Matcher matchDownload = patDownload.matcher(data);

			if (matchDownload.find()) {
				String urlStr = matchDownload.group(1);
				URL url = new URL(urlStr);
				BufferedReader in = new BufferedReader(new InputStreamReader(
						url.openStream()));

				StringBuilder kml = new StringBuilder();
				String inputLine;
				boolean heuristic = false, isCoordinateLine = false;
				int heuristicCounter = 0;
				while ((inputLine = in.readLine()) != null) {
					if (inputLine.contains("<LineString>")) {
						heuristic = true;
					}
					if (heuristic) {
						if (inputLine.contains("<coordinates>")) {
							isCoordinateLine = true;
							kml.append(inputLine).append(("|"));
							continue;
						}
					}
					if(inputLine.contains("</coordinates>")) {
						heuristic = false;
						isCoordinateLine = false;
						heuristicCounter = 0;
					}
					if(isCoordinateLine && ((++heuristicCounter)%10) != 0 ) {
						continue;
					}
					
					kml.append(inputLine).append((" "));
				}
				in.close();
				String pattern = "(<Placemark>.*?</Placemark>)";
				Pattern r = Pattern.compile(pattern);
				Matcher m = r.matcher(kml);
				StringBuilder allCoordinates = new StringBuilder();
				while (m.find()) {
					allCoordinates.append(m.group(1));
				}

				StringBuilder oldText = new StringBuilder();
				StringBuilder newText = new StringBuilder();
				
				oldText.append("<downloadLink>").append(urlStr).append("</downloadLink>");
				newText.append("<downloadLink>").append(urlStr).append("</downloadLink>").append(allCoordinates);
				data = data.replace(oldText, newText);
				
				crawler.addResult(data, id);

				System.out.println(id + " Link: " + urlStr + " time: " + (System.currentTimeMillis() - start));
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

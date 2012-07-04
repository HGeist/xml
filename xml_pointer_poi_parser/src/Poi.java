
public class Poi {
	public String title;
	public String wikiLink;
	public float lat;
	public float lon;
	
	Poi(String title, String wikiLink, float lat, float lon) {
		this.title = title;
		this.wikiLink = wikiLink;
		this.lat = lat;
		this.lon = lon;
	}

	public Poi() {
		title = "";
		wikiLink = "";
		lat = 0;
		lon = 0;
	}
}

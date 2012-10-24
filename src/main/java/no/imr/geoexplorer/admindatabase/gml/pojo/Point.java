package no.imr.geoexplorer.admindatabase.gml.pojo;

public class Point {
	private String coordinates;
	private String srsName;

	public Point(String coordinates, String srsName) {
		this.coordinates = coordinates;
		this.srsName = srsName;
	}

	public String getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(String coordinates) {
		this.coordinates = coordinates;
	}

	public String getSrsName() {
		return srsName;
	}

	public void setSrsName(String srs) {
		this.srsName = srs;
	}

}

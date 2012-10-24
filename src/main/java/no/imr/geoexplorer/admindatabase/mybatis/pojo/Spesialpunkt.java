package no.imr.geoexplorer.admindatabase.mybatis.pojo;

import java.sql.Timestamp;

public class Spesialpunkt {
	private long spesialpunktId;
	private int kartlagId;
	private int spesialpunkttypeId;
	private double xUtm33;
	private double yUtm33;
	private int radiusPixel;
	private String genericTitle;
	private String url;
	private Timestamp modified;
	private String urlThumbnail;
	private int mediaHeight;
	private int mediaWidth;
	private Kartlag kartlag;
	private Spesialpunkttype spesialpunkttype;
	
	public long getSpesialpunktId() {
		return spesialpunktId;
	}
	public void setSpesialpunktId(long spesialpunktId) {
		this.spesialpunktId = spesialpunktId;
	}
	public int getKartlagId() {
		return kartlagId;
	}
	public void setKartlagId(int kartlagId) {
		this.kartlagId = kartlagId;
	}
	public int getSpesialpunkttypeId() {
		return spesialpunkttypeId;
	}
	public void setSpesialpunkttypeId(int spesialpunkttypeId) {
		this.spesialpunkttypeId = spesialpunkttypeId;
	}
	public double getxUtm33() {
		return xUtm33;
	}
	public void setxUtm33(double xUtm33) {
		this.xUtm33 = xUtm33;
	}
	public double getyUtm33() {
		return yUtm33;
	}
	public void setyUtm33(double yUtm33) {
		this.yUtm33 = yUtm33;
	}
	public int getRadiusPixel() {
		return radiusPixel;
	}
	public void setRadiusPixel(int radiusPixel) {
		this.radiusPixel = radiusPixel;
	}
	public String getGenericTitle() {
		return genericTitle;
	}
	public void setGenericTitle(String genericTitle) {
		this.genericTitle = genericTitle;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public Timestamp getModified() {
		return modified;
	}
	public void setModified(Timestamp modified) {
		this.modified = modified;
	}
	public String getUrlThumbnail() {
		return urlThumbnail;
	}
	public void setUrlThumbnail(String urlThumbnail) {
		this.urlThumbnail = urlThumbnail;
	}
	public int getMediaHeight() {
		return mediaHeight;
	}
	public void setMediaHeight(int mediaHeight) {
		this.mediaHeight = mediaHeight;
	}
	public int getMediaWidth() {
		return mediaWidth;
	}
	public void setMediaWidth(int mediaWidth) {
		this.mediaWidth = mediaWidth;
	}

	public Kartlag getKartlag() {
		return kartlag;
	}
	public void setKartlag(Kartlag kartlag) {
		this.kartlag = kartlag;
	}

	public Spesialpunkttype getSpesialpunkttype() {
		return spesialpunkttype;
	}
	public void setSpesialpunkttype(Spesialpunkttype spesialpunkttype) {
		this.spesialpunkttype = spesialpunkttype;
	}
}

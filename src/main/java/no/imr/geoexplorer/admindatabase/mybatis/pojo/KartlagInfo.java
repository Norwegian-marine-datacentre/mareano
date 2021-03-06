package no.imr.geoexplorer.admindatabase.mybatis.pojo;

public class KartlagInfo {
	private long kartlagId;
	private String title;
	private String alternateTitle;
	private String abstracts;
	
	public long getKartlagId() {
		return kartlagId;
	}
	public void setKartlagId(long kartlagId) {
		this.kartlagId = kartlagId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getAlternateTitle() {
		return alternateTitle;
	}
	public void setAlternateTitle(String alternateTitle) {
		this.alternateTitle = alternateTitle;
	}
	public String getAbstracts() {
		return abstracts;
	}
	public void setAbstracts(String abstracts) {
		this.abstracts = abstracts;
	}
}

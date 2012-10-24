package no.imr.geoexplorer.admindatabase.mybatis.pojo;

public class KartBilderEnNo {
	private long kartbilderId;
	private String title;
	private String alternateTitle; 
	private String abstracts;
	
	public long getKartbilderId() {
		return kartbilderId;
	}
	public void setKartbilderId(long kartbilderId) {
		this.kartbilderId = kartbilderId;
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

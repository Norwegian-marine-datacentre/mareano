package no.imr.geoexplorer.admindatabase.mybatis.pojo;

public class Legend {
	private long legendsId;
	private long kartlagId;
	private String url;
	private String sort;
	private String genericTitle;
	
	public long getLegendsId() {
		return legendsId;
	}
	public void setLegendsId(long legendsId) {
		this.legendsId = legendsId;
	}
	public long getKartlagId() {
		return kartlagId;
	}
	public void setKartlagId(long kartlagId) {
		this.kartlagId = kartlagId;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getSort() {
		return sort;
	}
	public void setSort(String sort) {
		this.sort = sort;
	}
	public String getGenericTitle() {
		return genericTitle;
	}
	public void setGenericTitle(String genericTitle) {
		this.genericTitle = genericTitle;
	}
}

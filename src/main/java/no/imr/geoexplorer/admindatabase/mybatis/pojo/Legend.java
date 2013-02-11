package no.imr.geoexplorer.admindatabase.mybatis.pojo;

import java.util.Comparator;

public class Legend implements Comparable<Legend> {
	private long legendsId;
	private long kartlagId;
	private String url;
	private Integer sort;
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
	public Integer getSort() {
		return sort;
	}
	public void setSort(Integer sort) {
		this.sort = sort;
	}
	public String getGenericTitle() {
		return genericTitle;
	}
	public void setGenericTitle(String genericTitle) {
		this.genericTitle = genericTitle;
	}
	public int compareTo(Legend compareLegend) {
		Integer compareSort = ((Legend) compareLegend).getSort(); 
		//ascending order
		return this.sort - compareSort;
	}	

	public static Comparator<Legend> LegendComparator = new Comparator<Legend>() {
		public int compare(Legend legend1, Legend legend2) {
		return legend1.getSort().compareTo(legend2.getSort());
		}
	};	
}

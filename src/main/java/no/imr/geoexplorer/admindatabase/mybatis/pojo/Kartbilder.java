package no.imr.geoexplorer.admindatabase.mybatis.pojo;

import java.sql.Timestamp;
import java.util.List;

public class Kartbilder {

	private long kartbilderId;
	private long hovedtemaerId;
	private String type;
	private double startextentMinx;
	private double startextentMaxx;
	private double startextentMiny; 
	private double startextentMaxy;
	private int sort;
	private String genericTitle;
	private Timestamp modified;
	private Hovedtema hovedtema;
	private List<Kartlag> kartlag;
	
	public long getKartbilderId() {
		return kartbilderId;
	}
	public void setKartbilderId(long kartbilderId) {
		this.kartbilderId = kartbilderId;
	}
	public long getHovedtemaerId() {
		return hovedtemaerId;
	}
	public void setHovedtemaerId(long hovedtemaerId) {
		this.hovedtemaerId = hovedtemaerId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public double getStartextentMinx() {
		return startextentMinx;
	}
	public void setStartextentMinx(double startextentMinx) {
		this.startextentMinx = startextentMinx;
	}
	public double getStartextentMaxx() {
		return startextentMaxx;
	}
	public void setStartextentMaxx(double startextentMaxx) {
		this.startextentMaxx = startextentMaxx;
	}
	public double getStartextentMiny() {
		return startextentMiny;
	}
	public void setStartextentMiny(double startextentMiny) {
		this.startextentMiny = startextentMiny;
	}
	public double getStartextentMaxy() {
		return startextentMaxy;
	}
	public void setStartextentMaxy(double startextentMaxy) {
		this.startextentMaxy = startextentMaxy;
	}
	public int getSort() {
		return sort;
	}
	public void setSort(int sort) {
		this.sort = sort;
	}
	public String getGenericTitle() {
		return genericTitle;
	}
	public void setGenericTitle(String genericTitle) {
		this.genericTitle = genericTitle;
	}
	public Timestamp getModified() {
		return modified;
	}
	public void setModified(Timestamp modified) {
		this.modified = modified;
	}
	public Hovedtema getHovedtema() {
		return hovedtema;
	}
	public void setHovedtema(Hovedtema hovedtema) {
		this.hovedtema = hovedtema;
	}
	public List<Kartlag> getKartlag() {
		return kartlag;
	}
	public void setKartlag(List<Kartlag> kartlag) {
		this.kartlag = kartlag;
	}
}

package no.imr.geoexplorer.admindatabase.mybatis.pojo;

import java.util.Date;
import java.util.List;

public class Hovedtema {
	private long hovedtemaerId;
	private boolean simpleshow;
	private String type;
	private int sort;
	private String genericTitle;
	private Date modified;
	private List<Kartbilder> kartbilder;

	public long getHovedtemaerId() {
		return hovedtemaerId;
	}
	public void setHovedtemaerId(long hovedtemaerId) {
		this.hovedtemaerId = hovedtemaerId;
	}
	public boolean isSimpleshow() {
		return simpleshow;
	}
	public void setSimpleshow(boolean simpleshow) {
		this.simpleshow = simpleshow;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
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
	public Date getModified() {
		return modified;
	}
	public void setModified(Date modified) {
		this.modified = modified;
	}
	public List<Kartbilder> getKartbilder() {
		return kartbilder;
	}
	public void setKartbilder(List<Kartbilder> kartbilder) {
		this.kartbilder = kartbilder;
	}
}

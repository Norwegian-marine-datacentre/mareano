package no.imr.geoexplorer.admindatabase.jsp.pojo;

import java.util.ArrayList;
import java.util.List;

public class KartbilderVisning {
	private String gruppe;
	private String abstracts;
	private List<KartlagVisning> kart = new ArrayList<KartlagVisning>();
	private boolean visible = false;
	private double startextentMinx;
	private double startextentMaxx;
	private double startextentMiny;
	private double startextentMaxy;

	public String getAbstracts() {
		return abstracts;
	}
	public void setAbstracts(String abstracts) {
		this.abstracts = abstracts;
	}
	public boolean isVisible() {
		return visible;
	}
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	public String getGruppe() {
		return gruppe;
	}
	public void setGruppe(String gruppe) {
		this.gruppe = gruppe;
	}
	public List<KartlagVisning> getKart() {
		return kart;
	}
	public void setKart(List<KartlagVisning> kart) {
		this.kart = kart;
	}
	public void addKart(KartlagVisning kart) {
		this.kart.add(kart);
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
}

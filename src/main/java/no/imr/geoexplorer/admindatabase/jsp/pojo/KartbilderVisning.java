package no.imr.geoexplorer.admindatabase.jsp.pojo;

import java.util.ArrayList;
import java.util.List;

public class KartbilderVisning {
	private String gruppe;
	private List<KartlagVisning> kart = new ArrayList<KartlagVisning>();
	private boolean visible = false;

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
}

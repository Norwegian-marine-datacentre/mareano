package no.imr.geoexplorer.admindatabase.jsp.pojo;

import java.util.ArrayList;
import java.util.List;

public class HovedtemaVisning {
	private String hovedtema;
	private List<KartbilderVisning> bilder = new ArrayList<KartbilderVisning>();

	public String getHovedtema() {
		return hovedtema;
	}
	public void setHovedtema(String hovedtema) {
		this.hovedtema = hovedtema;
	}
	public List<KartbilderVisning> getBilder() {
		return bilder;
	}
	public void setBilder(List<KartbilderVisning> bilder) {
		this.bilder = bilder;
	}
	public void addBilder(KartbilderVisning bilde) {
		this.bilder.add(bilde);
	}
}

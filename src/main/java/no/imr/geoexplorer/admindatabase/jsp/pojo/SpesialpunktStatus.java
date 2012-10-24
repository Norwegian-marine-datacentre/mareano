package no.imr.geoexplorer.admindatabase.jsp.pojo;

import java.util.ArrayList;
import java.util.List;

/**
 * A too be convertet to JSON object with @ResponseBody
 * @author endrem
 *
 */
public class SpesialpunktStatus {
	
	private boolean noSpesialpunkt;
	private List<LegendsInfo> legends = new ArrayList<LegendsInfo>();
	private KartlagInfos kartlagInfo = new KartlagInfos();

	public boolean isNoSpesialpunkt() {
		return noSpesialpunkt;
	}
	public void setNoSpesialpunkt(boolean noSpesialpunkt) {
		this.noSpesialpunkt = noSpesialpunkt;
	}
	public List<LegendsInfo> getLegends() {
		return legends;
	}
	public void setLegends(List<LegendsInfo> legends) {
		this.legends = legends;
	}
	public void addLegendsInfo(LegendsInfo legendsInfo) {
		legends.add(legendsInfo);
	}
	public KartlagInfos getKartlagInfo() {
		return kartlagInfo;
	}
	public void setKartlagInfo(KartlagInfos kartlagInfo) {
		this.kartlagInfo = kartlagInfo;
	}	
}

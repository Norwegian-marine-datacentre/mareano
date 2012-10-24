package no.imr.geoexplorer.admindatabase.jsp.pojo;

public class KartlagInfos {
	private String text;
	private String kartlagInfoTitel;
	
	public KartlagInfos(){};
	public KartlagInfos( String kartlagInfoTitle, String kartlagInfo ) {
		this.kartlagInfoTitel = kartlagInfoTitle;
		this.text = kartlagInfo;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getKartlagInfoTitel() {
		return kartlagInfoTitel;
	}
	public void setKartlagInfoTitel(String kartlagInfoTitel) {
		this.kartlagInfoTitel = kartlagInfoTitel;
	}
}

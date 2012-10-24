package no.imr.geoexplorer.admindatabase.mybatis.pojo;

import java.util.Date;
import java.util.List;

public class Karttjenester {
	private long karttjenesterId;
	private String url;
	private String link2geonorge;
	private String urlLogo;
	private String urlOrganisation;
	private String genericTitle;
	private Date modified;
	private String format;
	private boolean available;
	private String skTjenesteid;
	private List<Kartlag> kartlag;
	public long getKarttjenesterId() {
		return karttjenesterId;
	}
	public void setKarttjenesterId(long karttjenesterId) {
		this.karttjenesterId = karttjenesterId;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getLink2geonorge() {
		return link2geonorge;
	}
	public void setLink2geonorge(String link2geonorge) {
		this.link2geonorge = link2geonorge;
	}
	public String getUrlLogo() {
		return urlLogo;
	}
	public void setUrlLogo(String urlLogo) {
		this.urlLogo = urlLogo;
	}
	public String getUrlOrganisation() {
		return urlOrganisation;
	}
	public void setUrlOrganisation(String urlOrganisation) {
		this.urlOrganisation = urlOrganisation;
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
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}
	public boolean isAvailable() {
		return available;
	}
	public void setAvailable(boolean available) {
		this.available = available;
	}
	public String getSkTjenesteid() {
		return skTjenesteid;
	}
	public void setSkTjenesteid(String skTjenesteid) {
		this.skTjenesteid = skTjenesteid;
	}
	public List<Kartlag> getKartlag() {
		return kartlag;
	}
	public void setKartlag(List<Kartlag> kartlag) {
		this.kartlag = kartlag;
	}
}

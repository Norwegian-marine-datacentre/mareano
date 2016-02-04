package no.imr.geoexplorer.admindatabase.mybatis.pojo;

import java.sql.Timestamp;
import java.util.List;

public class Kartlag {
	private long kartlagId;
	private long karttjenesterId;
	private String layers;
	private double scalemin;
	private double scalemax;
	
	private double scaleminPolar;
	private double scalemaxPolar;
	    
	private String downloadurl;
	private double exGeographicBoundingBoxWestBoundLongitude;
	private double exGeographicBoundingBoxEastBoundLongitude;
	private double exGeographicBoundingBoxSouthBoundLatitude;
	private double exGeographicBoundingBoxNorthBoundLatitude;

	private double westPolar;
	private double eastPolar;
	private double southPolar;
	private double northPolar;
	
	private String keyword;
	private boolean queryable;
	private int sort;
	private boolean available;
	private String genericTitle;
	private Timestamp modified;
	private String infoFormat;
	private List<Kartbilder> kartbilder;
	private Karttjenester karttjeneste;
	
	public long getKartlagId() {
		return kartlagId;
	}
	public void setKartlagId(long kartlagId) {
		this.kartlagId = kartlagId;
	}
	public long getKarttjenesterId() {
		return karttjenesterId;
	}
	public void setKarttjenesterId(long karttjenesterId) {
		this.karttjenesterId = karttjenesterId;
	}
	public String getLayers() {
		return layers;
	}
	public void setLayers(String layers) {
		this.layers = layers;
	}
	public double getScalemin() {
		return scalemin;
	}
	public void setScalemin(double scalemin) {
		this.scalemin = scalemin;
	}
	public double getScalemax() {
		return scalemax;
	}
	public void setScalemax(double scalemax) {
		this.scalemax = scalemax;
	}
	public String getDownloadurl() {
		return downloadurl;
	}
	public void setDownloadurl(String downloadurl) {
		this.downloadurl = downloadurl;
	}
	public double getExGeographicBoundingBoxWestBoundLongitude() {
		return exGeographicBoundingBoxWestBoundLongitude;
	}
	public void setExGeographicBoundingBoxWestBoundLongitude(double exGeographicBoundingBoxWestBoundLongitude) {
		this.exGeographicBoundingBoxWestBoundLongitude = exGeographicBoundingBoxWestBoundLongitude;
	}
	public double getExGeographicBoundingBoxEastBoundLongitude() {
		return exGeographicBoundingBoxEastBoundLongitude;
	}
	public void setExGeographicBoundingBoxEastBoundLongitude(double exGeographicBoundingBoxEastBoundLongitude) {
		this.exGeographicBoundingBoxEastBoundLongitude = exGeographicBoundingBoxEastBoundLongitude;
	}
	public double getExGeographicBoundingBoxSouthBoundLatitude() {
		return exGeographicBoundingBoxSouthBoundLatitude;
	}
	public void setExGeographicBoundingBoxSouthBoundLatitude(double exGeographicBoundingBoxSouthBoundLatitude) {
		this.exGeographicBoundingBoxSouthBoundLatitude = exGeographicBoundingBoxSouthBoundLatitude;
	}
	public double getExGeographicBoundingBoxNorthBoundLatitude() {
		return exGeographicBoundingBoxNorthBoundLatitude;
	}
	public void setExGeographicBoundingBoxNorthBoundLatitude(double exGeographicBoundingBoxNorthBoundLatitude) {
		this.exGeographicBoundingBoxNorthBoundLatitude = exGeographicBoundingBoxNorthBoundLatitude;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public boolean isQueryable() {
		return queryable;
	}
	public void setQueryable(boolean queryable) {
		this.queryable = queryable;
	}
	public int getSort() {
		return sort;
	}
	public void setSort(int sort) {
		this.sort = sort;
	}
	public boolean isAvailable() {
		return available;
	}
	public void setAvailable(boolean available) {
		this.available = available;
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
	public String getInfoFormat() {
		return infoFormat;
	}
	public void setInfoFormat(String infoFormat) {
		this.infoFormat = infoFormat;
	}
	public List<Kartbilder> getKartbilder() {
		return kartbilder;
	}
	public void setKartbilder(List<Kartbilder> kartbilder) {
		this.kartbilder = kartbilder;
	}
	public Karttjenester getKarttjeneste() {
		return karttjeneste;
	}
	public void setKarttjeneste(Karttjenester karttjeneste) {
		this.karttjeneste = karttjeneste;
	}
	public double getWestPolar() {
        return westPolar;
    }
    public void setWestPolar(double westPolar) {
        this.westPolar = westPolar;
    }
    public double getEastPolar() {
        return eastPolar;
    }
    public void setEastPolar(double eastPolar) {
        this.eastPolar = eastPolar;
    }
    public double getSouthPolar() {
        return southPolar;
    }
    public void setSouthPolar(double southPolar) {
        this.southPolar = southPolar;
    }
    public double getNorthPolar() {
        return northPolar;
    }
    public void setNorthPolar(double northPolar) {
        this.northPolar = northPolar;
    }
    public double getScaleminPolar() {
        return scaleminPolar;
    }
    public void setScaleminPolar(double scaleminPolar) {
        this.scaleminPolar = scaleminPolar;
    }
    public double getScalemaxPolar() {
        return scalemaxPolar;
    }
    public void setScalemaxPolar(double scalemaxPolar) {
        this.scalemaxPolar = scalemaxPolar;
    }
}

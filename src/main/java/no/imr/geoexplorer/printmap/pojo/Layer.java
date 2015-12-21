package no.imr.geoexplorer.printmap.pojo;

import java.util.List;


public class Layer {

    private String kartlagId;
    private List<Legend> legend;
    private String kartlagNavn;
    
    public String getKartlagId() {
        return kartlagId;
    }
    public void setKartlagId(String kartlagId) {
        this.kartlagId = kartlagId;
    }
    public List<Legend> getLegend() {
        return legend;
    }
    public void setLegend(List<Legend> legend) {
        this.legend = legend;
    }
    public String getKartlagNavn() {
        return kartlagNavn;
    }
    public void setKartlagNavn(String kartlagNavn) {
        this.kartlagNavn = kartlagNavn;
    }
    
}

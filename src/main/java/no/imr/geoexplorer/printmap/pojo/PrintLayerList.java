package no.imr.geoexplorer.printmap.pojo;

import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class PrintLayerList {

    private List<PrintLayer> printlayers;
    private Integer width;
    private Integer height;
    private List<Layer> layers;
    
    public List<PrintLayer> getPrintlayers() {
        return printlayers;
    }
    public void setPrintlayers(List<PrintLayer> printlayers) {
        this.printlayers = printlayers;
    }
    public Integer getWidth() {
        return width;
    }
    public void setWidth(Integer width) {
        this.width = width;
    }
    public Integer getHeight() {
        return height;
    }
    public void setHeight(Integer height) {
        this.height = height;
    }
    public List<Layer> getLayers() {
        return layers;
    }
    public void setLayers(List<Layer> layers) {
        this.layers = layers;
    }
}

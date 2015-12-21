package no.imr.geoexplorer.printmap.pojo;

import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class PrintLayer {

    private String url;
    private List<BoundingBox> gridBoundingBoxes;
    private int columnSize;
    private List<String> position;
    
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public List<BoundingBox> getGridBoundingBoxes() {
        return gridBoundingBoxes;
    }
    public void setGridBoundingBoxes(List<BoundingBox> gridBoundingBoxes) {
        this.gridBoundingBoxes = gridBoundingBoxes;
    }
    public int getColumnSize() {
        return columnSize;
    }
    public void setColumnSize(int columnSize) {
        this.columnSize = columnSize;
    }
    public List<String> getPosition() {
        return position;
    }
    public void setPosition(List<String> position) {
        this.position = position;
    }
}

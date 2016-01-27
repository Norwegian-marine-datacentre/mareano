package no.imr.geoexplorer.printmap.json.pojo;

public class BoundingBox {
    private String left;
    private String bottom;
    private String right;
    private String top;
    
    public String getLeft() {
        return left;
    }
    public void setLeft(String left) {
        this.left = left;
    }
    public String getBottom() {
        return bottom;
    }
    public void setBottom(String bottom) {
        this.bottom = bottom;
    }
    public String getRight() {
        return right;
    }
    public void setRight(String right) {
        this.right = right;
    }
    public String getTop() {
        return top;
    }
    public void setTop(String top) {
        this.top = top;
    }
    
    @Override
    public String toString() {
        return left + "," + bottom + "," + right + "," + top;
    }
}

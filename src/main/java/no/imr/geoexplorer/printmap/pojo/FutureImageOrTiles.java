package no.imr.geoexplorer.printmap.pojo;

import java.awt.image.BufferedImage;
import java.util.concurrent.Future;

public class FutureImageOrTiles {
    
    private Future<BufferedImage>[][] tiles = null;
    private Future<BufferedImage> image = null;
    private String errorMsg = null;
    
    public Future<BufferedImage>[][] getTiles() {
        return tiles;
    }
    public void setTiles(Future<BufferedImage>[][] tiles) {
        this.tiles = tiles;
    }
    public Future<BufferedImage> getImage() {
        return image;
    }
    public void setImage(Future<BufferedImage> image) {
        this.image = image;
    }
    public String getErrorMsg() {
        return errorMsg;
    }
    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}

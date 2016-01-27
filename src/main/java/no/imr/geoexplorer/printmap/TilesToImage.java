package no.imr.geoexplorer.printmap;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.concurrent.Future;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Component;


/**
 * Provides utility functions to assemble
 * a map with overlays. Like croping, stitching and
 * overlaying map layers
 * 
 * @author endrem
 *
 */
@Component
public class TilesToImage {
    
    private URLConnection con = null;
    private InputStream in = null;
    
    /**
     * Crops image to x, y from List<String> position.
     * @param src
     * @param position
     * @param width
     * @param height
     * @return
     * @throws Exception
     */
    public BufferedImage cropImage( BufferedImage src, List<String> position, int width, int height ) throws Exception {
        
        int x = new Integer(position.get(0) );
        int y = new Integer(position.get(1) );
        x = x * -1;
        y = y * -1;
        BufferedImage dest = src.getSubimage(x, y, width, height );
        return dest; 
    }

    /**
     * Stiches tiles from the given 2-d array of BufferedImages
     * @param tileSet
     * @return
     * @throws Exception
     */
    public BufferedImage stitchTiles(Future<BufferedImage>[][] tileSet) throws Exception {
        
        BufferedImage map = new BufferedImage(256 * tileSet[0].length, 256 * tileSet.length, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D)map.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, 256 * tileSet[0].length, 256 * tileSet.length);
        for (int i = 0; i < tileSet.length; i++) {
            for (int j = 0; j < tileSet[i].length; j++) {
                final BufferedImage tile = tileSet[i][j].get();
                g.drawImage(tile, null, j * 256, i * 256);
            }
        }
        return map;
    }
    
    /**
     * If url is null or empty - return null
     * @param url
     * @return
     * @throws Exception
     */
    public Future<BufferedImage> requestImage(String wmsUrl) throws IllegalArgumentException, IOException {
        
        if ( wmsUrl != null && !wmsUrl.equals("") ) {
            URL url = new URL(wmsUrl);
            
//            con = url.openConnection();
//            con.setConnectTimeout(2000);
//            con.setReadTimeout(2000);
//            in = con.getInputStream();
            
//            BufferedImage img = ImageIO.read(in);
//            return img;
            return DownloadConcurrent.startDownloading(url);
        }
        return null;
    }
    
    public BufferedImage appendImage( BufferedImage appendTo, BufferedImage appendThis) {
        BufferedImage c = new BufferedImage(appendTo.getWidth(), appendTo.getHeight(), BufferedImage.TYPE_INT_ARGB);

        Graphics2D g = (Graphics2D)c.getGraphics();
        g.drawImage(appendTo, 0, 0, null);
        g.drawImage(appendThis, 0, 0, null);
        
        return c;
    }
    
    public BufferedImage appendOverlay( BufferedImage appendTo, BufferedImage appendThis, List<String> position) {
        BufferedImage c = new BufferedImage(appendTo.getWidth(), appendTo.getHeight(), BufferedImage.TYPE_INT_ARGB);

        int x = new Integer(position.get(0)) * -1;
        int y = new Integer(position.get(1)) * -1;
        Graphics2D g = (Graphics2D)c.getGraphics();
        g.drawImage(appendTo, 0, 0, null);
        g.drawImage(appendThis, x, y, null);
        
        return c;
    }
    

}

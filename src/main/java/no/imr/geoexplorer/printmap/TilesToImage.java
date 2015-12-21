package no.imr.geoexplorer.printmap;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.TextAttribute;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.text.AttributedString;
import java.util.List;

import javax.imageio.ImageIO;

import no.imr.geoexplorer.printmap.pojo.Layer;
import no.imr.geoexplorer.printmap.pojo.Legend;

import org.springframework.stereotype.Component;

@Component
public class TilesToImage {
    
    public BufferedImage cropImage( BufferedImage src, List<String> position, int width, int height ) throws Exception {
        
        int x = new Integer(position.get(0) );
        int y = new Integer(position.get(1) );
        x = x * -1;
        y = y * -1;
        BufferedImage dest = src.getSubimage(x, y, width, height );
        return dest; 
    }

    public BufferedImage stitchTiles(BufferedImage[][] tileSet) throws Exception {
        
        BufferedImage map = new BufferedImage(256 * tileSet[0].length, 256 * tileSet.length, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D)map.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, 256 * tileSet[0].length, 256 * tileSet.length);
        for (int i = 0; i < tileSet.length; i++) {
            for (int j = 0; j < tileSet[i].length; j++) {
                final BufferedImage tile = tileSet[i][j];
                g.drawImage(tile, null, j * 256, i * 256);
            }
        }
        return map;
    }
    
    public BufferedImage requestImage(String url) throws Exception {
        
        BufferedImage img = ImageIO.read(new URL(url));
        return img;
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
    
    public BufferedImage writeLegend( BufferedImage mapImage, List<Layer> layers) throws Exception {
        Graphics2D g2 = (Graphics2D)mapImage.getGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Font LayerFont = new Font("Serif", Font.PLAIN, 20);
        g2.setColor(Color.black);
        Font legendFont = new Font("Serif", Font.PLAIN, 14);  
        
        int imageWidth = mapImage.getWidth();

        int writeWidth = imageWidth - 200;
        int writeHeight = 15;
        
        AttributedString as1 = null;
        for ( int i=0; i < layers.size(); i++) {
            g2.setFont(LayerFont);
            Layer layer = layers.get(i);
            
            as1 = new AttributedString(layer.getKartlagNavn());
            as1.addAttribute(TextAttribute.BACKGROUND, Color.WHITE);
            g2.drawString(as1.getIterator(), writeWidth, writeHeight );
            writeHeight += 15;
            
//            System.out.println("layer:"+layer.getKartlagNavn()+ "layer_size:"+layer.getLegend().size());
            
            List<Legend> legends = layer.getLegend();
            g2.setFont(legendFont);
            for ( int j=0; j < legends.size(); j++ ) {
                Legend legend = legends.get(j);
                URL url = new URL(legend.getUrl());
                BufferedImage legendImg = ImageIO.read(url);
                int legendWidth = imageWidth - 200;
//                System.out.println("width:"+legendWidth);
//                System.out.println("height:"+writeHeight);
                g2.drawImage(legendImg, legendWidth, writeHeight - legendImg.getHeight() + 4, null);
//                System.out.println("legendString:"+legend.getText());
                
                as1 = new AttributedString(legend.getText());
                as1.addAttribute(TextAttribute.BACKGROUND, Color.WHITE);
                g2.drawString(as1.getIterator(), legendWidth + legendImg.getWidth(), writeHeight);
                writeHeight += 15;
            }
        }
        return mapImage;
    }
}

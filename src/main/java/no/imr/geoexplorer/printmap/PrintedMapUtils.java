package no.imr.geoexplorer.printmap;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import no.imr.geoexplorer.printmap.json.pojo.PrintLayerList;

import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

/**
 * Provides utility functions for artifacts needed
 * when creating a printable map like legends, 
 * northarrow and scale bar 
 * 
 * @author endrem
 *
 */
@Component
public class PrintedMapUtils {
    
    private final static String NORTH_ARROW = "northArrow.png";

    public BufferedImage addNorthArrow( BufferedImage mapImage, ApplicationContext appContext ) throws Exception {
        Resource resource = appContext.getResource("classpath:"+NORTH_ARROW);
        BufferedImage northArrow = ImageIO.read(resource.getFile());
        mapImage = writeNorthArrow(mapImage, northArrow);
        return mapImage;
    }
    
    public BufferedImage writeNorthArrow( BufferedImage mapImage, BufferedImage northArrow ) {
        Graphics2D g2 = (Graphics2D)mapImage.getGraphics();
        int imageWidth = mapImage.getWidth();
        int imageHeight = mapImage.getHeight();
        
        g2.drawImage(northArrow, imageWidth -35, imageHeight -60, null);
        return mapImage;
    }
    
    public BufferedImage addScaleBar(BufferedImage mapImage, PrintLayerList pll) {
        Graphics2D g2 = (Graphics2D)mapImage.getGraphics();
        int imageWidth = mapImage.getWidth();
        int imageHeight = mapImage.getHeight();
        
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Font LayerFont = new Font("Serif", Font.PLAIN, 14);
        g2.setColor(Color.black);
        g2.setFont(LayerFont);
        
        Shape l = new Line2D.Double( imageWidth - (60 + pll.getScaleLine()), imageHeight -15, imageWidth - 60, imageHeight - 15 );
        Stroke stroke = g2.getStroke();
        g2.draw(l);
        
        g2.setStroke(stroke);
        g2.drawString( pll.getScaleLineText(), imageWidth - (62 + pll.getScaleLine()), imageHeight -17 );
        return mapImage;
    }
}

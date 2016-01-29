package no.imr.geoexplorer.printmap;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.List;

import javax.imageio.ImageIO;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import no.imr.geoexplorer.printmap.json.pojo.Legend;
import no.imr.geoexplorer.printmap.json.pojo.PrintLayer;

@Component
public class LegendUtil {
    
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(LegendUtil.class);
    
    private final static int LAYER_TEXT_SPACE = 19;
    private final static int LEGEND_TEXT_SPACE = 15;
    private final static int DEFAULT_LEGEND_WIDTH = 230;
    private final static int DEFAULT_LEGEND_WIDTH_WRITE_SPACE = 228;
    private final static int DEFAULT_LEGEND_BOARDER_WIDTH = 200;
    private final static int LEGEND_BOARDER_HEIGHT = 5;
    private final static int LEGEND_BOARDER_HEIGHT_BOTTOM_FILL = 4;
    
    private static int LEGEND_IMAGE_WIDTH = 30;
    
    private HTMLstring htmlString = new HTMLstring();
    
    public BufferedImage writeLegend( BufferedImage mapImage, List<PrintLayer> printLayers) throws Exception {
        
        int LEGEND_WIDTH = DEFAULT_LEGEND_WIDTH;
        int LEGEND_WIDTH_WRITE_SPACE = DEFAULT_LEGEND_WIDTH_WRITE_SPACE;
        int LEGEND_BOARDER_WIDTH = DEFAULT_LEGEND_BOARDER_WIDTH;
        
        Graphics2D g2 = (Graphics2D)mapImage.getGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.black);
        
        Font legendFont = new Font("Serif", Font.PLAIN, 14);
        Font layerFont = new Font("Serif", Font.BOLD, 14);
        
        int titleWidth = getMaxStringWidth( printLayers, g2, legendFont );
        if ( titleWidth > (DEFAULT_LEGEND_BOARDER_WIDTH - LEGEND_IMAGE_WIDTH) ) {
            LEGEND_BOARDER_WIDTH = titleWidth + LEGEND_IMAGE_WIDTH;
            LEGEND_WIDTH_WRITE_SPACE = titleWidth + 28 +LEGEND_IMAGE_WIDTH;
            LEGEND_WIDTH = titleWidth + 30 +LEGEND_IMAGE_WIDTH;
        }
        System.out.println("LEGEND_BOARDER_WIDTH:"+LEGEND_BOARDER_WIDTH);
        System.out.println("LEGEND_WIDTH_WRITE_SPACE:"+LEGEND_WIDTH_WRITE_SPACE);
        System.out.println("LEGEND_WIDTH:"+LEGEND_WIDTH);
        
        int imageWidth = mapImage.getWidth();
        int writeLegendWidth = imageWidth - LEGEND_WIDTH_WRITE_SPACE;
        int fillLegendWidth = imageWidth - LEGEND_WIDTH;
        int writeHeight = LAYER_TEXT_SPACE;
        
        int legendHeight = LEGEND_BOARDER_HEIGHT;
        for ( PrintLayer printlayer : printLayers ) {
            legendHeight += LAYER_TEXT_SPACE + (LEGEND_TEXT_SPACE * printlayer.getLegend().size()); 
        }
        legendHeight += LEGEND_BOARDER_HEIGHT_BOTTOM_FILL;
        fillRectangleWhite(g2, fillLegendWidth, LEGEND_BOARDER_HEIGHT, LEGEND_BOARDER_WIDTH, legendHeight);
         
        for ( int i=0; i < printLayers.size(); i++) {
            PrintLayer printlayer = printLayers.get(i);
            
            g2.setFont( layerFont );
            String title = printlayer.getKartlagTitle();
            if ( !title.equals("") ) {
                g2.drawString( title, writeLegendWidth, writeHeight );
            } else {
                g2.drawString("No title for layer", writeLegendWidth, writeHeight);
            }
            writeHeight += LAYER_TEXT_SPACE;
            
            List<Legend> legends = printlayer.getLegend();
            g2.setFont(legendFont);
            for ( int j=0; j < legends.size(); j++ ) {
                Legend legend = legends.get(j);
                boolean legendImgError = false;
                BufferedImage legendImg = null;
                if ( legend.getUrl() != null && !legend.getUrl().equals("") ) {
                    URI uri = new URI(legend.getUrl());
                    URL url = new URL(uri.toASCIIString());                    
                    try {
                        legendImg = ImageIO.read(url);
                        g2.drawImage(legendImg, writeLegendWidth, writeHeight - legendImg.getHeight() + 4, null);
                    } catch( IOException ioe ) {
                        g2.drawString(ioe.getMessage()+" "+legend.getText() , writeLegendWidth, writeHeight);
                        legendImg = new BufferedImage(1,1, BufferedImage.TYPE_INT_ARGB);
                        LOG.error("feil ved henting av legend bilde - url:"+legend.getUrl());
                        ioe.printStackTrace();
                        legendImgError=true;
                    }
                }
                g2.setFont( legendFont );
                if ( legendImgError == false ) {
                    int lengthOfLegendImg = 0;
                    if ( legendImg != null ) {
                        lengthOfLegendImg = legendImg.getWidth();
                    }
                    String legendText = legend.getText();
                    legendText = htmlString.removeHTMLfromString(legendText);
                    g2.drawString(legendText, writeLegendWidth + lengthOfLegendImg, writeHeight);
                }
                legendImg = null;
                writeHeight += LEGEND_TEXT_SPACE;
            }
        }
        addBoarder(g2, fillLegendWidth, LEGEND_BOARDER_HEIGHT, LEGEND_BOARDER_WIDTH, legendHeight);
        addBoarder(g2, 0, 0, imageWidth-1, mapImage.getHeight()-1);

        return mapImage;
    }
    
    protected int getMaxStringWidth(List<PrintLayer> printLayers, Graphics2D g2, Font legendFont) {
        
        FontMetrics fontMetrics = g2.getFontMetrics(legendFont);
        int maxWidth = 0;
        for ( PrintLayer pl : printLayers ) {
            maxWidth = getMaxWidth( pl.getKartlagTitle(), maxWidth, fontMetrics );
            for ( Legend l : pl.getLegend() ) {
                maxWidth = getMaxWidth( l.getText(), maxWidth, fontMetrics );
            }
        }
        return maxWidth;
    }
    
    private int getMaxWidth( String title, int maxWidth, FontMetrics fontMetrics ) {
        
        int newValue = fontMetrics.stringWidth(title);
        maxWidth = getMax(maxWidth, newValue);
//        System.out.println("title:"+title+" max:"+maxWidth);
        return maxWidth;
    }
    
    private int getMax(int max, int newValue) {
        if ( newValue > max) return newValue;
        else return max;
    }
    
    private void fillRectangleWhite(Graphics2D g2, double x, double y, double width, double height) {
        Color color = g2.getColor();
        g2.setPaint(Color.white);
        g2.fill(new Rectangle2D.Double(x, y, width, height));
        g2.setColor(color);
    }
    
    private void addBoarder(Graphics2D g2, int x, int y, int width, int height) {
        float thickness = 1;
        Stroke oldStroke = g2.getStroke();
        g2.setStroke(new BasicStroke(thickness));
        g2.drawRect(x, y, width, height);
        g2.setStroke(oldStroke);
    }
}

package no.imr.geoexplorer.printmap;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.List;

import javax.imageio.ImageIO;




import no.imr.geoexplorer.printmap.pojo.Legend;
import no.imr.geoexplorer.printmap.pojo.PrintLayer;
import no.imr.geoexplorer.printmap.pojo.PrintLayerList;

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
    
    private URLConnection con = null;
    private InputStream in = null;
    /**
     * If url is null or empty - returns 1x1 empty BufferedImage
     * @param url
     * @return
     * @throws Exception
     */
    public BufferedImage requestImage(String wmsUrl) throws IllegalArgumentException, IOException {
        
        if ( wmsUrl != null && !wmsUrl.equals("") ) {
            URL url = new URL(wmsUrl);
            
            con = url.openConnection();
            con.setConnectTimeout(2000);
            con.setReadTimeout(2000);
            in = con.getInputStream();
            
            BufferedImage img = ImageIO.read(in);
            System.out.println("getting image for url:"+wmsUrl);
            if (img != null) {
                System.out.println("and img:"+img.toString());
            } else { System.out.println("image empty!"+img);}
            return img;
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
    
    private final static int LAYER_TEXT_SPACE = 19;
    private final static int LEGEND_TEXT_SPACE = 15;
    private final static int LEGEND_WIDTH = 230;
    private final static int LEGEND_WIDTH_WRITE_SPACE = 228;
    private final static int LEGEND_BOARDER_WIDTH = 200;
    private final static int LEGEND_BOARDER_HEIGHT = 5;
    private final static int LEGEND_BOARDER_HEIGHT_BOTTOM_FILL = 4;
    
    public BufferedImage writeLegend( BufferedImage mapImage, List<PrintLayer> printLayers) throws Exception {
        Graphics2D g2 = (Graphics2D)mapImage.getGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.black);
        Font legendFont = new Font("Serif", Font.PLAIN, 14);
        Font layerFont = new Font("Serif", Font.BOLD, 14); 
        
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
                if ( legend.getUrl() != null && !legend.getUrl().equals("") ) {
                    URL url = new URL(legend.getUrl());
                    BufferedImage legendImg = ImageIO.read(url);
                    g2.drawImage(legendImg, writeLegendWidth, writeHeight - legendImg.getHeight() + 4, null);
                
                    g2.setFont( legendFont );
                    g2.drawString(legend.getText(), writeLegendWidth + legendImg.getWidth(), writeHeight);
                    writeHeight += LEGEND_TEXT_SPACE;
                }
            }
        }
        addBoarder(g2, fillLegendWidth, LEGEND_BOARDER_HEIGHT, LEGEND_BOARDER_WIDTH, legendHeight);
        addBoarder(g2, 0, 0, imageWidth-1, mapImage.getHeight()-1);

        return mapImage;
    }
    
    private void addBoarder(Graphics2D g2, int x, int y, int width, int height) {
        float thickness = 1;
        Stroke oldStroke = g2.getStroke();
        g2.setStroke(new BasicStroke(thickness));
        g2.drawRect(x, y, width, height);
        g2.setStroke(oldStroke);
    }
    
    private void fillRectangleWhite(Graphics2D g2, double x, double y, double width, double height) {
        Color color = g2.getColor();
        g2.setPaint(Color.white);
        g2.fill(new Rectangle2D.Double(x, y, width, height));
        g2.setColor(color);
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

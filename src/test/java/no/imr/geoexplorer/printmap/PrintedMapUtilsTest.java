package no.imr.geoexplorer.printmap;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.List;

import no.imr.geoexplorer.admindatabase.controller.PrintMapControllerTest;
import no.imr.geoexplorer.printmap.json.pojo.PrintLayer;
import no.imr.geoexplorer.printmap.json.pojo.PrintLayerList;

import org.junit.Test;

public class PrintedMapUtilsTest {
    
    private LegendUtil legenMapUtils = new LegendUtil(); 

    @Test
    public void testGetLongestTitleWidth() {
        PrintLayerList plls = PrintMapControllerTest.setupPrintLayers();
        
        BufferedImage buff = new BufferedImage(1,1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = (Graphics2D)buff.getGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.black);
        
        Font legendFont = new Font("Serif", Font.PLAIN, 14);
        
        int value = legenMapUtils.getMaxStringWidth( plls.getPrintlayers(), g2, legendFont);
        System.out.println("max:"+value);
    }
}

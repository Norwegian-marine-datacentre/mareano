package no.imr.geoexplorer.admindatabase.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.List;

import javax.imageio.ImageIO;

import no.imr.geoexplorer.printmap.TilesToImage;
import no.imr.geoexplorer.printmap.pojo.Layer;
import no.imr.geoexplorer.printmap.pojo.PrintLayerList;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:test-context-dont-check-inn.xml"})
public class RenderLegendGraphics {

    @Autowired(required=true)
    private PrintMapController printMap;
    
    private PrintMapControllerTest printMapTest = new PrintMapControllerTest();    
    
    private TilesToImage util = new TilesToImage();
    
    @Test
    public void testDrawLegendGraphics() throws Exception {

        PrintLayerList printLayers = printMapTest.setupMap();
        List<Layer> layers = printMapTest.createLegendInfo();
        printLayers.setLayers(layers);
        
        byte[] byteImage = printMap.getMapImage(printLayers, null );
        ByteArrayInputStream imputStream = new ByteArrayInputStream(byteImage); 
        BufferedImage image = ImageIO.read(imputStream);
        
        BufferedImage image2 = util.writeLegend(image, layers);
        
        ImageIO.write(image2, "png", new File("testDrawLegendGraphics.png"));
        
    }
    
    @Test
    public void testNorthArrow() throws Exception {
        BufferedImage dummyImage = new BufferedImage(500, 500, BufferedImage.TYPE_INT_ARGB);
        printMap.addNorthArrow(dummyImage);
    }
}

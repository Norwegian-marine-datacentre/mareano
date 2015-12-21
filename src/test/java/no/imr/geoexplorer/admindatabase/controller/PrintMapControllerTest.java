package no.imr.geoexplorer.admindatabase.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;

import no.imr.geoexplorer.printmap.TilesToImage;
import no.imr.geoexplorer.printmap.pojo.BoundingBox;
import no.imr.geoexplorer.printmap.pojo.Layer;
import no.imr.geoexplorer.printmap.pojo.Legend;
import no.imr.geoexplorer.printmap.pojo.PrintLayer;
import no.imr.geoexplorer.printmap.pojo.PrintLayerList;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class PrintMapControllerTest {
    
    @Test
    public BufferedImage createPDFReportForBarentswatchBackgroundRequestTest() throws Exception {
        PrintMapController printMap = new PrintMapController();
        printMap.setTilesToImage(new TilesToImage());

        PrintLayerList printLayers = setupMap();
        
        byte[] byteImage = printMap.getMapImage(printLayers, null );
        ByteArrayInputStream imputStream = new ByteArrayInputStream(byteImage); 
        BufferedImage image = ImageIO.read(imputStream);
        return image;
    }
    
    public PrintLayerList setupMap() {
        String url = "http://opencache.statkart.no/gatekeeper/gk/gk.open?&layers=barentswatch_grunnkart&FORMAT=image/png&TRANSPARENT=TRUE&ISBASELAYER=true&SERVICE=WMS&VERSION=1.1.1&REQUEST=GetMap&STYLES=&SRS=EPSG:32633&BBOX=-1806752,7659488,-1113504,8352736&WIDTH=256&HEIGHT=256";
        String[] gridSet = {
                "-1806752,7659488,-1113504,8352736","-1113504,7659488,-420256,8352736","-420256,7659488,272992,8352736","272992,7659488,966240,8352736","966240,7659488,1659488,8352736","1659488,7659488,2352736,8352736","2352736,7659488,3045984,8352736",
                "-1806752,6966240,-1113504,7659488","-1113504,6966240,-420256,7659488","-420256,6966240,272992,7659488","272992,6966240,966240,7659488","966240,6966240,1659488,7659488","1659488,6966240,2352736,7659488","2352736,6966240,3045984,7659488",
                "-1806752,6272992,-1113504,6966240","-1113504,6272992,-420256,6966240","-420256,6272992,272992,6966240","272992,6272992,966240,6966240","966240,6272992,1659488,6966240","1659488,6272992,2352736,6966240","2352736,6272992,3045984,6966240"
                };
        int columnSize = 7;
        String[] position = {"-178","-165"};
        
        PrintLayerList printLayers = new PrintLayerList();
        PrintLayer printLayer = new PrintLayer();
        printLayer.setColumnSize(columnSize);
        
        List<String> boundingboxes = new ArrayList<String>( Arrays.asList(gridSet) );
        List<BoundingBox> bboxes = new ArrayList<BoundingBox>( gridSet.length);
        for ( String bbox : boundingboxes ) {
            StringTokenizer st = new StringTokenizer(bbox,",");
            BoundingBox bb = new BoundingBox();
            bb.setLeft( (String) st.nextElement() );
            bb.setBottom( (String) st.nextElement() );
            bb.setRight( (String) st.nextElement() );
            bb.setTop( (String) st.nextElement() );
            bboxes.add(bb);
        }
        
        printLayer.setGridBoundingBoxes( bboxes );
        printLayer.setUrl( url );
        printLayer.setPosition( Arrays.asList(position) );
        
        List<PrintLayer> plls = new ArrayList<PrintLayer>(1);
        plls.add(printLayer);
        printLayers.setPrintlayers(plls);
        printLayers.setWidth(1300);
        printLayers.setHeight(270);
        
        printLayers.setLayers(createLegendInfo());
        
        return printLayers;
    }
    
    public List<Layer> createLegendInfo() {
        List<Layer> layers = new ArrayList<Layer>(2);
        Layer layer = new Layer();
        layer.setKartlagId("242");
        List<Legend> legends = new ArrayList<Legend>(2);
        Legend legend1 = new Legend();
        legend1.setText("Beamtrawl");
        legend1.setUrl("http://www.mareano.no/kart/images/legends/mareano_stations/beamtrawl.png");
        Legend legend2 = new Legend();
        legend2.setText("RP-slead");
        legend2.setUrl("http://www.mareano.no/kart/images/legends/mareano_stations/rp-slead.png");
        legends.add(legend1);
        legends.add(legend2);
        layer.setLegend(legends);
        
        Layer layer2 = new Layer();
        layer2.setKartlagId("243");
        List<Legend> legends2 = new ArrayList<Legend>(2);
        Legend legend3 = new Legend();
        legend3.setText("Boxcore");
        legend3.setUrl("http://www.mareano.no/kart/images/legends/mareano_stations/boxcore.png");
        Legend legend4 = new Legend();
        legend4.setText("vanVeen Grab");
        legend4.setUrl("http://www.mareano.no/kart/images/legends/mareano_stations/vanveen.png");     
        legends2.add(legend3);
        legends2.add(legend4);
        layer2.setLegend(legends2);
        
        layers.add(layer);
        layers.add(layer2);
        return layers;
    }
}

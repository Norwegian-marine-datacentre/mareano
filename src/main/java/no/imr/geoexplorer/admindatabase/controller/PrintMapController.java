package no.imr.geoexplorer.admindatabase.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;

import no.imr.geoexplorer.admindatabase.dao.MareanoAdminDbDao;
import no.imr.geoexplorer.admindatabase.mybatis.pojo.KartlagEnNo;
import no.imr.geoexplorer.printmap.TilesToImage;
import no.imr.geoexplorer.printmap.pojo.BoundingBox;
import no.imr.geoexplorer.printmap.pojo.Layer;
import no.imr.geoexplorer.printmap.pojo.PrintLayer;
import no.imr.geoexplorer.printmap.pojo.PrintLayerList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class PrintMapController {
    
    @Autowired
    private TilesToImage tilesUtil;
    
    public void setTilesToImage(TilesToImage tilesUtil) {
        this.tilesUtil = tilesUtil;
    }
    
    @Autowired(required = true)
    private MareanoAdminDbDao dao;
    
    @Autowired
    private ApplicationContext appContext;
    
    @RequestMapping(value="/getMapImage", method = RequestMethod.POST)
    public void getMapImage( @RequestParam("printImage") String jsonOfPrintLayer, HttpServletResponse resp) throws Exception {
        
        
        String decodeJson = URLDecoder.decode(jsonOfPrintLayer, "utf-8"); 
        PrintLayerList pll = 
                new ObjectMapper().readValue(decodeJson, PrintLayerList.class);
        getMapImage( pll, resp);
    }
    
    protected void getMapImage( PrintLayerList pll, HttpServletResponse resp) throws Exception {
        
        List<Layer> layers = pll.getLayers();
        for ( Layer layer : layers) {
            List<KartlagEnNo> kartlagene = dao.getKartlagNo(new Long(layer.getKartlagId()));
            if ( kartlagene != null && kartlagene.size() > 0) {
                layer.setKartlagNavn(kartlagene.get(0).getAlternateTitle());
            } else {
                layer.setKartlagNavn("Map id:"+layer.getKartlagId()+" not found");
            }
        }
        
        List<PrintLayer> printLayers = pll.getPrintlayers();
        int width = pll.getWidth();
        int height = pll.getHeight();
        
        BufferedImage mapImage = null;
        BufferedImage backgroundImage = null;
        BufferedImage overlayImage = null;
        List<String> position = null;
        
        //get position only from background grid
        for ( PrintLayer printLayer : printLayers) {
            if ( printLayer.getColumnSize() > 1) {
                position = printLayer.getPosition();
            }
        }
        for ( int i=0; i< printLayers.size(); i++ ) {
            
            //assert gridArray.size % columnSize == 0
            PrintLayer printLayer = printLayers.get(i);

            if ( printLayer.getColumnSize() > 1) {
                if ( backgroundImage != null) {
                    System.out.println("OVERWRITING BACKGROUND IMAGE");
                }
                backgroundImage = getTiledImage( mapImage, printLayer);
            } else {
                if (overlayImage != null) {
                    System.out.println("OVERWRITING OVERLAY IMAGE");
                }
                try {
                    overlayImage = getOverlay( printLayer.getUrl() );
                } catch(IOException e) {
                    Layer l = layers.get(i);
                    l.setKartlagNavn(l.getKartlagNavn() + " - Unable to get overlay");
                }
            }

            if (mapImage == null ) {
                if (backgroundImage != null ) {
                    mapImage = backgroundImage;
                    backgroundImage = null;
                } else if ( overlayImage != null ) {
                    mapImage = overlayImage;
                    overlayImage = null;
                }
            }
            
            if ( backgroundImage != null ) {
                mapImage = tilesUtil.appendImage(mapImage, backgroundImage);
                backgroundImage = null;
            }
            if ( overlayImage != null ) {
                mapImage = tilesUtil.appendOverlay(mapImage, overlayImage, position);
                overlayImage = null;
            }
        }
        mapImage = tilesUtil.cropImage( mapImage, position, width, height );
        
        mapImage = tilesUtil.writeLegend( mapImage, layers );
        
        mapImage = addNorthArrow( mapImage );
        
        mapImage = tilesUtil.addScaleBar( mapImage, pll);
        
        resp.setContentType("image/png");
        resp.setHeader("Content-Disposition", " attachment; filename=image.png");
//        resp.getOutputStream().write();
//        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        ImageIO.write(mapImage, "png", resp.getOutputStream());
        resp.flushBuffer();
//        return bao.toByteArray();
    }
    
    private final static String NORTH_ARROW = "northArrow.png";
    
    protected BufferedImage addNorthArrow( BufferedImage mapImage ) throws Exception {
        Resource resource = appContext.getResource("classpath:"+NORTH_ARROW);
        BufferedImage northArrow = ImageIO.read(resource.getFile());
        mapImage = tilesUtil.writeNorthArrow(mapImage, northArrow);
        return mapImage;
    }
        
    protected BufferedImage getTiledImage( BufferedImage aimage, PrintLayer printLayer ) throws Exception {

        String url = printLayer.getUrl();
        int columnSize = printLayer.getColumnSize();
        List<BoundingBox> gridArray = printLayer.getGridBoundingBoxes();
        
        if ( url.contains("BBOX")) {
            int indexOfBBOX = url.indexOf("BBOX");
            String firstHalf = url.substring(0, indexOfBBOX);
            String secondHalf = url.substring( indexOfBBOX, url.length() );
            url = firstHalf + secondHalf.substring( secondHalf.indexOf("&")+1, secondHalf.length() );
        }
        
        int rows = gridArray.size() / columnSize;
        String[][] gridSet = new String[rows][columnSize];
        String[][] urlSet = new String[rows][columnSize];
        BufferedImage[][] tileSet = new BufferedImage[rows][columnSize];
        int k = 0;
        int j = 0;
        for ( int i=0; i< gridArray.size(); i++) {
            k = i % columnSize;
            j = i / columnSize;
            gridSet[j][k] = gridArray.get(i).toString();
            urlSet[j][k] = url + "&BBOX=" + gridSet[j][k];
            BufferedImage aTile = tilesUtil.requestImage(urlSet[j][k]);
            tileSet[j][k] = aTile;
        }
        
        BufferedImage tiledImage = tilesUtil.stitchTiles( tileSet );
        return tiledImage;
    }
    
    public BufferedImage getOverlay( String url ) throws IllegalArgumentException, IOException {
        
        BufferedImage overlay = tilesUtil.requestImage( url );
        return overlay;

    }
}

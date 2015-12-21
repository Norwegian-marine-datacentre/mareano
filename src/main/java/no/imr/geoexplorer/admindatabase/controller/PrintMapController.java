package no.imr.geoexplorer.admindatabase.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;

import no.imr.geoexplorer.admindatabase.dao.MareanoAdminDbDao;
import no.imr.geoexplorer.admindatabase.mybatis.pojo.Kartlag;
import no.imr.geoexplorer.admindatabase.mybatis.pojo.KartlagEnNo;
import no.imr.geoexplorer.printmap.TilesToImage;
import no.imr.geoexplorer.printmap.pojo.BoundingBox;
import no.imr.geoexplorer.printmap.pojo.Layer;
import no.imr.geoexplorer.printmap.pojo.PrintLayer;
import no.imr.geoexplorer.printmap.pojo.PrintLayerList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PrintMapController {
    
    @Autowired
    private TilesToImage tilesUtil;
    
    public void setTilesToImage(TilesToImage tilesUtil) {
        this.tilesUtil = tilesUtil;
    }
    
    @Autowired(required = true)
    private MareanoAdminDbDao dao;
    
    @RequestMapping(value="/getMapImage.json", method = RequestMethod.POST, produces = "image/png")
    public @ResponseBody byte[] getMapImage( @RequestBody PrintLayerList pll, HttpServletResponse resp) throws Exception {
        
        PrintLayer p = pll.getPrintlayers().get(0);
//        System.out.println("getColumnSize:"+p.getColumnSize()+" url:"+p.getUrl()+" bbox:"+p.getGridBoundingBoxes()+" position"+p.getPosition());
        List<Layer> layers = pll.getLayers();
//        Layer l = layers.get(0);
//        System.out.println("layers:"+l.getKartlagId()+ "text:"+l.getLegend().get(0).getText()+"url:"+l.getLegend().get(0).getUrl());
        for ( Layer layer : layers) {
            List<KartlagEnNo> kartlagene = dao.getKartlagNo(new Long(layer.getKartlagId()));
            layer.setKartlagNavn(kartlagene.get(0).getAlternateTitle());
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
        for ( PrintLayer printLayer : printLayers) {
            //assert gridArray.size % columnSize == 0
            System.out.println("gridset:" + printLayer.getGridBoundingBoxes());
            System.out.println("url:" + printLayer.getUrl());
            System.out.println("columnSize:" + printLayer.getColumnSize());
            System.out.println("position:" + position.get(0) + position.get(1) );

            if ( printLayer.getColumnSize() > 1) {
                if ( backgroundImage != null) {
                    System.out.println("OVERWRITING BACKGROUND IMAGE");
                }
                backgroundImage = getTiledImage( mapImage, printLayer);
            } else {
                if (overlayImage != null) {
                    System.out.println("OVERWRITING OVERLAY IMAGE");
                }
                overlayImage = getOverlay( printLayer.getUrl() );
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
        
        mapImage = tilesUtil.writeLegend( mapImage, layers);
        
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        ImageIO.write(mapImage, "png", bao);
        return bao.toByteArray();
        
        
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
        
        System.out.println("array:"+gridArray+" size:"+gridArray.size()+ " columnSize:"+columnSize);
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
        System.out.println("image:" + tiledImage);
        return tiledImage;
    }
    
    public BufferedImage getOverlay( String url ) throws Exception {
        
        BufferedImage overlay = tilesUtil.requestImage( url );
        return overlay;

    }
}

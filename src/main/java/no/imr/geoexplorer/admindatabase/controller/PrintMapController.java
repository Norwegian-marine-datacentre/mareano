package no.imr.geoexplorer.admindatabase.controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import no.imr.geoexplorer.printmap.LegendUtil;
import no.imr.geoexplorer.printmap.PrintedMapUtils;
import no.imr.geoexplorer.printmap.TilesToImage;
import no.imr.geoexplorer.printmap.json.pojo.PrintLayer;
import no.imr.geoexplorer.printmap.json.pojo.PrintLayerList;
import no.imr.geoexplorer.printmap.pojo.FutureImageOrTiles;
import no.imr.geoexplorer.printmap.pojo.ImageFilenameResponse;

@Controller
public class PrintMapController {
    
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(PrintMapController.class);
    
    @Autowired
    private ApplicationContext appContext;
    
    
    @Autowired
    private TilesToImage tilesUtil;
    
    public void setTilesToImage(TilesToImage tilesUtil) {
        this.tilesUtil = tilesUtil;
    }

    @Autowired
    private PrintedMapUtils printedMapUtils;
    
    @Autowired
    private LegendUtil legendUtil;
    
    @RequestMapping(value="/postMapImage", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    protected @ResponseBody ImageFilenameResponse postMapImage( @RequestBody PrintLayerList pll, HttpServletResponse resp) throws Exception {
        
        Long startTime = System.currentTimeMillis();
        LOGGER.debug("startTime:"+startTime);
        
        List<PrintLayer> printLayers = pll.getPrintlayers();
        int width = pll.getWidth();
        int height = pll.getHeight();
        
        BufferedImage mapImage = null;

        List<String> position = null;
        
        List<PrintLayer> printLayersWithouEmptyUrl = new ArrayList<PrintLayer>(printLayers.size());
        
        //get position only from background grid
        for ( PrintLayer printLayer : printLayers) {
        	if ( printLayer.getUrl() != null && !printLayer.getUrl().equals("") ) {
        		if ( printLayer.getColumnSize() > 1) {
        			position = printLayer.getPosition();
        		}
        		printLayersWithouEmptyUrl.add(printLayer);
        		System.out.println("printLayer.url:"+printLayer.getUrl());
        	} else System.out.println("empty url:"+printLayer.getUrl()+" name:"+printLayer.getKartlagTitle());
        }
        Map<Integer, FutureImageOrTiles> layerMap = requestAllLayers(printLayersWithouEmptyUrl);
        LOGGER.debug("requestAllLayers:"+(System.currentTimeMillis()-startTime));
        
        for ( int i=0; i < printLayersWithouEmptyUrl.size(); i++ ) {
            FutureImageOrTiles futureImage =  layerMap.get(i);
            try {
                if ( futureImage.getTiles() != null ) {
                    Future<BufferedImage>[][] futureTiles = futureImage.getTiles();
                    BufferedImage backgroundImage = tilesUtil.stitchTiles(futureTiles);
                    if ( mapImage != null)
                        mapImage = tilesUtil.appendImage(mapImage, backgroundImage);
                    else 
                        mapImage = backgroundImage;
                } else if ( futureImage.getImage() != null ) {
                    BufferedImage overlayImage = futureImage.getImage().get();
                    mapImage = tilesUtil.appendOverlay(mapImage, overlayImage, position);
                }
            } catch (ExecutionException ee) {
                PrintLayer pl = printLayers.get(i);
                String title = pl.getKartlagTitle();
                pl.setKartlagTitle(title + " "+ ee.getMessage());
                ee.printStackTrace();
            }
        }
        
        LOGGER.debug("crop:"+(System.currentTimeMillis()-startTime));
        mapImage = tilesUtil.cropImage( mapImage, position, width, height );
        LOGGER.debug("legend:"+(System.currentTimeMillis()-startTime));
        mapImage = legendUtil.writeLegend( mapImage, printLayers );
        LOGGER.debug("northarrow:"+(System.currentTimeMillis()-startTime));
        mapImage = printedMapUtils.addNorthArrow( mapImage, appContext );
        
        mapImage = printedMapUtils.addScaleBar( mapImage, pll);
        
        String fileName = "printMap";        
        File temp = File.createTempFile(fileName, "."+PNG);
        LOGGER.debug("BeforeSave image:"+(System.currentTimeMillis()-startTime));
        ImageIO.write(mapImage, PNG, temp);
        LOGGER.debug("AfterSave image:"+(System.currentTimeMillis()-startTime));

        ImageFilenameResponse respJson = new ImageFilenameResponse();
        respJson.setFilename(temp.getName());
        LOGGER.debug("complete:"+(System.currentTimeMillis()-startTime));
        return respJson;
    }
    
    private final static String PNG = "png";
    private String tempImageFilePath = "";
    
    @RequestMapping(value="/getMapImage", method = RequestMethod.GET)
    public void getMapImage(@RequestParam("printFilename") String filename, HttpServletResponse resp) throws Exception {
        
        if ( tempImageFilePath.equals("")) { //Get temporary file path
            File findDir = File.createTempFile("temp-file-name", ".tmp"); 
            String path = findDir.getAbsolutePath();
            String tempFilePath = path.substring(0,path.lastIndexOf(File.separator));
            tempImageFilePath = tempFilePath;
        }
        
        File tempMapFile = new File(tempImageFilePath + File.separator + filename);
        BufferedImage mapImage = ImageIO.read(tempMapFile); 
        resp.setContentType("image/png");
        resp.setHeader("Content-Disposition", "attachment; filename="+filename);
        ImageIO.write(mapImage, PNG, resp.getOutputStream());
        resp.flushBuffer();        
    }

    protected Map<Integer, FutureImageOrTiles> requestAllLayers( List<PrintLayer> printLayers ) {
        
        Future<BufferedImage>[][] backgroundTilesFuture = null;
        Future<BufferedImage> overlayFuture = null;
        Map<Integer, FutureImageOrTiles> layerMap = 
                new HashMap<Integer, FutureImageOrTiles>(printLayers.size());
        
        for ( int i=0; i< printLayers.size(); i++ ) {
            //assert gridArray.size % columnSize == 0
            PrintLayer printLayer = printLayers.get(i);
            if ( printLayer.getColumnSize() > 1) {                
                try {
                    backgroundTilesFuture = tilesUtil.getTiledImage( printLayer);
                    FutureImageOrTiles tiles = new FutureImageOrTiles();
                    tiles.setTiles(backgroundTilesFuture);
                    layerMap.put(i, tiles);
                } catch( IOException ioe) {
                    String errorMsg = printLayer.getKartlagTitle() + " err:"+ ioe.getMessage();
                    printLayer.setKartlagTitle(errorMsg);
                    LOGGER.error("tiles io exception:"+errorMsg);
                    ioe.printStackTrace();
                }
            } else {
                try {
                    overlayFuture = tilesUtil.requestImage( printLayer.getUrl() );
                    FutureImageOrTiles overlay = new FutureImageOrTiles();
                    overlay.setImage(overlayFuture);
                    layerMap.put(i, overlay);
                } catch(IOException ioe) {
                    String errorMsg = printLayer.getKartlagTitle() + " err:"+ ioe.getMessage();
                    printLayer.setKartlagTitle(errorMsg);
                    LOGGER.error("overlay io exception:"+errorMsg);
                    ioe.printStackTrace();
                }
            }
        }
        return layerMap;
    }
}
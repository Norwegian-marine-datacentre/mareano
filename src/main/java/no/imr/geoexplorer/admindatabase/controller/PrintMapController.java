package no.imr.geoexplorer.admindatabase.controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;

import no.imr.geoexplorer.admindatabase.dao.MareanoAdminDbDao;
import no.imr.geoexplorer.printmap.PrintedMapUtils;
import no.imr.geoexplorer.printmap.TilesToImage;
import no.imr.geoexplorer.printmap.json.pojo.BoundingBox;
import no.imr.geoexplorer.printmap.json.pojo.PrintLayer;
import no.imr.geoexplorer.printmap.json.pojo.PrintLayerList;
import no.imr.geoexplorer.printmap.pojo.FutureImageOrTiles;
import no.imr.geoexplorer.printmap.pojo.ImageFilenameResponse;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PrintMapController {
    
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(PrintMapController.class);
    
    @Autowired
    private TilesToImage tilesUtil;
    
    public void setTilesToImage(TilesToImage tilesUtil) {
        this.tilesUtil = tilesUtil;
    }
    
    @Autowired(required = true)
    private MareanoAdminDbDao dao;
    
    @Autowired
    private ApplicationContext appContext;
    
    @Autowired
    private PrintedMapUtils printedMapUtils;
    
    @RequestMapping(value="/postMapImage", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    protected @ResponseBody ImageFilenameResponse postMapImage( @RequestBody PrintLayerList pll, HttpServletResponse resp) throws Exception {
        
        Long startTime = System.currentTimeMillis();
        System.out.println("startTime:"+startTime);
        
        List<PrintLayer> printLayers = pll.getPrintlayers();
        int width = pll.getWidth();
        int height = pll.getHeight();
        
        BufferedImage mapImage = null;
        Future<BufferedImage>[][] backgroundTilesFuture = null;
        Future<BufferedImage> overlayFuture = null;
        Map<Integer, FutureImageOrTiles> layerMap = 
                new HashMap<Integer, FutureImageOrTiles>(printLayers.size());
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
                try {
                    System.out.println("tiledImage:"+printLayer.getKartlagTitle()+" "+(System.currentTimeMillis()-startTime));
                    backgroundTilesFuture = getTiledImage( printLayer);
                    FutureImageOrTiles tiles = new FutureImageOrTiles();
                    tiles.setTiles(backgroundTilesFuture);
                    layerMap.put(i, tiles);
                } catch( IOException ioe) {
                    String errorMsg = printLayer.getKartlagTitle() + " err:"+ ioe.getMessage();
                    printLayer.setKartlagTitle(errorMsg);
                    LOG.error(errorMsg);
                    ioe.printStackTrace();
                }
            } else {
                try {
                    System.out.println("overlay:"+printLayer.getKartlagTitle()+" "+(System.currentTimeMillis()-startTime));
                    System.out.println("url overlay:"+printLayer.getUrl() );
                    overlayFuture = getOverlay( printLayer.getUrl() );
                    FutureImageOrTiles overlay = new FutureImageOrTiles();
                    overlay.setImage(overlayFuture);
                    layerMap.put(i, overlay);
                } catch(IOException ioe) {
                    String errorMsg = printLayer.getKartlagTitle() + " err:"+ ioe.getMessage();
                    printLayer.setKartlagTitle(errorMsg);
                    LOG.error(errorMsg);
                    ioe.printStackTrace();
                }
            }
        }
        
        for ( int i=0; i < printLayers.size(); i++ ) {
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
                    overlayFuture = null;
                }
            } catch (ExecutionException ee) {
                PrintLayer pl = printLayers.get(i);
                String title = pl.getKartlagTitle();
                pl.setKartlagTitle(title + " "+ ee.getMessage());
            }
        }
        
        System.out.println("crop:"+(System.currentTimeMillis()-startTime));
        mapImage = tilesUtil.cropImage( mapImage, position, width, height );
        System.out.println("legend:"+(System.currentTimeMillis()-startTime));
        mapImage = printedMapUtils.writeLegend( mapImage, printLayers );
        System.out.println("northarrow:"+(System.currentTimeMillis()-startTime));
        mapImage = addNorthArrow( mapImage );
        
        mapImage = printedMapUtils.addScaleBar( mapImage, pll);
        
        String fileName = "printMap";        
        File temp = File.createTempFile(fileName, "."+PNG);
        System.out.println("BeforeSave image:"+(System.currentTimeMillis()-startTime));
        ImageIO.write(mapImage, PNG, temp);
        System.out.println("AfterSave image:"+(System.currentTimeMillis()-startTime));
        //System.out.println("filename:"+temp.getAbsolutePath()+" name:"+temp.getName());

        ImageFilenameResponse respJson = new ImageFilenameResponse();
        respJson.setFilename(temp.getName());
        System.out.println("complete:"+(System.currentTimeMillis()-startTime));
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
    
    private final static String NORTH_ARROW = "northArrow.png";
    
    protected BufferedImage addNorthArrow( BufferedImage mapImage ) throws Exception {
        Resource resource = appContext.getResource("classpath:"+NORTH_ARROW);
        BufferedImage northArrow = ImageIO.read(resource.getFile());
        mapImage = printedMapUtils.writeNorthArrow(mapImage, northArrow);
        return mapImage;
    }
        
    protected Future<BufferedImage>[][] getTiledImage( PrintLayer printLayer ) throws Exception {

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
        Future<BufferedImage>[][] tileSet = new Future[rows][columnSize];
        int k = 0;
        int j = 0;
        for ( int i=0; i< gridArray.size(); i++) {
            k = i % columnSize;
            j = i / columnSize;
            gridSet[j][k] = gridArray.get(i).toString();
            urlSet[j][k] = url + "&BBOX=" + gridSet[j][k];
            Future<BufferedImage> aTile = tilesUtil.requestImage(urlSet[j][k]);
            tileSet[j][k] = aTile;
        }
        return tileSet;
    }
    
    
    public Future<BufferedImage> getOverlay( String url ) throws IllegalArgumentException, IOException {
        
        Future<BufferedImage> overlay = tilesUtil.requestImage( url );
        return overlay;

    }
}

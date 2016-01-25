package no.imr.geoexplorer.admindatabase.controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;

import no.imr.geoexplorer.admindatabase.dao.MareanoAdminDbDao;
import no.imr.geoexplorer.admindatabase.mybatis.pojo.KartlagEnNo;
import no.imr.geoexplorer.printmap.PrintedMapUtils;
import no.imr.geoexplorer.printmap.TilesToImage;
import no.imr.geoexplorer.printmap.pojo.BoundingBox;
import no.imr.geoexplorer.printmap.pojo.ImageFilenameResponse;
import no.imr.geoexplorer.printmap.pojo.PrintLayer;
import no.imr.geoexplorer.printmap.pojo.PrintLayerList;

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
    
//    @RequestMapping(value="/postMapImage", method = RequestMethod.POST)
//    public @ResponseBody ImageFilenameResponse postMapImage( @RequestParam("printImage") String jsonOfPrintLayer, HttpServletResponse resp) throws Exception {
//        
//        String decodeJson = URLDecoder.decode(jsonOfPrintLayer, "utf-8"); 
//        PrintLayerList pll = 
//                new ObjectMapper().readValue(decodeJson, PrintLayerList.class);
//        return getMapImage( pll, resp);
//    }
    
    @RequestMapping(value="/postMapImage", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    protected @ResponseBody ImageFilenameResponse postMapImage( @RequestBody PrintLayerList pll, HttpServletResponse resp) throws Exception {
        
        Long startTime = System.currentTimeMillis();
        System.out.println("startTime:"+startTime);
        
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
                    System.out.println("sysout: OVERWRITING BACKGROUND IMAGE");
                    LOG.error("OVERWRITING BACKGROUND IMAGE");
                }
                
                try {
                    System.out.println("tiledImage:"+printLayer.getKartlagTitle()+" "+(System.currentTimeMillis()-startTime));
                    backgroundImage = getTiledImage( mapImage, printLayer);
                } catch( IOException ioe) {
                    printLayer.setKartlagTitle(printLayer.getKartlagTitle() + " err:"+ ioe.getMessage());
                }
            } else {
                if (overlayImage != null) {
                    LOG.error("OVERWRITING OVERLAY IMAGE");
                }
                try {
                    System.out.println("overlay:"+printLayer.getKartlagTitle()+" "+(System.currentTimeMillis()-startTime));
                    System.out.println("url overlay:"+printLayer.getUrl() );
                    overlayImage = getOverlay( printLayer.getUrl() );
                } catch(IOException ioe) {
                    printLayer.setKartlagTitle(printLayer.getKartlagTitle() + " err:"+ ioe.getMessage());
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
        
        if ( tempImageFilePath.equals("")) {
            //Get tempropary file path
            File temp2 = File.createTempFile("temp-file-name", ".tmp"); 
            System.out.println("Temp file : " + temp2.getAbsolutePath());
            
            String absolutePath = temp2.getAbsolutePath();
            String tempFilePath = absolutePath.
                substring(0,absolutePath.lastIndexOf(File.separator));
            
            tempImageFilePath = tempFilePath;
            System.out.println(tempImageFilePath + File.separator + filename);
        }
        
        File temp = new File(tempImageFilePath + File.separator + filename);
        BufferedImage mapImage = ImageIO.read(temp); 
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

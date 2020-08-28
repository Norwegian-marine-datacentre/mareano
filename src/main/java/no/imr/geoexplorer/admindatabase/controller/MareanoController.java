package no.imr.geoexplorer.admindatabase.controller;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import no.imr.geoexplorer.admindatabase.dao.MareanoAdminDbDao;
import no.imr.geoexplorer.admindatabase.jsp.pojo.HovedtemaVisning;
import no.imr.geoexplorer.admindatabase.jsp.pojo.KartbilderVisning;
import no.imr.geoexplorer.admindatabase.jsp.pojo.KartlagVisning;
import no.imr.geoexplorer.admindatabase.mybatis.pojo.Hovedtema;
import no.imr.geoexplorer.admindatabase.mybatis.pojo.HovedtemaEnNo;
import no.imr.geoexplorer.admindatabase.mybatis.pojo.KartBilderEnNo;
import no.imr.geoexplorer.admindatabase.mybatis.pojo.Kartbilder;
import no.imr.geoexplorer.admindatabase.mybatis.pojo.Kartlag;
import no.imr.geoexplorer.admindatabase.mybatis.pojo.KartlagEnNo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * Gets data from the mareano admin database and returns jsp friendly pojos with
 * the mav. An update to the database is sent if it last was updated more than
 * one day ago.
 *
 * @author endrem
 */
@Controller
public class MareanoController {
	private static final Logger LOGGER = LoggerFactory.getLogger(MareanoController.class);
	
    private long mavLastUpdatedNo = new Date().getTime();
    private long mavLastUpdatedEn = new Date().getTime();
    private long mavLastUpdatedPolar = new Date().getTime();
    private long mavLastUpdatedPolarEn = new Date().getTime();

    private final static long ONEHOUR = 60 * 60 * 1000;
    private final static String ENGLISH = "en";
    private final static String TEST_SERVER = "webtest1.nodc.no";
    private final static String HOVEDTEMA_NOT_IN_PRODUCTION = "Under utvikling";
    
    private final static String URL_MAREANO_EN = "http://www.mareano.no/en";
    private final static String URL_MAREANO = "http://www.mareano.no/";
    
    private final static String UTM33 = "EPSG:32633";
    private final static Double UTM33_MAX_RESOLUTION = 10832.0;
    private final static double[] UTM33_BBOX = {-2500000.0,3500000.0,3045984.0,9045984.0};
    private final static double[] UTM33_CENTER = {450000, 7550000};
    
    private final static String POLAR = "EPSG:3575";
    private final static Double POLAR_MAX_RESOLUTION = 38197.92815;
    private final static double[] POLAR_BBOX = {-4889334.802954878,-4889334.802954878,4889334.802954878,4889334.802954878};
    private final static double[] POLAR_CENTER = {-433382.43932, -2457833.949055};
    
    private final static String NO = "no";
    private final static String EN = "en";
    
    private final static String POLAR_BACKGROUND_GROUP = "backgroundPolarLand";
    private final static String POLAR_BACKGROUND_GROUP_SEA = "backgroundPolarSea";
    private final static String HOVEDTEMA_BACKGROUND = "Bakgrunnskart";
    
    //html page the request comes from
    private final static String MAREANO_POLAR_JSP = "mareanoPolar";
    private final static String MAREANO_POLAR_EN_JSP = "mareanoPolar_en";
    private final static String MAREANO_JSP = "mareano";
    private final static String MAREANO_EN_JSP = "mareano_en";
    
    
    //mareano stasjoner - default visible 'kartbilde' in json
    private final static String MAREANO_STASJONER = "MAREANO-stasjoner";
    private final static String MAREANO_STATIONS = "MAREANO-stations";
    private final static String MAREANO_OVERSIKTSKART = "MAREANO-oversiktskart";
    private final static String MAREANO_OVERVIEW = "MAREANO - overview";
    
    @Autowired(required = true)
    private MareanoAdminDbDao dao;

    private ModelAndView mavNo = null;
    private ModelAndView mavEn = null;
    private ModelAndView mavPolar = null;
    private ModelAndView mavPolarEn = null;
    
    @RequestMapping(value = "/viewer", method = RequestMethod.GET)
    public String getViewer(HttpServletResponse resp)  {
    	return "viewer";
    }
    
    
    @RequestMapping(value = "/update", method = RequestMethod.GET)
    public ModelAndView updateMareano(HttpServletResponse resp) throws IOException {
    	mavNo = null;
    	mavEn = null;
    	mavPolar = null;
    	mavPolarEn = null;
    	return getMareano( "", resp );
    }
    
    @RequestMapping(value = "/mareano", method = RequestMethod.GET)
    public ModelAndView getMareano(
    		@RequestParam(value = "selectedLayers", required=false) String selectedLayers,
    		HttpServletResponse resp) throws IOException {

        if (mavNo == null || (System.currentTimeMillis() - mavLastUpdatedNo) > ONEHOUR) {
            mavNo = commonGetMareano(resp, NO, MAREANO_JSP); 
            mavLastUpdatedNo = new Date().getTime();
        } 
        defaultLayersOnOrOff( selectedLayers, mavNo );
        UTM33Config(mavNo);
        return mavNo;
    }
    
    @RequestMapping(value = "/mareano_en", method = RequestMethod.GET)
    public ModelAndView getMareanoEn(
    		@RequestParam(value = "selectedLayers", required=false) String selectedLayers,
    		HttpServletResponse resp) throws IOException {
    	
        if (mavEn == null || (System.currentTimeMillis() - mavLastUpdatedEn) > ONEHOUR) {
            mavEn = commonGetMareano(resp, EN, MAREANO_EN_JSP); 
            mavLastUpdatedEn = new Date().getTime();
        } 
        defaultLayersOnOrOff( selectedLayers, mavEn );
        UTM33Config(mavEn);
        return mavEn;
    }
    
    @RequestMapping(value = "/mareanoPolar", method = RequestMethod.GET)
    public ModelAndView getMareanoPolar(
    		@RequestParam(value = "selectedLayers", required=false) String selectedLayers,
    		HttpServletResponse resp) throws IOException {

        if (mavPolar == null || (System.currentTimeMillis() - mavLastUpdatedPolar) > ONEHOUR) {
            mavPolar = commonGetMareano(resp, NO, MAREANO_POLAR_JSP); 
            mavLastUpdatedPolar = new Date().getTime();
        } 
        defaultLayersOnOrOff( selectedLayers, mavPolar );
        polarConfig(mavPolar);
        return mavPolar;
    }
    
    @RequestMapping(value = "/mareanoPolar_en", method = RequestMethod.GET)
    public ModelAndView getMareanoPolarEn(
    		@RequestParam(value = "selectedLayers", required=false) String selectedLayers,
    		HttpServletResponse resp) throws IOException {

        if (mavPolarEn == null || (System.currentTimeMillis() - mavLastUpdatedPolarEn) > ONEHOUR) {
            mavPolarEn = commonGetMareano(resp, EN, MAREANO_POLAR_EN_JSP); 
            mavLastUpdatedPolarEn = new Date().getTime();
        } 
        defaultLayersOnOrOff( selectedLayers, mavPolarEn );
        
        polarConfig(mavPolarEn);
        return mavPolarEn;
    }
    
    private void defaultLayersOnOrOff( String selectedLayers, ModelAndView mav ) throws JsonProcessingException {
    	List<HovedtemaVisning> visninger = (List<HovedtemaVisning>) mav.getModel().get("tmpHovedTema");
    	boolean isChanged = false;
        if ( selectedLayers != null && !selectedLayers.equals("") ) {
        	isChanged = changedDisplayDefaultLayers( false, visninger );
        } else {
        	isChanged = changedDisplayDefaultLayers( true, visninger );
        }
    	if ( isChanged ) {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(visninger);
            mav.addObject("hovedtemaer_json", json);                
    	}
//        mav.getModel().remove("tmpHovedTema");  
    	mav.getModel().remove("selectedLayers"); 
    	mav.addObject("selectedLayers", selectedLayers); 
    }
    
    private boolean changedDisplayDefaultLayers( boolean toDisplay, List<HovedtemaVisning> hovedtemaVisninger ) {
    	
    	boolean changedDisplay = false;
    	for (HovedtemaVisning hovedtema : hovedtemaVisninger) {
	    	for (KartbilderVisning kartbilderVisning : hovedtema.getBilder()) {
	    		String groupName = kartbilderVisning.getGruppe().trim(); 
	            if (groupName.equals( MAREANO_STASJONER ) || groupName.equals( MAREANO_STATIONS )) {
	            	LOGGER.debug("MAREANO-stasjoner _ kartbilderVisning:" + kartbilderVisning.isVisible() + " toDisplay:"+toDisplay);
	            	if ( kartbilderVisning.isVisible() != toDisplay) {
	            		kartbilderVisning.setVisible( toDisplay ); //default = true
	            		changedDisplay = true;
	            	}	                
	            }
	            /**
	             * Dont load mareano_oversiktskart - get it from saved map
	             */
	            /*
	            if (groupName.equals( MAREANO_OVERSIKTSKART ) || groupName.equals( MAREANO_OVERVIEW )) {
	            	if ( kartbilderVisning.isVisible() != toDisplay) {
	            		kartbilderVisning.setVisible( toDisplay ); //default = true
	            		changedDisplay = true;
	            	}	                
	            }
	            */	            
	    	}
    	}
    	return changedDisplay;
    }
    
    private void UTM33Config(ModelAndView mav) {
        mav.addObject("projection", UTM33);
        mav.addObject("maxResolution", UTM33_MAX_RESOLUTION);
        mav.addObject("maxExtent", Arrays.toString(UTM33_BBOX));
        mav.addObject("center", Arrays.toString(UTM33_CENTER));
    }
    
    private void polarConfig(ModelAndView mav) {
        mav.addObject("projection", POLAR);
        mav.addObject("maxResolution", POLAR_MAX_RESOLUTION);
        mav.addObject("maxExtent", Arrays.toString(POLAR_BBOX));
        mav.addObject("center", Arrays.toString(POLAR_CENTER));
    }
    
    private ModelAndView commonGetMareano(HttpServletResponse resp, String language, String mareanoJSP) throws IOException {
    	
		ModelAndView mav = new ModelAndView(mareanoJSP);
		getMareano(mav, language, mareanoJSP);

		String heading = getMareanoHeading(language);
		mav.addObject("heading", heading);

		resp.setCharacterEncoding("UTF-8"); 
		return mav;    	
    }

    @RequestMapping("/mareanoJson")
    public @ResponseBody JsonNode getMareanoJson() throws IOException {
        List<HovedtemaVisning> visninger = listOrganizedToBrowser("no", MAREANO_JSP);
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(visninger);
        return mapper.readTree(json);
    }
    
    @RequestMapping("/mareanoPolarJson")
    public @ResponseBody JsonNode getMareanoPolarJson() throws IOException {
        
        List<HovedtemaVisning> visninger = listOrganizedToBrowser("no", MAREANO_POLAR_JSP);
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(visninger);

        return mapper.readTree(json);
    }
    
    protected ModelAndView getMareano(ModelAndView mav, String language, String mareanoJSP) throws IOException {
        
        List<HovedtemaVisning> visninger = listOrganizedToBrowser(language, mareanoJSP);
        mav.addObject("tmpHovedTema", visninger);
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(visninger);

        mav.addObject("hovedtemaer_json", json);
        return mav;
    }

    protected List<HovedtemaVisning> listOrganizedToBrowser(String language, String mareanoJSP) throws IOException{

        List<Hovedtema> hovedtemaer = dao.getHovedtemaer();
        List<HovedtemaVisning> hovedtemaVisninger = new ArrayList<HovedtemaVisning>(hovedtemaer.size());

        String hostname =InetAddress.getLocalHost().getHostName();
        for (Hovedtema hovedtema : hovedtemaer) {
        	
        	if ( hostname.equals(TEST_SERVER) ) { //for webtest1
        		addToList(hovedtemaVisninger, hovedtema, language, mareanoJSP);
            } else if (!hovedtema.getGenericTitle().equals(HOVEDTEMA_NOT_IN_PRODUCTION) ) { //for prod 
            	addToList(hovedtemaVisninger, hovedtema, language, mareanoJSP);
            }
        }
        return hovedtemaVisninger;
    }

    protected List<HovedtemaVisning> addToList(List<HovedtemaVisning> hovedtemaVisninger, Hovedtema hovedtema, String language, String mareanoJSP) {
    	
        HovedtemaVisning hovedtemaVisning = new HovedtemaVisning();
        if (language.equals("en")) {
            List<HovedtemaEnNo> en = dao.getHovedtemaEn(hovedtema.getHovedtemaerId());
            if (en.size() > 0) {
                hovedtemaVisning.setHovedtema(en.get(0).getAlternateTitle());
                hovedtemaVisning.setAbstracts(en.get(0).getAbstracts());
            } else {
                hovedtemaVisning.setHovedtema(hovedtema.getGenericTitle());
            }
        } else {
            List<HovedtemaEnNo> norsk = dao.getHovedtemaNo(hovedtema.getHovedtemaerId());
            if (norsk.size() > 0) {
                hovedtemaVisning.setHovedtema(norsk.get(0).getAlternateTitle());
                hovedtemaVisning.setAbstracts(norsk.get(0).getAbstracts());
            } else {
                hovedtemaVisning.setHovedtema(hovedtema.getGenericTitle());
            }
        }

        for (Kartbilder kartbilde : hovedtema.getKartbilder()) {
            KartbilderVisning kartbilderVisning = new KartbilderVisning();

            if (language.equals("en")) {
                List<KartBilderEnNo> en = dao.getKartbilderEn(kartbilde.getKartbilderId());
                if (en.size() > 0) {
                    kartbilderVisning.setGruppe(en.get(0).getAlternateTitle());
                    kartbilderVisning.setAbstracts(en.get(0).getAbstracts());
                } else {
                    kartbilderVisning.setGruppe(kartbilde.getGenericTitle());
                }
            } else {
                List<KartBilderEnNo> norsk = dao.getKartbilderNo(kartbilde.getKartbilderId());
                if (norsk.size() > 0) {
                    kartbilderVisning.setGruppe(norsk.get(0).getAlternateTitle());
                    kartbilderVisning.setAbstracts(norsk.get(0).getAbstracts());
                } else {
                    kartbilderVisning.setGruppe(kartbilde.getGenericTitle());
                }
            }
            
            if ( hovedtema.getGenericTitle().equals(HOVEDTEMA_BACKGROUND)) {
                if ( (mareanoJSP.equals(MAREANO_POLAR_JSP) || mareanoJSP.equals(MAREANO_POLAR_EN_JSP) ) && 
                		( kartbilderVisning.getGruppe().equals(POLAR_BACKGROUND_GROUP) ||
                				kartbilderVisning.getGruppe().equals(POLAR_BACKGROUND_GROUP_SEA) ) ) {
                    addGroupAndLayers(kartbilderVisning, kartbilde, mareanoJSP, language);
                    hovedtemaVisning.addBilder(kartbilderVisning);
                } else if ( (mareanoJSP.equals(MAREANO_JSP) || mareanoJSP.equals(MAREANO_EN_JSP) ) && 
                		!kartbilderVisning.getGruppe().equals(POLAR_BACKGROUND_GROUP) &&
                		!kartbilderVisning.getGruppe().equals(POLAR_BACKGROUND_GROUP_SEA) ) {
                    addGroupAndLayers(kartbilderVisning, kartbilde, mareanoJSP, language);
                    hovedtemaVisning.addBilder(kartbilderVisning);
                }
            } else {
                addGroupAndLayers(kartbilderVisning, kartbilde, mareanoJSP, language);
                hovedtemaVisning.addBilder(kartbilderVisning);
            }

        }
        if (hovedtemaVisning.getBilder().size() > 0) {
            hovedtemaVisninger.add(hovedtemaVisning);
        }
        return hovedtemaVisninger;
    }
    
    protected void addGroupAndLayers(KartbilderVisning kartbilderVisining, Kartbilder kartbilde, String mareanoJSP, String language) {
        
        kartbilderVisining.setStartextentMaxx( kartbilde.getStartextentMaxx() );
        kartbilderVisining.setStartextentMaxy( kartbilde.getStartextentMaxy() );
        kartbilderVisining.setStartextentMinx( kartbilde.getStartextentMinx() );
        kartbilderVisining.setStartextentMiny( kartbilde.getStartextentMiny() );

        if (kartbilderVisining.getGruppe().equals( MAREANO_STASJONER ) || kartbilderVisining.getGruppe().equals( MAREANO_STATIONS )) {
            kartbilderVisining.setVisible( true ); //default = true
        }
        List<Kartlag> kartlagene = dao.getKartlagene(kartbilde.getKartbilderId());
        for (Kartlag kartlag : kartlagene) {
            if (kartlag.isAvailable()) {
                KartlagVisning kart = new KartlagVisning();
                kart.setId(kartlag.getKartlagId());
                kart.setLayers(kartlag.getLayers());
                kart.setKeyword(kartlag.getKeyword());
                if ( mareanoJSP.equals( MAREANO_POLAR_JSP ) || mareanoJSP.equals( MAREANO_POLAR_EN_JSP )) {
                    kart.setExGeographicBoundingBoxEastBoundLongitude(kartlag.getEastPolar());
                    kart.setExGeographicBoundingBoxWestBoundLongitude(kartlag.getWestPolar());
                    kart.setExGeographicBoundingBoxNorthBoundLatitude(kartlag.getNorthPolar());
                    kart.setExGeographicBoundingBoxSouthBoundLatitude(kartlag.getSouthPolar());
                    kart.setScalemin(kartlag.getScaleminPolar());
                    kart.setScalemax(kartlag.getScalemaxPolar());                            
                } else {
                    kart.setExGeographicBoundingBoxEastBoundLongitude(kartlag.getExGeographicBoundingBoxEastBoundLongitude());
                    kart.setExGeographicBoundingBoxWestBoundLongitude(kartlag.getExGeographicBoundingBoxWestBoundLongitude());
                    kart.setExGeographicBoundingBoxNorthBoundLatitude(kartlag.getExGeographicBoundingBoxNorthBoundLatitude());
                    kart.setExGeographicBoundingBoxSouthBoundLatitude(kartlag.getExGeographicBoundingBoxSouthBoundLatitude());
                    kart.setScalemin(kartlag.getScalemin());
                    kart.setScalemax(kartlag.getScalemax());
                }
                kart.setQueryable(kartlag.isQueryable());
                kart.setGruppe( kartbilderVisining.getGruppe());

                if (language.equals("en")) {
                    List<KartlagEnNo> en = dao.getKartlagEn(kart.getId());                 
                    if (en.size() > 0) {
                        kart.setTitle(en.get(0).getAlternateTitle());
                        kart.setTitleTooltip(en.get(0).getTitle());
                        kart.setAbstracts(en.get(0).getAbstracts());       
                    } else {
                        kart.setTitle(kartlag.getGenericTitle());
                    }
                } else {
                    List<KartlagEnNo> norsk = dao.getKartlagNo(kart.getId());
                 
                    if (norsk.size() > 0) {
                        kart.setTitle(norsk.get(0).getAlternateTitle());
                        kart.setTitleTooltip(norsk.get(0).getTitle());
                        kart.setAbstracts(norsk.get(0).getAbstracts());     
                    } else {
                        kart.setTitle(kartlag.getGenericTitle());
                    }
                }

                kart.setUrl(kartlag.getKarttjeneste().getUrl());
                kart.setFormat(kartlag.getKarttjeneste().getFormat());
                kartbilderVisining.addKart(kart);
            }
        }
    }

    protected String getMareanoHeading(String language) {
        StringBuffer heading = new StringBuffer();
        try {
            URL url = null;
            if (language.equals(ENGLISH)) {
                url = new URL(URL_MAREANO_EN);  
            } else {
                url = new URL(URL_MAREANO); 
            }
    	    BufferedReader reader;
    	    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    	    if (connection.getResponseMessage().equals("Not Implemented")){
    	        DataInputStream inStream = new DataInputStream(connection.getErrorStream());
    	        reader = new BufferedReader(new InputStreamReader(inStream));
    	    } else {
    	        reader = new BufferedReader(new InputStreamReader(url.openStream()));
    	    }
    	            
            String line;
            boolean headerContent = false;
            while ((line = reader.readLine()) != null) {
                if (line.contains("<!--mainmenustart-->")) {
                    headerContent = true;
                }
                if (headerContent) {
                    heading.append(line);
                }
                if (line.contains("<!--mainmenuend-->")) {
                    headerContent = false;
                }
            }
    	    connection.disconnect();
    	    reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String someHeading = "<table width=\"100%\" cellspacing=\"0\" border=\"0\"><tr height=\"45\"> "
                + "<td valign=\"middle\" height=\"45\" style=\"background-image:url(http://www.mareano.no/kart/images/top/ny_heading_397.gif); background-repeat: repeat;\"> "
                + "<a style=\"text-decoration: none\" target=\"_top\" href=\"http://www.mareano.no\"> "
                + "<img border=\"0\" alt=\"MAREANO<br>samler kunnskap om havet\" src=\"http://www.mareano.no/kart/images/top/ny_logo.gif\"> "
                + "</a> "
                + "</td> "
                + "<td width=\"627\" align=\"right\" height=\"45\" style=\"background-image:url(http://www.mareano.no/kart/images/top/ny_heading_627.gif);\"> </td> "
                + "</tr></table>";

        String newHeading = heading.toString();
        newHeading = newHeading.replaceAll("href=\"/", "href=\"http://www.mareano.no/");
        return someHeading + newHeading;
    }
}
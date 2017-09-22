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

import javax.imageio.ImageIO;
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

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

    private long mavLastUpdatedNo = new Date().getTime();
    private long mavLastUpdatedEn = new Date().getTime();
    private long mavLastUpdatedPolar = new Date().getTime();
    private long mavLastUpdatedPolarEn = new Date().getTime();

    private final static long ONEHOUR = 60 * 60 * 1000;
    private final static String ENGLISH = "en";
    private final static String TEST_SERVER = "webtest1.nodc.no";
    private final static String HOVEDTEMA_NOT_IN_PRODUCTION = "Under utvikling";
    
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
    private final static String POLAR_VIEW = "mareanoPolar";
    private final static String POLAR_EN_VIEW = "mareanoPolar_en";
    private final static String VIEW = "mareano";
    private final static String VIEW_EN = "mareano_en";
    private final static String HOVEDTEMA_BACKGROUND = "Bakgrunnskart";
    
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
    	return getMareano( resp );
    }
    
    @RequestMapping(value = "/mareano", method = RequestMethod.GET)
    public ModelAndView getMareano(HttpServletResponse resp) throws IOException {
        if (mavNo == null || (System.currentTimeMillis() - mavLastUpdatedNo) > ONEHOUR) {
            mavNo = commonGetMareano(resp, NO, "mareano");
            mavLastUpdatedNo = new Date().getTime();
        } 
        UTM33Config(mavNo);
        return mavNo;
    }
    
    @RequestMapping(value = "/mareano_en", method = RequestMethod.GET)
    public ModelAndView getMareanoEn(HttpServletResponse resp) throws IOException {
        if (mavEn == null || (System.currentTimeMillis() - mavLastUpdatedEn) > ONEHOUR) {
            mavEn = commonGetMareano(resp, EN, "mareano_en");
            mavLastUpdatedEn = new Date().getTime();
        } 
        UTM33Config(mavEn);
        return mavEn;
    }
    
    @RequestMapping(value = "/mareanoPolar", method = RequestMethod.GET)
    public ModelAndView getMareanoPolar(HttpServletResponse resp) throws IOException {
        if (mavPolar == null || (System.currentTimeMillis() - mavLastUpdatedPolar) > ONEHOUR) {
            mavPolar = commonGetMareano(resp, NO, "mareanoPolar");
            mavLastUpdatedPolar = new Date().getTime();
        } 
        polarConfig(mavPolar);
        return mavPolar;
    }
    
    @RequestMapping(value = "/mareanoPolar_en", method = RequestMethod.GET)
    public ModelAndView getMareanoPolarEn(HttpServletResponse resp) throws IOException {
        if (mavPolarEn == null || (System.currentTimeMillis() - mavLastUpdatedPolarEn) > ONEHOUR) {
            mavPolarEn = commonGetMareano(resp, EN, "mareanoPolar_en");
            mavLastUpdatedPolarEn = new Date().getTime();
        } 
        polarConfig(mavPolarEn);
        return mavPolarEn;
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

		resp.setCharacterEncoding("UTF-8"); //todo: remove
		return mav;    	
    }

    @RequestMapping("/mareanoJson")
    public @ResponseBody JsonNode getMareanoJson() throws IOException {
        List<HovedtemaVisning> visninger = listOrganizedToBrowser("no", VIEW);
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(visninger);
        return mapper.readTree(json);
    }
    
    @RequestMapping("/mareanoPolarJson")
    public @ResponseBody JsonNode getMareanoPolarJson(  ) throws IOException {
        
        List<HovedtemaVisning> visninger = listOrganizedToBrowser("no", POLAR_VIEW);
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(visninger);

        return mapper.readTree(json);
    }
    
    protected ModelAndView getMareano(ModelAndView mav, String language, String mareanoJSP) throws IOException {
        
        List<HovedtemaVisning> visninger = listOrganizedToBrowser(language, mareanoJSP);
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(visninger);

        mav.addObject("hovedtemaer_json", json);
        return mav;
    }

    protected List<HovedtemaVisning> listOrganizedToBrowser(String language, String mareanoJSP)  throws IOException{

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
            } else {
                hovedtemaVisning.setHovedtema(hovedtema.getGenericTitle());
            }
        } else {
            List<HovedtemaEnNo> norsk = dao.getHovedtemaNo(hovedtema.getHovedtemaerId());
            if (norsk.size() > 0) {
                hovedtemaVisning.setHovedtema(norsk.get(0).getAlternateTitle());
            } else {
                hovedtemaVisning.setHovedtema(hovedtema.getGenericTitle());
            }
        }

        
        for (Kartbilder kartbilde : hovedtema.getKartbilder()) {
            KartbilderVisning kartbilderVisining = new KartbilderVisning();

            if (language.equals("en")) {
                List<KartBilderEnNo> en = dao.getKartbilderEn(kartbilde.getKartbilderId());
                if (en.size() > 0) {
                    kartbilderVisining.setGruppe(en.get(0).getAlternateTitle());
                } else {
                    kartbilderVisining.setGruppe(kartbilde.getGenericTitle());
                }
            } else {
                List<KartBilderEnNo> norsk = dao.getKartbilderNo(kartbilde.getKartbilderId());
                if (norsk.size() > 0) {
                    kartbilderVisining.setGruppe(norsk.get(0).getAlternateTitle());
                } else {
                    kartbilderVisining.setGruppe(kartbilde.getGenericTitle());
                }
            }
            
            if ( hovedtema.getGenericTitle().equals(HOVEDTEMA_BACKGROUND)) {
                if ( (mareanoJSP.equals(POLAR_VIEW) || mareanoJSP.equals(POLAR_EN_VIEW) ) && 
                		( kartbilderVisining.getGruppe().equals(POLAR_BACKGROUND_GROUP) ||
                				kartbilderVisining.getGruppe().equals(POLAR_BACKGROUND_GROUP_SEA) ) ) {
                    addGroupAndLayers(kartbilderVisining, kartbilde, mareanoJSP, language);
                    hovedtemaVisning.addBilder(kartbilderVisining);
                } else if ( (mareanoJSP.equals(VIEW) || mareanoJSP.equals(VIEW_EN) ) && 
                		!kartbilderVisining.getGruppe().equals(POLAR_BACKGROUND_GROUP) &&
                		!kartbilderVisining.getGruppe().equals(POLAR_BACKGROUND_GROUP_SEA) ) {
                    addGroupAndLayers(kartbilderVisining, kartbilde, mareanoJSP, language);
                    hovedtemaVisning.addBilder(kartbilderVisining);
                }
            } else {
                addGroupAndLayers(kartbilderVisining, kartbilde, mareanoJSP, language);
                hovedtemaVisning.addBilder(kartbilderVisining);
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

        if (kartbilderVisining.getGruppe().equals("MAREANO-stasjoner") || kartbilderVisining.getGruppe().equals("MAREANO-stations")) {
            kartbilderVisining.setVisible(true);
        }
        List<Kartlag> kartlagene = dao.getKartlagene(kartbilde.getKartbilderId());
        for (Kartlag kartlag : kartlagene) {
            if (kartlag.isAvailable()) {
                KartlagVisning kart = new KartlagVisning();
                kart.setId(kartlag.getKartlagId());
                kart.setLayers(kartlag.getLayers());
                kart.setKeyword(kartlag.getKeyword());
                if ( mareanoJSP.equals("mareanoPolar") || mareanoJSP.equals("mareanoPolar_en")) {
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
                url = new URL("http://www.mareano.no/en");
            } else {
                url = new URL("http://www.mareano.no/");
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

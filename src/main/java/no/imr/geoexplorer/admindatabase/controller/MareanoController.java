package no.imr.geoexplorer.admindatabase.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.DataInputStream;
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

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

    private List<HovedtemaVisning> visninger = null;
    private long mavLastUpdatedNo = new Date().getTime();
    private long mavLastUpdatedEn = new Date().getTime();
    private long mavLastUpdatedPolar = new Date().getTime();

    private final static long ONEHOUR = 60 * 1000;
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
    
    @Autowired(required = true)
    private MareanoAdminDbDao dao;

    private ModelAndView mavNo = null;
    private ModelAndView mavEn = null;
    private ModelAndView mavPolar = null;
    
    @RequestMapping("/update")
    public ModelAndView updateMareano(HttpServletResponse resp) throws IOException {
    	mavNo = null;
    	mavEn = null;
    	mavPolar = null;
    	return getMareano( resp );
    }
    
    @RequestMapping("/mareano")
    public ModelAndView getMareano(HttpServletResponse resp) throws IOException {
        if (mavNo == null || (System.currentTimeMillis() - mavLastUpdatedNo) > ONEHOUR) {
            mavNo = commonGetMareano(resp, NO, "mareano");
            mavLastUpdatedNo = new Date().getTime();
        } 
        UTM33Config(mavNo);
        return mavNo;
    }
    
    @RequestMapping("/mareano_en")
    public ModelAndView getMareanoEn(HttpServletResponse resp) throws IOException {
        if (mavEn == null || (System.currentTimeMillis() - mavLastUpdatedEn) > ONEHOUR) {
            mavEn = commonGetMareano(resp, EN, "mareano_en");
            mavLastUpdatedEn = new Date().getTime();
        } 
        UTM33Config(mavEn);
        return mavEn;
    }
    
    @RequestMapping("/mareanoPolar")
    public ModelAndView getMareanoPolar(HttpServletResponse resp) throws IOException {
        if (mavPolar == null || (System.currentTimeMillis() - mavLastUpdatedPolar) > ONEHOUR) {
            mavPolar = commonGetMareano(resp, NO, "mareanoPolar");
            mavLastUpdatedPolar = new Date().getTime();
        } 
        polarConfig(mavPolar);
        return mavPolar;
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

    protected ModelAndView getMareano(ModelAndView mav, String language, String mareanoJSP) throws IOException {
        visninger = listOrganizedToBrowser(language, mareanoJSP);

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
                        if ( mareanoJSP.equals("mareanoPolar")) {
                            kart.setExGeographicBoundingBoxEastBoundLongitude(kartlag.getEastPolar());
                            kart.setExGeographicBoundingBoxWestBoundLongitude(kartlag.getWestPolar());
                            kart.setExGeographicBoundingBoxNorthBoundLatitude(kartlag.getNorthPolar());
                            kart.setExGeographicBoundingBoxSouthBoundLatitude(kartlag.getSoutPolar());
                            kart.setScalemin(kartlag.getScalemin());
                            kart.setScalemax(kartlag.getScalemax());                            
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
                                kart.setAbstracts(en.get(0).getAbstracts());
                            } else {
                                kart.setTitle(kartlag.getGenericTitle());
                            }
                        } else {
                            List<KartlagEnNo> norsk = dao.getKartlagNo(kart.getId());
                            if (norsk.size() > 0) {
                                kart.setTitle(norsk.get(0).getAlternateTitle());
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
                hovedtemaVisning.addBilder(kartbilderVisining);
            }
            if (hovedtemaVisning.getBilder().size() > 0) {
                hovedtemaVisninger.add(hovedtemaVisning);
            }
            return hovedtemaVisninger;
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

	    }
	    else {
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
    
//    @Value("${propertiesMsg_no.advanced}") 
//    private String test;
}

package no.imr.geoexplorer.admindatabase.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import no.imr.geoexplorer.admindatabase.dao.MareanoAdminDbDao;
import no.imr.geoexplorer.admindatabase.gml.XstreamConfig;
import no.imr.geoexplorer.admindatabase.gml.pojo.Attributes;
import no.imr.geoexplorer.admindatabase.gml.pojo.BoundedBy;
import no.imr.geoexplorer.admindatabase.gml.pojo.FeatureCollection;
import no.imr.geoexplorer.admindatabase.gml.pojo.FeatureMember;
import no.imr.geoexplorer.admindatabase.gml.pojo.Point;
import no.imr.geoexplorer.admindatabase.jsp.pojo.HovedtemaVisning;
import no.imr.geoexplorer.admindatabase.jsp.pojo.KartbilderVisning;
import no.imr.geoexplorer.admindatabase.jsp.pojo.KartlagInfos;
import no.imr.geoexplorer.admindatabase.jsp.pojo.KartlagVisning;
import no.imr.geoexplorer.admindatabase.jsp.pojo.LegendsInfo;
import no.imr.geoexplorer.admindatabase.jsp.pojo.SpesialpunktStatus;
import no.imr.geoexplorer.admindatabase.mybatis.pojo.Hovedtema;
import no.imr.geoexplorer.admindatabase.mybatis.pojo.HovedtemaEnNo;
import no.imr.geoexplorer.admindatabase.mybatis.pojo.KartBilderEnNo;
import no.imr.geoexplorer.admindatabase.mybatis.pojo.Kartbilder;
import no.imr.geoexplorer.admindatabase.mybatis.pojo.Kartlag;
import no.imr.geoexplorer.admindatabase.mybatis.pojo.KartlagEnNo;
import no.imr.geoexplorer.admindatabase.mybatis.pojo.KartlagInfo;
import no.imr.geoexplorer.admindatabase.mybatis.pojo.Legend;
import no.imr.geoexplorer.admindatabase.mybatis.pojo.Spesialpunkt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

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
	private long lastupdated = new Date().getTime();
//	private final static long ADAY = 24 * 60 * 60 * 1000;
	private final static long TENMIN = 10 * 1000;
	private final static String ENGLISH = "en";

	@Autowired(required = true)
	private MareanoAdminDbDao dao;

	@Autowired(required = true)
	private XstreamConfig xstream;

	@Autowired(required = true)
	private ApplicationContext ctx;

	@RequestMapping("/mareano")
	public ModelAndView getMareanoTest(HttpServletResponse resp) {
		ModelAndView mav = new ModelAndView("mareano");
		getMareano( mav, "no" );
		mav.addObject("heading", getMareanoHeading(""));

		resp.setCharacterEncoding("UTF-8");	
		return mav;
	}
	
	protected ModelAndView getMareano( ModelAndView mav, String language) {
		long now = new Date().getTime();		
		if (visninger == null || (lastupdated + TENMIN) < now) {
			visninger = listOrganizedToBrowser(language);
			lastupdated = new Date().getTime();
		}
		mav.addObject("hovedtemaer", visninger);
		return mav;
	}
	
	@RequestMapping("/mareano_en")
	public ModelAndView getMareanoEN(HttpServletResponse resp) {
		ModelAndView mav = new ModelAndView("mareano_en");
		mav = getMareano( mav, "en" );

		mav.addObject("heading", getMareanoHeading(ENGLISH));
		return mav;
	}

	protected List<HovedtemaVisning> listOrganizedToBrowser(String language) {

		List<Hovedtema> hovedtemaer = dao.getHovedtemaer();
		List<HovedtemaVisning> hovedtemaVisninger = new ArrayList<HovedtemaVisning>();

		for (Hovedtema hovedtema : hovedtemaer) {
			if (!hovedtema.getGenericTitle().equals("Under utvikling")) {

				HovedtemaVisning hovedtemaVisning = new HovedtemaVisning();

				if( language.equals("en") ) {
					List<HovedtemaEnNo> en = dao.getHovedtemaEn(hovedtema.getHovedtemaerId());
					if ( en.size() > 0 ) {
						hovedtemaVisning.setHovedtema(en.get(0).getAlternateTitle());
					} else hovedtemaVisning.setHovedtema(hovedtema.getGenericTitle()); 
				} else  {
					List<HovedtemaEnNo> norsk = dao.getHovedtemaNo(hovedtema.getHovedtemaerId());
					if ( norsk.size() > 0 ) {
						hovedtemaVisning.setHovedtema(norsk.get(0).getAlternateTitle());
					} else hovedtemaVisning.setHovedtema(hovedtema.getGenericTitle());
				}

				for (Kartbilder kartbilde : hovedtema.getKartbilder()) {
					KartbilderVisning kartbilderVisining = new KartbilderVisning();
					
					if ( language.equals("en") ) {
						List<KartBilderEnNo> en = dao.getKartbilderEn(kartbilde.getKartbilderId());
						if ( en.size() > 0 ) {
							kartbilderVisining.setGruppe(en.get(0).getAlternateTitle());
						} else kartbilderVisining.setGruppe(kartbilde.getGenericTitle()); 
					} else  {
						List<KartBilderEnNo> norsk = dao.getKartbilderNo(kartbilde.getKartbilderId());
						if ( norsk.size() > 0 ) {
							kartbilderVisining.setGruppe(norsk.get(0).getAlternateTitle());
						} else kartbilderVisining.setGruppe(kartbilde.getGenericTitle());
					}

					if ( kartbilderVisining.getGruppe().equals("MAREANO-oversiktskart") || kartbilderVisining.getGruppe().equals("MAREANO - overview") ) {
						kartbilderVisining.setVisible(true);
					}
					List<Kartlag> kartlagene = dao.getKartlagene(kartbilde.getKartbilderId());
					for (Kartlag kartlag : kartlagene) {
						if (kartlag.isAvailable()) {
							KartlagVisning kart = new KartlagVisning();
							kart.setId(kartlag.getKartlagId());
							kart.setLayers(kartlag.getLayers());
                            kart.setKeyword(kartlag.getKeyword());
                            kart.setExGeographicBoundingBoxEastBoundLongitude(kartlag.getExGeographicBoundingBoxEastBoundLongitude());
                            kart.setExGeographicBoundingBoxWestBoundLongitude(kartlag.getExGeographicBoundingBoxWestBoundLongitude());
                            kart.setExGeographicBoundingBoxNorthBoundLatitude(kartlag.getExGeographicBoundingBoxNorthBoundLatitude());
                            kart.setExGeographicBoundingBoxSouthBoundLatitude(kartlag.getExGeographicBoundingBoxSouthBoundLatitude());
							
							if ( language.equals("en") ) {
								List<KartlagEnNo> en = dao.getKartlagEn(kart.getId());
								if ( en.size() > 0)
									kart.setTitle(en.get(0).getAlternateTitle());
								else kart.setTitle(kartlag.getGenericTitle()); 
							}else {
								List<KartlagEnNo> norsk = dao.getKartlagNo(kart.getId());
								if ( norsk.size() > 0)
									kart.setTitle(norsk.get(0).getAlternateTitle());
								else kart.setTitle(kartlag.getGenericTitle()); 	
							}
							
							kart.setUrl(kartlag.getKarttjeneste().getUrl());
							kartbilderVisining.addKart(kart);
						}
					}
					hovedtemaVisning.addBilder(kartbilderVisining);
				}
				if (hovedtemaVisning.getBilder().size() > 0) {
					hovedtemaVisninger.add(hovedtemaVisning);
				}
			}
		}
		return hovedtemaVisninger;
	}
	
	protected String getMareanoHeading(String language) {
		StringBuffer heading = new StringBuffer();
		try {
			URL url = null;
			if ( language.equals(ENGLISH)) {
				url = new URL("http://www.mareano.no/english/index.html");
			} else {
				url = new URL("http://www.mareano.no/");
			}
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            String line;
            boolean headerContent = false;
            while ((line = reader.readLine()) != null) {
            	if ( line.contains("<!--mainmenustart-->") ) {
            		headerContent = true;
            	}
            	if ( headerContent ) {
            		heading.append(line);
            	}
            	if ( line.contains("<!--mainmenuend-->") ) {
            		headerContent = false;
            	}
            	
            }
            reader.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }  catch (IOException e) {
            e.printStackTrace();
        } 
        String someHeading = "<table width=\"100%\"><tr height=\"45\"> " +
        "<td valign=\"middle\" height=\"45\" style=\"background-image:url(http://www.mareano.no/kart/images/top/ny_heading_397.gif); background-repeat: repeat;\"> " +
        "<a style=\"text-decoration: none\" target=\"_top\" href=\"http://www.mareano.no\"> " +
        "<img border=\"0\" alt=\"MAREANO<br>samler kunnskap om havet\" src=\"http://www.mareano.no/kart/images/top/ny_logo.gif\"> " +
        "</a> " +
        "</td> " +
        "<td width=\"627\" align=\"right\" height=\"45\" style=\"background-image:url(http://www.mareano.no/kart/images/top/ny_heading_627.gif);\"> </td> " +
        "</tr></table>";
        
        String newHeading = heading.toString(); 
        newHeading = newHeading.replaceAll("href=\"/", "href=\"http://www.mareano.no/");
        return someHeading + newHeading;
	}

	@RequestMapping("/spesialpunkt")
	public @ResponseBody SpesialpunktStatus getSpesialpunktAsGML(
			@RequestParam("extent") String extent, 
			@RequestParam("kartlagId") String kartlagId, 
			HttpServletRequest req, HttpServletResponse response) throws IOException {

		List<Spesialpunkt> punkter = getSpesialpunkt(new Long(kartlagId));
		SpesialpunktStatus spesialpunktJSON = null;
		if (punkter.size() > 0) {
			FeatureCollection features = toGMLPojos(punkter, extent);
			String xml = xstream.toXML(features);
			xml = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>" + xml;
			writeGmlToFile(xml, req);
			
			spesialpunktJSON = getLegendsInfo(kartlagId, "norsk");
			spesialpunktJSON.setNoSpesialpunkt( false );
		} else {
			spesialpunktJSON = new SpesialpunktStatus();
			spesialpunktJSON.setNoSpesialpunkt(true);		
		}
		return spesialpunktJSON;
	}

	private final static String baseUrlForLegend = "http://www.mareano.no/kart/";
	
	@RequestMapping("/legend")
	public @ResponseBody SpesialpunktStatus getLegend(
			@RequestParam("kartlagId") String kartlagId, @RequestParam("language") String language) {
		
		SpesialpunktStatus spesialpunktJSON = getLegendsInfo(kartlagId, language);
		getKartlagInfo( kartlagId, spesialpunktJSON, language);
		return spesialpunktJSON;
	}

	protected SpesialpunktStatus getLegendsInfo( String kartlagId, String language) {
		SpesialpunktStatus spesialpunktJSON = new SpesialpunktStatus();
		List<Legend> legends = dao.getALegend(new Long(kartlagId), language);
		if ( legends.size() > 0 ) { 
			for ( Legend aLegend : legends ) {
				String text = aLegend.getGenericTitle();
				if ( aLegend.getUrl() !=  null ) {
					String legendUrl = aLegend.getUrl().trim();
					if ( !legendUrl.startsWith("http://") ) {
						spesialpunktJSON.addLegendsInfo(new LegendsInfo(baseUrlForLegend + legendUrl, text));
					} else {
						spesialpunktJSON.addLegendsInfo(new LegendsInfo(legendUrl, text));
					}
				} else {
					spesialpunktJSON.addLegendsInfo(new LegendsInfo("", text));
				}
			}
		}		
		return spesialpunktJSON;
	}
	
	protected List<Spesialpunkt> getSpesialpunkt(Long kartlagId) {
		List<Spesialpunkt> spesialpunktDb = dao.getSpesialpunkt(kartlagId);
		return spesialpunktDb;
	}
	
	protected SpesialpunktStatus getKartlagInfo( String kartlagId, SpesialpunktStatus spesialpunktJSON, String noOrEn)  {
		KartlagInfo kartlagInfo = dao.getKartlagInfo(new Long(kartlagId), noOrEn);
		spesialpunktJSON.setKartlagInfo(new KartlagInfos(kartlagInfo.getTitle(), kartlagInfo.getAbstracts()));
		return spesialpunktJSON;
	}

	/**
	 * write gml to file spesialpunkt.xml in root of sevletContext
	 * (/webapp/geodata/) Hack to remove namespace from endtag of
	 * FeatureCollection since XStream does not support multiple namespaces (xsi
	 * and gml).
	 * 
	 * @param xml
	 * @param req
	 */
	protected void writeGmlToFile(String xml, HttpServletRequest req) throws IOException {
		int lengthXml = xml.indexOf("</FeatureCollection");
		xml = xml.substring(0, lengthXml + ("</FeatureCollection".length())) + ">"; // hack to remove namespace from endtag of featureCollection

		ServletContext sct = req.getSession().getServletContext();
		File file1 = new File(sct.getRealPath("") + File.separator + "spesialpunkt.xml");
		if (file1.exists())
			file1.delete();
		File file = new File(sct.getRealPath("") + File.separator + "spesialpunkt.xml");

		Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF8"));
		out.write(xml);
		out.close();
	}

	@RequestMapping("/getgml")
	public void getgml(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		getGMLResource("spesialpunkt.xml", req, resp);
	}

	private void getGMLResource(String sldResource, HttpServletRequest request, HttpServletResponse response) throws IOException {
		Resource template = ctx.getResource(sldResource);
		File output = template.getFile();
		writeGmlToResponse(output, response);
	}

	private void writeGmlToResponse(File sldOutput, HttpServletResponse response) throws IOException {
		BufferedReader inReader = new BufferedReader(new FileReader(sldOutput.getAbsoluteFile()));
		Writer respWriter = response.getWriter();

		try {
			String thisLine;
			StringBuffer sldXml = new StringBuffer();
			while ((thisLine = inReader.readLine()) != null) {
				sldXml.append(thisLine);
			}
			respWriter.write(sldXml.toString());
		} finally {
			respWriter.close();
			inReader.close();
		}
	}

	protected FeatureCollection toGMLPojos(List<Spesialpunkt> spesialpunkter, String extent) {
		FeatureCollection features = new FeatureCollection();
		BoundedBy boundedby = new BoundedBy();
		Point bbox = new Point(extent, "epsg:32633");
		boundedby.setBox(bbox);
		features.setBoundedBy(boundedby);
		for (Spesialpunkt punkt : spesialpunkter) {
			FeatureMember member = new FeatureMember();
			Attributes attr = new Attributes();
			String koordinat = punkt.getxUtm33() + "," + punkt.getyUtm33();
			Point koord = new Point(koordinat, "epsg:32633");
			attr.setPoint(koord);

			attr.setName(punkt.getGenericTitle());
			attr.setPid(punkt.getSpesialpunktId() + "");
			attr.setDescription(punkt.getUrl());
			if (punkt.getSpesialpunkttype() != null)
				attr.setType(punkt.getSpesialpunkttype().getTitle());
			else
				attr.setType("empty");
			attr.setId(punkt.getSpesialpunktId() + "");

			member.setAttributes(attr);
			features.addMember(member);
		}
		return features;
	}
}

package no.imr.geoexplorer.admindatabase.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import no.imr.geoexplorer.admindatabase.dao.MareanoAdminDbDao;
import no.imr.geoexplorer.admindatabase.gml.XstreamConfig;
import no.imr.geoexplorer.admindatabase.gml.pojo.Attributes;
import no.imr.geoexplorer.admindatabase.gml.pojo.BoundedBy;
import no.imr.geoexplorer.admindatabase.gml.pojo.FeatureCollection;
import no.imr.geoexplorer.admindatabase.gml.pojo.FeatureMember;
import no.imr.geoexplorer.admindatabase.gml.pojo.Point;
import no.imr.geoexplorer.admindatabase.jsp.pojo.KartlagInfos;
import no.imr.geoexplorer.admindatabase.jsp.pojo.LegendsInfo;
import no.imr.geoexplorer.admindatabase.jsp.pojo.SpesialpunktStatus;
import no.imr.geoexplorer.admindatabase.mybatis.pojo.KartBilderEnNo;
import no.imr.geoexplorer.admindatabase.mybatis.pojo.KartlagInfo;
import no.imr.geoexplorer.admindatabase.mybatis.pojo.Legend;
import no.imr.geoexplorer.admindatabase.mybatis.pojo.Spesialpunkt;

@Controller
public class JsonDataController {
    @Autowired(required = true)
    private XstreamConfig xstream;
    
    @Autowired(required = true)
    private MareanoAdminDbDao dao;
    
    private final static String baseUrlForLegend = "http://www.mareano.no/kart/";
    protected final static String  EPSG_32633 = "epsg:32633";
    
    @RequestMapping("/legend")
    public @ResponseBody SpesialpunktStatus getLegend(
    		@RequestParam("kartlagId") String kartlagId, 
    		@RequestParam("language") String language) throws IOException{

        SpesialpunktStatus spesialpunktJSON = getLegendsInfo(kartlagId, language);
        getKartlagInfo(kartlagId, spesialpunktJSON, language);
        return spesialpunktJSON;
    }

    @RequestMapping("/legendAndSpesialpunkt")
    public @ResponseBody SpesialpunktStatus getLegendAndSpesialpunkt(
    		@RequestParam("extent") String extent,
    		@RequestParam("kartlagId") String kartlagId, 
    		@RequestParam("language") String language,
    		HttpServletRequest req) throws IOException {

        SpesialpunktStatus spesialpunktJSON = getLegendsInfo(kartlagId, language);
        getKartlagInfo(kartlagId, spesialpunktJSON, language);
        
        spesialpunktJSON.setNoSpesialpunkt(false);
        return spesialpunktJSON;
    }
    
    @RequestMapping("/infoKartBilde")
    public @ResponseBody SpesialpunktStatus getInfoKartBilde(
    		@RequestParam("kartbildeNavn") String kartbildeNavn,
    		@RequestParam("language") String language) throws Exception {
    	SpesialpunktStatus json = new SpesialpunktStatus();
    	KartlagInfos info = new KartlagInfos();

    	KartBilderEnNo kartBildeInfo = dao.getKartBildeInfo(kartbildeNavn, language);
    	
        if ( kartBildeInfo != null ) {
            info.setKartlagInfoTitel(kartBildeInfo.getAlternateTitle());
            info.setText(kartBildeInfo.getAbstracts());
            json.setKartlagInfo(info);
        } else {
            if ( language.equals("en") ) {
                info.setKartlagInfoTitel("No layer info for layer:"+kartbildeNavn );
                info.setText("No abstract info for layer:"+kartbildeNavn );
            } else {
                info.setKartlagInfoTitel("Ingen info for kartlag:"+kartbildeNavn );
                info.setText("Ingen Abstract for kartlag:"+kartbildeNavn );
            }
            json.setKartlagInfo(info);
        }
    	return json;
    }
    
    protected SpesialpunktStatus getLegendsInfo(String kartlagId, String language) {
        SpesialpunktStatus spesialpunktJSON = new SpesialpunktStatus();
        List<Legend> legends = dao.getALegend(new Long(kartlagId), language);
        if (legends.size() > 0) {
        	Legend[] legendsArray = legends.toArray(new Legend[]{}); 
            Arrays.sort(legendsArray, Legend.LegendComparator);
            for (Legend aLegend : legendsArray) {
                String text = aLegend.getGenericTitle();
                if (aLegend.getUrl() != null && !aLegend.getUrl().equals("")) {
                    String legendUrl = aLegend.getUrl().trim();
                    if (!legendUrl.toLowerCase().startsWith("http://")) {
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

    protected SpesialpunktStatus getKartlagInfo(String kartlagId, SpesialpunktStatus spesialpunktJSON, String noOrEn) {
        KartlagInfo kartlagInfo = dao.getKartlagInfo(new Long(kartlagId), noOrEn);
		if (kartlagInfo == null ) {
		    spesialpunktJSON.setKartlagInfo(new KartlagInfos("No title set for kartlagId:"+kartlagId, "No abstract set"));
		} else {
		    spesialpunktJSON.setKartlagInfo(new KartlagInfos(kartlagInfo.getTitle(), kartlagInfo.getAbstracts()));
		}
        return spesialpunktJSON;
    }


    @RequestMapping("/getgml")
    public void getgml(@RequestParam("kartlagId") String kartlagId, HttpServletResponse resp) throws IOException {
        List<Spesialpunkt> punkter = dao.getSpesialpunkt(new Long(kartlagId));
        if (punkter.size() > 0) {
            FeatureCollection features = toGMLPojos( punkter );
            String xml = xstream.toXML(features);
            int lengthXml = xml.indexOf("</FeatureCollection");
            xml = xml.substring(0, lengthXml + ("</FeatureCollection".length())) + ">"; // hack to remove namespace from endtag of featureCollection
            xml = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n" + xml;
            
            resp.setContentType("text/plain;charset=UTF-8");
            PrintWriter writer = resp.getWriter();
            writer.append(xml);
            resp.getWriter().flush(); 
            resp.flushBuffer();
            resp.getWriter().close();
        }
    }

    protected FeatureCollection toGMLPojos( List<Spesialpunkt> spesialpunkter ) {
        FeatureCollection features = new FeatureCollection();
        BoundedBy boundedby = new BoundedBy();
        String extent = "-3278916,6274532,4178916,8825468"; //dummy extent
        Point bbox = new Point(extent, EPSG_32633);
        boundedby.setBox(bbox);
        features.setBoundedBy(boundedby);
        for (Spesialpunkt punkt : spesialpunkter) {
            FeatureMember member = new FeatureMember();
            Attributes attr = new Attributes();
            String koordinat = punkt.getxUtm33() + "," + punkt.getyUtm33();
            Point koord = new Point(koordinat, EPSG_32633);
            attr.setPoint(koord);

            attr.setName(punkt.getGenericTitle());
            attr.setPid(punkt.getSpesialpunktId() + "");
            attr.setDescription(punkt.getUrl());
            if (punkt.getSpesialpunkttype() != null) {
                attr.setType(punkt.getSpesialpunkttype().getTitle());
            } else {
                attr.setType("empty");
            }
            attr.setId(punkt.getSpesialpunktId() + "");

            member.setAttributes(attr);
            features.addMember(member);
        }
        return features;
    }
}

package no.imr.geoexplorer.admindatabase.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Arrays;
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
import no.imr.geoexplorer.admindatabase.jsp.pojo.KartlagInfos;
import no.imr.geoexplorer.admindatabase.jsp.pojo.LegendsInfo;
import no.imr.geoexplorer.admindatabase.jsp.pojo.SpesialpunktStatus;
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

@Controller
public class JsonDataController {
    @Autowired(required = true)
    private XstreamConfig xstream;
    
    @Autowired(required = true)
    private MareanoAdminDbDao dao;
    
    @Autowired(required = true)
    private ApplicationContext ctx;
    
    private final static String baseUrlForLegend = "http://www.mareano.no/kart/";
    protected final static String  EPSG_32633 = "epsg:32633";
	
//    @RequestMapping("/spesialpunkt")
    public @ResponseBody SpesialpunktStatus getSpesialpunktAsGML(
            @RequestParam("extent") String extent,
            @RequestParam("kartlagId") String kartlagId,
            HttpServletRequest req) throws IOException {

        List<Spesialpunkt> punkter = getSpesialpunkt(new Long(kartlagId));
        SpesialpunktStatus spesialpunktJSON = null;
        if (punkter.size() > 0) {
            FeatureCollection features = toGMLPojos(punkter, extent);
            String xml = xstream.toXML(features);
            xml = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>" + xml;
            writeGmlToFile(xml, req);

            spesialpunktJSON = getLegendsInfo(kartlagId, "norsk");
            spesialpunktJSON.setNoSpesialpunkt(false);
        } else {
            spesialpunktJSON = new SpesialpunktStatus();
            spesialpunktJSON.setNoSpesialpunkt(true);
        }
        return spesialpunktJSON;
    }
    
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
        
        spesialpunktJSON.setNoSpesialpunkt(true);
        List<Spesialpunkt> punkter = getSpesialpunkt(new Long(kartlagId));
        if (punkter.size() > 0) {
            FeatureCollection features = toGMLPojos(punkter, extent);
            String xml = xstream.toXML(features);
            xml = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>" + xml;
            writeGmlToFile(xml, req);
            spesialpunktJSON.setNoSpesialpunkt(false);
        }
        return spesialpunktJSON;
    }
    
//    protected void createGMLFile(String kartlagId, String extent, HttpServletRequest req) throws IOException{
//        List<Spesialpunkt> punkter = getSpesialpunkt(new Long(kartlagId));
//        if (punkter.size() > 0) {
//            FeatureCollection features = toGMLPojos(punkter, extent);
//            String xml = xstream.toXML(features);
//            xml = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n\n\t" + xml;
//            System.out.println(xml);
//            writeGmlToFile(xml, req);
//        }
//    }
    
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

    protected List<Spesialpunkt> getSpesialpunkt(Long kartlagId) {
        List<Spesialpunkt> spesialpunktDb = dao.getSpesialpunkt(kartlagId);
        return spesialpunktDb;
    }

    protected SpesialpunktStatus getKartlagInfo(String kartlagId, SpesialpunktStatus spesialpunktJSON, String noOrEn) {
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
        if (file1.exists()) {
            file1.delete();
        }
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

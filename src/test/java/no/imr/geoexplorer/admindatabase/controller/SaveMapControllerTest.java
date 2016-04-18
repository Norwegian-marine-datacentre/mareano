package no.imr.geoexplorer.admindatabase.controller;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockServletContext;

import com.fasterxml.jackson.databind.JsonNode;

public class SaveMapControllerTest {

	private SaveMapsController save = new SaveMapsController();
	
	private MockHttpServletRequest req = new MockHttpServletRequest();
	private File sqlite = new File("geoexplorer.db");
	
	@Before
	public void setup() {
		
		MockServletContext ctx = new MockServletContext();	
		String absolutePath = sqlite.getAbsolutePath();
		String filePath = absolutePath.
			    substring(0,absolutePath.lastIndexOf(File.separator));
		System.out.println( "path:" + filePath );
		
		ctx.setInitParameter("GEOEXPLORER_DATA", filePath);
		MockHttpSession session = new MockHttpSession(ctx);
		req.setSession(session);
	}
	
	@After
	public void cleanup() {
		//sqlite.delete();
	}
	
	@Test
	public void POSTMapTest() throws Exception {
		
		req.setContentType("application/json");
		req.setContent( ("{\"test\":\"test}").getBytes("UTF-8") );
		
		req.setMethod("POST");
		JsonNode json = save.saveMap(req);
		System.out.println("json:"+json.toString());
		assertTrue( json.toString().contains("{\"id\":") );
	}
	
	@Test
	public void GETMapTest()  throws Exception {
		
		String config = "{\"proxy\":\"proxy/?url=\",\"printService\":null,\"about\":{\"title\":\"Mareano\",\"abstract\":\"Copyright (C) 2005-2013 Mareano. Kartprojeksjon WGS84, UTM 33 N\",\"contact\":\"For more information, contact <a href='http://www.imr.no'>Institute of Marine Research</a>.\"},\"defaultSourceType\":\"gxp_wmscsource\",\"sources\":{\"ol\":{\"ptype\":\"gx_olsource\",\"projection\":\"EPSG:32633\",\"id\":\"ol\"}},\"map\":{\"projection\":\"EPSG:32633\",\"units\":\"m\",\"maxResolution\":10832,\"maxExtent\":[-2500000,3500000,3045984,9045984],\"numZoomLevels\":18,\"wrapDateLine\":false,\"layers\":[{\"source\":\"ol\",\"name\":\"Norgeskart\",\"title\":\"Norgeskart\",\"visibility\":false,\"opacity\":1,\"group\":\"background\",\"fixed\":false,\"selected\":false,\"type\":\"OpenLayers.Layer.WMS\",\"args\":[\"Norgeskart\",\"http://wms.geonorge.no/skwms1/wms.toporaster2\",{\"layers\":\"toporaster\",\"format\":\"image/png\",\"transparent\":true,\"isBaseLayer\":true},{\"singleTile\":true}]},{\"source\":\"ol\",\"name\":\"Norgeskart (gr\u00E5tone)\",\"title\":\"Norgeskart (gr\u00E5tone)\",\"visibility\":false,\"opacity\":1,\"group\":\"background\",\"fixed\":false,\"selected\":false,\"type\":\"OpenLayers.Layer.WMS\",\"args\":[\"Norgeskart (gr\u00E5tone)\",\"http://wms.geonorge.no/skwms1/wms.topo2.graatone\",{\"layers\":\"topo2_graatone_WMS\",\"format\":\"image/png\",\"transparent\":true,\"isBaseLayer\":true},{\"singleTile\":true}]},{\"source\":\"ol\",\"name\":\"Europa\",\"title\":\"Europa\",\"visibility\":false,\"opacity\":1,\"group\":\"background\",\"fixed\":false,\"selected\":false,\"type\":\"OpenLayers.Layer.WMS\",\"args\":[\"Europa\",\"http://wms.geonorge.no/skwms1/wms.europa\",{\"layers\":\"europa_wms\",\"format\":\"image/jpeg\",\"transparent\":true,\"isBaseLayer\":true},{\"singleTile\":true}]},{\"source\":\"ol\",\"name\":\"Europa -hvit bakgrunn\",\"title\":\"Europa -hvit bakgrunn\",\"visibility\":false,\"opacity\":1,\"group\":\"background\",\"fixed\":false,\"selected\":false,\"type\":\"OpenLayers.Layer.WMS\",\"args\":[\"Europa -hvit bakgrunn\",\"http://wms.geonorge.no/skwms1/wms.europa\",{\"layers\":\"Land,Vmap0Land,Vmap0Bebyggelse,Vmap0Skog,Vmap0Sletteland,Vmap0Innsjo,Vmap0MyrSump,Vmap0Isbre,Vmap0Hoydekontur,Vmap0Kystkontur,Vmap0Elver,Vmap0AdministrativeGrenser\",\"format\":\"image/jpeg\",\"transparent\":true,\"isBaseLayer\":true},{\"singleTile\":true}]},{\"source\":\"ol\",\"name\":\"Europa og Gebco\",\"title\":\"Europa og Gebco\",\"visibility\":true,\"opacity\":1,\"group\":\"background\",\"fixed\":false,\"selected\":false,\"type\":\"OpenLayers.Layer.WMS\",\"args\":[\"Europa og Gebco\",\"http://wms.geonorge.no/skwms1/wms.barents_watch\",{\"layers\":\"barents_watch_WMS\",\"format\":\"image/jpeg\",\"transparent\":true,\"isBaseLayer\":true},{\"singleTile\":true}]},{\"source\":\"ol\",\"name\":\"Grid\",\"title\":\"Grid\",\"visibility\":true,\"opacity\":1,\"group\":\"common\",\"fixed\":false,\"selected\":false,\"type\":\"OpenLayers.Layer.WMS\",\"args\":[\"Grid\",\"http://maps.imr.no/geoserver/wms\",{\"layers\":\"grid_UTM33,utm33n_01bgX05lg,utm33n_02bgX10lg,utm33n_15bmX01lg,utm33n_30bmX02lg\",\"format\":\"image/png\",\"transparent\":true},{\"opacity\":1,\"singleTile\":true}]},{\"source\":\"ol\",\"name\":\"Havbunn skyggerelieff  \",\"title\":\"Havbunn skyggerelieff  \",\"visibility\":true,\"opacity\":1,\"group\":\"default\",\"fixed\":false,\"selected\":false,\"type\":\"OpenLayers.Layer.WMS\",\"args\":[\"Havbunn skyggerelieff  \",\"http://wms.geonorge.no/skwms1/wms.havbunnraster2?VERSION=1.1.1&SERVICE=WMS&REQUEST=getMap&SRS=EPSG:32633&TRANSPARENT=true\",{\"layers\":\"Havbunnraster\",\"format\":\"image/png\",\"transparent\":true},{\"opacity\":1,\"metadata\":{\"keyword\":\"Havbunnraster Skyggerelieff\",\"kartlagId\":\"311\"},\"maxExtent\":[-472030,7130780,1123110,8293680],\"singleTile\":true,\"buffer\":0,\"ratio\":1}]},{\"source\":\"ol\",\"name\":\"Forvaltningsplanomr.\",\"title\":\"Forvaltningsplanomr.\",\"visibility\":true,\"opacity\":1,\"group\":\"default\",\"fixed\":false,\"selected\":false,\"type\":\"OpenLayers.Layer.WMS\",\"args\":[\"Forvaltningsplanomr.\",\"http://wms.dirnat.no/geoserver/forvaltningsplanomrader_hav/wms?VERSION=1.1.1&SERVICE=WMS&REQUEST=GetMap&SRS=EPSG:32633&TRANSPARENT=true&BGCOLOR=0xffffff&\",{\"layers\":\"forvaltningsplanomrader_hav:fp_barentshavet_grenser,forvaltningsplanomrader_hav:fp_nordsjoen_grenser,forvaltningsplanomrader_hav:fp_norskehavet_grenser\",\"format\":\"image/png\",\"transparent\":true},{\"opacity\":1,\"metadata\":{\"keyword\":\"\",\"kartlagId\":\"10\"},\"maxExtent\":[-1836490,5578460,1627100,8166960],\"singleTile\":true,\"buffer\":0,\"ratio\":1}]},{\"source\":\"ol\",\"name\":\"Sj\u00F8m\u00E5ling i MAREANO\",\"title\":\"Sj\u00F8m\u00E5ling i MAREANO\",\"visibility\":true,\"opacity\":1,\"group\":\"default\",\"fixed\":false,\"selected\":false,\"type\":\"OpenLayers.Layer.WMS\",\"args\":[\"Sj\u00F8m\u00E5ling i MAREANO\",\"http://wms.geonorge.no/skwms1/wms.dekning_sjomaaling?version=1.1.1&service=wms&REQUEST=getMap&SRS=EPSG:32633&TRANSPARENT=true&styles=\",{\"layers\":\"Mareano_ferdig_sjomaalt\",\"format\":\"image/png\",\"transparent\":true},{\"opacity\":1,\"metadata\":{\"keyword\":\"dekning, multistr\u00E5ledata, Mareano-data\",\"kartlagId\":\"209\"},\"maxExtent\":[304500,7395000,1173000,8081000],\"singleTile\":true,\"buffer\":0,\"ratio\":1}]},{\"source\":\"ol\",\"name\":\"Marine omr\u00E5der\",\"title\":\"Marine omr\u00E5der\",\"visibility\":true,\"opacity\":1,\"group\":\"default\",\"fixed\":false,\"selected\":false,\"type\":\"OpenLayers.Layer.WMS\",\"args\":[\"Marine omr\u00E5der\",\"http://geo.ngu.no/mapserver/MarinBunnsedimenterWMS?VERSION=1.1.1&SERVICE=WMS&REQUEST=GetMap&SRS=EPSG:32633&TRANSPARENT=true& \",{\"layers\":\"Havomrader\",\"format\":\"image/png\",\"transparent\":true},{\"opacity\":1,\"metadata\":{\"keyword\":\"\",\"kartlagId\":\"14\"},\"maxExtent\":[-21422,7391860,1341550,8996500],\"singleTile\":true,\"buffer\":0,\"ratio\":1}]}],\"center\":[1088474,7689849],\"zoom\":2},\"viewerTools\":[{\"leaf\":true,\"text\":\"Panorer kart\",\"checked\":true,\"iconCls\":\"gxp-icon-pan\",\"ptype\":\"gxp_navigation\",\"toggleGroup\":\"toolGroup\"},{\"leaf\":true,\"text\":\"Get Feature Info\",\"checked\":true,\"iconCls\":\"gxp-icon-getfeatureinfo\",\"ptype\":\"gxp_wmsgetfeatureinfo\",\"toggleGroup\":\"toolGroup\"},{\"leaf\":true,\"text\":\"Measure\",\"checked\":true,\"iconCls\":\"gxp-icon-measure-length\",\"ptype\":\"gxp_measure\",\"controlOptions\":{\"immediate\":true},\"toggleGroup\":\"toolGroup\"},{\"leaf\":true,\"text\":\"Zoom in / Zoom out\",\"checked\":true,\"iconCls\":\"gxp-icon-zoom-in\",\"numberOfButtons\":2,\"ptype\":\"gxp_zoom\"},{\"leaf\":true,\"text\":\"Zoom til forrige utstrekning / Zoom til neste utstrekning\",\"checked\":true,\"iconCls\":\"gxp-icon-zoom-previous\",\"numberOfButtons\":2,\"ptype\":\"gxp_navigationhistory\"},{\"leaf\":true,\"text\":\"Zoom til synlig utstrekning\",\"checked\":true,\"iconCls\":\"gxp-icon-zoomtoextent\",\"ptype\":\"gxp_zoomtoextent\"},{\"leaf\":true,\"text\":\"Show Legend\",\"checked\":true,\"iconCls\":\"gxp-icon-legend\",\"ptype\":\"gxp_legend\"}],\"modified\":1413287526784}";
		
		req.setContentType("application/json");
		req.setContent( (config).getBytes("UTF-8") );
		
		req.setMethod("POST");
		JsonNode savedJson = save.saveMap(req);
		System.out.println("id of saved json:"+savedJson.toString());
		
		req.setContentType("application/json");		
		//req.setContent( (savedJson.toString()).getBytes("UTF-8") );
		System.out.println("savedJson.toString():"+savedJson.toString());
		
		String jsonString = savedJson.toString();
		int indexToRead = jsonString.indexOf("\"id\":");
		System.out.println("get index of:"+indexToRead);
		String mapId = jsonString.substring(indexToRead+5, jsonString.length()-1);
		System.out.println("mapId:"+mapId);
		req.setRequestURI("http://localhost/mareano/mareano.html#maps/"+mapId);
		req.setPathInfo("mareano.html#maps/"+mapId);
		System.out.println("req:"+req.getRequestURI());
		
		req.setMethod("GET");
		JsonNode json = save.getMap(64, req);
		System.out.println("json:"+json.toString());
	}
	
	public void PUTMapTest()  throws Exception {
		
	}
	
	public void DELETEMapTest()  throws Exception {
		
	}
}

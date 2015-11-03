<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
/**
 * 1. Create Mareano.Composer constructor into app var and define background maps.
 * 2. Create hovedtema, kartbilde, and kartlag structure in left layer panel 
 * 3. Create Generelle folder in right most layer panel
 */
var app = new Mareano.Composer({
	<!-- authStatus: < status >, -->
	proxy: "proxy/?url=",
	printService: null,
	about: {
		title: "Mareano",
		"abstract": "<spring:message code="projectAbstract" text="Abstract" />",
		contact: "<spring:message code="contact" text="Contact" />"
	},
	defaultSourceType: "gxp_wmscsource",
	sources: {
	    ol: {
	        ptype: "gx_olsource"
	    }
	},
	map: {
	    projection: "EPSG:32633",
	    units: "m",
	    maxResolution: 10832.0,
	    maxExtent: [-2500000.0,3500000.0,3045984.0,9045984.0],
	    numZoomLevels: 18,
	    wrapDateLine: false,
	    layers: [
	    {
		    source: "ol",
		    type: "OpenLayers.Layer.WMS",
		    group: "background",
		    args: [
		            "<spring:message code="nauticalChart" text="nautical chart" />",
		            "http://maps.imr.no/geoserver/gwc/service/wms",
		            {layers: "Sjokart_Hovedkartserien2", format: "image/png", transparent: true, isBaseLayer: true},
		            {singleTile:false}
		        ]
		    }, 
		    /*
		    * Kartloesninger fra geonorge.
		    * 1. http://wms.geonorge.no - ubegrenset tilgang for HI (fordi de har IP rangen vaar) men en begrensning paa ca 3 kall i sekundet. Kjoerer raskest med singleTile
		    * 2. opencache.statkart.no/gatekeeper - open loesning men begresning paa 10 000 kall pr dag. Tilet loesning
		    * 3. gatekeeper1.geonorge.no - ubegrenset med tilet tilgang men hver request krever en token som krever paalogging og som har timeout. Saa token forrandrer seg over tid
		    */
            /* {
		        source: "ol",
		        type: "OpenLayers.Layer.WMS",
		        group: "background",
		        args: [
		            "<spring:message code="norwayGray" text="norway gray scale" />",
		            "http://wms.geonorge.no/skwms1/wms.topo2.graatone",
		            {layers: "topo2_graatone_WMS", format: "image/png", transparent: true, isBaseLayer: true},
		            {singleTile:true,  
			     longDesc:"<spring:message code="norwayGrayDesc" text="norway gray scale" />"}
			    
		        ]
		    }, {
		        source: "ol",
		        type: "OpenLayers.Layer.WMS",
		        group: "background",
		        args: [
		            "<spring:message code="Europa" text="Europa" />",
		            "http://maps.imr.no/geoserver/gwc/service/wms",
		            {layers: "Europa_WMS", format: "image/jpeg", transparent: true, isBaseLayer: true},
		            {singleTile:false}
		        ]
		    },*/ {
		        source: "ol",
		        type: "OpenLayers.Layer.WMS",
		        group: "background",
		        args: [
		            "<spring:message code="gebco" text="Gebco grayscale" />",
		            "http://maps.imr.no/geoserver/gwc/service/wms",
		            {layers: "geonorge:geonorge_norge_skyggerelieff", format: "image/jpeg", transparent: true, isBaseLayer: true},
		            {singleTile:false}
		        ]                            
		    }, {
		        source: "ol",
		        type: "OpenLayers.Layer.WMS",
		        group: "background",
		        args: [
		            "<spring:message code="europaWhite" text="Europa White background" />",
		            "http://maps.imr.no/geoserver/gwc/service/wms",
		            {layers: "geonorge_europa_hvit_bakgrunn", format: "image/jpeg", transparent: true, isBaseLayer: true},
		            {singleTile:false,
			     longDesc:"<spring:message code="europaWhiteDesc" text="Europa white" />"}
		        ]
		    }/*, {
		        source: "ol",
		        type: "OpenLayers.Layer.WMS",
		        group: "background",
		        args: [
		            "<spring:message code="europaAndGebco" text="Europa and Gebco" />",
		            "http://maps.imr.no/geoserver/gwc/service/wms",
		            {layers: "barents_watch_WMS", format: "image/jpeg", transparent: true, isBaseLayer: true},
		            {singleTile:false,
			     longDesc:"<spring:message code="europaAndGebcoDesc" text="Europa and Gebco" />"}
			]
		    }*/                    
	   ],
	   center: [450000, 7550000],
	   zoom: 2
	}
});

//Global vars
var layers = [];
var generelleLayers = [];
var generelleLayerNames = {};
var OLRecord;

var hovedtemaer=[];  //Non generelle and non background
var generelle=[];
var bakgrunn=[]; 

//JSON of all layers
var alleHovedtemaer=${hovedtemaer_json};
//TODO discuss how background layers should be flagged
var backgroundGroupName ="alt";

var hovedtema,gruppe;




/**
 * Creates layers for background layers.
 * This could be merged into generic function that createLayerRecord could use
 * to create layer part of layer record. Would need to check extra atttributes that
 * createLayerRecord adds are valid for background layers as well
*/
function createBackgroundLayerObject(layer)
{
    return  {source: "ol",
	     type: "OpenLayers.Layer.WMS",
	     group: "background",
	     args: [
		 layer.title,
		 layer.url,
		 {layers: layer.layers,
		  format: layer.format,
		  transparent: true,
		  isBaseLayer: true},
		 {
		     metadata: {
			 keyword: layer.keyword,
			 'kartlagId': layer.id
                     },
		     singleTile:false
		 }
	     ]};
}


function createLayerRecord(panelGroup,isVisible,layer)
{
    return gxp.plugins.OLSource.prototype.createLayerRecord({
        source: "ol",
        type: "OpenLayers.Layer.WMS",
        group: panelGroup,
        queryable: layer.queryable,
        visibility: isVisible,
        properties: "mareano_wmslayerpanel",            
        args: [
            layer.title,
	    layer.url,
            {layers: layer.layers, format: layer.format, transparent: true},
            {
                opacity: 1,
                metadata: {
                    keyword: layer.keyword,
                    'kartlagId': ''+layer.id
                },
                minScale: layer.scalemax*(96/0.0254),
                maxScale: (layer.scalemin > 0) ? layer.scalemin*(96/0.0254) : 0.001,
                units: "m",
                maxExtent: [
                    layer.exGeographicBoundingBoxWestBoundLongitude,
                    layer.exGeographicBoundingBoxSouthBoundLatitude,
                    layer.exGeographicBoundingBoxEastBoundLongitude,
                    layer.exGeographicBoundingBoxNorthBoundLatitude
                ],
                singleTile:true,
                buffer: 0, //getting no boarder around image - so panning will get a new image.
                ratio: 1 //http://dev.openlayers.org/releases/OpenLayers-2.12/doc/apidocs/files/OpenLayers/Layer/Grid-js.html#OpenLayers.Layer.Grid.ratio                                        
            }
        ]
    });
}


for (var i=0;i<alleHovedtemaer.length;i++)
{
    hovedtema=alleHovedtemaer[i];

    
    //Split into three groups so have lists of each type of layers for later use
    //Test is a bit sloppy as it assumes each tema does not have mix of groups
    if (hovedtema.bilder[0].gruppe == backgroundGroupName) {
	bakgrunn.push(hovedtema);
    } else if (hovedtema.bilder[0].gruppe == "generelle") {
	generelle.push(hovedtema);
    }
    else
    {
	hovedtemaer.push(hovedtema);
    }
    
    for (var j=0;j<hovedtema.bilder.length;j++)
    {
	gruppe=hovedtema.bilder[j];
	for (var k=0;k<gruppe.kart.length;k++)
	{
	    layer = gruppe.kart[k];
	    if (gruppe.gruppe == backgroundGroupName) {
		if (app.map) { // If map does not exist at this point then GeoExplorer is loading saved map
		    app.map.layers.push(createBackgroundLayerObject(layer));
		}
	    } else {
		OLRecord = createLayerRecord(gruppe.gruppe,gruppe.visible,layer);
 		if (gruppe.gruppe == "generelle") {
		    generelleLayers.push(OLRecord);
		    generelleLayerNames[layer.title]=true;

		} else {
		    layers.push(OLRecord);
		}
	    }
	}
    }
}

var store = new GeoExt.data.LayerStore();
store.add(layers);   
store.add(generelleLayers);

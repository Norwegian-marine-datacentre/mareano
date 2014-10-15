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
		    }, {
		    /*
		    * Kartloesninger fra geonorge.
		    * 1. http://wms.geonorge.no - ubegrenset tilgang for HI (fordi de har IP rangen vaar) men en begrensning paa ca 3 kall i sekundet. Kjoerer raskest med singleTile
		    * 2. opencache.statkart.no/gatekeeper - open loesning men begresning paa 10 000 kall pr dag. Tilet loesning
		    * 3. gatekeeper1.geonorge.no - ubegrenset med tilet tilgang men hver request krever en token som krever paalogging og som har timeout. Saa token forrandrer seg over tid
		    */
		    source: "ol",
		    type: "OpenLayers.Layer.WMS",
		    group: "background",
		    args: [
		        "<spring:message code="norwayChart" text="norway" />",
		        "http://wms.geonorge.no/skwms1/wms.toporaster2",
		        {layers: "toporaster", format: "image/png", transparent: true, isBaseLayer: true},
		        {singleTile:true}
		        ]
		    }, {
		        source: "ol",
		        type: "OpenLayers.Layer.WMS",
		        group: "background",
		        args: [
		            "<spring:message code="norwayGray" text="norway gray scale" />",
		            "http://wms.geonorge.no/skwms1/wms.topo2.graatone",
		            {layers: "topo2_graatone_WMS", format: "image/png", transparent: true, isBaseLayer: true},
		            {singleTile:true}
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
		    }, {
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
		            {singleTile:false}
		        ]
		    }, {
		        source: "ol",
		        type: "OpenLayers.Layer.WMS",
		        group: "background",
		        args: [
		            "<spring:message code="europaAndGebco" text="Europa and Gebco" />",
		            "http://maps.imr.no/geoserver/gwc/service/wms",
		            {layers: "barents_watch_WMS", format: "image/jpeg", transparent: true, isBaseLayer: true},
		            {singleTile:false}
		        ]
		    }                    
	   ],
	   center: [450000, 7550000],
	   zoom: 2
	}
});
        
var layers = [];
var OLRecord;
<c:forEach var="hovedtema" items="${hovedtemaer}">
    if ( !("${hovedtema.hovedtema}" == "generelle") ) {
        <c:forEach var="bilde" items="${hovedtema.bilder}">
            <c:forEach var="kartlaget" items="${bilde.kart}">
                OLRecord = gxp.plugins.OLSource.prototype.createLayerRecord({
                    source: "ol",
                    type: "OpenLayers.Layer.WMS",
                    group: "${bilde.gruppe}",
                    queryable: ${kartlaget.queryable},
                    visibility: !(app.id > 0) ? ${bilde.visible} : false,
                    properties: "mareano_wmslayerpanel",            
                    args: [
                        "${kartlaget.title}",
                        "${kartlaget.url}",
                        {layers: "${kartlaget.layers}", format: "image/png", transparent: true},
                        {
                            opacity: 1,
                            metadata: {
                                keyword: "${kartlaget.keyword}",
                                'kartlagId': '${kartlaget.id}'
                            },
                            minScale: ${kartlaget.scalemax}*(96/0.0254),
                            maxScale: (${kartlaget.scalemin} > 0) ? ${kartlaget.scalemin}*(96/0.0254) : 0.001,
                            units: "m",
                            maxExtent: [
                                ${kartlaget.exGeographicBoundingBoxWestBoundLongitude},
                                ${kartlaget.exGeographicBoundingBoxSouthBoundLatitude},
                                ${kartlaget.exGeographicBoundingBoxEastBoundLongitude},
                                ${kartlaget.exGeographicBoundingBoxNorthBoundLatitude}
                            ],
                            singleTile:true,
                            buffer: 0, //getting no boarder around image - so panning will get a new image.
                            ratio: 1 //http://dev.openlayers.org/releases/OpenLayers-2.12/doc/apidocs/files/OpenLayers/Layer/Grid-js.html#OpenLayers.Layer.Grid.ratio                                        
                        }
                    ]
                });
                layers.push(OLRecord);
            </c:forEach>
        </c:forEach>
    }
</c:forEach>                                
var store = new GeoExt.data.LayerStore();
store.add(layers);   

var generelleLayers = []; 
var OLRecord2;
<c:forEach var="hovedtema" items="${hovedtemaer}">
    if ( "${hovedtema.hovedtema}" == "generelle" ) {
        <c:forEach var="bilde" items="${hovedtema.bilder}">
            <c:forEach var="kartlaget" items="${bilde.kart}">
                OLRecord2 = gxp.plugins.OLSource.prototype.createLayerRecord({
                    source: "ol",
                    type: "OpenLayers.Layer.WMS",
                    group: "${bilde.gruppe}",
                    queryable: ${kartlaget.queryable}, //needed to make getFeatureInfo work with gx_olsource  
                    visibility: ${bilde.visible},
                    properties: "mareano_wmslayerpanel",           
                    //properties: "${kartlaget.id}",
                    //id: "${kartlaget.id}",   
                    args: [
                        "${kartlaget.title}",
                        "${kartlaget.url}",
                        {layers: "${kartlaget.layers}", format: "image/png", transparent: true},
                        {
                            opacity: 1,
                            metadata: {
                                keyword: "${kartlaget.keyword}",
                                'kartlagId': '${kartlaget.id}'
                            },
                            maxExtent: [
                                ${kartlaget.exGeographicBoundingBoxWestBoundLongitude},
                                ${kartlaget.exGeographicBoundingBoxSouthBoundLatitude},
                                ${kartlaget.exGeographicBoundingBoxEastBoundLongitude},
                                ${kartlaget.exGeographicBoundingBoxNorthBoundLatitude}
                            ],
                            singleTile:true,
                            buffer: 0, //getting no boarder around image - so panning will get a new image.
                            ratio: 1 //http://dev.openlayers.org/releases/OpenLayers-2.12/doc/apidocs/files/OpenLayers/Layer/Grid-js.html#OpenLayers.Layer.Grid.ratio                                        
                        }
                    ]
                });
                generelleLayers.push(OLRecord2);
            </c:forEach>
        </c:forEach>
    }
</c:forEach> 
store.add(generelleLayers);  
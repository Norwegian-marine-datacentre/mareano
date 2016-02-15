<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
/**
 * 1. Create Mareano.Composer constructor into app var and define background maps.
 * 2. Create hovedtema, kartbilde, and kartlag structure in left layer panel 
 * 3. Create Generelle folder in right most layer panel
 */

/*
 * Kartloesninger fra geonorge.
 * 1. http://wms.geonorge.no - ubegrenset tilgang for HI (fordi de har IP rangen vaar) men en begrensning paa ca 3 kall i sekundet. Kjoerer raskest med singleTile
 * 2. opencache.statkart.no/gatekeeper - open loesning men begresning paa 10 000 kall pr dag. Tilet loesning
 * 3. gatekeeper1.geonorge.no - ubegrenset med tilet tilgang men hver request krever en token som krever paalogging og som har timeout. Saa token forrandrer seg over tid
*/

app = new Mareano.Composer({
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
        projection: "${projection}",
	    units: "m",
	    maxResolution: "${maxResolution}",
	    maxExtent: ${maxExtent},
	    numZoomLevels: 18,
	    wrapDateLine: false,
	    layers: [             
	   ],
	   center:${center},
	   zoom: 2
	}
});

//global vars used in mareanoGroupLayers.js
//JSON of all layers
var alleHovedtemaer=${hovedtemaer_json};
var projection = "${projection}";

var store = addLayersToHovedTemaOrBackgroundLayer(alleHovedtemaer, projection); 

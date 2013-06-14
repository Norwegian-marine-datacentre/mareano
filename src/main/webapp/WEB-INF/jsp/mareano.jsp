<!DOCTYPE HTML SYSTEM>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 
<html>
    <head>
        <title>Mareano</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="shortcut icon" href="theme/app/img/favicon.ico">
        <!-- Ext resources -->
        <link rel="stylesheet" type="text/css" href="externals/ext/resources/css/ext-all.css">
        <link rel="stylesheet" type="text/css" href="externals/ext/resources/css/xtheme-gray.css">
        <script type="text/javascript" src="externals/ext/adapter/ext/ext-base-debug.js"></script>
        <script type="text/javascript" src="externals/ext/ext-all-debug-w-comments.js"></script>
        <style type="text/css">
            .olImageLoadError { 
                /* when OL encounters a 404, don't display the pink image */
                display: none !important;
            }
            #layertree .x-tree-node-cb[type="checkbox"] {
            	display: none;
            }            
        </style>
        <meta http-equiv="X-UA-Compatible" content="IE=IE8" >
        <!--script type="text/javascript" src="javascript/googleAnalyticsStatistics.js"></script -->

        <script type="text/javascript" src="javascript/jquery-1.6.2.min.js"></script>
        <script type="text/javascript">jQuery.noConflict();</script>

        <link href="theme/imr/imr.css" rel="stylesheet" type="text/css">
        <script type="text/javascript" src="javascript/layerIcon.js"></script>
        <script type="text/javascript" src="javascript/toEnglishTranslateMenuButtons.js"></script>

        <!-- Mareano.no -->
        <link rel="stylesheet" type="text/css" href="http://www.mareano.no/kart/styles/mareanoStyle.css">
        <style type="text/css">
        #nav-main {
            background-image: url("http://www.mareano.no/kart/images/nav-main-background.jpg");
        }
        </style>

        <!-- OpenLayers resources -->
        <link rel="stylesheet" type="text/css" href="externals/openlayers/theme/default/style.css">
        <script type="text/javascript" src="script/OpenLayers.js"></script>

        <!-- GeoExt resources -->
        <link rel="stylesheet" type="text/css" href="externals/GeoExt/resources/css/popup.css">
        <link rel="stylesheet" type="text/css" href="externals/GeoExt/resources/css/layerlegend.css">
        <link rel="stylesheet" type="text/css" href="externals/GeoExt/resources/css/gxtheme-gray.css">
        <script type="text/javascript" src="script/GeoExt.js"></script>

        <!-- gxp resources -->
        <link rel="stylesheet" type="text/css" href="externals/gxp/src/theme/all.css">
        <script type="text/javascript" src="script/gxp.js"></script>

        <!-- proj4js resources -->
        <script type="text/javascript" src="javascript/proj4js-compressed.js"></script>

        <!-- GeoExplorer resources -->
        <link rel="stylesheet" type="text/css" href="theme/app/geoexplorer.css" />
        <!--[if IE]><link rel="stylesheet" type="text/css" href="theme/app/ie.css"/><![endif]-->
        <link rel="stylesheet" type="text/css" href="theme/ux/colorpicker/color-picker.ux.css" />
        <script type="text/javascript" src="script/GeoExplorer.js"></script>
        <script type="text/javascript" src="script/ux.js"></script>
        
        <script type="text/javascript" src="javascript/WMSLayerPanel.js"></script>
        <script type="text/javascript" src="javascript/mareano_common.js"></script>        

        <!-- PrintPreview resources -->
        <link rel="stylesheet" type="text/css" href="externals/PrintPreview/resources/css/printpreview.css">
        <script type="text/javascript" src="script/PrintPreview.js"></script>

        <script>
            function init() {
                gxp.plugins.LayerTree.prototype.baseNodeText = "Bakgrunnskart";
                gxp.plugins.LayerTree.prototype.overlayNodeText = "Kartlag";
                /*gxp.plugins.AddLayers.prototype.addActionTip = "Legg til kartlag";
                gxp.plugins.AddLayers.prototype.addServerText = "Legg til ny server";
                gxp.plugins.AddLayers.prototype.addButtonText = "Legg til kartlag";
                gxp.plugins.AddLayers.prototype.availableLayersText = "Tilgjengelige kartlag";
                gxp.plugins.AddLayers.prototype.layerSelectionText = "Se tilgjengelig data fra:";
                gxp.plugins.AddLayers.prototype.doneText = "Ferdig";
                gxp.plugins.AddLayers.prototype.addButtonText = "Legg til kartlag";*/
                gxp.plugins.Navigation.prototype.tooltip = "Panorer kart";    
                gxp.plugins.NavigationHistory.prototype.previousTooltip = "Zoom til forrige utstrekning";
                gxp.plugins.NavigationHistory.prototype.nextTooltip = "Zoom til neste utstrekning";   
                gxp.plugins.ZoomToExtent.prototype.tooltip = "Zoom til synlig utstrekning";
                gxp.plugins.LayerProperties.prototype.toolTip = "kartlag egenskaper";
                gxp.plugins.Measure.prototype.lengthTooltip = "M\u00e5l lengde";
                gxp.plugins.Measure.prototype.areaTooltip = "M\u00e5l areal";
                // optionally set locale based on query string parameter
                if (GeoExt.Lang) {
                    GeoExt.Lang.set(OpenLayers.Util.getParameters()["locale"] || GeoExt.Lang.locale);
                }
                //Ext.BLANK_IMAGE_URL = "theme/app/img/blank.gif";
                OpenLayers.ImgPath = "theme/app/img/";
                // optionally set locale based on query string parameter
                if (GeoExt.Lang) {
                    GeoExt.Lang.set(OpenLayers.Util.getParameters()["locale"] || GeoExt.Lang.locale);
                }
                var app = new GeoExplorer.Composer({
                    <!-- authStatus: < status >, -->
                    proxy: "proxy/?url=",
                    printService: null,
                    about: {
                        title: "Mareano",
                        "abstract": "Copyright (C) 2005-2013 Mareano. Kartprojeksjon WGS84, UTM 33 N",
                        contact: "For more information, contact <a href='http://www.imr.no'>Institute of Marine Research</a>."
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
                            	/*
                            	 * Kartloesninger fra geonorge.
                            	 * 1. http://wms.geonorge.no - ubegrenset tilgang for HI (fordi de har IP rangen vaar) men en begrensning paa ca 3 kall i sekundet. Kjoerer raskest med singleTile
//                            	 * 2. opencache.statkart.no/gatekeeper - open loesning men begresning paa 10 000 kall pr dag. Tilet loesning
                            	 * 3. gatekeeper1.geonorge.no - ubegrenset med tilet tilgang men hver request krever en token som krever paalogging og som har timeout. Saa token forrandrer seg over tid
                            	 */
                                source: "ol",
                                type: "OpenLayers.Layer.WMS",
                                group: "background",
                                args: [
                                    "Norgeskart",
                                    "http://wms.geonorge.no/skwms1/wms.toporaster2",
                                    //"http://opencache.statkart.no/gatekeeper/gk/gk.open",
                                    {layers: "toporaster", format: "image/png", transparent: true, isBaseLayer: true}
                                    ,{singleTile:true}
                                ]
                            }, {
                                source: "ol",
                                type: "OpenLayers.Layer.WMS",
                                group: "background",
                                args: [
                                    "Norgeskart (gr\u00e5tone)",
                                    "http://wms.geonorge.no/skwms1/wms.topo2.graatone",
                                    //"http://opencache.statkart.no/gatekeeper/gk/gk.open",
                                    {layers: "topo2_graatone_WMS", format: "image/png", transparent: true, isBaseLayer: true}
                                    ,{singleTile:true}
                                ]
                            }, {
                                source: "ol",
                                type: "OpenLayers.Layer.WMS",
                                group: "background",
                                args: [
                                      	"Europa",
                                      	"http://wms.geonorge.no/skwms1/wms.europa",
                                      	//"http://opencache.statkart.no/gatekeeper/gk/gk.open",
                                      	{layers: "europa_wms", format: "image/jpeg", transparent: true, isBaseLayer: true}
                                      	//{layers: "europa", format: "image/jpeg", transparent: true, isBaseLayer: true}
                                      	,{singleTile:true}
                                ]
                            }				
                        ],
                        center: [1088474,8089849],
                        zoom: 2
                    }
                });
		
                var layers = [];
                <c:forEach var="hovedtema" items="${hovedtemaer}">
                    <c:forEach var="bilde" items="${hovedtema.bilder}">
                        <c:forEach var="kartlaget" items="${bilde.kart}">
                            var OLRecord = gxp.plugins.OLSource.prototype.createLayerRecord({
                                source: "ol",
                                type: "OpenLayers.Layer.WMS",
                                group: "${bilde.gruppe}",
                                visibility: ${bilde.visible},
                                properties: "mareano_wmslayerpanel",           
                                //properties: "${kartlaget.id}",
                                //id: "${kartlaget.id}",   
                                args: [
                                    "${kartlaget.title}",
                                    "${kartlaget.url}",
                                    {layers: "${kartlaget.layers}", format: "image/png", transparent: true},
                                    {
                                        opacity: 0.5,
                                        metadata: {
                                            keyword: "${kartlaget.keyword}",
                                            //'abstract': '${kartlaget.abstracts}', //causes error: missing } after property list genereres daglig fra OD's operasjonelle databaser. Detaljeringsg...
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
                            layers.push(OLRecord);
                        </c:forEach>
                    </c:forEach>
                </c:forEach>              
            	var store = new GeoExt.data.LayerStore();
                store.add(layers);   
	                
                /**
                 * Whenever a layer is turned on or off - send a request to local server (this server) to see
                 * if layer also should include Spesialpunkt from Mareano.
                 */
                app.on("ready", function() {
                    Ext.getCmp('topPanelHeading').update('${heading}');
                    
                	loadMareano( this.mapPanel, app, layers );
                	
                    store.each(function(record) {
                    	if (record.getLayer().visibility === true) {
	                    	var clone = record.clone();
	                    	clone.set("group", "default");
	                    	clone.getLayer().metadata['kartlagId'] = record.getLayer().metadata['kartlagId'];
	                    	this.mapPanel.layers.add(clone);
	                    	displayLegendGraphics(clone.getLayer().metadata['kartlagId']);
                    	}
                    }, this);
                    
                    var treeRoot = Ext.getCmp('thematic_tree');
                    var mergedSomeHovedtema;
                    <c:forEach var="hovedtema" items="${hovedtemaer}">
                        mergedSomeHovedtema = new Ext.tree.TreeNode({
                            text: "${hovedtema.hovedtema}"
                        });			
                        <c:forEach var="bilde" items="${hovedtema.bilder}">
	                    	var group = addLayerToGroup("${bilde.gruppe}","${bilde.gruppe}", this.map, this.mapPanel, layers, store, app);
	                    	if (group.attributes.expanded === true) {
	                    		mergedSomeHovedtema.expanded = true;
	                    	}
	                    	mergedSomeHovedtema.appendChild( group );
                            </c:forEach>
                        treeRoot.getRootNode().appendChild( mergedSomeHovedtema );
                    </c:forEach>
                    /***********************************/
                    treeRoot.getRootNode().appendChild( mergedSomeHovedtema );
                    /***********************************/
                    var tmp = Ext.ComponentMgr.all.find(function(c) {
                        if (c instanceof Ext.Button) {
                            if (c.tooltip=="Publish map") {c.setTooltip("Publiser kartet");
                            } else if (c.tooltip=="Save map") {c.setTooltip("Lagre kartet");
                            } else if (c.tooltip=="Draw polygon") {c.setTooltip("tegn polygon");
                            } else if (c.tooltip=="Draw line") {c.setTooltip("tegn linje");
                            } else if (c.tooltip=="Add layer") {c.setTooltip("Legg til kartlag");
                            } else if (c.tooltip=="Remove layer") {c.setTooltip("Fjern kartlag");
                            } else if (c.tooltip=="Layer properties") {c.setTooltip("Kartlag egenskaper");
                            } else if (c.tooltip=="Manage Layer stiles") {c.setTooltip("Behandle kartlagstiler");
                            } else if (c.tooltip=="Pan") {c.setTooltip("panorere kartet");
                            } else if (c.tooltip=="Get Feature Info") {c.setTooltip("Hent Feature Info");
                            } else if (c.tooltip=="Create a new feature") {c.setTooltip("Lag en ny feature");
                            } else if (c.tooltip=="Edit existing feature") {c.setTooltip("Editer eksisterende feature");
                            } else if (c.tooltip=="Measure") {c.setTooltip("M&aring;l");
                            } else if (c.tooltip=="Zoom in") {c.setTooltip("Zoom inn");
                            } else if (c.tooltip=="Zoom out") {c.setTooltip("Zoom ut");
                            } else if (c.tooltip=="Zoom to last extent") {c.setTooltip("Zoom til forrige utstrekning");
                            } else if (c.tooltip=="Zoom to next extent") {c.setTooltip("Zoom til neste utstrekning");
                            } else if (c.tooltip=="Zoom to visible extent") {c.setTooltip("Zoom til synlig utstrekning");
                            } else if (c.tooltip=="Print map") {c.setTooltip("Skriv ut kartet");
                            } else if (c.tooltip=="Go to coordinat") {c.setTooltip("til koordinat");}
                        }
                        if(c instanceof Ext.menu.CheckItem) {
                            if(c.text=="Length") c.text = "Lengde";
                            if(c.text=="Area") c.setText("Areal");
                        }
                    });
                });
            }
               
            function openURI(uri){ // needed by GMLselected(evt)
            	window.open(uri,'Punktdata');
            }
        </script>
    </head>
    <body onload="init()">
        <div id="choises" style="display:none"></div>
        <form id="hidden_pdf" method="post"></form>
    </body>
</html>
<!DOCTYPE HTML SYSTEM>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 
<html>
    <head>
        <title>Mareano</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="shortcut icon" href="theme/app/img/mareanoLogo.png">
        <!-- Ext resources -->
        <link rel="stylesheet" type="text/css" href="externals/ext/resources/css/ext-all.css">
        <link rel="stylesheet" type="text/css" href="externals/ext/resources/css/xtheme-gray.css">
        <!-- script type="text/javascript" src="externals/ext/adapter/ext/ext-base-debug.js"></script -->
        <script type="text/javascript" src="externals/ext/adapter/ext/ext-base.js"></script>
        <!-- script type="text/javascript" src="externals/ext/ext-all-debug-w-comments.js"></script -->
        <script type="text/javascript" src="externals/ext/ext-all.js"></script>
			
        <meta http-equiv="X-UA-Compatible" content="IE=IE8" >
        <!--script type="text/javascript" src="javascript/googleAnalyticsStatistics.js"></script -->

        <script type="text/javascript" src="javascript/jquery-1.6.2.min.js"></script>
        <script type="text/javascript">jQuery.noConflict();</script>

		<link rel="stylesheet" type="text/css" href="css/mareano.css">
		<link rel="stylesheet" type="text/css" href="css/mareanoMenu.css">
        
        <!-- Mareano.no -->
        <link href="theme/imr/imr.css" rel="stylesheet" type="text/css">
<!--         <link rel="stylesheet" type="text/css" href="http://www.mareano.no/kart/styles/mareanoStyle.css"> -->
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

        <!-- PrintPreview resources 
        <link rel="stylesheet" type="text/css" href="externals/PrintPreview/resources/css/printpreview.css">
        <script type="text/javascript" src="script/PrintPreview.js"></script>-->
        
        <script type="text/javascript" src="script/mareano.js"></script>

        <script>
        	var app;
            function init() {
                gxp.plugins.LayerTree.prototype.baseNodeText = "Base Layer";
                gxp.plugins.LayerTree.prototype.overlayNodeText = "Overlays";
                                
                //Ext.BLANK_IMAGE_URL = "theme/app/img/blank.gif";
                OpenLayers.ImgPath = "theme/app/img/";
                GeoExt.Lang.set('en');
                app = new GeoExplorer.Composer({
                    <!-- authStatus: < status >, -->
                    proxy: "proxy/?url=",
                    printService: null,
                    about: {
                        title: "Mareano",
                        "abstract": "Copyright (C) 2005-2013 Mareano. Map projection WGS84, UTM 33 N",
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
                            	source: "ol",
                                type: "OpenLayers.Layer.WMS",
                                group: "background",
                                args: [
                                       "Norway",
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
                                       "Norway (gray scale)",
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
                                      	"http://maps.imr.no/geoserver/gwc/service/wms",
                                      	//"http://opencache.statkart.no/gatekeeper/gk/gk.open",
                                      	{layers: "geonorge_europa:Europa_WMS", format: "image/jpeg", transparent: true, isBaseLayer: true}
                                      	//{layers: "europa", format: "image/jpeg", transparent: true, isBaseLayer: true}
                                      	,{singleTile:false}
                                ]
                            }, {
                                source: "ol",
                                type: "OpenLayers.Layer.WMS",
                                group: "background",
                                args: [
                                      	"Gebco shaded relief in grayscale",
                                      	"http://maps.imr.no/geoserver/gwc/service/wms",
                                      	{layers: "geonorge:geonorge_norge_skyggerelieff", format: "image/jpeg", transparent: true, isBaseLayer: true},
                                      	{singleTile:false}
                                ]                            
                            }, {                            	
                                source: "ol",
                                type: "OpenLayers.Layer.WMS",
                                group: "background",
                                args: [
                                      	"Europa - white background",
                                      	"http://maps.imr.no/geoserver/gwc/service/wms",
                                      	//"http://opencache.statkart.no/gatekeeper/gk/gk.open",
                                      	{layers: "geonorge_europa:geonorge_europa_hvit_bakgrunn",format: "image/jpeg", transparent: true, isBaseLayer: true}
                                      	//{layers: "europa", format: "image/jpeg", transparent: true, isBaseLayer: true}
                                      	,{singleTile:false}
                                ]
                            }, {
                                source: "ol",
                                type: "OpenLayers.Layer.WMS",
                                group: "background",
                                args: [
                                      	"Europa og Gebco",
                                      	"http://maps.imr.no/geoserver/gwc/service/wms",
                                      	{layers: "geonorge:barents_watch_WMS", format: "image/jpeg", transparent: true, isBaseLayer: true}
                                      	,{singleTile:false}
                                ]
                            }	      
                        ],
                        center: [1088474,7489849],
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
                                            'abstract': '${kartlaget.abstracts}',
                                            'kartlagId': '${kartlaget.id}'
                                        },
                                        minScale: ${kartlaget.scalemax}*(96/0.0254),
                                        maxScale: (${kartlaget.scalemin} > 0) ? ${kartlaget.scalemin}*(96/0.0254) : 1,
                                        units: "m",
                                        maxExtent: [
                                            ${kartlaget.exGeographicBoundingBoxWestBoundLongitude},
                                            ${kartlaget.exGeographicBoundingBoxSouthBoundLatitude},
                                            ${kartlaget.exGeographicBoundingBoxEastBoundLongitude},
                                            ${kartlaget.exGeographicBoundingBoxNorthBoundLatitude}
                                        ],
                                        singleTile:true,
                                        buffer: 0, 
                                        ratio: 1
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
                                visibility: ${bilde.visible},
                                properties: "mareano_wmslayerpanel",           
                                args: [
                                    "${kartlaget.title}",
                                    "${kartlaget.url}",
                                    {layers: "${kartlaget.layers}", format: "image/png", transparent: true},
                                    {
                                        opacity: 1,
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
                            generelleLayers.push(OLRecord2);
                        </c:forEach>
                    </c:forEach>
            		}
                </c:forEach> 
             	store.add(generelleLayers);
                /**
                * Whenever a layer is turned on or off - send a request to local server (this server) to see
                * if layer also should include Spesialpunkt from Mareano.
                */
                app.on("ready", function() {
                    Ext.getCmp('topPanelHeading').update('${heading}');
                	loadMareano( this.mapPanel, app, layers );

                    this.mapPanel.layers.each(function(record) {
                         if (record.get('visibility') === true && record.getLayer().metadata['kartlagId'] !== undefined) {
                             displayLegendGraphics(record.getLayer().metadata['kartlagId']);
                         }
                    });

                    store.each(function(record) {
                    	if (record.getLayer().visibility === true) {
                                var clone = record.clone();
                                clone.set("group", "default");
                                clone.getLayer().metadata['kartlagId'] = record.getLayer().metadata['kartlagId'];
                                var idx = this.mapPanel.layers.findBy(function(r) {
                                    return (record.getLayer().metadata['kartlagId'] === r.getLayer().metadata['kartlagId']);
                                });
                                if (idx === -1) {
                                    this.mapPanel.layers.add(clone);
                                    displayLegendGraphics(clone.getLayer().metadata['kartlagId']);
                                }
                    	}
                    }, this);
                    
                    /***********************************/
                    var treeRoot = Ext.getCmp('thematic_tree');
                    var mergedSomeHovedtema;
                    <c:forEach var="hovedtema" items="${hovedtemaer}">
                    	if ( !("${hovedtema.hovedtema}" == "generelle") ) {
                        mergedSomeHovedtema = new Ext.tree.TreeNode({
                            text: "${hovedtema.hovedtema}"
                        });			
                        <c:forEach var="bilde" items="${hovedtema.bilder}">
                        	var group = addLayerToGroup("${bilde.gruppe}","${bilde.gruppe}", this.map, this.mapPanel, layers, store, app);
                        	if (group.attributes.expanded === true) {
                        		mergedSomeHovedtema.expanded = true;
                        	}
                                group.attributes.maxExtent = [
                                    ${bilde.startextentMinx},
                                    ${bilde.startextentMiny},
                                    ${bilde.startextentMaxx},
                                    ${bilde.startextentMaxy}
                                ];
                        	mergedSomeHovedtema.appendChild( group );
                        </c:forEach>
                        treeRoot.getRootNode().appendChild( mergedSomeHovedtema );
                    	}
                    </c:forEach>
                    treeRoot.getRootNode().appendChild( mergedSomeHovedtema );
                    /***********************************/
                    var rootRightTree = Ext.getCmp('layertree');
                    rootRightTree.getRootNode().appendChild( addGenerelleLayerToGroup("generelle", "General Maps", this.map, this.mapPanel, generelleLayers, store, app) );                    
                    /***********************************/
                    var tmp = Ext.ComponentMgr.all.find(function(c) {
                    	if( c instanceof Ext.Button ) {
                        	if (c.tooltip=="Publiser kartet") {c.setTooltip("Publish map");
                        	} else if (c.tooltip=="Lagre kartet") {c.setTooltip("Save map");
                        	} else if (c.tooltip=="tegn polygon") {c.setTooltip("Draw polygon");
                        	} else if (c.tooltip=="tegn linje") {c.setTooltip("Draw line");
                        	} else if (c.tooltip=="Legg til kartlag") {c.setTooltip("Add layer");
                        	} else if (c.tooltip=="Fjern kartlag") {c.setTooltip("Remove layer");
                        	} else if (c.tooltip=="Kartlag egenskaper") {c.setTooltip("Layer properties");
                        	} else if (c.tooltip=="Behandle kartlagstiler") {c.setTooltip("Manage Layer stiles");
                        	} else if (c.tooltip=="panorere kartet") {c.setTooltip("Pan");
                        	} else if (c.tooltip=="Hent Feature Info") {c.setTooltip("Get Feature Info");
                        	} else if (c.tooltip=="Lag en ny feature") {c.setTooltip("Create a new feature");
                        	} else if (c.tooltip=="Editer eksisterende feature") {c.setTooltip("Edit existing feature");
                        	} else if (c.tooltip=="M&aring;l") {c.setTooltip("Measure");
                        	} else if (c.tooltip=="Zoom inn") {c.setTooltip("Zoom in");
                        	} else if (c.tooltip=="Zoom ut") {c.setTooltip("Zoom out");
                        	} else if (c.tooltip=="Zoom til forrige utstrekning") {c.setTooltip("Zoom to last extent");
                        	} else if (c.tooltip=="Zoom til neste utstrekning") {c.setTooltip("Zoom to next extent");
                        	} else if (c.tooltip=="Zoom til synlig utstrekning") {c.setTooltip("Zoom to visible extent");
                        	} else if (c.tooltip=="Skriv ut kartet") {c.setTooltip("Print map");
                        	} else if (c.tooltip=="til koordinat") {c.setTooltip("Go to coordinat");}
                    	}
                        if( c instanceof Ext.menu.CheckItem ) {
                            if ( c.text=="Lengde" ) c.text = "Length";
        			if ( c.text == "Areal" ) c.setText("Area");
                        }  
                    });
                    
                    addDropdownmenuToMareanoMenuIfIe();
                });
            }
            
            function addDropdownmenuToMareanoMenuIfIe(){
                var sfEls = document.getElementById("nav").getElementsByTagName("LI");
              	for (var i=0; i<sfEls.length; i++) {
	                    sfEls[i].onmouseover=function() {
	                      this.className+=" sfhover";
                    }
	                sfEls[i].onmouseout=function() {
	                    this.className=this.className.replace(new RegExp(" sfhover\\b"), "");
                	}
				}
            }
            
            function openURI(uri) { // needed by GMLselected(event)
            	window.open(uri,'Punktdata');
            }
        </script>
    </head>
    <body onload="init()">
    </body>
</html>

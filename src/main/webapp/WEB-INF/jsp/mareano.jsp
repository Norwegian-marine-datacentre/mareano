<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 
<html>
    <head>
        <title id="page-title">Mareano</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="shortcut icon" href="theme/app/img/favicon.ico">

        <!-- Ext resources -->
        <link rel="stylesheet" type="text/css" href="externals/ext/resources/css/ext-all.css">
        <link rel="stylesheet" type="text/css" href="externals/ext/resources/css/xtheme-gray.css">
        <script type="text/javascript" src="externals/ext/adapter/ext/ext-base-debug.js"></script>
        <script type="text/javascript" src="externals/ext/ext-all-debug-w-comments.js"></script>
        <style type="text/css">
            select.fishexchange{
                width:150px;
                border:1px solid #000;
                font-family:Georgia,Serif;
            }
            .olImageLoadError { 
                /* when OL encounters a 404, don't display the pink image */
                display: none !important;
            }
        </style>
        <meta http-equiv="X-UA-Compatible" content="IE=IE8" >
        <!--script type="text/javascript" src="javascript/googleAnalyticsStatistics.js"></script -->

        <script type="text/javascript" src="javascript/jquery-1.6.2.min.js"></script>
        <script type="text/javascript">jQuery.noConflict();</script>

        <link href="theme/imr/imr.css" rel="stylesheet" type="text/css">
        <script type="text/javascript" src="javascript/layerIcon.js"></script>
        <script type="text/javascript" src="javascript/combobox.js"></script>
        <script type="text/javascript" src="javascript/controlFishexchangeDialog.js"></script>
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

        <!-- PrintPreview resources -->
        <link rel="stylesheet" type="text/css" href="externals/PrintPreview/resources/css/printpreview.css">
        <script type="text/javascript" src="script/PrintPreview.js"></script>

        <script>
            function init() {
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
                        layers: [
                            {
                                source: "ol",
                                type: "OpenLayers.Layer.WMS",
                                group: "background",
                                args: [
                                    "Norgeskart",
                                    "http://opencache.statkart.no/gatekeeper/gk/gk.open",
                                    {layers: "toporaster2", format: "image/jpeg", transparent: true, isBaseLayer: true}
                                ]
                            }, {
                                source: "ol",
                                type: "OpenLayers.Layer.WMS",
                                group: "background",
                                args: [
                                    "Norgeskart (gråtone)",
                                    "http://opencache.statkart.no/gatekeeper/gk/gk.open",
                                    {layers: "topo2graatone", format: "image/jpeg", transparent: true, isBaseLayer: true}
                                ]
                            }, {
                                source: "ol",
                                type: "OpenLayers.Layer.WMS",
                                group: "background",
                                args: [
                                    "Europakart (gråtone)",
                                    "http://atlas2.nodc.no/geoserver/wms",
                                    {layers: "bakgrunnskart_nymareano", format: "image/jpeg", transparent: true, isBaseLayer: true}
                                ]
                            }
            <c:forEach var="hovedtema" items="${hovedtemaer}">
                <c:forEach var="bilde" items="${hovedtema.bilder}">
                    <c:forEach var="kartlaget" items="${bilde.kart}">
                                        ,{source: "ol",
                                            type: "OpenLayers.Layer.WMS",
                                            group: "${bilde.gruppe}",
                                            visibility: ${bilde.visible},
                                            properties:"${kartlaget.id}",
                                            args: [
                                                "${kartlaget.title}",
                                                "${kartlaget.url}",
                                                {layers: "${kartlaget.layers}", format: "image/png", transparent: true},
                                                {opacity: 0.5}
                                            ]
                                        }
                    </c:forEach>
                </c:forEach>
            </c:forEach> 				
                            ],
                            center: [1088474,8089849],
                            zoom: 2
                        }
                    });
		
                    app.on("ready", function() {
                        var treeRoot = Ext.ComponentMgr.all.find(function(c) {
                            return c instanceof Ext.tree.TreePanel;
                        });
                        var mergedSomeHovedtema;
            <c:forEach var="hovedtema" items="${hovedtemaer}">
                        mergedSomeHovedtema = new Ext.tree.TreeNode({
                            text: "${hovedtema.hovedtema}"
                        });			
                <c:forEach var="bilde" items="${hovedtema.bilder}">
                            mergedSomeHovedtema.appendChild( addLayerToGroup("${bilde.gruppe}","${bilde.gruppe}", this.map, this.mapPanel) );
                </c:forEach>
                            treeRoot.getRootNode().appendChild( mergedSomeHovedtema );
            </c:forEach>			

                        function addLayerToGroup( gruppeNavn, gruppeText, map, mapPanel ) {
                            var indexOfWMSgruppe = [];
                            var layerName = [];
                            for (var i = map.layers.length-1;i>=0;--i) {
                                if ( map.layers[i].group == gruppeNavn ) {
                                    for( var j= mapPanel.map.layers.length-1; j>=0;--j) {
                                        if ( mapPanel.map.layers[j].params != null && map.layers[i].args[2].layers == mapPanel.map.layers[j].params['LAYERS'] ) {
                                            //alert("layer:"+map.layers[i].args[2].layers+" param:"+mapPanel.map.layers[j].params['LAYERS']);
                                            indexOfWMSgruppe.push( j );
                                            layerName.push( map.layers[i].args[2].layers );
                                        }
                                    }
                                }
                            }

                            var tmpLoader = new GeoExt.tree.LayerLoader({
                                filter: function(record) {
                                    var featureInfoEvents = [];
                                    /** Add event for getFeatureInfo */
                                    function setThisHTML(response) {
                                        var from = response.responseText.indexOf("<body>");
                                        var to = response.responseText.indexOf("</body>");
                                        var bodyStr = response.responseText.substring(from, to);
                                        /** Ugly - fix by not sending request when click outside layer */
                                        if ( response.responseText != null && response.responseText != "" && bodyStr.length > 14 ) {
                                            //Ext.MessageBox.show( 'Feature Info', response.responseText );
                                            winPanel = new Ext.Window({title: 'Feature Info',autoHeight: true,width:300,html: response.responseText});
                                            winPanel.show();
                                            //Ext.MessageBox.show( {title: 'Feature Info', msg: response.responseText, setAutoScroll:true} );
                                        }
                                    };
                                    var tmpMap = mapPanel.map;
                                    if (record.get("layer").url!=null && !(record.get("layer") instanceof OpenLayers.Layer.Vector) &&
                                        record.get("layer").url.indexOf( 'http://maps.imr.no/geoserver/wms' ) != -1 ) {

                                        var isRegFlag = 0;
                                        if ( featureInfoEvents != [] ) {
                                            for ( var i=0, len=featureInfoEvents.length; i<len; i++ ) {
                                                if ( featureInfoEvents[i] == record.get("layer") ) {
                                                    isRegFlag = 1;
                                                }
                                            }
                                        }

                                        if ( isRegFlag == 0 ) {
                                            featureInfoEvents.push( record.get("layer") );
                                            tmpMap.events.register('click', record.get("layer"), function (e) {

                                                if ( record.get("layer").getVisibility() ) {
                                                    var params = {
                                                        REQUEST: "GetFeatureInfo",
                                                        EXCEPTIONS: "application/vnd.ogc.se_xml",
                                                        BBOX: tmpMap.getExtent().toBBOX(),
                                                        X: e.xy.x,
                                                        Y: e.xy.y,
                                                        INFO_FORMAT: 'text/html',
                                                        QUERY_LAYERS: record.get("layer").params['LAYERS'],
                                                        FEATURE_COUNT: 50,
                                                        Layers: record.get("layer").params['LAYERS'],
                                                        Styles: '',
                                                        Srs: 'EPSG:32633',
                                                        WIDTH: tmpMap.size.w,
                                                        HEIGHT: tmpMap.size.h,
                                                        format: 'image/jpeg'
                                                    };
                                                    var returned = OpenLayers.loadURL("http://maps.imr.no/geoserver/wms", params, this, setThisHTML);
                                                    //returned.abort(); //to avoid two popups
                                                    OpenLayers.Event.stop(e);
                                                }
                                            });
                                        }
                                    }
                                    /** adding the right layer to the right container */
                                    for( var i= layerName.length-1; i>=0; --i ) {
                                        if ( record.get("group") == gruppeNavn ) {
                                            return true;
                                        }
                                    }
                                    return false;
                                },
                                createNode: function(attr) {
                                    var layerRecord = this.store.getByLayer(attr.layer);
                                    var cssBgImg = "";
                                    var url = "";
                                    if ( layerRecord.getLayer() instanceof OpenLayers.Layer.WMS ) {
                                        url = layerRecord.getLayer().url;
                                    }
                                    cssBgImg = getLayerIcon(url);
                                    attr.id=layerRecord.data.id;
                                    attr.iconCls = cssBgImg;
                                    return GeoExt.tree.LayerLoader.prototype.createNode.call(this, attr);
                                }
                            });

                            var layerContainerGruppe = new GeoExt.tree.LayerContainer({
                                text: gruppeText,
                                checked: false,
                                listeners: {
                                    "checkchange": function(node, checked) { //routene for setting all subnodes if parent is checked
                                        node.expand();
                                        node.eachChild(function(child){
                                            child.ui.toggleCheck(checked);
                                        });
                                    }
                                },
                                layerStore: mapPanel.layers,
                                loader: tmpLoader
                            });

                            return layerContainerGruppe;
                        }
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
                                } else if (c.tooltip=="Show Google Earth") {c.setTooltip("Vis Google Earth");
                                } else if (c.tooltip=="Go to coordinat") {c.setTooltip("til koordinat");}
                            }
                            if(c instanceof Ext.menu.CheckItem) {
                                if(c.text=="Length") c.text = "Lengde";
                                if(c.text=="Area") c.setText("Areal");
                            }
                        });
                    });

                    //Adding overviewmap and keyboard defaults
                    app.on("ready", function() {
                        var layerOptions = {
                            units: "m",
                            projection: "EPSG:32633",
                            maxExtent: new OpenLayers.Bounds( -4101096.2210526327,5925725.768421048,4999746.221052637,9135005.768421048 ),
                            minResolution: new OpenLayers.Bounds( -4101096.2210526327,5925725.768421048,4999746.221052637,9135005.768421048 ),
                            bounds: new OpenLayers.Bounds( -4101096.2210526327,5925725.768421048,4999746.221052637,9135005.768421048 ),
                            theme: null
                        };
                        var ol_wms2 = new OpenLayers.Layer.WMS(
                        "geonorge",
                        "http://wms.geonorge.no/skwms1/wms.europa?brukerid=EHAV_MOEEND&passord=spartakus234&VERSION=1.1.1&SERVICE=WMS",
                        {layers: "Land,Vmap0Land,Vmap0Kystkontur"}
                    );
                        var tmpLayerOptions = {layers: [ol_wms2], mapOptions: layerOptions, maximized: false, minRatio: 48, maxRatio: 72};
                        this.mapPanel.map.addControl(new OpenLayers.Control.OverviewMap(tmpLayerOptions));
                        this.mapPanel.map.addControl(new OpenLayers.Control.KeyboardDefaults());

                        /*** Fix to avoid vector layer below baselayers ***/
                        for ( var i=this.map.layers.length-1; i>=0; --i ) {
                            if ( this.mapPanel.map.layers[i] instanceof OpenLayers.Layer.Vector ) {
                                this.mapPanel.map.setLayerIndex( this.mapPanel.map.layers[i], 33 );
                            }
                        }
                    });

                    /**
                     * Whenever a layer is turned on or off - send a request to local server (this server) to see
                     * if layer also should include Spesialpunkt from Mareano.
                     ***/
                    app.on("ready", function() {
                        var extent = this.mapPanel.map.getExtent() + ""; // hack so jQuery can access extjs/openlayers object
                        var extMap = this.mapPanel.map;	
                        var mapOfGMLspesialpunkt = new Object();
                        var kartlagInfoState = "";
                        Ext.getCmp('topPanelHeading').update('${heading}'); 
                        jQuery('body').change(function(event) {
                            if ( jQuery(event.target).is(':checkbox') && jQuery(event.target).is(':checked') ) {
                                jQuery.each(jQuery(event.target).siblings(), function() {
                                    if ( jQuery(this).is("img.x-tree-elbow-plus") ) {
                                        jQuery(this).click(); //if clickbox is a kartbilde it will have img and we click it to expand child nodes (pluss sign)
                                    }
                                    if ( jQuery(this).text() != "" ) {
                                        var kartlagId = jQuery(this).closest("[ext\\:tree-node-id]").attr("ext:tree-node-id");
                                        if( kartlagId.match(/[^0-9]/) ) { //clicked element is kartbilde not kartlag
                                            var startForKartbilde = jQuery(this).closest("[ext\\:tree-node-id]");
                                            startForKartbilde = jQuery(startForKartbilde).parent();
                                            jQuery(startForKartbilde).find("[ext\\:tree-node-id]").each(function(index, value){
                                                kartlagId = jQuery(value).attr("ext:tree-node-id");
                                                if( kartlagId.match(/^[0-9]+$/) ) {			        				
                                                    addAKartlag(kartlagId);
                                                }
                                            });
                                        } else  {
                                            addAKartlag(kartlagId);
                                        }
                                    }
                                });	          
                            } else if (jQuery(event.target).is(':checkbox') && !jQuery(event.target).is(':checked') ) {
                                jQuery.each(jQuery(event.target).siblings(), function() {
                                    if ( jQuery(this).is("img.x-tree-elbow-plus") ) {
                                        jQuery(this).click(); //if clickbox is a kartbilde it will have img and we click it to expand child nodes (pluss sign)
                                    }
                                    if ( jQuery(this).text() != "" ) {
                                        var kartlagId = jQuery(this).closest("[ext\\:tree-node-id]").attr("ext:tree-node-id");
                                        if( kartlagId.match(/[^0-9]/) ) { //clicked element is kartbilde not kartlag
                                            var startForKartbilde = jQuery(this).closest("[ext\\:tree-node-id]");
                                            startForKartbilde = jQuery(startForKartbilde).parent();
                                            jQuery(startForKartbilde).find("[ext\\:tree-node-id]").each(function(index, value){
                                                kartlagId = jQuery(value).attr("ext:tree-node-id");
                                                if( kartlagId.match(/^[0-9]+$/) ) {			        				
                                                    removeAKartlag(mapOfGMLspesialpunkt, kartlagId);
                                                }
                                            });
                                        } else {	        			
                                            removeAKartlag(mapOfGMLspesialpunkt, kartlagId);
                                        }
                                    }
                                }); 
                            }
                        });

                        function addAKartlag(kartlagId) {
                            displayLegendGraphics(kartlagId);
                            displaySpesialpunkt(kartlagId);
                        }

                        function removeAKartlag(mapOfGMLspesialpunkt, kartlagId) {
                            if ( mapOfGMLspesialpunkt[kartlagId] != null ) {
                                extMap.removeLayer(mapOfGMLspesialpunkt[kartlagId], false);
                            }
                            var legendDiv = '#'+kartlagId;
                            jQuery(legendDiv).remove();

                            var temp = jQuery("<div>").html(kartlagInfoState); //fjern kartlaginfo 
                            jQuery(temp).find(legendDiv+'tips').remove();
                            kartlagInfoState = jQuery(temp).html();			
                            if ( Ext.getCmp('tips').rendered ) {
                                Ext.getCmp('tips').update(kartlagInfoState);
                            } else {
                                Ext.getCmp('tips').html = kartlagInfoState;
                            }
                        }
	    
                        function displaySpesialpunkt(kartlagId) {
                            jQuery.ajax({
                                type: 'get',
                                url: "spring/spesialpunkt",
                                contentType: "application/json",
                                data: {
                                    extent : extent,
                                    kartlagId: kartlagId
                                },
                                success:function(data){
                                    if ( data.noSpesialpunkt == false) {
                                        var styleMap = new 	OpenLayers.StyleMap({
                                            'default':{externalGraphic: "theme/imr/images/geofotoSpesialpunkt.png"}
                                        });
                                        var snitt = new OpenLayers.Layer.GML("Spesialpunkt","spring/getgml", {styleMap: styleMap});   
                                        mapOfGMLspesialpunkt[kartlagId] = snitt;	    
                                        snitt.setVisibility(true);	   
                                        snitt.events.register( "featureselected", snitt, GMLselected );
                                        extMap.addLayers([snitt]);
                                        var control = new OpenLayers.Control.SelectFeature( snitt );
                                        extMap.addControl( control );
                                        control.activate();		                	
                                    }  
                                }
                            });            
                        }
	    
                        function displayLegendGraphics(kartlagId) {
                            jQuery.ajax({
                                type: 'get',
                                url: "spring/legend",
                                contentType: "application/json",
                                data: {
                                    kartlagId: kartlagId,
                                    language: "norsk"
                                },
                                success:function(data) {
                                    //if ( data.legendText != null ) {
                                    var currentLegend;
                                    jQuery('#newLegend').children().each(function(index, value){
                                        jQuery(value).children().each(function(index, value){
                                            currentLegend = jQuery(value).html();
                                        });	        
                                    });
                                    buildLegendGraphicsHTML( currentLegend, kartlagId, data );
                                    visKartlagInfoHTML( kartlagId, data );
                                    //}
                                }
                            }); 
                        }

                        function buildLegendGraphicsHTML( currentLegend, kartlagId, data ) {
                            var legendGraphicsHTML = currentLegend+'<div id="'+kartlagId+'">';
                            for ( var i=0; i < data.legends.length; i++ ) {
                                if ( i > 0 ) {
                                    legendGraphicsHTML += '<div>';     
                                }
                                legendGraphicsHTML += '<img src="' + data.legends[i].url + '"/>' + data.legends[i].text;
                                if ( i > 0 ) {
                                    legendGraphicsHTML += '</div>';     
                                }
                            } 
                            legendGraphicsHTML += '</div>';
                            Ext.getCmp('newLegend').update(legendGraphicsHTML);
                        }

                        /**
                         * bug: I have a panel in a tab that is not shown. 
                         * I call update(somehtml) on that panel. The panel's html body is not updated. 
                         * After I show the panel for the first time, all future updates() behave correctly whether it is hidden or shown
                         * http://www.sencha.com/forum/archive/index.php/t-103797.html
                         **/
                        function visKartlagInfoHTML(kartlagId, data) {
                            var infoHTML = '<div id="'+kartlagId+'tips"><b>'+data.kartlagInfo.kartlagInfoTitel+':</b>'+data.kartlagInfo.text+'</div>';

                            kartlagInfoState += infoHTML;
                            if ( Ext.getCmp('tips').rendered ) {
                                Ext.getCmp('tips').update(kartlagInfoState);
                            } else {
                                Ext.getCmp('tips').html = kartlagInfoState;
                            }
                        }
	    
                        function GMLselected (evt) {
                            if ( evt.feature.data.type == "bilder" ) {
                                Ext.MessageBox.show({
                                    title:evt.feature.data.name, 
                                    msg:'<a href="' + evt.feature.data.description + '" TARGET="_blank"><img src=" '+evt.feature.data.description+'" width=400 height=400 /></a>'});
                            } else if ( evt.feature.data.type == "video" ) {
                                Ext.MessageBox.show({
                                    title:evt.feature.data.name, 
                                    msg:'<embed width="330" height="200" controls="TRUE" autoplay="TRUE" loop="FALSE" src="'+evt.feature.data.description+'">'});
                            } else if ( evt.feature.data.type == "pdf" ) { // finnes ennå ikke
                                Ext.MessageBox.show({title:evt.feature.data.name,msg:'<a href="' + evt.feature.data.description + '" TARGET="_blank">' + evt.feature.data.name + '</a>'});    
                            } else if ( evt.feature.data.type == "text" ) {
                                jQuery.get('/geodata/proxy?url=http://atlas.nodc.no/website/mareano/' + evt.feature.data.description, function(response) { 
                                    Ext.MessageBox.show({title:evt.feature.data.name, msg: response}); 
                                });
	
                            }
                        }
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
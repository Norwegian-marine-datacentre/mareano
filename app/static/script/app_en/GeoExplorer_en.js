/**
 * Copyright (c) 2009-2011 The Open Planning Project
 */

Ext.USE_NATIVE_JSON = true;

(function() {
    // backwards compatibility for reading saved maps
    // these source plugins were renamed after 2.3.2
    Ext.preg("gx_wmssource", gxp.plugins.WMSSource);
    Ext.preg("gx_olsource", gxp.plugins.OLSource);
    Ext.preg("gx_googlesource", gxp.plugins.GoogleSource);
    Ext.preg("gx_bingsource", gxp.plugins.BingSource);
    Ext.preg("gx_osmsource", gxp.plugins.OSMSource);
})();

var MAREANO_EN = "mareano_en.html";
/**
 * api: (define)
 * module = GeoExplorer
 * extends = gxp.Viewer
 */

/** api: constructor
 *  .. class:: GeoExplorer(config)
 *     Create a new GeoExplorer application.
 *
 *     Parameters:
 *     config - {Object} Optional application configuration properties.
 *
 *     Valid config properties:
 *     map - {Object} Map configuration object.
 *     sources - {Object} An object with properties whose values are WMS endpoint URLs
 *
 *     Valid map config properties:
 *         projection - {String} EPSG:xxxx
 *         units - {String} map units according to the projection
 *         maxResolution - {Number}
 *         layers - {Array} A list of layer configuration objects.
 *         center - {Array} A two item array with center coordinates.
 *         zoom - {Number} An initial zoom level.
 *
 *     Valid layer config properties (WMS):
 *     name - {String} Required WMS layer name.
 *     title - {String} Optional title to display for layer.
 */
var GeoExplorer = Ext.extend(gxp.Viewer, {

    // Begin i18n.
    zoomSliderText: "<div>Zoom Level: {zoom}</div><div>Scale: 1:{scale}</div>",
    loadConfigErrorText: "Trouble reading saved configuration: <br />",
    loadConfigErrorDefaultText: "Server Error.",
    xhrTroubleText: "Communication Trouble: Status ",
    layersText: "Layers",
    titleText: "Title",
    saveErrorText: "Trouble saving: ",
    bookmarkText: "Bookmark URL",
    permakinkText: 'Permalink',
    appInfoText: "GeoData",
    aboutText: "About GeoExplorer",
    mapInfoText: "Map Info",
    descriptionText: "Description",
    contactText: "Contact",
    aboutThisMapText: "About this Map",
    thematicText: "Thematic tree",
    // End i18n.
    
    /**
     * private: property[mapPanel]
     * the :class:`GeoExt.MapPanel` instance for the main viewport
     */
    mapPanel: null,
    
    toggleGroup: "toolGroup",

    constructor: function(config) {
        this.mapItems = [
            {
                xtype: "gxp_scaleoverlay"
            }, {
                xtype: "gx_zoomslider",
                vertical: true,
                height: 100,
                plugins: new GeoExt.ZoomSliderTip({
                    template: this.zoomSliderText
                })
            }
        ];

        // both the Composer and the Viewer need to know about the viewerTools
        // First row in each object is needed to correctly render a tool in the treeview
        // of the embed map dialog. TODO: make this more flexible so this is not needed.
        config.viewerTools = [
            {
                leaf: true, 
                text: gxp.plugins.Navigation.prototype.tooltip, 
                checked: true, 
                iconCls: "gxp-icon-pan",
                ptype: "gxp_navigation", 
                toggleGroup: this.toggleGroup
            }, {
                leaf: true, 
                text: gxp.plugins.WMSGetFeatureInfo.prototype.infoActionTip, 
                checked: true, 
                iconCls: "gxp-icon-getfeatureinfo",
                ptype: "gxp_wmsgetfeatureinfo", 
                toggleGroup: this.toggleGroup
            }, {
                leaf: true, 
                text: gxp.plugins.Measure.prototype.measureTooltip, 
                checked: true, 
                iconCls: "gxp-icon-measure-length",
                ptype: "gxp_measure",
                controlOptions: {immediate: true},
                toggleGroup: this.toggleGroup
            }, {
                leaf: true, 
                text: gxp.plugins.Zoom.prototype.zoomInTooltip + " / " + gxp.plugins.Zoom.prototype.zoomOutTooltip, 
                checked: true, 
                iconCls: "gxp-icon-zoom-in",
                numberOfButtons: 2,
                ptype: "gxp_zoom"
            }, {
                leaf: true, 
                text: gxp.plugins.NavigationHistory.prototype.previousTooltip + " / " + gxp.plugins.NavigationHistory.prototype.nextTooltip, 
                checked: true, 
                iconCls: "gxp-icon-zoom-previous",
                numberOfButtons: 2,
                ptype: "gxp_navigationhistory"
            }, {
                leaf: true, 
                text: gxp.plugins.ZoomToExtent.prototype.tooltip, 
                checked: true, 
                iconCls: gxp.plugins.ZoomToExtent.prototype.iconCls,
                ptype: "gxp_zoomtoextent"
            }, {
                leaf: true, 
                text: gxp.plugins.Legend.prototype.tooltip, 
                checked: true, 
                iconCls: "gxp-icon-legend",
                ptype: "gxp_legend"
            }, {
                leaf: true,
                text: gxp.plugins.GoogleEarth.prototype.tooltip,
                checked: true,
                iconCls: "gxp-icon-googleearth",
                ptype: "gxp_googleearth"
        }];

        GeoExplorer.superclass.constructor.apply(this, arguments);
    }, 

    loadConfig: function(config) {
        var mapUrl = window.location.hash.substr(1);
        var match = mapUrl.match(/^maps\/(\d+)$/);
        if (match) {
            this.id = Number(match[1]);
            OpenLayers.Request.GET({
                url: mapUrl,
                success: function(request) {
                    var addConfig = Ext.util.JSON.decode(request.responseText);
                    this.applyConfig(Ext.applyIf(addConfig, config));
                },
                failure: function(request) {
                    var obj;
                    try {
                        obj = Ext.util.JSON.decode(request.responseText);
                    } catch (err) {
                        // pass
                    }
                    var msg = this.loadConfigErrorText;
                    if (obj && obj.error) {
                        msg += obj.error;
                    } else {
                        msg += this.loadConfigErrorDefaultText;
                    }
                    this.on({
                        ready: function() {
                            this.displayXHRTrouble(msg, request.status);
                        },
                        scope: this
                    });
                    delete this.id;
                    window.location.hash = "";
                    this.applyConfig(config);
                },
                scope: this
            });
        } else {
            var query = Ext.urlDecode(document.location.search.substr(1));
            if (query && query.q) {
                var queryConfig = Ext.util.JSON.decode(query.q);
                Ext.apply(config, queryConfig);
            }
            this.applyConfig(config);
        }
    },
    
    displayXHRTrouble: function(msg, status) {
        Ext.Msg.show({
            title: this.xhrTroubleText + status,
            msg: msg,
            icon: Ext.MessageBox.WARNING
        });
    },
    
    /** private: method[initPortal]
     * Create the various parts that compose the layout.
     */
    initPortal: function() {
/** import fra gammel versjon  */
		var dataLevertPanel = new Ext.Panel({
            border: true,
            unstyled: true,
            region: "south",
            height: 40,
            split: true,
            collapsed: true, 
            collapsible: true,
            collapseMode: "mini",
            html:"Data supplied by:" +
            	"<img src=\"/geodata/theme/app/img/geosilk/kilder/DN_lite.png\" title=\"Direktoratet for naturforvaltning\"/>"+
            	"<img src=\"/geodata/theme/app/img/geosilk/kilder/FD_lite.png\" title=\"Fiskeridirektoratet\"/>"+
            	"<img src=\"/geodata/theme/app/img/geosilk/kilder/HI_lite.png\" title=\"Havforskningsinstituttet\"/>"+
            	"<img src=\"/geodata/theme/app/img/geosilk/kilder/NGU_lite.png\" title=\"Norges geologiske unders&oslash;kelse\"/>"+
            	"<img src=\"/geodata/theme/app/img/geosilk/kilder/OD_lite.png\" title=\"Oljedirektoratet\"/>"+
            	"<img src=\"/geodata/theme/app/img/geosilk/kilder/kystverket_lite.jpg\" title=\"Kystverket\"/>"+
            	"<img src=\"/geodata/theme/app/img/geosilk/kilder/SK_lite.png\" title=\"Statens kartverk\"/>"
        });
        
		var geoExplorerOrMareanoLegendContainer;
		var legendContainerContainerItems;
		if (document.location.href.indexOf(MAREANO_EN) != -1) {
			geoExplorerOrMareanoLegendContainer = new Ext.Panel({
				region: 'center', 
				layout: 'fit', 
				border: false, 
				autoScroll: true,
				height: 200, 
				html: '',
				id: 'newLegend'
			});
			var dummyLegendContainer = new Ext.Panel({ 
				region: 'center', 
				xtype: 'container', 
				layout: "fit", 
				border: false, 
				height:0, 
				width:0, 
				id: 'legend', 
				'visible':false 
			});
			legendContainerContainerItems = [dummyLegendContainer, geoExplorerOrMareanoLegendContainer];
		} else {
			geoExplorerOrMareanoLegendContainer = new Ext.Panel({
				region: 'center', 
				xtype: 'container', 
				layout: 'fit', 
				border: false, 
				height: 200 , 
				id: 'legend'
			});
			legendContainerContainerItems = [geoExplorerOrMareanoLegendContainer, dataLevertPanel];
		}
		
        var legendContainerContainer = new Ext.Panel({
            border: false,
            title: "Legend",
            layout: "border",
            region: "south",
            width: 200,
            height: 250,
            split: true,
            collapsible: true,
            items: legendContainerContainerItems
        });

        /** westpanel *****/        
        var westPanel = new Ext.Panel({
            border: true,
            title: "Layers",
            layout: "border",
            region: "center",
            width: 250,
            split: true,
//            collapsible: true,
            collapseMode: "mini",
            items: [
                 {region: 'center', autoScroll: true, tbar: [], border: false, id: 'tree' /*title: this.layersText*/},
                 legendContainerContainer
            ]
        });    
        
        var tipsPanel;
        if (document.location.href.indexOf(MAREANO_EN) != -1) {
        	tipsPanel = new Ext.Panel({
				title: 'Info about layers', 
	    		html:  "", 
	    		region: 'center', 
	    		id: 'tips', 
	    		preventBodyReset: true,
	    		autoHeight: true
	        });        	
        } else {
	        	tipsPanel = new Ext.Panel({
				title: 'about WMS/WFS', 
	    		html:
	    			"<p style='font-size:10pt'>IMR provides access to subject data by mapping the standard" +
	    			"Web Map Services (WMS) and Web Features Services (WFS). Services makes it easy to include" +
	    			"topic maps in their own mapping solutions or tools for geographical information systems (GIS).</p>" +
	
	        			"<p style='font-size:10pt'>Services may be used freely under the following conditions:" +
	        			"<ul>" +
	        				"<li style='font-size:10pt'>fill in a registration form so that we can inform our users of any changes, maintenance etc.</li>" +
	        				"<li style='font-size:10pt'>reference IMR as your source on the page where our WMS is being used</li>" + 
	        				"<li style='font-size:10pt'>contact IMR if the WMS is to be used in a commercial context</li></ul></p>" + 
	        			
	        			"<p style='font-size:10pt'><a href='http://maps.imr.no/geoserver/web/'>" +
	        			"URL to the WMS and WFS server: http://maps.imr.no/geoserver/web/</a></p>"    		
		, 
	    		region: 'center', 
	//    		bodyStyle: "background-image:url('theme/app/img/background_body_None.jpg')",
	    		id: 'tips', 
	//    		contentEl: 'tips', 
	//    		autoScroll: true, 
	    		preventBodyReset: true,
	    		autoHeight: true
	        });
        }
        var westPanelTabs = new Ext.TabPanel({
    		activeTab: 0,
    		region: "center",
//    		deferredRender: false,
    		items: [westPanel, tipsPanel,
    			{title:"Hjelp", html:"Help", region: "center", disabled: "true"}
    				]
		});
		
		var westPanel2 = new Ext.Panel({
            border: true,
            layout: "hbox",
            layoutConfig: {
                align: 'stretch',
                pack: 'start'
            },
            region: "west",
            unstyled:true,
            width: 400,
            split: true,
//            height: "100%",
//            collapsible: true,
            defaults:{ autoScroll:true },
            collapseMode: "mini",
            items: [
                {
                    xtype: 'treepanel',
                    enableDrag: true,
                    enableDrop: false,
                    loader: new Ext.tree.TreeLoader(),
                    root: new Ext.tree.AsyncTreeNode(),
                    rootVisible: false,
                    title: this.thematicText,
                    layout: "fit",
                    id: "thematic_tree",
                    flex: 1
                }, {
                    xtype: 'container',
                    layout: "border",
                    width: 250,
                    items: westPanelTabs
                }
            ]
        });        
/** slutt: import fra gammel versjon */    	
        
         /*var westPanel = new Ext.Panel({
            border: false,
            layout: "border",
            region: "west",
            width: 250,
            split: true,
            collapsible: true,
            collapseMode: "mini",
            header: false,
            items: [
                {region: 'center', autoScroll: true, tbar: [], border: false, id: 'tree', title: this.layersText}, 
                {region: 'south', xtype: "container", layout: "fit", border: false, height: 200, id: 'legend'}
            ]
        }); */ 
        
        this.toolbar = new Ext.Toolbar({
            disabled: true,
            id: 'paneltbar',
            items: this.createTools()
        });
        this.on("ready", function() {
            // enable only those items that were not specifically disabled
            var disabled = this.toolbar.items.filterBy(function(item) {
                return item.initialConfig && item.initialConfig.disabled;
            });
            this.toolbar.enable();
            disabled.each(function(item) {
                item.disable();
            });
        });

        var googleEarthPanel = new gxp.GoogleEarthPanel({
            mapPanel: this.mapPanel,
            listeners: {
                beforeadd: function(record) {
                    return record.get("group") !== "background";
                }
            }
        });
        
        // TODO: continue making this Google Earth Panel more independent
        // Currently, it's too tightly tied into the viewer.
        // In the meantime, we keep track of all items that the were already
        // disabled when the panel is shown.
        var preGoogleDisabled = [];

        googleEarthPanel.on("show", function() {
            preGoogleDisabled.length = 0;
            this.toolbar.items.each(function(item) {
                if (item.disabled) {
                    preGoogleDisabled.push(item);
                }
            })
            this.toolbar.disable();
            // loop over all the tools and remove their output
            for (var key in this.tools) {
                var tool = this.tools[key];
                if (tool.outputTarget === "map") {
                    tool.removeOutput();
                }
            }
            var layersContainer = Ext.getCmp("tree");
            var layersToolbar = layersContainer && layersContainer.getTopToolbar();
            if (layersToolbar) {
                layersToolbar.items.each(function(item) {
                    if (item.disabled) {
                        preGoogleDisabled.push(item);
                    }
                });
                layersToolbar.disable();
            }
        }, this);

        googleEarthPanel.on("hide", function() {
            // re-enable all tools
            this.toolbar.enable();
            
            var layersContainer = Ext.getCmp("tree");
            var layersToolbar = layersContainer && layersContainer.getTopToolbar();
            if (layersToolbar) {
                layersToolbar.enable();
            }
            // now go back and disable all things that were disabled previously
            for (var i=0, ii=preGoogleDisabled.length; i<ii; ++i) {
                preGoogleDisabled[i].disable();
            }

        }, this);

        this.mapPanelContainer = new Ext.Panel({
            layout: "card",
            region: "center",
            height: "60%",
            defaults: {
                border: false
            },
            items: [
                this.mapPanel,
                googleEarthPanel
            ],
            activeItem: 0
        });

        var innerNorthPanel = null;
        if (document.location.href.indexOf(MAREANO_EN) != -1) {
        	innerNorthPanel = new Ext.Panel({
	            border: true,
	            region: "north",
	            split: true,
	            id: "topPanelHeading",
	            collapseMode: "mini",
	            bodyStyle: "background-image:url('http://www.mareano.no/kart/images/nav-main-background.jpg')",
	            html:'<table width="100%"><tr height="45"> ' +
	            '<td valign="middle" height="45" style="background-image:url(http://www.mareano.no/kart/images/top/ny_heading_397.gif); background-repeat: repeat;"> ' +
	            '<a style="text-decoration: none" target="_top" href="http://www.mareano.no"> ' +
	            '<img border="0" alt="MAREANO<br>samler kunnskap om havet" src="http://www.mareano.no/kart/images/top/ny_logo.gif"> ' +
	            '</a> ' +
	            '</td> ' +
	            '<td width="627" align="right" height="45" style="background-image:url(http://www.mareano.no/kart/images/top/ny_heading_627.gif);"> </td> ' +
	            '</tr></table> '+
	            '<div id="nav-main"><ul id="nav"><li><a href="/start">Startsiden</a></li></ul></div>'
        	});
        } else {
	        	innerNorthPanel = new Ext.Panel({
		            border: true,
		            region: "north",
		            split: true,
		//            collapsible: true,
		            collapseMode: "mini",
		            bodyStyle: "background-image:url('theme/app/img/background_body_None.jpg')",
		            html:"" +
"<table width=\"100%\" border=\"0\">" +
"<tr>" +
"<td>" +
"	<div id=\"pageheading\" style=\"display:inline-block;\">" +
"		<a href=\"http://www.imr.no\"><img src=\"theme/imr/logo_imr.png\" height=\"67px\" width=\"461\" /></a>" +
"	</div>" +
"</td>" +
"<td>" +	
"	<span style=\"float:right;\">"+
"   <img src=\"theme/imr/background_top_trans.jpg\" alt=\"IMR\" height=\"118px\" width=\"490\" ></span>" +
"</td>" +
"</tr></table>"            
	        	});
        }
        
        var northPanel = new Ext.Panel({
            height: "40%",
            split: true,
//            collapsible: true,
            unstyled:true,
            collapseMode: "mini",
        	region: "north",
			items: [innerNorthPanel,this.toolbar]
		});
        
        this.portalItems = [{
            region: "center",
            layout: "border",
//            tbar: this.toolbar,
            items: [
                northPanel,
                this.mapPanelContainer,
                westPanel2
            ]
        }];
        
        GeoExplorer.superclass.initPortal.apply(this, arguments);        
    },
    
    /** private: method[createTools]
     * Create the toolbar configuration for the main panel.  This method can be 
     * overridden in derived explorer classes such as :class:`GeoExplorer.Composer`
     * or :class:`GeoExplorer.Viewer` to provide specialized controls.
     */
    createTools: function() {

    	var vector = new OpenLayers.Layer.Vector("Polygon");
    	this.mapPanel.map.addLayer( vector );    
    	var drawPolyAction = new GeoExt.Action({
        	//text: "tegn polygon",
            control: new OpenLayers.Control.DrawFeature(
            	vector, OpenLayers.Handler.Polygon
    		),
    		iconCls: "icon-square",
            map: this.mapPanel.map,
            toggleGroup: "draw",
            tooltip: "Draw polygon"
//            toggleGroup: toolGroup
    	}); 
        var drawLineAction = new GeoExt.Action({
            //text: "tegn linje",
            control: new OpenLayers.Control.DrawFeature(
                vector, OpenLayers.Handler.Path
            ),
            iconCls: "icon-line",
            map: this.mapPanel.map,
            toggleGroup: "draw",
            tooltip: "Draw line"
//            toggleGroup: toolGroup
        });	
    	
    	Proj4js.defs["EPSG:32633"] = "+proj=utm +zone=33 +ellps=WGS84 +datum=WGS84 +units=m +no_defs";
    	var oSrcPrj = new Proj4js.Proj('WGS84');
        var oDestPrj = new Proj4js.Proj('EPSG:32633');
        function formatLonlats(lonLat) {
        	var lat = lonLat.lat;
            var longi = lonLat.lon;
            var aPoint = new Proj4js.Point( longi, lat );
            Proj4js.transform(oDestPrj,oSrcPrj,aPoint);

            var ns = OpenLayers.Util.getFormattedLonLat(aPoint.y);
            var ew = OpenLayers.Util.getFormattedLonLat(aPoint.x,'lon');
            
            return 'Coordinates (WGS84): ' + ns + ', ' + ew;
            // + ' - EPSG:32633: (' + lat + ', ' + longi + ')';
        }	
    	MousePositionBox = Ext.extend(Ext.BoxComponent, {
    		map: null,
    		afterRender: function() {
    			var control = new OpenLayers.Control.MousePosition({
    				div: this.getEl().dom,
    				numDigits: 0,
    				prefix: "",
    				formatOutput: formatLonlats
    			});
    		this.map.addControl(control);
    		MousePositionBox.superclass.afterRender.apply(this, arguments);
    		}
    	});     
    	var tmpMouseP = new MousePositionBox( {map: this.mapPanel.map} );

    	var fishExBtn = new Ext.Button({
            tooltip: "FishExchange",
            handler: function(){
            	Ext.Ajax.request({
            	    url: 'spring/parameter.html?language=en&grid=gridname&grid_value=FishExChange',
            	    success: function(objServerResponse) {
            	        var responseText = objServerResponse.responseText;
            	        Ext.MessageBox.show({title:'FishExchange', msg: responseText}); 
            	    }
            	});
            },
            iconCls: "icon-yellow-db",
            text: "FishExchange",
            scope: this,
            disabled: false
        });
    	
    	var gaaTilKoord = new Ext.Button({
            tooltip: "Go to coordinate",
            text: "Go to coordinate",
            handler: function(){
				Ext.MessageBox.prompt('Name', 'Position in WGS84 (Latitude, Longitude - eg. - 60.2,1.5):', showResultText);
        		//this.mapPanel.map.panTo( new OpenLayers.LonLat( showResultText ) ); // -1644,6934116 ) );
    			function showResultText(btn, text){
    				var thisMapPanel = Ext.ComponentMgr.all.find(function(c) {
                		return c instanceof GeoExt.MapPanel;
            		});
					var bar = text.split(",");
					for(var i = 0;i<bar.length;i++){
							bar[i] = bar[i].split(",");
					}
					var x = bar[0];
					var y = bar[1];
					var newPoint = new Proj4js.Point( y, x );
					newPoint.y = y;
					newPoint.x = x;
    				Proj4js.transform(new Proj4js.Proj('WGS84'),new Proj4js.Proj('EPSG:32633'), newPoint);
    				thisMapPanel.map.panTo( new OpenLayers.LonLat( newPoint.y, newPoint.x ) ); // -1644,6934116 ) );
				};    				
            },
            //iconCls: "icon-zoom-out",
            scope: this
        });
    	
    	var gaaTilHav = new Ext.form.ComboBox({
			fieldLabel: 'Number',
			hiddenName: 'number',
			store: new Ext.data.SimpleStore({
    			fields: ['number'],
    			data : [ ['Barents Sea '], [' Norwegian Sea '], [' North Sea' ], ['Skagerrak'] ]
			}),
			displayField: 'number',
			height: 10,
			typeAhead: true,
			mode: 'local',
			triggerAction: 'all',
			emptyText: "Go to area",
			selectOnFocus:true,
			listeners: {
    			select: {
        			fn:function(combo, value) {
        				var thisMapPanel = Ext.ComponentMgr.all.find(function(c) {
                			return c instanceof GeoExt.MapPanel;
            			});
        				if ( combo.getValue() == "Norwegian Sea" ) 
        					thisMapPanel.map.panTo( new OpenLayers.LonLat( -1644,7334116 ) );
        				else if ( combo.getValue() == "Barents Sea" )
        					thisMapPanel.map.panTo( new OpenLayers.LonLat( 1088474,8089849 ) );
        				else if ( combo.getValue() == "North Sea" )
        					thisMapPanel.map.panTo( new OpenLayers.LonLat( -1644,6934116 ) );
        				else if ( combo.getValue() == "Skagerrak" )
        					thisMapPanel.map.panTo( new OpenLayers.LonLat( -1644,6434116 ) );    
						else if ( combo.getValue() == "Polhavet" )
        					thisMapPanel.map.panTo( new OpenLayers.LonLat( 1088474,8089849 ) );            					
        			}
    			}
			}
		});
    	
    	var norskBtn = new Ext.Button({
            tooltip: "Norsk",
            buttonAlign: "center",
            handler: function(){
				location.href = location.href.substring(0,location.href.lastIndexOf('/')) + "/geodataHI.html"; 				
			},
            iconCls: "icon-norsk",
            scope: this
        });
    	
    	var mareanoNorskBtn = new Ext.Button({
            tooltip: "Norsk",
            buttonAlign: "center",
            handler: function(){
				location.href = location.href.substring(0,location.href.lastIndexOf('/')) + "/mareano.html"; 				
			},
            iconCls: "icon-norsk",
            scope: this
        });
    	
		var engelskBtn = new Ext.Button({
            tooltip: "English",
            buttonAlign: "right", 
            handler: function(){
//				location.href = location.href.substring(0,location.href.lastIndexOf('/')) + "/geodataHI_en.html"; 
			},
            iconCls: "icon-english",
            scope: this
        });
		
    	var tools = null;
        if (document.location.href.indexOf(MAREANO_EN) != -1) {
	        tools = [
	            drawPolyAction,
	            drawLineAction,               
	            gaaTilKoord,  
				gaaTilHav,               
	            "-",
				tmpMouseP,
	            "->",
				mareanoNorskBtn,
				engelskBtn  			
	        ];
        } else {
	        tools = [
	            drawPolyAction,
	            drawLineAction,
	            fishExBtn,
	            gaaTilKoord,
	        	gaaTilHav,
	            "-",
				tmpMouseP,
	            "->",
	            norskBtn,
	            engelskBtn
	        ];
        }
        return tools;
    },
    
    /** private: method[save]
     *
     * Saves the map config and displays the URL in a window.
     */ 
    save: function(callback, scope) {
        var configStr = Ext.util.JSON.encode(this.getState());
        var method, url;
        if (this.id) {
            method = "PUT";
            url = "maps/" + this.id;
        } else {
            method = "POST";
            url = "maps";
        }
        OpenLayers.Request.issue({
            method: method,
            url: url,
            data: configStr,
            callback: function(request) {
                this.handleSave(request);
                if (callback) {
                    callback.call(scope || this);
                }
            },
            scope: this
        });
    },
        
    /** private: method[handleSave]
     *  :arg: ``XMLHttpRequest``
     */
    handleSave: function(request) {
        if (request.status == 200) {
            var config = Ext.util.JSON.decode(request.responseText);
            var mapId = config.id;
            if (mapId) {
                this.id = mapId;
                window.location.hash = "#maps/" + mapId;
            }
        } else {
            throw this.saveErrorText + request.responseText;
        }
    },
    
    /** private: method[showUrl]
     */
    showUrl: function() {
        var win = new Ext.Window({
            title: this.bookmarkText,
            layout: 'form',
            labelAlign: 'top',
            modal: true,
            bodyStyle: "padding: 5px",
            width: 300,
            items: [{
                xtype: 'textfield',
                fieldLabel: this.permakinkText,
                readOnly: true,
                anchor: "100%",
                selectOnFocus: true,
                value: window.location.href
            }]
        });
        win.show();
        win.items.first().selectText();
    },
    
    /** api: method[getBookmark]
     *  :return: ``String``
     *
     *  Generate a bookmark for an unsaved map.
     */
    getBookmark: function() {
        var params = Ext.apply(
            OpenLayers.Util.getParameters(),
            {q: Ext.util.JSON.encode(this.getState())}
        );
        
        // disregard any hash in the url, but maintain all other components
        var url = 
            document.location.href.split("?").shift() +
            "?" + Ext.urlEncode(params);
        
        return url;
    },

    /** private: method[displayAppInfo]
     * Display an informational dialog about the application.
     */
    displayAppInfo: function() {
        var appInfo = new Ext.Panel({
            title: "GeoExplorer",
            html: "<iframe style='border: none; height: 100%; width: 100%' src='about.html' frameborder='0' border='0'><a target='_blank' href='about.html'>"+this.aboutText+"</a> </iframe>"
        });

        var about = Ext.applyIf(this.about, {
            title: '', 
            "abstract": '', 
            contact: ''
        });

        var mapInfo = new Ext.Panel({
            title: this.mapInfoText,
            html: '<div class="gx-info-panel">' +
                  '<h2>'+this.titleText+'</h2><p>' + about.title +
                  '</p><h2>'+this.descriptionText+'</h2><p>' + about['abstract'] +
                  '</p> <h2>'+this.contactText+'</h2><p>' + about.contact +'</p></div>',
            height: 'auto',
            width: 'auto'
        });

        var tabs = new Ext.TabPanel({
            activeTab: 0,
            items: [mapInfo, appInfo]
        });

        var win = new Ext.Window({
            title: this.aboutThisMapText,
            modal: true,
            layout: "fit",
            width: 300,
            height: 300,
            items: [tabs]
        });
        win.show();
    }
});


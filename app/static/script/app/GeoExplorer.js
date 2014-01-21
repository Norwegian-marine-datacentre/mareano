/**
 * Copyright (c) 2009-2011 The Open Planning Project
 */

Ext.USE_NATIVE_JSON = true;

(function() {
    // backwards compatibility for reading saved maps
    // these source plugins were renamed after 2.3.2
    Ext.preg("gx_wmssource", gxp.plugins.WMSSource);
    Ext.preg("gx_olsource", gxp.plugins.OLSource);
    Ext.preg("gx_bingsource", gxp.plugins.BingSource);
    Ext.preg("gx_osmsource", gxp.plugins.OSMSource);
})();
var globalconfig;

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
    layersText: "Kartlag",
    titleText: "Title",
    saveErrorText: "Trouble saving: ",
    bookmarkText: "Bookmark URL",
    permakinkText: 'Permalink',
    appInfoText: "Mareano",
    aboutText: "About GeoExplorer",
    mapInfoText: "Map Info",
    descriptionText: "Description",
    contactText: "Contact",
    aboutThisMapText: "About this Map",
    thematicText: "Temakart",
    dataSuppliedText: "Data levert av:",
    legendTitle: "Tegnforklaring",
    infoTitle: "Info om kartlag",
    helpTitle: "Hjelp",
    zoomBoxTooltip: "Zoom til omr\u00e5de",
    mousePositionText: "Koordinater (WGS84): ",
    goToTooltip: "G&aring; til koordinat",
    goToText: "G&aring; til koordinat",
    goToPrompt: "Posisjon i WGS84 (Breddegrad, Lengdegrad - for eksempel: 60.2,1.5):",
    expandText: "Expand Layers",
    collapseText: "P\u00e5sl\u00e5tt kartlag",
	expandCollapseTooltip: "Sl\u00e5 sammen eller ekspander kartlag panel",
    visibilityText: "Sl\u00e5 av",
    visibilityTooltip: "Sl\u00e5 av alle kartlag",
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
            },{
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
            }
            ];
        globalconfig = config.viewerTools;

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
                    // Don't use persisted tool configurations from old maps
                    delete addConfig.tools;
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
            html: this.dataSuppliedText +
		        "<img src=\"/geodata/theme/app/img/geosilk/kilder/DN_lite.png\" title=\"Direktoratet for naturforvaltning\"/>"+
		        "<img src=\"/geodata/theme/app/img/geosilk/kilder/FD_lite.png\" title=\"Fiskeridirektoratet\"/>"+
		        "<img src=\"/geodata/theme/app/img/geosilk/kilder/HI_lite.png\" title=\"Havforskningsinstituttet\"/>"+
		        "<img src=\"/geodata/theme/app/img/geosilk/kilder/NGU_lite.png\" title=\"Norges geologiske unders&oslash;kelse\"/>"+
		        "<img src=\"/geodata/theme/app/img/geosilk/kilder/OD_lite.png\" title=\"Oljedirektoratet\"/>"+
		        "<img src=\"/geodata/theme/app/img/geosilk/kilder/kystverket_lite.jpg\" title=\"Kystverket\"/>"+
		        "<img src=\"/geodata/theme/app/img/geosilk/kilder/SK_lite.png\" title=\"Statens kartverk\"/>"
        });        
		
	    var mareanoLegendContainer = new Ext.Panel({
	        region: 'center', 
	        layout: 'fit', 
	        autoScroll: true,
	        border: false, 
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
	    var legendContainerContainerItems = [dummyLegendContainer, mareanoLegendContainer];
		
        var legendContainerContainer = new Ext.Panel({
            border: false,
            title: this.legendTitle,
            layout: "border",
            region: "south",
            width: 215,
            height: 200,
            flex: 1, 
            split: true,
            collapsible: true,
            CollapseMode: 'header', //PlaceHolder
            items: legendContainerContainerItems
        });

        /** westpanel *****/        
        var westPanel = new Ext.Panel({
            border: true,
            title: this.layersText,
            layout: "fit",
            region: "center",
            width: 215,
            split: true,
            collapseMode: "mini",
            resizable: true,
            items: [
	            {autoScroll:true,tbar:[],border:false, id:'tree', resizable: true, flex: 0,height:100}
            ]
        });    
        
        var tipsPanel = new Ext.Panel({
            title: this.infoTitle, 
            html:  "", 
            region: 'center', 
            id: 'tips', 
            preventBodyReset: true,
            autoHeight: true
        });        	
        
        var westPanelTabs = new Ext.TabPanel({
            activeTab: 0,
            region: "center",
            items: [westPanel, tipsPanel,
            {title: this.helpTitle, 
            	html: this.helpText,
                disabled: Ext.isEmpty(this.helpText),
            	region: "center"}
            ]
        });

        var btnPostfix = '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;';
        var expandDiv = '<div style="position:absolute; top: 5px; right: 5px;" class="x-tool x-tool-toggle x-tool-collapse-east">&nbsp;</div>';
        var collapseDiv = '<div style="position:absolute; top: 5px; right: 5px;" class="x-tool x-tool-toggle x-tool-collapse-west">&nbsp;</div>';
      
        var westPanel2 = new Ext.Panel({
            border: true,
            layout: "border",
            region: "west",
            unstyled:true,
            width: 240,  //200
            tbar: [{
                text: this.visibilityText,
                tooltip: this.visibilityTooltip,
                handler: function() {
                    var tree = Ext.getCmp('thematic_tree');
                    var checked = tree.getChecked();
                    for (var i=0, ii = checked.length; i<ii; ++i) {
                        checked[i].ui.toggleCheck(false);
                    }
                }
            }, '->', {
                tooltip: this.expandCollapseTooltip,
                cls: "expand-collapse",
                text: this.expandText + btnPostfix + expandDiv,
                handler: function(cmp) {
                    if (!cmp._expanded) {
                        cmp.setText(this.collapseText + btnPostfix + collapseDiv);
                        westPanel2.setWidth(440);  //415
                    } else {
                        cmp.setText(this.expandText + btnPostfix + expandDiv);
                        westPanel2.setWidth(240);  //200
                    }
                    westPanel2.ownerCt.doLayout();
                    cmp._expanded = !cmp._expanded;
                },
                scope: this
            }],
            split: true,
            defaults:{ autoScroll:true },
            collapseMode: "mini",
            items: [{
                layout: 'border',
                width: 240,  //200
                autoScroll: false,
                xtype: 'container',
                split: true,
                region: "west",
                items: [{
                    xtype: 'treepanel',
                    region: "center",
                    autoScroll: true,
                    enableDrag: true,
                    enableDrop: false,
                    loader: new Ext.tree.TreeLoader(),
                    root: new Ext.tree.AsyncTreeNode(),
                    rootVisible: false,
                    title: this.thematicText,
                    layout: "fit",
                    id: "thematic_tree"
                }, legendContainerContainer]
            }, {
                xtype: 'panel',
                layout: "border",
                width: 200,  //215
                region: "center",
                items: westPanelTabs
            }]
        }); 
        /** slutt: import fra gammel versjon */    	
        
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

        this.toolbar.enable();

        this.mapPanelContainer = new Ext.Panel({
            layout: "card",
            region: "center",
            height: "60%",
            defaults: {border: false},
            items: [this.mapPanel],
            activeItem: 0
        });

        var innerNorthPanel = new Ext.Panel({
            border: true,
            region: "north",
            split: true,
            id: "topPanelHeading",
            collapseMode: "mini",
            bodyStyle: "background-image:url('http://www.mareano.no/kart/images/nav-main-background.jpg')",
            //html - content reloaded with content from MareanoController - but this must be here for the rest of the rest of the panels to
            //     load in the correct size. 
            html:'<table width="100%" cellspacing="0" border="1"><tr height="45"> ' + 
            '<td valign="middle" height="45" style="background-image:url(http://www.mareano.no/kart/images/top/ny_heading_397.gif); background-repeat: repeat;"> ' +
            '<a style="text-decoration: none" target="_top" href="http://www.mareano.no"> ' +
            '<img border="0" alt="MAREANO<br>samler kunnskap om havet" src="http://www.mareano.no/kart/images/top/ny_logo.gif"> ' +
            '</a> ' +
            '</td> ' +
            '<td width="627" align="right" height="45" style="background-image:url(http://www.mareano.no/kart/images/top/ny_heading_627.gif);"> </td> ' +
            '</tr></table> '+
            '<div id="nav-main"><ul id="nav"><li><a href="/start">Startsiden</a></li></ul></div>'
        });
        
        var northPanel = new Ext.Panel({
            height: "40%",
            split: true,
            unstyled:true,
            collapseMode: "mini",
            region: "north",
            id: 'page-header-panel',
            items: [innerNorthPanel,this.toolbar]
        });
        
        this.portalItems = [{
            region: "center",
            layout: "border",
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

        var zoomBoxAction = new GeoExt.Action({
            control: new OpenLayers.Control.ZoomBox({alwaysZoom:true}),
            iconCls: "icon-zoom-to", //app\static\externals\openlayers\img\drag-rectangle-on.png
            map: this.mapPanel.map,
            toggleGroup: this.toggleGroup,
            tooltip: this.zoomBoxTooltip
        });	          
    	
        Proj4js.defs["EPSG:32633"] = "+proj=utm +zone=33 +ellps=WGS84 +datum=WGS84 +units=m +no_defs";
        var oSrcPrj = new Proj4js.Proj('WGS84');
        var oDestPrj = new Proj4js.Proj('EPSG:32633');
        var me = this;
        function formatLonlats(lonLat) {
            var lat = lonLat.lat;
            var longi = lonLat.lon;
            var aPoint = new Proj4js.Point( longi, lat );
            Proj4js.transform(oDestPrj,oSrcPrj,aPoint);

            var ns = OpenLayers.Util.getFormattedLonLat(aPoint.y);
            var ew = OpenLayers.Util.getFormattedLonLat(aPoint.x,'lon');
            
            return me.mousePositionText + ns + ', ' + ew;
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
    	
        var gaaTilKoord = new Ext.Button({
            tooltip: this.goToTooltip,
            text: this.goToText,
            handler: function(){
                Ext.MessageBox.prompt('Name', this.goToPrompt, showResultText);
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
            scope: this
        });

        var gaaTilHav = new Ext.form.ComboBox({
            fieldLabel: 'Number',
            hiddenName: 'number',
            store: new Ext.data.SimpleStore({
                fields: ['number'],
                data : [ [this.zoomToItem1], [this.zoomToItem2], [this.zoomToItem3], [this.zoomToItem4], [this.zoomToItem5] ]
            }),
            displayField: 'number',
            height: 10,
            typeAhead: true,
            mode: 'local',
            triggerAction: 'all',
            emptyText: this.zoomToEmptyText,
            selectOnFocus:true,
            listeners: {
                select: {
                    fn:function(combo, value) {
                        var thisMapPanel = Ext.ComponentMgr.all.find(function(c) {
                            return c instanceof GeoExt.MapPanel;
                        });
                        if ( combo.getValue() == this.zoomToItem2 ) 
                            thisMapPanel.map.panTo( new OpenLayers.LonLat( -1644,7334116 ) );
                        else if ( combo.getValue() == this.zoomToItem1 )
                            thisMapPanel.map.panTo( new OpenLayers.LonLat( 1088474,8089849 ) );
                        else if ( combo.getValue() == this.zoomToItem3 )
                            thisMapPanel.map.panTo( new OpenLayers.LonLat( -1644,6934116 ) );
                        else if ( combo.getValue() == this.zoomToItem4 )
                            thisMapPanel.map.panTo( new OpenLayers.LonLat( -1644,6434116 ) );    
                        else if ( combo.getValue() == this.zoomToItem5 )
                            thisMapPanel.map.panTo( new OpenLayers.LonLat( 1000000,8999999 ) );            					
                    },
                    scope: this
                }
            }
        });
    	
        var mareanoNorskBtn = new Ext.Button({
            tooltip: "Norsk",
            buttonAlign: "center",
            handler: function(){
                if (location.href.indexOf("mareano.html") === -1) {
                    location.href = location.href.substring(0,location.href.lastIndexOf('/mareano_en.html')) + "/mareano.html";
                }
            },
            iconCls: "icon-norsk",
            scope: this
        });
    	
        var mareanoEngelskBtn = new Ext.Button({
            tooltip: "English",
            buttonAlign: "right", 
            handler: function(){
                if (location.href.indexOf("mareano_en.html") === -1) {
                    location.href = location.href.substring(0,location.href.lastIndexOf('/mareano.html')) + "/mareano_en.html";
                }
            },
            iconCls: "icon-english",
            scope: this
        });    	
        
        var helpIcon = new Ext.Button({
            type:'help',
            tooltip: 'Get Help',
            handler: function(event, toolEl, panel) {
                alert('Do you need help?');
        	},
        	iconCls: "icon-question",
        	scope: this
        });
    	
        var tools = [
             "", 
             zoomBoxAction,
             "-",
             gaaTilKoord,  
             gaaTilHav,               
             "-",
             tmpMouseP,
             "->",
             helpIcon,
             mareanoNorskBtn,
             mareanoEngelskBtn  			
        ];
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


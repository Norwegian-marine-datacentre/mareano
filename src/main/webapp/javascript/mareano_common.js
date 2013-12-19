(function() {
    Ext.preg("gxp_layertree", gxp.plugins.LayerTree);
})();

Ext.ns("Mareano");

Mareano.Composer = Ext.extend(GeoExplorer.Composer, {

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
    collapseText: "Collapse Layers",
    expandCollapseTooltip: "Expand or collapse the Layers panel",
    visibilityText: "Turn off",
    visibilityTooltip: "Turn off all overlays",
    // End i18n.

    loadConfig: function(config) {
        var idx;
        for (var i=0, ii=config.tools.length; i<ii; ++i) {
            var tool = config.tools[i];
            if (tool.ptype === "gxp_layermanager") {
                idx = i;
                break;
            }
        }
        var me = this;
        config.tools.splice(idx, 1);
        config.tools.splice(0, 0 ,{
            ptype: "gxp_layertree",
            outputConfig: {
                id: "layers",
                enableDD:true,
                plugins: [{
                    ptype: "gx_treenodeactions",
                    actions: [{
                        action: "zoomscale",
                        qtip: me.zoomScaleTip,
                    }],
                    listeners: {
                        action: function(node, action, evt) {
                            var layer = node.layer;
                            if (layer.maxExtent) {
                                layer.map.zoomToExtent(layer.maxExtent, true);
                            }
                        }
                    }
                }]
            },
            outputTarget: "tree"
        });
        // TODO: get rid of gxp_legend, gxp_addlayers, gxp_styler, gxp_featureeditor, gxp_googleearth
        config.tools.push({
            actions: ["zoomboxbutton"],  actionTarget: "paneltbar"
        }, {
            actions: ["-", "gaaTilKoordButton"], actionTarget: "paneltbar"
        }, {
            actions: ["gaaTilHavCombo"], actionTarget: "paneltbar"
        }, {
            actions: ["-", "mouseposition"], actionTarget: "paneltbar"
        }, {
            actions: ["->", "mareanoNorskBtn"], actionTarget: "paneltbar"
        }, {
            actions: ["mareanoEngelskBtn"], actionTarget: "paneltbar"
        });
        Mareano.Composer.superclass.loadConfig.call(this, config);
    },

    createTools: function() {
        Mareano.Composer.superclass.createTools.apply(this, arguments);
        var zoomBoxAction = new GeoExt.Action({
            control: new OpenLayers.Control.ZoomBox({alwaysZoom:true}),
            iconCls: "icon-zoom-to", //app\static\externals\openlayers\img\drag-rectangle-on.png
            map: this.mapPanel.map,
            id: "zoomboxbutton",
            toggleGroup: this.toggleGroup,
            tooltip: this.zoomBoxTooltip
        });
        new Ext.Button(zoomBoxAction);

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
        var tmpMouseP = new MousePositionBox( {id: "mouseposition", map: this.mapPanel.map} );
        var gaaTilKoord = new Ext.Button({
            id: "gaaTilKoordButton",
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
            id: "gaaTilHavCombo",
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
            id: "mareanoNorskBtn",
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
            id: "mareanoEngelskBtn",
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

        /*var tools = [
             "",
             zoomBoxAction,
             "-",
             gaaTilKoord, 
             gaaTilHav,   
             "-",
             tmpMouseP,
             "->",
             mareanoNorskBtn,
             mareanoEngelskBtn
        ];
        return tools;*/
    },

    // there is currently no better way than overriding initPortal and copying the contents
    // TODO revisit this
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

/*        var westPanel = new gxp.CrumbPanel({
            id: "tree",
            region: "west",
            width: 320,
            split: true,
            collapsible: true,
            collapseMode: "mini",
            hideCollapseTool: true,
            header: false
        });*/

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
            width: 200,
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
                        westPanel2.setWidth(415);
                    } else {
                        cmp.setText(this.expandText + btnPostfix + expandDiv);
                        westPanel2.setWidth(200);
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
                width: 200,
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
                width: 215,
                region: "center",
                items: westPanelTabs
            }]
        });
        /** slutt: import fra gammel versjon */

        var southPanel = new Ext.Panel({
            region: "south",
            id: "south",
            height: 220,
            border: false,
            split: true,
            collapsible: true,
            collapseMode: "mini",
            collapsed: true,
            hideCollapseTool: true,
            header: false,
            layout: "border",
            items: [{
                region: "center",
                id: "table",
                title: this.tableText,
                layout: "fit"
            }, {
                region: "west",
                width: 320,
                id: "query",
                title: this.queryText,
                split: true,
                collapsible: true,
                collapseMode: "mini",
                collapsed: true,
                hideCollapseTool: true,
                layout: "fit"
            }]
        });
        var toolbar = new Ext.Toolbar({
            disabled: true,
            id: 'paneltbar',
            items: []
        });
        this.on("ready", function() {
            // enable only those items that were not specifically disabled
            var disabled = toolbar.items.filterBy(function(item) {
                return item.initialConfig && item.initialConfig.disabled;
            });
            toolbar.enable();
            disabled.each(function(item) {
                item.disable();
            });
        });

        var googleEarthPanel = new gxp.GoogleEarthPanel({
            mapPanel: this.mapPanel,
            id: "globe",
            tbar: [],
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
            toolbar.items.each(function(item) {
                if (item.disabled) {
                    preGoogleDisabled.push(item);
                }
            });
            toolbar.disable();
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
            toolbar.enable();

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
            defaults: {
                border: false
            },
            items: [
                this.mapPanel,
                googleEarthPanel
            ],
            activeItem: 0
        });

        var innerNorthPanel = new Ext.Panel({
            border: true,
            region: "north",
            split: true,
            id: "topPanelHeading",
            collapseMode: "mini",
            bodyStyle: "background-image:url('http://www.mareano.no/kart/images/nav-main-background.jpg')",
            html:'<table width="100%" cellspacing="0"><tr height="45"> ' + //content reloaded with content from MareanoController
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
            height: 95,
            split: true,
            unstyled:true,
            collapseMode: "mini",
            region: "north",
            items: [innerNorthPanel, toolbar]
        });

        this.portalItems = [{
            region: "center",
            layout: "border",
            items: [
                northPanel,
                this.mapPanelContainer,
                westPanel2,
                southPanel
            ]
        }];

        GeoExplorer.Composer.superclass.initPortal.apply(this, arguments);

    }

});

// end of GeoExplorer overrides

var silent = false;
var kartlagInfoState = ""; //used by removeLayerLegendAndInfo(mapOfGMLspesialpunkt, kartlagId)

function loadMareano(mapPanel, app) {
	addOverviewMapAndKeyboardDefaults(mapPanel.map);
    app.mapOfGMLspesialpunkt = new Object();        
    
    var layertree = Ext.getCmp("layers");

    var updateLegendScale = function() {
        layertree.getRootNode().cascade(function(n) {
            var id = n.attributes.layer && n.attributes.layer.metadata['kartlagId'];
            if (id) {
                var legdiv = Ext.get(id);
                if (legdiv !== null) {
                    if (n.disabled === true) {
                        legdiv.addClass('out-of-scale');
                    } else {
                        legdiv.removeClass('out-of-scale');
                    }
                }
            }
        });
    };
    app.mapPanel.on('afterlayout', updateLegendScale);
    app.mapPanel.map.events.register('zoomend', app, updateLegendScale);
    
    layertree.on('startdrag', function() {
    	silent = true;
	});
	layertree.on('dragdrop', function() {
    	silent = false;
	});
    	
    layertree.on('beforeinsert', function(tree, container, node) {
    	node.attributes.iconCls = getLayerIcon(node.layer.url);
    }, this, {single: false});
    // we cannot specify this in outputConfig see: https://github.com/opengeo/gxp/issues/159   
    layertree.on('beforenodedrop', function(event) {
    	// prevent dragging complete folders
        if (!event.dropNode.layer || event.target.text == "Base Layer" ||
        		event.target.parentNode.text == "Base Layer")  {
            return false;
        }
        if (event.source.tree.id === "thematic_tree") {
            var group = event.target.attributes.group || event.target.parentNode.attributes.group;
            var layer = event.dropNode.layer;
            var record = event.dropNode.layerStore.getByLayer(layer);
            var iconCls = event.dropNode.attributes.iconCls;
            var kartlagId = event.dropNode.attributes.id;
            
            if (!layer.map) {
    			record.set("group", group);
    			record.getLayer().setVisibility(true);
            }
            return false;
        }
    }, app);
}

var gfiCache = {};

function addLayerToGroup( gruppeNavn, gruppeText, map, mapPanel, layers, store, app ) {
    var indexOfWMSgruppe = [];
    var layerName = [];
    var childrenVisible = 0;
    var count = 0;    
    for (var i = layers.length-1;i>=0;--i) {
        if ( layers[i].get("group") == gruppeNavn ) {
            count++;
            var idx = mapPanel.layers.findBy(function(record) {
                return record.getLayer().metadata['kartlagId'] === layers[i].getLayer().metadata['kartlagId'];
            });
            if (layers[i].getLayer().visibility === true || idx !== -1) {
                childrenVisible++;
            } 
            layerName.push(layers[i].getLayer().params.LAYERS);
        }
    }

    var groupChecked = (childrenVisible === count);
    var tmpLoader = new GeoExt.tree.LayerLoader({
        store: store,
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
                gfiCache = {};
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
                        var pressed = false;
                        for (var key in app.tools) {
                            if (app.tools[key].ptype === 'gxp_wmsgetfeatureinfo') {
                                pressed = app.tools[key].actions[0].items[0].pressed;
                                break;
                            }
                        }
                        if (pressed && !gfiCache[record.get("layer").id] && record.get("layer").getVisibility() ) {
                            gfiCache[record.get("layer").id] = true;
                            var params = {
                                REQUEST: "GetFeatureInfo",
                                EXCEPTIONS: "application/vnd.ogc.se_xml",
                                BBOX: tmpMap.getExtent().toBBOX(),
                                X: parseInt(e.xy.x),
                                Y: parseInt(e.xy.y),
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
            /** adding matching layer to matching container group */
            for( var i= layerName.length-1; i>=0; --i ) {
                if ( record.get("group") == gruppeNavn) {
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
            attr.iconCls = cssBgImg;
            // if layer already in map, set checked
            var idx = app.mapPanel.layers.findBy(function(record) {
                return record.getLayer().metadata['kartlagId'] === attr.layer.metadata['kartlagId'];
            });
            attr.checked = (layerRecord.getLayer().visibility || (idx !== -1));
            attr.id = layerRecord.data.id;

            attr.autoDisable = false;
            var node = GeoExt.tree.LayerLoader.prototype.createNode.call(this, attr);       
            app.mapPanel.layers.on("remove", function(store, record) {
            	if (silent !== true && record.getLayer().metadata['kartlagId'] === attr.layer.metadata['kartlagId']) {
            		node.ui.toggleCheck(false);
            	}
            });            
            node.on("checkchange", function(event) {
                var layer = layerRecord.getLayer();
                var record = event.layerStore.getByLayer(layer);
                var id = layer.metadata['kartlagId'];
                // check if we should also check the parent node
                var setGroupChecked = function(node) {
                    var allChildrenChecked = true;
                    node.parentNode.eachChild(function(child) {
                        if (!child.ui.checkbox.checked) {
                            allChildrenChecked = false;
                        }
                    });
                    node.parentNode.ui.checkbox.checked = allChildrenChecked;
                };
                setGroupChecked(node);
                // the layer can be associated with multiple nodes, so search the tree
                var origNode = node;
                while (node.parentNode) {
                    node = node.parentNode;
                }
                node.cascade(function(n) {
                    if (n.attributes.layer && n.attributes.layer.metadata['kartlagId'] === id) {
                        n.ui.checkbox.checked = event.ui.checkbox.checked;
                        setGroupChecked(n);
                    }
                });
                node = origNode;
            	if (event.ui.checkbox.checked) {
                    //app.mapPanel.layers.add(record);
                    /** bart code */
                    var doAdd = true;
                    app.mapPanel.layers.each(function(record) {
                        if (record.getLayer().metadata['kartlagId'] === id) {
                            doAdd = false;
                            return false;
                        }
                    });
                    if (doAdd) {
                        var clone = record.clone(); 
                        clone.set("group", "default"); 
                        clone.getLayer().setVisibility(true);
                        clone.getLayer().metadata['kartlagId'] = id;
                        app.mapPanel.layers.add(clone);
                        var maxExtent = clone.getLayer().maxExtent;
                        if (event.ui._silent !== true && maxExtent) {
                            app.mapPanel.map.zoomToExtent(maxExtent, true);
                        }
                        displayLegendGraphicsAndSpesialpunkt(app.mapPanel.map.getExtent() + "", layer.metadata['kartlagId'], layerRecord.getLayer(), event, app);
                    }
            	    //app.mapPanel.map.addLayer(layer); //adds layer to Overlay but mareano_wmslayerpanel is missing from properties and no layer properties are shown                        
		    //displayLegendGraphicsAndSpesialpunkt(app.mapPanel.map.getExtent() + "", layer.metadata['kartlagId'], layerRecord.getLayer(), event, app);   
            	    //getSpesialPunkt(app.mapPanel.map.getExtent() + "", layerRecord.getLayer().metadata['kartlagId'], layerRecord.getLayer(), event, app);
                } else {
            	    removeLayerLegendAndInfo(app.mapOfGMLspesialpunkt, layer.metadata['kartlagId'], record, layer, app);
                }
            });                                    
            return node;
        }
    });
    
    var layerContainerGruppe = new GeoExt.tree.LayerContainer({
    	checked: groupChecked,
        expanded: groupChecked,    	
        text: gruppeText,   
        listeners: {
            "checkchange": function(node, checked) { //setting all subnodes if parent is checked
                var extent = node.attributes.maxExtent;
                if (extent && checked) {
                    app.mapPanel.map.zoomToExtent(extent, true);
                }
                node.expand();
                var cs = node.childNodes;
                for(var c = cs.length-1; c >= 0; c--) { //add layers in reverse of reverse order - so in the right order
                    cs[c].ui._silent = true;
                    cs[c].ui.toggleCheck(checked);
                    delete cs[c].ui._silent;
            	} 
            }
        },                            
        layerStore: store,
        loader: tmpLoader
    });

    return layerContainerGruppe;
} 

function getSpesialPunkt(extent, kartlagId, layer, event, app) {
    jQuery.ajax({
        type: 'get',
        url: "spring/spesialpunkt",
        contentType: "application/json",
        data: {
            extent : extent,
            kartlagId: kartlagId
        },                	
        success:function(data) {

        }                
    });
}

function displayLegendGraphicsAndSpesialpunkt(extent, kartlagId, layer, event, app) {
	var languageChoosen = "en";
	if (document.location.href.indexOf("mareano.html") != -1) {
		languageChoosen = "norsk";
	}
    jQuery.ajax({
        type: 'get',
        url: "spring/legendAndSpesialpunkt",
        contentType: "application/json",
        data: {
            kartlagId: kartlagId,
            language: languageChoosen,
            extent: extent
        },
        success:function(data) {
        	addLegendGraphics(kartlagId, data);
        	addSpesialpunkt(extent, kartlagId, layer, event, app, data);
        }
    }); 
}

function displayLegendGraphics(kartlagId) {
	var languageChoosen = "en";
	if (document.location.href.indexOf("mareano.html") != -1) {
		languageChoosen = "norsk";
	}
    jQuery.ajax({
        type: 'get',
        url: "spring/legend",
        contentType: "application/json",
        data: {
            kartlagId: kartlagId,
            language: languageChoosen
        },
        success:function(data) {
        	addLegendGraphics(kartlagId, data);
        }
    }); 
}

function addLegendGraphics(kartlagId, data) {
    var currentLegend;
    jQuery('#newLegend').children().each(function(index, value){
        jQuery(value).children().each(function(index, value){
            currentLegend = jQuery(value).html();
        });	        
    });
    buildLegendGraphicsHTML( currentLegend, kartlagId, data );
    visKartlagInfoHTML( kartlagId, data ); 
}

function addSpesialpunkt(extent, kartlagId, layer, event, app, data) {
	if ( data.noSpesialpunkt == false ) { 
		var layerName = "";
		var styleMap = new OpenLayers.StyleMap({
			'default':{
				externalGraphic: "theme/imr/images/geofotoSpesialpunkt.png",
				cursor: "pointer"
			}
		});
		
        var snitt = new OpenLayers.Layer.Vector("GML", { 
        	displayInLayerSwitcher: false,
            protocol: new OpenLayers.Protocol.HTTP({ 
                url: "spring/getgml", 
                format: new OpenLayers.Format.GML()
            }), 
            strategies: [new OpenLayers.Strategy.Fixed()], 
            visibility: true,                                         
            projection: new OpenLayers.Projection("EPSG:32633"),
            styleMap: styleMap
        }); 
		
		snitt.events.register( "featureselected", snitt, GMLselected );
		app.mapOfGMLspesialpunkt[kartlagId] = snitt;	    
		app.mapPanel.map.addLayer( snitt );   	           
		
		var control = new OpenLayers.Control.SelectFeature( snitt );
		app.mapPanel.map.addControl( control );
		control.activate(); 	 
	}
}

function buildLegendGraphicsHTML( currentLegend, kartlagId, data ) {
    var legendGraphicsHTML = currentLegend+'<div id="'+kartlagId+'">';
    for ( var i=0; i < data.legends.length; i++ ) {
    	if ( i > 0 ) {
    		legendGraphicsHTML += '<div>';     
    	}
    	if ( data.legends[i].url != '') {
    		legendGraphicsHTML += '<table><tr><td><img src="' + data.legends[i].url + '"/></td>';
    		legendGraphicsHTML += '<td>' + data.legends[i].text + '</td></tr></table>';
    	} else {
    		legendGraphicsHTML += data.legends[i].text;
    	}
    	
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
    var infoHTML = '<div id="'+kartlagId+'tips" style="margin-bottom: 0.1cm;"><font style="font-size: 12px;"><b>'+ 
    	data.kartlagInfo.kartlagInfoTitel+'</b>' + ':<br />' + 
    	data.kartlagInfo.text + '</font></div>';

    kartlagInfoState += infoHTML;
    updateOrSetKartlagInfo(kartlagInfoState);
}

/**
 * Remove Legend div tag and KartlagInfo div tag associated with kartlagId
 */
function removeLayerLegendAndInfo(mapOfGMLspesialpunkt, kartlagId, record, layer, app) {
	
	app.mapPanel.layers.each(function(record) {
		if (record.getLayer().metadata['kartlagId'] === kartlagId) {
			this.remove(record);
			return false;
		}
	}, app.mapPanel.layers);
	
	if ( mapOfGMLspesialpunkt[kartlagId] != null ) { //fjern spesialpunkt     
    	app.mapPanel.map.removeLayer(mapOfGMLspesialpunkt[kartlagId], false);
    	mapOfGMLspesialpunkt[kartlagId] = null;
    }
    var legendDiv = '#'+kartlagId; //fjern legend 
    jQuery(legendDiv).remove();
    
    var temp = jQuery("<div>").html(kartlagInfoState); //fjern kartlaginfo 
    jQuery(temp).find(legendDiv+'tips').remove();
    kartlagInfoState = jQuery(temp).html();			
    updateOrSetKartlagInfo(kartlagInfoState);
}

function updateOrSetKartlagInfo(kartlagInfoState) {
    if ( Ext.getCmp('tips').rendered ) {
    	Ext.getCmp('tips').update(kartlagInfoState);
    } else {
    	Ext.getCmp('tips').html = kartlagInfoState;
    }	
}

/**
 * Show popup box with spesialpunkt
 */
function GMLselected (event) {
    if ( event.feature.data.type == "bilder" ) {
    	Ext.MessageBox.show({
    		title:event.feature.data.name, 
    		msg:'<a href="' + event.feature.data.description + '" TARGET="_blank"><img src=" '+event.feature.data.description+'" width=400 height=400 /></a>'
    	});
    } else if ( event.feature.data.type == "video" ) {
        Ext.MessageBox.show({
        	title:event.feature.data.name, 
        	msg:'<embed width="330" height="200" controls="TRUE" autoplay="TRUE" loop="FALSE" src="'+event.feature.data.description+'">'
        });
    } else if ( event.feature.data.type == "pdf" ) { // finnes ennaa ikke
        Ext.MessageBox.show({title:event.feature.data.name,msg:'<a href="' + event.feature.data.description + '" TARGET="_blank">' + event.feature.data.name + '</a>'});    
    } else if ( event.feature.data.type == "text" ) {
        jQuery.get('/geodata/proxy?url=http://atlas.nodc.no/website/mareano/' + event.feature.data.description, function(response) { 
            Ext.MessageBox.show({title:event.feature.data.name, msg: response}); 
		});
    }
}

/**
 * Adding overviewmap and keyboard defaults
 */
function addOverviewMapAndKeyboardDefaults(thisMap) {
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
    		{layers: "Land,Vmap0Land,Vmap0Kystkontur"},
                {singleTile: true, ratio: 1}
	);
    var tmpLayerOptions = {layers: [ol_wms2], mapOptions: layerOptions, maximized: false, minRatio: 48, maxRatio: 72, size: {w: 300, h: 150}};
    thisMap.addControl(new OpenLayers.Control.OverviewMap(tmpLayerOptions));
    thisMap.addControl(new OpenLayers.Control.KeyboardDefaults());

    /*** Fix to avoid vector layer below baselayers ***/
    for ( var i = thisMap.layers.length-1; i>=0; --i ) {
        if ( thisMap.layers[i] instanceof OpenLayers.Layer.Vector ) {
            thisMap.setLayerIndex( thisMap.layers[i], 33 );
        }
    }
}  

(function() {
    OpenLayers.DOTS_PER_INCH = 96.047217;
})();

Ext.ns("Mareano.plugins");

Mareano.plugins.LayerTree = Ext.extend(gxp.plugins.LayerTree, {
    ptype: "mareano_layertree",
    configureLayerNode: function(loader, attr) {
        attr.iconCls = getLayerIcon(attr.layer.url);
        var record = attr.layerStore.getByLayer(attr.layer);
        
        //Adds tooltip (qtip) to overlays and background layers 
        //see https://github.com/boundlessgeo/gxp/blob/5f2ae367f84e4f01eaeaca73a464a16acd0e8a71/src/script/plugins/LayerTree.js#L218
        if (attr.layer.abstracts != null && attr.layer.abstracts != undefined) {
            record.set('abstract', attr.layer.abstracts); 
        } else {
            record.set('abstract', attr.layer.name);
        }
        
        if (record.get('queryable') === true) {
            attr.cls = 'feature-info';
        }
        Mareano.plugins.LayerTree.superclass.configureLayerNode.apply(this, arguments);
    }
});

Ext.preg(Mareano.plugins.LayerTree.prototype.ptype, Mareano.plugins.LayerTree);

Mareano.Composer = Ext.extend(GeoExplorer.Composer, {

    appInfoText: "Mareano",
    aboutUrl: "./about.html",
    loadMapBaseUrl: "./",

    constructor: function() {
        Mareano.Composer.superclass.constructor.apply(this, arguments);
        
        this.on("beforesave", this.beforeSave, this);
        this.on("beforecreateportal", this.modifyPortal, this);
        
        //configure url for publish map to be correct
        gxp.EmbedMapDialog.prototype.initComponent = function() {
        	this.url = this.url.replace('../viewer/', './viewer/');
        	Ext.apply(this, this.getConfig());
        	gxp.EmbedMapDialog.superclass.initComponent.call(this);
        };

    },
    
    save: function(callback, scope) {
        var configStr = Ext.util.JSON.encode(this.getState());
        var requestConfig = {
            method: "POST",
            url: "../maps/",
            data: configStr
        };
        if (this.fireEvent("beforesave", requestConfig, callback) !== false) {
            OpenLayers.Request.issue(Ext.apply(requestConfig, {
                callback: function(request) {
                    this.handleSave(request);
                    if (callback) {
                        callback.call(scope || this, request);
                    }
                },
                scope: this
            }));
        }
    },

    beforeSave: function(requestConfig, callback) {
        requestConfig.url = requestConfig.url.replace('../maps', './maps');
    },

    loadConfig: function(config) {
        if (this.mapItems.length > 0 && this.mapItems[0].xtype == "gxp_scaleoverlay") {
            this.mapItems.splice(0, 1);
        }
        if (this.mapItems.length > 0 && this.mapItems[0].xtype == "gx_zoomslider") {
            this.mapItems.splice(0, 1);
        }        
        var zoomSliderRegionLookup = {                              
                0: this.international,
                1: this.international,
                2: this.national,
                3: this.national,
                4: this.national,
                5: this.regional,
                6: this.regional,
                7: this.regional,
                8: this.localArea,
                9: this.localArea,
                10: this.localArea,
                11: this.localArea,
                12: this.localArea,
                13: this.localArea,
                14: this.localArea,
                15: this.localArea,
                16: this.localArea,
                17: this.localArea
        }; 

        this.mapItems = [{
            xtype: "gx_zoomslider",
            vertical: true,
            height: 100,
            plugins: new GeoExt.ZoomSliderTip({
                template: this.zoomSliderText,
                getText: function(thumb) {                          
                    var data = {
                            zoom: thumb.value,
                            resolution: this.slider.getResolution(),
                            scale: Math.round(this.slider.getScale()),
                            region: zoomSliderRegionLookup[thumb.value]
                    };
                    return this.compiledTemplate.apply(data);
                }
            })
        }];
        
        var ptypes = ["gxp_featuremanager", "gxp_queryform", "gxp_featuregrid",
                      "gxp_zoomtoselectedfeatures", "gxp_layermanager", "gxp_legend", "gxp_addlayers",
                      "gxp_styler", "gxp_featureeditor", "gxp_googleearth"];
        var map_ptypes = ["gxp_navigation", "gxp_zoom", "gxp_navigationhistory", "gxp_zoomtoextent"];
        var mapTools = [];
        for (var i=config.tools.length-1; i>= 0; --i) {
            var tool = config.tools[i];
            if (tool.ptype === "gxp_layerproperties") {
                // direct output to new window instead of 'tree'
                tool.outputTarget = null;
            }
            if (tool.actions && tool.actions[0] === "mapmenu") {
                tool.actions[0] = "save-map";
                tool.actions.push("export-map");
            }
            if (map_ptypes.indexOf(tool.ptype) !== -1) {
                tool.actionTarget = "paneltbar";
                mapTools.push(tool);
                config.tools.splice(i, 1);
            }
            // remove the above ptypes and also the login button
            if (ptypes.indexOf(tool.ptype) !== -1 ||
                    (tool.actions && tool.actions.length > 0 &&
                            (tool.actions[0] == "->" || tool.actions[0] == "loginbutton"))) {
                config.tools.splice(i, 1);
            }
        }
        
        var me = this;
        var layerTreeGroups = {
                ptype: "mareano_layertree",
                groups: {
                    "default": "Overlays", // title can be overridden with overlayNodeText
                    "background": {
                        title: "Base Layers", // can be overridden with baseNodeText
                        exclusive: true
                    },
                    "backgroundSea": {
                        title: "Base Layers Sea", 
                        exclusive: true
                    }
                },      
                outputConfig: {
                    tbar: [],
                    id: "layers",
                    enableDD:true,
                    plugins: [{
                        ptype: "gx_treenodeactions",
                        actions: [{
                            action: "zoomscale",
                            qtip: me.zoomScaleTip
                        }, {
                            action: "queryable",
                            qtip: me.queryableTip
                        }],
                        listeners: {
                            action: function(node, action, evt) {
                                if (action === 'zoomscale') {
                                    var layer = node.layer;
                                    if (layer.maxExtent) {
                                        layer.map.zoomToExtent(layer.maxExtent, true);
                                    }
                                }
                            }
                        }
                    }]
                },
                outputTarget: "tree"
            };
            if ( config.map.projection == EPSG3575 ) {
                layerTreeGroups["groups"] = {
                    "default": "Overlays", 
                    "backgroundPolar": {
                        title: "Polar Base Layers", 
                        exclusive: true
                    }
                }
            }        
        config.tools.splice(0, 0 ,layerTreeGroups);
        config.tools.push({actions: ["saveImage"], actionTarget: {target: "paneltbar", index: 4}}); //add print icon next to save and publish icons 
        config.tools = config.tools.concat(mapTools.reverse());
        config.tools.push({
            ptype: "gxp_loadingindicator"
        }, {
            actions: ["-", "gaaTilKoordButton"], actionTarget: "paneltbar"
        }, {
            actions: ["gaaTilHavCombo"], actionTarget: "paneltbar"
        }, {
            actions: ["-", "mouseposition"], actionTarget: "paneltbar"
        },  {
            actions: ["->", "support"], actionTarget: "paneltbar"
        }, {
            actions: ["helpIcon"], actionTarget: "paneltbar"
        }, {
            actions: ["mareanoPolarBtn"], actionTarget: "paneltbar"
        }, {
            actions: ["mareanoNorskBtn"], actionTarget: "paneltbar"
        }, {
            actions: ["mareanoEngelskBtn"], actionTarget: "paneltbar"
        });
        Mareano.Composer.superclass.loadConfig.call(this, config);
    },

    createTools: function() {
        GeoExplorer.Composer.superclass.createTools.apply(this, arguments);
        new Ext.Button({
            id: "export-map",
            tooltip: this.exportMapText,
            handler: function() {
                this.doAuthorized(["ROLE_ADMINISTRATOR"], function() {
                    this.save(this.showEmbedWindow);
                }, this);
            },
            scope: this,
            iconCls: 'icon-export'
        });
        new Ext.Button({
            id: "save-map",
            tooltip: this.saveMapText,
            handler: function() {
                this.doAuthorized(["ROLE_ADMINISTRATOR"], function() {
                    this.save(this.showUrl);
                }, this);
            },
            scope: this,
            iconCls: "icon-save"
        });
        
        var oSrcPrj = new Proj4js.Proj('WGS84');
        var oDestPrj = new Proj4js.Proj('EPSG:32633');
        var me = this;
        function formatLonLats(lonLat) {
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
                    formatOutput: formatLonLats
                });
                this.map.addControl(control);
                MousePositionBox.superclass.afterRender.apply(this, arguments);
            }
        });
        var tmpMouseP = new MousePositionBox( {id: "mouseposition", map: this.mapPanel.map} );
        var theMap = this.mapPanel.map;
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
                    for(var i = 0; i<bar.length; i++){
                        bar[i] = bar[i].split(",");
                    }
                    var y = parseInt( bar[0] );
                    var x = parseInt( bar[1] );
                    var newPoint = new Proj4js.Point( x, y );
                    var geometryPoint
                    
                    if ( location.href.indexOf( "Polar" ) == -1 ) {
                        Proj4js.transform(new Proj4js.Proj('WGS84'), new Proj4js.Proj('EPSG:32633'), newPoint);
                    } else {
                        Proj4js.transform(new Proj4js.Proj('WGS84'), new Proj4js.Proj(EPSG3575), newPoint);
                    }
                    var geomPoint = new OpenLayers.Geometry.Point( newPoint.x, newPoint.y);
                    addMarker( "point("+newPoint.x+", "+newPoint.y+")", geomPoint, "panTo");
                };
            },
            scope: this
        });
        var vectorLayer = null; 
        function addMarker( vectorText, geomPoint, panFunc) {       
            var x = geomPoint.x;
            var y = geomPoint.y;
            
            var feature = new OpenLayers.Feature.Vector(geomPoint);
            feature.style = {
                externalGraphic: "externals/openlayers/img/marker.png",
                graphicWidth: 21,
                graphicHeight: 25,
                fillOpacity: 1
            };
 
            var lonLatPoint = new OpenLayers.LonLat( x, y );
            if ( panFunc == "panTo") {
                theMap.panTo( lonLatPoint ); 
                //use http://cs2cs.mygeodata.eu/ to convert
            } else {
                theMap.setCenter( lonLatPoint, 2 );
            }
            if (vectorLayer != null) {
                theMap.removeLayer(vectorLayer);
            }
            vectorLayer = new OpenLayers.Layer.Vector( vectorText );
            vectorLayer.addFeatures(feature);
            theMap.addLayer(vectorLayer);  
        }
        var barentshavet = new OpenLayers.Geometry.Point(1088474,8089849);
        var norskehavet = new OpenLayers.Geometry.Point(-1644,7334116);
        var nordsjoen = new OpenLayers.Geometry.Point(-164400,6734116);
        var skagerak = new OpenLayers.Geometry.Point(164400,6484116);
        var polhavet = new OpenLayers.Geometry.Point(1000000,8999999);
        if ( window.location.href.indexOf("Polar") > -1) {
            barentshavet = Proj4js.transform(new Proj4js.Proj(EPSG32633), new Proj4js.Proj(EPSG3575), barentshavet);
            norskehavet = Proj4js.transform(new Proj4js.Proj(EPSG32633), new Proj4js.Proj(EPSG3575), norskehavet);
            nordsjoen = Proj4js.transform(new Proj4js.Proj(EPSG32633), new Proj4js.Proj(EPSG3575), nordsjoen);
            skagerak = Proj4js.transform(new Proj4js.Proj(EPSG32633), new Proj4js.Proj(EPSG3575), skagerak);
            polhavet = Proj4js.transform(new Proj4js.Proj(EPSG32633), new Proj4js.Proj(EPSG3575), polhavet);
        }        
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
                        if ( combo.getValue() == this.zoomToItem1 ) {
                            addMarker( combo.getValue(), barentshavet, "setCenter" );
                        } else if ( combo.getValue() == this.zoomToItem2 ) {
                            addMarker( combo.getValue(), norskehavet, "setCenter" );
                        } else if ( combo.getValue() == this.zoomToItem3 ) {
                            addMarker( combo.getValue(), nordsjoen, "setCenter" );
                        }else if ( combo.getValue() == this.zoomToItem4 ) {
                            addMarker( combo.getValue(), skagerak, "setCenter" );
                        }else if ( combo.getValue() == this.zoomToItem5 ) {
                            addMarker( combo.getValue(), polhavet, "setCenter" );
                        }
                    },
                    scope: this
                }
            }
        });

        var support = new Ext.Button({
            type:'support',
            tooltip: 'Support',
            id: "support",
            handler: function() {
                window.location = 'mailto:datahjelp@imr.no';
            },
            iconCls: "icon-support",
            scope: this
        });

        var url  = location.href; 
        var indexMareano = url.indexOf("mareano.html");
        var indexMareanoEn = url.indexOf("mareano_en.html");
        var indexMareanoPolar = url.indexOf("mareanoPolar.html");
        var indexMareanoPolarEn = url.indexOf("mareanoPolar_en.html");
        var polarBtnTooltip = "Polar";
        if ( indexMareanoPolar > -1 || indexMareanoPolarEn > -1) {
            polarBtnTooltip = "UTM33";
        }        
        var helpIcon = new Ext.Button({
            type:'help',
            tooltip: this.helpTitle,
            id: "helpIcon",
            handler: function(event, toolEl, panel) {
                if ( indexMareano > -1) {
                    window.open( url.substring(0, indexMareano) + "pdf/GeoExplorer toolbar_Mareano_Norsk.pdf" );
                } else if ( indexMareanoPolar > -1) {
                        window.open( url.substring(0, indexMareanoPolar) + "pdf/GeoExplorer toolbar_Mareano_Norsk.pdf" );                    
                } else if ( indexMareanoEn > -1) {   
                    window.open( url.substring(0,indexMareanoEn) + "pdf/GeoExplorer toolbar_Mareano_Engelsk.pdf" );
                } else if ( indexMareanoPolarEn > -1) {   
                    window.open( url.substring(0,indexMareanoPolarEn) + "pdf/GeoExplorer toolbar_Mareano_Engelsk.pdf" );
                }
            },
            iconCls: "icon-question",
            scope: this
        });
        
        var mareanoNorskBtn = new Ext.Button({
            tooltip: "Norsk",
            id: "mareanoNorskBtn",
            buttonAlign: "center",
            handler: function(){
                if ( indexMareanoPolarEn > -1) {
                    location.href = url.substring(0,indexMareanoPolar) + "mareanoPolar.html?language=no";                    
                } else if ( indexMareanoEn > -1) {   
                    location.href = url.substring(0,indexMareanoEn) + "mareano.html?language=no";
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
                if ( indexMareanoPolar > -1) {
                    location.href = url.substring(0,indexMareanoPolar) + "mareanoPolar_en.html?language=en";                    
                } else if ( indexMareano > -1) {   
                    location.href = url.substring(0,indexMareanoEn) + "mareano_en.html?language=en";
                }
            },
            iconCls: "icon-english",
            scope: this
        }); 
        
        var mareanoPolarBtn = new Ext.Button({
            id: "mareanoPolarBtn",
            tooltip: polarBtnTooltip,
            buttonAlign: "right",
            handler: function(){
                if ( indexMareanoPolar > -1) {
                    location.href = url.substring(0,indexMareanoPolar) + "mareano.html?language=no";
                } else if ( indexMareanoPolarEn > -1) {   
                    location.href = url.substring(0,indexMareanoPolarEn) + "mareano_en.html?language=en";
                } else if ( indexMareano > -1) {   
                    location.href = url.substring(0,indexMareano) + "mareanoPolar.html?language=no";
                } else if ( indexMareanoEn > -1) {   
                    location.href = url.substring(0,indexMareanoEn) + "mareanoPolar_en.html?language=en";
                }
            },
            iconCls: "icon-polar",
            scope: this
        });   
        
        var saveImage = new Ext.Button({
            id: "saveImage",
            tooltip: "Export",
            buttonAlign: "right",
            handler: function() {
                toPrintMenuButton.call(this);
            },
            iconCls: "icon-printer",
            scope: this
        });          
    },

    modifyPortal: function() {
        var mareanoLegendContainer = new Ext.Panel({
            draggable:true,
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
            autoScroll:true,
            items: [westPanel, tipsPanel,
                    {   
                title: this.helpTitle,
                html: this.helpText,
                disabled: Ext.isEmpty(this.helpText),
                region: "center",
                autoScroll:true
                    }
            ]
        });

        var filteredNodes = [];
        var btnPostfix = '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;';
        var expandDiv = '<div style="position:absolute; top: 5px; right: 5px;" class="x-tool x-tool-toggle x-tool-collapse-east">&nbsp;</div>';
        var collapseDiv = '<div style="position:absolute; top: 5px; right: 5px;" class="x-tool x-tool-toggle x-tool-collapse-west">&nbsp;</div>';
        var westPanel2 = new Ext.Panel({
            border: true,
            layout: "border",
            region: "west",
            unstyled:true,
            width: 240, 
            tbar: [{
                text: this.visibilityText,
                tooltip: this.visibilityTooltip,
                handler: function() {
                    var tree = Ext.getCmp('thematic_tree');
                    var checked = tree.getChecked();
                    for (var i=0, ii = checked.length; i<ii; ++i) {
                        checked[i].ui.toggleCheck(false);
                    }
                    var rootRightTree = Ext.getCmp('layers');
                    checked = rootRightTree.getChecked();
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
                        westPanel2.setWidth(440); 
                    } else {
                        cmp.setText(this.expandText + btnPostfix + expandDiv);
                        westPanel2.setWidth(240); 
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
                width: 240,
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
                    //title: this.thematicText,
                    
                    tbar: [{
                        text: '<b>'+this.thematicText +'</b>'
                    }, '->', {
                        xtype: 'combo',
                        id: 'filterLayers',
                        width:160,
                        store: ['Layer:','Abstract:'],
                        triggerAction: 'all',
                        editable: true,
                        mode: 'local',
                        enableKeyEvents: true,
                        listeners: {
                            afterrender:function(rec) { //set default value
                                Ext.getCmp("filterLayers").setValue('Layer:');
                            },
                            keydown: function(form, e) {
                                if ( this.lastQuery == "" || this.lastQuery == "Layer:" || this.layerQuery == "Abstract:") {
                                    var tree = Ext.getCmp('thematic_tree');
                                    var treeRoot = tree.getRootNode();
                                    treeRoot.expandChildNodes(true);
                                }
                            },
                            keydown: function(form, e) {
                                var queryIsEmpty = this.lastQuery == "" || this.lastQuery == "Layer:" || this.layerQuery == "Abstract:";
                                //if ( queryIsEmpty ) {
                                    var tree = Ext.getCmp('thematic_tree');
                                    var treeRoot = tree.getRootNode();
                                    treeRoot.expandChildNodes(true);
                                //}
                            },
                            keyup: function(form, e) {
                                var query = this.getRawValue();
                                if ( query == undefined ) {
                                    console.log("query undefined:")
                                }
                                var filterChoosen = "Layer:";
                                if ( query.indexOf("Layer:") > -1 ) {
                                    if ( query != "Layer:") {
                                        Ext.each(filteredNodes, function(n) {
                                            n.getUI().show();
                                        });
                                        filteredNodes = [];
                                    }
                                    query = query.replace("Layer:", "");    
                                    filterChoosen = "Layer:";
                                } else if ( query.indexOf("Abstract:") > -1 ) {
                                    if ( query != "Abstract:") {
                                        Ext.each(filteredNodes, function(n) {
                                            n.getUI().show();
                                        });
                                        filteredNodes = [];
                                    }
                                    query = query.replace("Abstract:", ""); 
                                    filterChoosen = "Abstract:";
                                }
                                
                                if ( e.keyCode == 8 || query == "" ) {
                                    Ext.each(filteredNodes, function(n) {
                                        n.getUI().show();
                                    });
                                    filteredNodes = [];
                                }
                                var tree = Ext.getCmp('thematic_tree');
                                var treeRoot = tree.getRootNode();
                                if ( query == "" ) {
                                    treeRoot.collapseChildNodes( true );
                                    return;
                                }

                                if ( query.length < 2 ) {
                                    return;
                                }
                                var re = new RegExp(Ext.escapeRe( query ), 'i');
                                //console.log("re:"+query);
                                
                                var getNodeValue = function( anode ) {
                                    if ( filterChoosen == "Layer:") {
                                        if ( anode.attributes != null && anode.attributes.text != null ) {
                                            return anode.attributes.text;
                                        } else if ( anode.layer!= null && anode.layer.name != null ) {
                                            return anode.layer.name;
                                        } 
                                        return "";
                                    } else if ( filterChoosen == "Abstract:" ) {
                                        if ( anode.attributes != null && anode.attributes.qtip != null ) {
                                            return anode.attributes.qtip;
                                        } 
                                        return "";
                                    }
                                }                               

                                var filter = function(node) { // descends into child nodes recursivly)
                                    if ( node.attributes != null && node.attributes.text != null) { //its a parent folder
                                        if ( re.test( getNodeValue(node) ) ) { //basecase 1: If match on folder name - ignore children
                                            return true;
                                        }
                                    }
                                    var thisLeafMatch = false;
                                    if(node.hasChildNodes()) {
                                        for ( var i=0; i < node.childNodes.length; i++) {
                                            var childNode = node.childNodes[i];
                                            if(childNode.isLeaf()) {
                                                if ( re.test( getNodeValue(childNode) ) ) {
                                                    return true;
                                                }
                                            } else { 
                                                var childLeafMatch = filter(childNode);
                                                if ( childLeafMatch == false ) {
                                                    filteredNodes.push(childNode);
                                                } else {
                                                    thisLeafMatch = true;
                                                }
                                            }
                                        }
                                    }
                                    return thisLeafMatch;
                                }
                                treeRoot.eachChild( function(childNode) {
                                    //alternatively dont remove hovedtema without match but dont expand either
                                    //filter(childNode);
                                    var childLeafMatch = filter(childNode);
                                    if ( childLeafMatch == false ) {
                                        filteredNodes.push(childNode);
                                    }                                   
                                }); 
                                Ext.each(filteredNodes, function(n) {
                                    n.getUI().hide();
                                });  
                            },
                            focus : {
                                fn : function(view, record, item, index, even) {
                                    //this.setValue("");
                                    var tree = Ext.getCmp('thematic_tree');
                                    var treeRoot = tree.getRootNode();
                                    Ext.each(filteredNodes, function(n) {
                                        //var el = Ext.fly(tree.getView().getNodeByRecord(n));
                                        var indexEl = treeRoot.indexOf(n);
                                        var el = treeRoot.item(indexEl);
                                        if (el != null) {
                                            el.getUI().show();
                                        }
                                    });
                                    treeRoot.collapseChildNodes( true );
                                }
                            }                       
                        }
                    }],    
                    layout: "fit",
                    id: "thematic_tree"
                }, legendContainerContainer]
            }, {
                xtype: 'panel',
                layout: "border",
                width: 200,
                region: "center",
                items: westPanelTabs
            }]
        });

        var innerNorthPanel = new Ext.Panel({
            border: true,
            region: "north",
            split: false,
            id: "topPanelHeading",
            collapseMode: "mini",
            bodyStyle: "background-image:url('http://www.mareano.no/kart/images/nav-main-background.jpg')",
            html:
                //html - content reloaded with content from MareanoController - but this must be here for the rest of the rest of the panels to
                //load in the correct size. 
                '<table width="100%" cellspacing="0" border="1"><tr height="45"> ' + 
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
            height: 97,
            split: false,
            unstyled:true,
            collapseMode: "mini",
            region: "north",
            id: 'page-header-panel',
            items: [innerNorthPanel, this.portalItems[0].tbar /* add the existing tbar */]
        });       

        for (var i = this.portalItems[0].items.length-1; i>=0; --i) {
            // get rid of GeoExplorer's west and south panel
            if (this.portalItems[0].items[i].region == "west" || this.portalItems[0].items[i].region == "south") {
                this.portalItems[0].items.splice(i, 1);
            }
        }
        // get rid of GeoExplorer's tbar, it it part of the north panel in our case
        delete this.portalItems[0].tbar;
        // add our new panels here
        this.portalItems[0].items.push(northPanel, westPanel2);
    }
});

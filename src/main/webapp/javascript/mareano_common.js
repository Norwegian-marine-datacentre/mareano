/** global variables in the window object (inside app.ready)*/
var silent = false;
var kartlagInfoState = ""; //used by removeLayerLegendAndInfo(mapOfGMLspesialpunkt, kartlagId)
var orderOfLayerIdsCache = [10];

function loadMareano(mapPanel, app) {
    OpenLayers.Util.alphaHackNeeded=false;
    
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

function addLayerToGroup( gruppeNavn, gruppeText, map, mapPanel, layers, store, app ) {
    var layerName = [];
    var groupChecked = 
    	getAllLayersForAGroupAndIsGroupChecked(gruppeNavn, layers, mapPanel, layerName);
    
    var layerLoader = new GeoExt.tree.LayerLoader({
        store: store,
        filter: function(record) {
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
            
            //If layer already in map, set checked
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
                addKartbildeAbstractOrRemove(node.parentNode, node.parentNode.ui.checkbox.checked);
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
//                      var maxExtent = clone.getLayer().maxExtent; //zoom to extent for layers
//                      if (event.ui._silent !== true && maxExtent) {
//                      app.mapPanel.map.zoomToExtent(maxExtent, true);
//                      }
                        displayLegendGraphicsAndSpesialpunkt(app.mapPanel.map.getExtent() + "", layer.metadata['kartlagId'], layerRecord.getLayer(), event, app);
                    }
                    //app.mapPanel.map.addLayer(layer); //adds layer to Overlay but mareano_wmslayerpanel is missing from properties and no layer properties are shown                        
                    //displayLegendGraphicsAndSpesialpunkt(app.mapPanel.map.getExtent() + "", layer.metadata['kartlagId'], layerRecord.getLayer(), event, app);   
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
//              if (extent && checked) { //zoom to extent for pictures
//              app.mapPanel.map.zoomToExtent(extent, true);
//              }
                node.expand();
//              addKartbildeAbstractOrRemove(node, checked);

                var cs = node.childNodes;
                for(var c = cs.length-1; c >= 0; c--) { //add layers in reverse of reverse order
                	if ( checked == true ) {
                		var kartlagFromKartbilde = cs[c].layer.metadata.kartlagId;
                		orderOfLayerIdsCache.unshift( kartlagFromKartbilde );
                	}
                    cs[c].ui._silent = true;
                    cs[c].ui.toggleCheck(checked);
                    delete cs[c].ui._silent;
                } 
            }
        },                            
        layerStore: store,
        loader: layerLoader
    });
    return layerContainerGruppe;
} 

function getKartlagBildeDiv(nodeText) {   
    nodeText = nodeText.toLowerCase();
    nodeText = nodeText.replace(/æ/g,'ae'); // /g global option
    nodeText = nodeText.replace(/ø/g,'oe');
    nodeText = nodeText.replace(/å/g,'aa');
    nodeText = nodeText.replace(/ /g,'');

    nodeText = nodeText.replace(/[!"#$%&'()*+,.\/:;<=>?@[\\\]^`{|}~]/g, "");
    return nodeText;
}

function getLanguage() {
    var languageChoosen = "en";
    if (document.location.href.indexOf("mareano.html") != -1) {
        languageChoosen = "no";
    }
    return languageChoosen;
}

function addKartbildeAbstractOrRemove(node, checked) {
    addKartbildeAbstractOrRemoveWithName(node.attributes.text, checked);
}
    
function addKartbildeAbstractOrRemoveWithName(text, checked) {
    if ( checked == true) {
        var languageChoosen = getLanguage();
        jQuery.ajax({
            type: 'get',
            url: "spring/infoKartBilde",
            contentType: "application/json",
            data: {
                kartbildeNavn: text,
                language: languageChoosen
            },
            success:function(data) {
                var legendDiv = getKartlagBildeDiv(text)
                visKartlagInfoHTML( legendDiv, data );
            }
        }); 
    } else if ( checked == false ) {
        var legendDiv = getKartlagBildeDiv(attributes.text)
        fjernKartlagInfo(legendDiv);
    }
}

function addLegend(kartlagId) {
	displayLegendGraphicsAndSpesialpunkt(null, kartlagId, null, null, null);
}

function displayLegendGraphicsAndSpesialpunkt(extent, kartlagId, layer, event, app) {
    var languageChoosen = getLanguage();
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
//        	if ( orderOfLayerIdsCache.length == 0 ) {
        		addLegendGraphics(kartlagId, data);
        		addSpesialpunkt(extent, kartlagId, layer, event, app, data);
//        	} else {
//        		if ( orderOfLayerIdsCache[0] == kartlagId) {
//            		addLegendGraphics(kartlagId, data);
//            		addSpesialpunkt(extent, kartlagId, layer, event, app, data);
//        		} else {
//            		setTimeout(function (){
//            		addLegendGraphics(kartlagId, data);
//            		addSpesialpunkt(extent, kartlagId, layer, event, app, data);
//            		}, 5000);
//            	}
//        		orderOfLayerIdsCache.splice(0, 1);
//        	}
        }
    }); 
}

function displayLegendGraphics(kartlagId) {
    var languageChoosen = getLanguage();
//    jQuery.ajax({
//        type: 'get',
//        url: "spring/legend",
//        contentType: "application/json",
//        data: {
//            kartlagId: kartlagId,
//            language: languageChoosen
//        },
//        success:function(data) {
//            addLegendGraphics(kartlagId, data);
//        }
//    }); 
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

var controlSelectFeature = null;
function addSpesialpunkt(extent, kartlagId, layer, event, app, data) {
    if ( data.noSpesialpunkt == false ) { 
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

        if ( controlSelectFeature == null ) {
            controlSelectFeature = new OpenLayers.Control.SelectFeature( snitt );
            defineSelectFeatureAddLayer();

            // add openlayers pull request: https://github.com/openlayers/openlayers/issues/958
            defineRemoveLayer();
            defineDestroy();
            defineSetMap();
            defineResetRoot();
        } else {
            controlSelectFeature.addLayer( snitt );
        }

        app.mapPanel.map.addControl( controlSelectFeature );
        controlSelectFeature.activate(); 	 
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
    
    fjernKartlagInfo(kartlagId);
}

function fjernKartlagInfo(legendDiv) {
    var temp = jQuery("<div>").html(kartlagInfoState); 

    legendDiv = getKartlagBildeDiv(legendDiv);
    jQuery(temp).find('#'+legendDiv+'tips').remove();

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
            msg:'<a href="' + event.feature.data.description + '" TARGET="_blank"><img src=" '+event.feature.data.description+'" width=150 height=100 /></a>'
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




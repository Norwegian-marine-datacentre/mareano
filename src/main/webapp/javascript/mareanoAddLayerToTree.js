/** global variables in the window object (inside app.ready)*/
var silent = false;
var kartlagInfoState = []; //used by removeLayerLegendAndInfo(mapOfGMLspesialpunkt, kartlagId)
var layersInPicture = [];
var bakgrunnInfoDiv;   // Used to hold background layer info

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

    //Hacky approach to find current background layer on initial map display
    changeBakgrunnsInfo(app.mapPanel.map.getLayersBy("visibility", true)[1]);  
    var mapLayerChanged = function(e) {
    	if (e.layer.visibility) {
    	    changeBakgrunnsInfo(e.layer);
    	} 
    };
    
    app.mapPanel.map.events.register('changelayer', app, mapLayerChanged);
    
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

/** code common with generelleKartLayerGroup.js */
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
                        displayLegendGraphicsAndSpesialpunkt(app.mapPanel.map.getExtent() + "", layerRecord.getLayer(), event, app);
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
                node.expand();

                var childNodes = node.childNodes;
                if ( checked == true) {
                    for(var c = childNodes.length-1; c >= 0; c--) { 
                        layersInPicture.unshift( childNodes[c].layer.metadata.kartlagId );
                    }
                } else {
                    layersInPicture = [];    
                }
                for(var c = childNodes.length-1; c >= 0; c--) { //add layers in reverse of reverse order
                    childNodes[c].ui._silent = true;
                    childNodes[c].ui.toggleCheck(checked);
                    delete childNodes[c].ui._silent;
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
    nodeText = nodeText.replace(/�/g,'ae'); // /g global option
    nodeText = nodeText.replace(/�/g,'oe');
    nodeText = nodeText.replace(/�/g,'aa');
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
                var legendDiv = getKartlagBildeDiv(text);                
                addKartbildeInfo( legendDiv, data );
            }
        }); 
    } else if ( checked == false ) {
        var legendDiv = getKartlagBildeDiv(text)
        removeInfo(legendDiv);
    }
}

function addLegend(layer) {
	displayLegendGraphicsAndSpesialpunkt(null, layer, null, null);
}

function displayLegendGraphicsAndSpesialpunkt(extent, layer, event, app) {
    var languageChoosen = getLanguage();
    var id = layer.metadata['kartlagId'];
    var title = layer.metadata['kartlagTitle'];
    jQuery.ajax({
        type: 'get',
        url: "spring/legendAndSpesialpunkt",
        contentType: "application/json",
        data: {
            kartlagId: id,
            language: languageChoosen,
            extent: extent
        },
        success:function(data) {
            addLegendAndInfo(layer, data);
            addSpesialpunkt(extent, id, layer, event, app, data);
        }
    }); 
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

function addLegendAndInfo( layer, data ) {

    var kartlagId = layer.metadata['kartlagId'];
    var kartlagTitle = layer.metadata['kartlagTitle'];
    var currentLegends = getCurrentLegendFragment();
    
    var insertAfterIndex = insertLegendAtIndex( currentLegends, kartlagId )
    var newLegendDiv = createNewLegendFragment( kartlagId, kartlagTitle, data );
    var arrayCurrentLegend = getArrayOfLegendDivs( currentLegends );
    
    arrayCurrentLegend.splice(insertAfterIndex +1, 0, newLegendDiv); 
    Ext.getCmp('newLegend').update(arrayCurrentLegend.join(""));
    
    var newInfoDiv = createNewInfoFragment(layer, data);
    kartlagInfoState.splice(insertAfterIndex +1, 0, newInfoDiv);
    //cannot use .update() as component isnt visible
    //and extjs doesnt update hidden components - so have to set html
    updateOrSetKartlagInfo(kartlagInfoState);
}

function changeBakgrunnsInfo(layer) {
    var languageChoosen = getLanguage();
    jQuery.ajax({
        type: 'get',
        url: "spring/legendAndSpesialpunkt",
        contentType: "application/json",
        data: {
            kartlagId: layer.metadata['kartlagId'],
            language: languageChoosen,
            extent: ""
        },
        success: function(data) {
            bakgrunnInfoDiv = createNewInfoFragment(layer ,data); 
            updateOrSetKartlagInfo(kartlagInfoState);
        },
        error:function() {
            bakgrunnInfoDiv = "";
            updateOrSetKartlagInfo(kartlagInfoState);
        }
    }); 
}

function getCurrentLegendFragment() {
    var currentLegends;
    jQuery('#newLegend').children().each(function(index, value){
        jQuery(value).children().each(function(index, value){
            currentLegends = jQuery(value).html();
        });         
    });
    return currentLegends; 
}

/**
 * Bug: If panel that is updated is not visible - then the panel's html body is not updated. 
 * After the panel is made visible first time- all future updates() behave correctly whether it is hidden or not.
 * http://www.sencha.com/forum/archive/index.php/t-103797.html
 **/
function addKartbildeInfo(kartlagId, data) {
    var newInfoDiv = '<div id="'+kartlagId+'tips" style="margin-bottom: 0.1cm;"><font style="font-size: 12px;"><b>'+ 
        data.kartlagInfo.kartlagInfoTitel+'</b>' + ':<br />' + 
        data.kartlagInfo.text + '</font></div>';

    kartlagInfoState.push(newInfoDiv);
    updateOrSetKartlagInfo( kartlagInfoState );
}

/**
 * Remove Legend div tag and KartlagInfo div tag associated with kartlagId
 */
function removeLayerLegendAndInfo(mapOfGMLspesialpunkt, kartlagId, record, layer, app) {

    removeKartlagFromHash(kartlagId);
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
    
    removeInfo(kartlagId);
}

function removeInfo(legendDiv) {
    var infoDivsAsDom = jQuery("<div>").html(kartlagInfoState.join("")); 

    legendDiv = getKartlagBildeDiv(legendDiv);

    jQuery(infoDivsAsDom).find('#'+legendDiv+'tips').remove();

    var newLegendFragment = jQuery(infoDivsAsDom).html(); 
    kartlagInfoState = getArrayOfInfoDivs( newLegendFragment );
    updateOrSetKartlagInfo(kartlagInfoState);
}

/**
 * Update Info tab with text
 * @param kartlagInfoState
 */
function updateOrSetKartlagInfo(kartlagInfoState) {
    if ( Ext.getCmp('tips').rendered ) {
        Ext.getCmp('tips').update(kartlagInfoState.join("")+bakgrunnInfoDiv);
    } else {
        Ext.getCmp('tips').html = kartlagInfoState.join("")+bakgrunnInfoDiv;
    }	
}

function showBilder(imageTitle,imageURL)
{
    var preloadImage = new Image();
    preloadImage.onload = function() {
	var wind =new Ext.Window({
	    width:Math.min(preloadImage.width,Ext.getBody().getViewSize().width/2),
	    y:200,
	    x:200,
	    boxMaxWidth:preloadImage.width,
	    title:imageTitle,
	    closeAction:'destroy',
	    items: [{
		xtype: 'box',
		autoEl:{ tag: 'a',
			 href: imageURL,
			 target: '_blank'
		       },
		html: { tag: 'img',
			src: imageURL,
			width:"100%"
		      }
	    }, {
		xtype: 'box',
		html: { tag: 'img',
			width:'100%',
			src: 'theme/imr/img/arrow_down_right.png'
		      },
		cls:"expand-image-window",
		listeners: {
		    render: function(c){
			c.getEl().on({
			    click: function() {
				//Calc new window width
				//First constrain to image size
				var newWidth=Math.min(wind.getWidth()*2,preloadImage.width);
				//Then constrain to fit in browser window
				newWidth=Math.min(newWidth,Ext.getBody().getViewSize().width-wind.x);
				wind.setWidth(newWidth);
			    }})}}
		
	    }],
	    layout: 'fit'
	}).show();
	

    };
    preloadImage.onerror = function() { //Is this enough of a message?
	Ext.MessageBox.show({
	    title:imageTitle,
	    buttons: Ext.Msg.OK,
	    msg:'Unable to load image at this time'
	});
    }
    preloadImage.src=imageURL;
 
}


/**
 * Show popup box with spesialpunkt
 */
function GMLselected (event) {
    if ( event.feature.data.type == "bilder" ) {
	showBilder(event.feature.data.name,event.feature.data.description);
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




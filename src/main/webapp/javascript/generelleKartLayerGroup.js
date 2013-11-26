function addGenerelleLayerToGroup( gruppeNavn, gruppeText, map, mapPanel, layers, store, app  ) {
    var indexOfWMSgruppe = [];
    var layerName = [];
    var childrenVisible = 0;
    var count = 0;    
    for (var i = layers.length-1;i>=0;--i) {
        if ( layers[i].get("group") == gruppeNavn ) {
        	count++;
        	if (layers[i].getLayer().visibility === true) {
        		childrenVisible++;
        	} 
            layerName.push(layers[i].getLayer().params.LAYERS);
        }
    }

    var groupChecked = (childrenVisible === count);
    var generelleLayerLoader = new GeoExt.tree.LayerLoader({
        store: store,
        filter: function(record) {
            /** adding matching layer to matching container group */
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
            attr.iconCls = cssBgImg;
            attr.checked = layerRecord.getLayer().visibility;
            attr.id = layerRecord.data.id;
            attr.cls = "general-layers-w-checkbox";

            attr.autoDisable = false;
            var node = GeoExt.tree.LayerLoader.prototype.createNode.call(this, attr);       
            app.mapPanel.layers.on("remove", function(store, record) {
            	if (silent !== true && record.getLayer().metadata['kartlagId'] === attr.layer.metadata['kartlagId']) {
            		node.ui.toggleCheck(false);
            	}
            });            
            node.on("checkChange", function(event) {
            	var cb = node.getUI().checkbox;
            	if ( cb && Ext.get(cb).getAttribute('type') === 'checkbox' ) {
            		var layer = layerRecord.getLayer();
            		var record = event.layerStore.getByLayer(layer);
            		if (event.ui.checkbox.checked) {
            			//app.mapPanel.layers.add(record);
            			/** bart code */
                        var id = layer.metadata['kartlagId'];
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
                        }
            			//app.mapPanel.map.addLayer(layer); //adds layer to Overlay but mareano_wmslayerpanel is missing from properties and no layer properties are shown                        
		                displayLegendGraphicsAndSpesialpunkt(app.mapPanel.map.getExtent() + "", layer.metadata['kartlagId'], layerRecord.getLayer(), event, app);   
            			//getSpesialPunkt(app.mapPanel.map.getExtent() + "", layerRecord.getLayer().metadata['kartlagId'], layerRecord.getLayer(), event, app);
            		} else {
            			removeLayerLegendAndInfo(app.mapOfGMLspesialpunkt, layer.metadata['kartlagId'], record, layer, app);
            		}
            	}
            });                                    
            return node;
        }
    });
    
    var layerContainerGruppe = new GeoExt.tree.LayerContainer({
    	checked: groupChecked,
        expanded: groupChecked,    	
        text: gruppeText,   
        cls: "general-layers-w-checkbox",
        listeners: {
            "checkchange": function(node, checked) { //setting all subnodes if parent is checked
            	node.expand();
            	var cs = node.childNodes;
            	for(var c = cs.length-1; c >= 0; c--) { //add layers in reverse of reverse order - so in the right order
            		cs[c].ui.toggleCheck(checked);
            	} 
            }
        },                            
        layerStore: store,
        loader: generelleLayerLoader
    });

    return layerContainerGruppe;
}
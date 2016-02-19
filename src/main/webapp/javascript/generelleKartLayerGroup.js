/** code common with generelleKartLayerGroup.js 
 * Method is used in addLayerTreeToRoot.jsp
*/
function addGenerelleLayerToGroup( gruppeNavn, gruppeText, map, mapPanel, layers, store, app  ) {
    var layerName = [];
    var groupChecked = 
    	getAllLayersForAGroupAndIsGroupChecked(gruppeNavn, layers, mapPanel, layerName);
    
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
            
            //If layer already in map, set checked
            var idx = app.mapPanel.layers.findBy(function(record) {
                return record.getLayer().metadata['kartlagId'] === attr.layer.metadata['kartlagId'];
            });
            cssBgImg = getLayerIcon(url);
            attr.iconCls = cssBgImg;
            attr.cls = "general-layers-w-checkbox";
            attr.checked = (layerRecord.getLayer().visibility || (idx !== -1));
            attr.id = layerRecord.data.id;
            attr.autoDisable = false;
            
            if (attr.layer.abstracts != null && attr.layer.abstracts != undefined) {
                attr.qtip = layerRecord.getLayer().abstracts;
            } else {
                attr.qtip = layerRecord.getLayer().name;
            }
            
            
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
		                displayLegendGraphicsAndSpesialpunkt(app.mapPanel.map.getExtent() + "", layerRecord.getLayer(), event, app);   
            		} else {
            			removeLayerLegendAndInfo(app.mapOfGMLspesialpunkt, layer.metadata['kartlagId'], record, layer, app);
            		}
            	}
            });                                    
            return node;
        }
    });
    
    var generelleLayerContainerGruppe = new GeoExt.tree.LayerContainer({
    	checked: groupChecked,
        expanded: groupChecked,    	
        text: gruppeText,   
        qtip: gruppeText,
        expanded: true,
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
    return generelleLayerContainerGruppe;
}
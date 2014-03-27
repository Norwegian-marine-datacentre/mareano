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

/**
 * Show GML points (spesialpunkt) 
 * Code related to bug: "Visning av bilder (spesialpunkt) virker ikke) -
 * https://github.com/Norwegian-marine-datacentre/mareano/issues/2
*/
OpenLayers.Control.SelectFeature.prototype.clickFeature = function(feature) {	
    if(!this.hover) {
        var selected = (OpenLayers.Util.indexOf(
            feature.layer.selectedFeatures, feature) > -1);
        if(selected) {
            if(this.toggleSelect()) {
                this.unselect(feature);
            } else if(!this.multipleSelect()) {
                this.unselectAll({except: feature});
            }
            // bartvde, even if feature was selected before, fire the featureselected event
            // So - it should be possible to show the same picture two or more times
            this.select(feature);
        } else {
            if(!this.multipleSelect()) {
                this.unselectAll({except: feature});
            }
            this.select(feature);
        }
    }
}; 

/**
 * APIMethod: addLayer
 * Add a layer to the control, making the existing layers still selectable
 * If layer and layers property both set - use layers property
 * If only layer property set - set layers to [layer, newLayer]
 * and make initLayer create a RootContainer
 *
 * Parameters:
 * layer - element <OpenLayers.Layer.Vector> 
 */
function defineSelectFeatureAddLayer() {
	OpenLayers.Control.SelectFeature.prototype.addLayer = function( newLayer ) {
	    var isActive = this.active;
	    this.deactivate();
		if (this.layers == null) {
			if (this.layer != null) {
				this.layers = [this.layer];
				this.layers.push(newLayer);
			} else {
				this.layers = [newLayer];
			}
		} else {	
			this.layers.push(newLayer);
		}
		this.initLayer(this.layers);
		this.handlers.feature.layer = this.layer;
	    if (isActive) {
	        this.activate();
	    }
	}
};

/**
 * Method: removeLayer
 * Handles the map's preremovelayer event
 *
 * Parameters:
 * evt - {Object} The listener argument
 */
function defineRemoveLayer() {
	OpenLayers.Control.SelectFeature.prototype.removeLayer = function(evt) {
		alert("removeLayer");
	 	if(this.layers) {
	 		for(var i=0; i<this.layers.length; ++i) {
	 			if(evt.layer = this.layers[i]) {
	 				this.layer.resetRoot(evt.layer);
	 				OpenLayers.Util.removeItem(this.layers, evt.layer);
	 				return;
	 			}
	 		}
	 	}
	 }
}

/**
 * Method: destroy
 */
function defineDestroy() {
	OpenLayers.Control.SelectFeature.prototype.destroy = function() {
		alert("destroy");
	    if(this.active && this.layers) {
	        this.map.removeLayer(this.layer);
	    }
	    if (this.map) {
	    	this.map.events.unregister("preremovelayer", this, this.removeLayer);
	    }
	    OpenLayers.Control.prototype.destroy.apply(this, arguments);
	    if(this.layers) {
	        this.layer.destroy();
	    }
	}
};

/** 
 * Method: setMap
 * Set the map property for the control. 
 * 
 * Parameters:
 * map - {<OpenLayers.Map>} 
 */
function defineSetMap() {
	OpenLayers.Control.SelectFeature.prototype.setMap = function(map) {
		alert("setMap");
	    this.handlers.feature.setMap(map);
	    if (this.box) {
	        this.handlers.box.setMap(map);
	    }
	    map.events.unregister("preremovelayer", this, this.removeLayer);
	    map.events.register("preremovelayer", this, this.removeLayer);
	    OpenLayers.Control.prototype.setMap.apply(this, arguments);
	}
};

/**
 * Method: resetRoot
 * Resets the root node for a single layer back into the layer it belongs to.
 *
 * Parameters:
 * layer - {<OpenLayers.Layer.Vector>}
 */
function defineResetRoot() {
	OpenLayers.Layer.Vector.RootContainer.prototype.resetRoot = function(layer) {
		alert("resetRoot");
	    for(var i=0; i<this.layers.length; ++i) {
	        if(layer == this.layers[i] && this.renderer && layer.renderer.getRenderLayerId() == this.id) {
	            this.renderer.moveRoot(layer.renderer);
	            OpenLayers.Util.removeItem(this.layers, layer);
	            return;
	        }
	    }
	}
};  



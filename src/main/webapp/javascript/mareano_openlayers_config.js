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

function defineSelectFeatureAddLayer() {
	OpenLayers.Control.SelectFeature.prototype.addLayer = function(layer) {
	    var isActive = this.active;
	    var currLayers = this.layers; 
	    this.deactivate();
	    
	    if(this.layers) {
	        this.layer.destroy();
	        this.layers = null;
	    }
	    if ( currLayers != null) {
	    	currLayers.push(layer);	
	    	this.initLayer(currLayers);
	    } else {
	    	this.initLayer([layer]);
	    }
	    this.handlers.feature.layer = this.layer;
	    if (isActive) {
	        this.activate();
	    }
	}
};
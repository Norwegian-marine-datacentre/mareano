/**
 * Given a group name and the list of all layers,
 * sets the array layerNames to all layers beloning to this group
 * and returns groupChecked=true if all layers are checked (visible) 
 * 
 * Special case: if layers are from a saved map (url with #maps/) then
 * show groupChecked if any layer in that group is checked - unless
 * the group is Genellere kart/General layers
 * 
 * If requesting a saved map the url will look like
 * http://www.mareano.no/kart/mareano.html#maps/5
 * Then expand to show which layers are selected
 * And request to show lengend and info about layer
 * 
 * @param gruppeNavn
 * @param layers
 * @param layerNames
 * @returns Boolean - groupChecked
 */
var globalLayersFromSavedMapAlreadyAddedLegend = [];
function getAllLayersForAGroupAndIsGroupChecked(gruppeNavn, layers, mapPanel, layerNames) {
	var childrenVisible = 0;
	var count = 0;    
	for (var i = layers.length-1; i>=0; --i) {
	    if ( layers[i].get("group") == gruppeNavn ) {
	        count++;
	        var idx = mapPanel.layers.findBy(function(record) {
	            return record.getLayer().metadata['kartlagId'] === layers[i].getLayer().metadata['kartlagId'];
	        });
	        if(!gruppeNavn.indexOf("Gener") > 0) {
	        if (layers[i].getLayer().visibility === true || idx !== -1) {
	            childrenVisible++;
	            layerId = layers[i].data.layer.metadata['kartlagId'];
	            if ( globalLayersFromSavedMapAlreadyAddedLegend.indexOf (layerId) == -1) {
	            	addLegend(layerId);
	            	globalLayersFromSavedMapAlreadyAddedLegend.push(layerId);
	            }
	        } 
	        }
	        layerNames.push(layers[i].getLayer().params.LAYERS);
	    }
	}

	var groupChecked = (childrenVisible === count);
    if ( window.location.href.indexOf("#maps/") > -1 && childrenVisible > 0 && gruppeNavn.indexOf("Gener") > 0) {
    	groupChecked = true;		
    }
	return groupChecked;
}
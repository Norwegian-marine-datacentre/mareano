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

var KARTLAG_ID = "kartlagId";
var GROUP = "group";
var SAVED_MAP = "#maps/";
var GROUP_GENERELLE = "generelle";

function getAllLayersForAGroupAndIsGroupChecked(gruppeNavn, layers, mapPanel, layerNames) {
	var childrenVisible = 0;
	var count = 0;    
	var layerIds = [];
	var layersFromKartbildeAdded = false;
	var gruppenavnFromKartbildeAdded = "";
	for (var i = layers.length-1; i>=0; --i) {
	    if ( layers[i].get("group") == gruppeNavn ) {
	        count++;
	        var idx = mapPanel.layers.findBy(function(record) {
	            return record.getLayer().metadata[ KARTLAG_ID ] === layers[i].getLayer().metadata[ KARTLAG_ID ];
	        });
	        if (layers[i].getLayer().visibility === true || idx !== -1) {
	            childrenVisible++;
	            layerId = layers[i].data.layer.metadata[ KARTLAG_ID ];
	            if ( globalLayersFromSavedMapAlreadyAddedLegend.indexOf (layerId) == -1) {
	            	addLegend(layerId);
	            	globalLayersFromSavedMapAlreadyAddedLegend.push(layerId);
	            	
	            	layerIds.push(layerId);
	            	layersFromKartbildeAdded = true;
	            }
	        } 
	        layerNames.push(layers[i].getLayer().params.LAYERS);     
	    }
        //treepanel not renedered yet
//      var treePanel = Ext.getCmp('thematic_tree');
//      var rootNode = treePanel.getRootNode();
//      var c = rootNode.findChild("text","MAREANO-oversiktskart",true);
//      c.expand();
          if (layersFromKartbildeAdded && gruppenavnFromKartbildeAdded != gruppeNavn ) {
              addKartbildeAbstractOrRemoveWithName(gruppeNavn, true);
              gruppenavnFromKartbildeAdded = gruppeNavn;
          }
	}

	var groupChecked = (childrenVisible === count);
    if ( window.location.href.indexOf( SAVED_MAP ) > -1 && childrenVisible > 0 && gruppeNavn.indexOf( GROUP_GENERELLE ) == -1 ) {
    	//expand any group if it has 1 or more layers turned on and this is a saved map
    	groupChecked = true;		
    }
	return groupChecked;
}
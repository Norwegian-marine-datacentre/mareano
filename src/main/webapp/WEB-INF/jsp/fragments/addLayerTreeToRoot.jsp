<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
/**
* Whenever a layer is turned on or off - send a request to server to see
* if layer also should include Spesialpunkt from Mareano.
*/
app.on("ready", function( ) {
    console.time("on ready start");
    Ext.getCmp('topPanelHeading').update('${heading}');
    loadMareano( this.mapPanel, app, layers );
    turnOnDefaultLayers( this, store );
    /***********************************/
    var treeRoot = Ext.getCmp('thematic_tree'); 
	var rootNode = treeRoot.getRootNode();
    var mergedSomeHovedtema;
    for (var i=0; i < hovedtemaer.length; i++) {
        var hovedTemaCls = "normal-text-hovedtema";
        //when loading map there are default maps turned on in Mareano oversiktskart - make hovedtema bold
        if ( hovedtemaer[i].hovedtema == "MAREANO oversiktskart" || hovedtemaer[i].hovedtema == "MAREANO overviewmap") {
            hovedTemaCls = "bold-text-hovedtema";
        }
        mergedSomeHovedtema = new Ext.tree.TreeNode({
            text: hovedtemaer[i].hovedtema,
            qtip: hovedtemaer[i].hovedtema,
            cls: hovedTemaCls
        });
        for (var j=0; j < hovedtemaer[i].bilder.length; j++) {
            var group = addLayerToGroup(hovedtemaer[i].bilder[j].gruppe,hovedtemaer[i].bilder[j].gruppe, this.map, this.mapPanel, layers, store, app);
            if ( group.attributes.text == "MAREANO-stasjoner" || group.attributes.text == "MAREANO-stations" ) {
                group.setCls("bold-text-hovedtema");
            }
            if (group.attributes.expanded === true) {
                mergedSomeHovedtema.expanded = true;
            }
            group.attributes.maxExtent = [
                hovedtemaer[i].bilder[j].startextentMinx,
                hovedtemaer[i].bilder[j].startextentMiny,
                hovedtemaer[i].bilder[j].startextentMaxx,
                hovedtemaer[i].bilder[j].startextentMaxy
            ];
            mergedSomeHovedtema.appendChild( group );
        }
        rootNode.appendChild( mergedSomeHovedtema );
    }
//    rootNode.appendChild( mergedSomeHovedtema );
 
	/***********************************/
    var rootRightTree = Ext.getCmp('layers');
    var i18nGenerallMaps = "<spring:message code="generelleKart" text="Generelle kart" />";
    rootRightTree.getRootNode().appendChild( 
        addGenerelleLayerToGroup("generelle", i18nGenerallMaps, this.map, this.mapPanel, generelleLayers, store, app) );
    /***********************************/                    
    addDropdownmenuToMareanoMenuIfIe();
	turnOnPreselectedLayers(rootNode);
	
	console.timeEnd("on ready start-end");
});	

function addDropdownmenuToMareanoMenuIfIe() {
    //if ( navigator.userAgent.toLowerCase().indexOf('msie') != -1) {} works either way in other browsers 
    var sfEls = document.getElementById("nav").getElementsByTagName("LI");
    for (var i=0; i < sfEls.length; i++) {
        sfEls[i].onmouseover=function() {
            this.className+=" sfhover";
        }
        sfEls[i].onmouseout=function() {
            this.className=this.className.replace(new RegExp(" sfhover\\b"), "");
        }
    }
}    

function turnOnPreselectedLayers(rootNode) {
	
	var aurl = document.location.href;
	var layersToTurnOn = aurl.split('selectedLayers=')[1];
	if ( layersToTurnOn != null ) {
		var isSelected = layersToTurnOn.split(',');
		
		rootNode.expandChildNodes(true);
		addPreselectedLayersToMap( isSelected );
		expandSelectedNodes( rootNode, isSelected );
	}
}

function addPreselectedLayersToMap( isSelected ) {
	
	var layerStore = Ext.StoreMgr.items[0];
	var layers = layerStore.data.items;

	for ( var i=0; i < isSelected.length; i++ ) {
		if  ( isSelected[i] != "&" ) {
			isSelected[i] = decodeURIComponent( isSelected[i] );

			for ( var j=0; j < layers.length; j++ ) {
				if ( layers[j].getLayer().metadata['kartlagId'] == isSelected[i] ) {					
					layers[j].data.selected = true;
					layers[i].data['layer'].visibility = true;
					var clone = layers[j].clone();
					clone.set("group", "default");
					clone.getLayer().setVisibility(true);
					clone.getLayer().metadata['kartlagId'] = layers[j].getLayer().metadata['kartlagId'];
					app.mapPanel.layers.add(clone);
					ga('send','event', "kategori","addLayer", layers[j].getLayer().metadata['kartlagTitle']);
					displayLegendGraphicsAndSpesialpunkt(app.mapPanel.map.getExtent() + "", layers[j].getLayer(), event, app);
				}
			}
		}
	}
}

function expandSelectedNodes( rootNode, isSelected ) {
	
	rootNode.expandChildNodes(true);
	for ( var j=0; rootNode.childNodes.length > j; j++ ) {
		var hovedtemaExpand = false;
		for ( var k=0; rootNode.childNodes[j].childNodes.length > k; k++ ) {
			var kartbildeExpand = false;
			for ( var l=0; rootNode.childNodes[j].childNodes[k].childNodes.length > l; l++ ) {
				for ( var i=0; i < isSelected.length; i++ ) {
					if ( rootNode.childNodes[j].childNodes[k].childNodes[l].layer.metadata.kartlagId == isSelected[i]) {
						rootNode.childNodes[j].childNodes[k].childNodes[l].ui.checkbox.checked = true;
						kartbildeExpand = true;
						hovedtemaExpand = true;
					}
				}
			}
			if ( kartbildeExpand == false ) {
				rootNode.childNodes[j].childNodes[k].collapse(true);
			}
		}
		if ( hovedtemaExpand == false ) {
			rootNode.childNodes[j].collapse(true);
		}
	}
}
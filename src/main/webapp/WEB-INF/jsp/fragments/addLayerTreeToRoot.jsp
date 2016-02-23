<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
/**
* Whenever a layer is turned on or off - send a request to server to see
* if layer also should include Spesialpunkt from Mareano.
*/
app.on("ready", function() {
    console.time("on ready start");
    Ext.getCmp('topPanelHeading').update('${heading}');
    loadMareano( this.mapPanel, app, layers );
    turnOnDefaultLayers( this, store );
    /***********************************/
    var treeRoot = Ext.getCmp('thematic_tree'); 
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
        treeRoot.getRootNode().appendChild( mergedSomeHovedtema );
    }
//    treeRoot.getRootNode().appendChild( mergedSomeHovedtema );
 
	/***********************************/
    var rootRightTree = Ext.getCmp('layers');
    var i18nGenerallMaps = "<spring:message code="generelleKart" text="Generelle kart" />";
    rootRightTree.getRootNode().appendChild( 
        addGenerelleLayerToGroup("generelle", i18nGenerallMaps, this.map, this.mapPanel, generelleLayers, store, app) );
    /***********************************/                    
    addDropdownmenuToMareanoMenuIfIe();
    console.timeEnd("on ready start");
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

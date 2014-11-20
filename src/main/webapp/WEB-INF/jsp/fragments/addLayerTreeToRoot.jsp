<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
/**
* Whenever a layer is turned on or off - send a request to server to see
* if layer also should include Spesialpunkt from Mareano.
*/
app.on("ready", function() {
    Ext.getCmp('topPanelHeading').update('${heading}');
    loadMareano( this.mapPanel, app, layers );
    turnOnDefaultLayers( this, store );
    /***********************************/
    var treeRoot = Ext.getCmp('thematic_tree'); 
    var mergedSomeHovedtema;
	<c:forEach var="hovedtema" items="${hovedtemaer}">
	    if ( !("${hovedtema.hovedtema}" == "generelle") ) {
	        mergedSomeHovedtema = new Ext.tree.TreeNode({
	            text: "${hovedtema.hovedtema}"
	        });         
	    <c:forEach var="bilde" items="${hovedtema.bilder}">
	        var group = addLayerToGroup("${bilde.gruppe}","${bilde.gruppe}", this.map, this.mapPanel, layers, store, app);
	        if (group.attributes.expanded === true) {
	            mergedSomeHovedtema.expanded = true;
	        }
	        group.attributes.maxExtent = [
	            ${bilde.startextentMinx},
	            ${bilde.startextentMiny},
	            ${bilde.startextentMaxx},
	            ${bilde.startextentMaxy}
	        ];
	        mergedSomeHovedtema.appendChild( group );
        </c:forEach>
	    treeRoot.getRootNode().appendChild( mergedSomeHovedtema );
	    }
	</c:forEach>
	treeRoot.getRootNode().appendChild( mergedSomeHovedtema );
	/***********************************/
    var rootRightTree = Ext.getCmp('layers');
    rootRightTree.getRootNode().appendChild( addGenerelleLayerToGroup("generelle", "Generelle kart", this.map, this.mapPanel, generelleLayers, store, app) );
    /***********************************/                    
    addDropdownmenuToMareanoMenuIfIe();                
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
<!DOCTYPE HTML SYSTEM>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<html>
    <head>
        <jsp:include page = "jsAndCssIncludes.jsp" /> 

        <script>
        	var app;
            function init() {
                gxp.plugins.LayerTree.prototype.baseNodeText = "Base Layer";
                gxp.plugins.LayerTree.prototype.overlayNodeText = "Overlays";
                                
                //Ext.BLANK_IMAGE_URL = "theme/app/img/blank.gif";
                OpenLayers.ImgPath = "theme/imr/img/";
                GeoExt.Lang.set('en');
                <jsp:include page = "mareanoConstructor.jsp" />
                /**
                * Whenever a layer is turned on or off - send a request to local server (this server) to see
                * if layer also should include Spesialpunkt from Mareano.
                */
                app.on("ready", function() {
                    Ext.getCmp('topPanelHeading').update('${heading}');
                    loadMareano( this.mapPanel, app, layers );
                	turnOnDefaultLayers( this, store );
                    /***********************************/
                    var treeRoot = Ext.getCmp('thematic_tree');
                    <jsp:include page = "addLayerTreeToRoot.jsp" />
                    /***********************************/
                    var rootRightTree = Ext.getCmp('layers');
                    rootRightTree.getRootNode().appendChild( addGenerelleLayerToGroup("generelle", "General Maps", this.map, this.mapPanel, generelleLayers, store, app) );                    
                    /***********************************/                    
                    addDropdownmenuToMareanoMenuIfIe();
                });
            }
            
            function addDropdownmenuToMareanoMenuIfIe(){
                var sfEls = document.getElementById("nav").getElementsByTagName("LI");
              	for (var i=0; i<sfEls.length; i++) {
	                    sfEls[i].onmouseover=function() {
	                      this.className+=" sfhover";
                    }
	                sfEls[i].onmouseout=function() {
	                    this.className=this.className.replace(new RegExp(" sfhover\\b"), "");
                	}
				}
            }
            
            function openURI(uri) { // needed by GMLselected(event)
            	window.open(uri,'Punktdata');
            }
        </script>
    </head>
    <body onload="init()">
    </body>
</html>

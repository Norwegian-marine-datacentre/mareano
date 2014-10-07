<!DOCTYPE HTML SYSTEM>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<html>
    <head>
        <jsp:include page = "fragments/jsAndCssIncludes.jsp" />

        <script>
            function init() {
                gxp.plugins.LayerTree.prototype.baseNodeText = "Bakgrunnskart";
                gxp.plugins.LayerTree.prototype.overlayNodeText = "Kartlag";
                gxp.plugins.Navigation.prototype.tooltip = "Panorer kart";    
                gxp.plugins.NavigationHistory.prototype.previousTooltip = "Zoom til forrige utstrekning";
                gxp.plugins.NavigationHistory.prototype.nextTooltip = "Zoom til neste utstrekning";   
                gxp.plugins.ZoomToExtent.prototype.tooltip = "Zoom til synlig utstrekning";
                gxp.plugins.LayerProperties.prototype.toolTip = "kartlag egenskaper";
                gxp.plugins.Measure.prototype.lengthTooltip = "M\u00e5l lengde";
                gxp.plugins.Measure.prototype.areaTooltip = "M\u00e5l areal";
                gxp.plugins.ZoomToLayerExtent.prototype.menuText = "Zoom til kartlagets utstrekning";
                gxp.plugins.ZoomToLayerExtent.prototype.tooltip = "Zoom til kartlagets utstrekning";
                gxp.plugins.ZoomToLayerExtent.prototype.tooltip = "Zoom til kartlagets utstrekning";
                gxp.plugins.RemoveLayer.prototype.removeMenuText = "Fjern kartlag";
                gxp.plugins.RemoveLayer.prototype.removeActionTip = "Fjern kartlag";
                gxp.plugins.LayerProperties.prototype.menuText = "Kartlag egenskaper";
                gxp.plugins.LayerProperties.prototype.toolTip = "Kartlag egenskaper";
                
                //Ext.BLANK_IMAGE_URL = "theme/app/img/blank.gif";
                OpenLayers.ImgPath = "theme/imr/img/";
                GeoExt.Lang.set('no');
                <jsp:include page = "fragments/mareanoConstructor.jsp" />
	                
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
                    <jsp:include page = "fragments/addLayerTreeToRoot.jsp" />
                    /***********************************/
                    var rootRightTree = Ext.getCmp('layers');
                    rootRightTree.getRootNode().appendChild( addGenerelleLayerToGroup("generelle", "Generelle kart", this.map, this.mapPanel, generelleLayers, store, app) );
                    /***********************************/                    
					addDropdownmenuToMareanoMenuIfIe();
                  	
                });
            }
               
            function addDropdownmenuToMareanoMenuIfIe() {
            	//if ( navigator.userAgent.toLowerCase().indexOf('msie') != -1) {} works either way in other browsers 
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

        </script>
    </head>
    <body onload="init()">
    </body>
</html>

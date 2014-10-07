<!DOCTYPE HTML SYSTEM>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<html>
    <head>
        <jsp:include page = "jsAndCssIncludes.jsp" />

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
                <jsp:include page = "mareanoConstructor.jsp" />
	                
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
                    <jsp:include page = "addLayerTreeToRoot.jsp" />
                    /***********************************/
                    var rootRightTree = Ext.getCmp('layers');
                    rootRightTree.getRootNode().appendChild( addGenerelleLayerToGroup("generelle", "Generelle kart", this.map, this.mapPanel, generelleLayers, store, app) );
                    /***********************************/
                    
                    var tmp = Ext.ComponentMgr.all.find(function(c) {
                        if (c instanceof Ext.Button) {
                            if (c.tooltip=="Publish map") {c.setTooltip("Publiser kartet");
                            } else if (c.tooltip=="Save map") {c.setTooltip("Lagre kartet");
                            } else if (c.tooltip=="Draw polygon") {c.setTooltip("tegn polygon");
                            } else if (c.tooltip=="Draw line") {c.setTooltip("tegn linje");
                            } else if (c.tooltip=="Add layer") {c.setTooltip("Legg til kartlag");
                            } else if (c.tooltip=="Remove layer") {c.setTooltip("Fjern kartlag");
                            } else if (c.tooltip=="Layer properties") {c.setTooltip("Kartlag egenskaper");
                            } else if (c.tooltip=="Manage Layer stiles") {c.setTooltip("Behandle kartlagstiler");
                            } else if (c.tooltip=="Pan") {c.setTooltip("panorere kartet");
                            } else if (c.tooltip=="Get Feature Info") {c.setTooltip("Hent Feature Info");
                            } else if (c.tooltip=="Create a new feature") {c.setTooltip("Lag en ny feature");
                            } else if (c.tooltip=="Edit existing feature") {c.setTooltip("Editer eksisterende feature");
                            } else if (c.tooltip=="Measure") {c.setTooltip("M&aring;l");
                            } else if (c.tooltip=="Zoom in") {c.setTooltip("Zoom inn");
                            } else if (c.tooltip=="Zoom out") {c.setTooltip("Zoom ut");
                            } else if (c.tooltip=="Zoom to last extent") {c.setTooltip("Zoom til forrige utstrekning");
                            } else if (c.tooltip=="Zoom to next extent") {c.setTooltip("Zoom til neste utstrekning");
                            } else if (c.tooltip=="Zoom to visible extent") {c.setTooltip("Zoom til synlig utstrekning");
                            } else if (c.tooltip=="Print map") {c.setTooltip("Skriv ut kartet");
                            } else if (c.tooltip=="Go to coordinat") {c.setTooltip("til koordinat");}
                        }
                        if(c instanceof Ext.menu.CheckItem) {
                            if(c.text=="Length") c.text = "Lengde";
                            if(c.text=="Area") c.setText("Areal");
                        }
                    });
                    
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
            
            function openURI(uri){ // needed by GMLselected(evt)
            	window.open(uri,'Punktdata');
            }
        </script>
    </head>
    <body onload="init()">
    </body>
</html>

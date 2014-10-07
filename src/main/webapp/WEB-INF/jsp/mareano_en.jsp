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
                    var tmp = Ext.ComponentMgr.all.find(function(c) {
                    	if( c instanceof Ext.Button ) {
                        	if (c.tooltip=="Publiser kartet") {c.setTooltip("Publish map");
                        	} else if (c.tooltip=="Lagre kartet") {c.setTooltip("Save map");
                        	} else if (c.tooltip=="tegn polygon") {c.setTooltip("Draw polygon");
                        	} else if (c.tooltip=="tegn linje") {c.setTooltip("Draw line");
                        	} else if (c.tooltip=="Legg til kartlag") {c.setTooltip("Add layer");
                        	} else if (c.tooltip=="Fjern kartlag") {c.setTooltip("Remove layer");
                        	} else if (c.tooltip=="Kartlag egenskaper") {c.setTooltip("Layer properties");
                        	} else if (c.tooltip=="Behandle kartlagstiler") {c.setTooltip("Manage Layer stiles");
                        	} else if (c.tooltip=="panorere kartet") {c.setTooltip("Pan");
                        	} else if (c.tooltip=="Hent Feature Info") {c.setTooltip("Get Feature Info");
                        	} else if (c.tooltip=="Lag en ny feature") {c.setTooltip("Create a new feature");
                        	} else if (c.tooltip=="Editer eksisterende feature") {c.setTooltip("Edit existing feature");
                        	} else if (c.tooltip=="M&aring;l") {c.setTooltip("Measure");
                        	} else if (c.tooltip=="Zoom inn") {c.setTooltip("Zoom in");
                        	} else if (c.tooltip=="Zoom ut") {c.setTooltip("Zoom out");
                        	} else if (c.tooltip=="Zoom til forrige utstrekning") {c.setTooltip("Zoom to last extent");
                        	} else if (c.tooltip=="Zoom til neste utstrekning") {c.setTooltip("Zoom to next extent");
                        	} else if (c.tooltip=="Zoom til synlig utstrekning") {c.setTooltip("Zoom to visible extent");
                        	} else if (c.tooltip=="Skriv ut kartet") {c.setTooltip("Print map");
                        	} else if (c.tooltip=="til koordinat") {c.setTooltip("Go to coordinat");}
                    	}
                        if( c instanceof Ext.menu.CheckItem ) {
                            if ( c.text=="Lengde" ) c.text = "Length";
        			if ( c.text == "Areal" ) c.setText("Area");
                        }  
                    });
                    
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

var filterLayers = function() {
    var filteredNodes = [];
    return {
        afterrender:function(rec) { //set default value
            Ext.getCmp("filterLayers").setValue('Layer:');
        },
        keydown: function(form, e) {
            if ( this.lastQuery == "" || this.lastQuery == "Layer:" || this.layerQuery == "Abstract:") {
                var tree = Ext.getCmp('thematic_tree');
                var treeRoot = tree.getRootNode();
                treeRoot.expandChildNodes(true);
            }
        },
        keydown: function(form, e) {
            var queryIsEmpty = this.lastQuery == "" || this.lastQuery == "Layer:" || this.layerQuery == "Abstract:";
            //if ( queryIsEmpty ) {
                var tree = Ext.getCmp('thematic_tree');
                var treeRoot = tree.getRootNode();
                treeRoot.expandChildNodes(true);
            //}
        },
        keyup: function(form, e) {
            var query = this.getRawValue();
            if ( query == undefined ) {
                console.log("query undefined:")
            }
            var filterChoosen = "Layer:";
            if ( query.indexOf("Layer:") > -1 ) {
                if ( query != "Layer:") {
                    Ext.each(filteredNodes, function(n) {
                        n.getUI().show();
                    });
                    filteredNodes = [];
                }
                query = query.replace("Layer:", "");    
                filterChoosen = "Layer:";
            } else if ( query.indexOf("Abstract:") > -1 ) {
                if ( query != "Abstract:") {
                    Ext.each(filteredNodes, function(n) {
                        n.getUI().show();
                    });
                    filteredNodes = [];
                }
                query = query.replace("Abstract:", ""); 
                filterChoosen = "Abstract:";
            }
            
            if ( e.keyCode == 8 || query == "" ) {
                Ext.each(filteredNodes, function(n) {
                    n.getUI().show();
                });
                filteredNodes = [];
            }
            var tree = Ext.getCmp('thematic_tree');
            var treeRoot = tree.getRootNode();
            if ( query == "" ) {
                treeRoot.collapseChildNodes( true );
                return;
            }
        
            if ( query.length < 2 ) {
                return;
            }
            var re = new RegExp(Ext.escapeRe( query ), 'i');
            //console.log("re:"+query);
            
            var getNodeValue = function( anode ) {
                if ( filterChoosen == "Layer:") {
                    if ( anode.attributes != null && anode.attributes.text != null ) {
                        return anode.attributes.text;
                    } else if ( anode.layer!= null && anode.layer.name != null ) {
                        return anode.layer.name;
                    } 
                    return "";
                } else if ( filterChoosen == "Abstract:" ) {
                    if ( anode.attributes != null && anode.attributes.qtip != null ) {
                        return anode.attributes.qtip;
                    } 
                    return "";
                }
            }                               
        
            var filter = function(node) { // descends into child nodes recursivly)
                if ( node.attributes != null && node.attributes.text != null) { //its a parent folder
                    if ( re.test( getNodeValue(node) ) ) { //basecase 1: If match on folder name - ignore children
                        return true;
                    }
                }
                var thisLeafMatch = false;
                if(node.hasChildNodes()) {
                    for ( var i=0; i < node.childNodes.length; i++) {
                        var childNode = node.childNodes[i];
                        if(childNode.isLeaf()) {
                            if ( re.test( getNodeValue(childNode) ) ) {
                                return true;
                            }
                        } else { 
                            var childLeafMatch = filter(childNode);
                            if ( childLeafMatch == false ) {
                                filteredNodes.push(childNode);
                            } else {
                                thisLeafMatch = true;
                            }
                        }
                    }
                }
                return thisLeafMatch;
            }
            treeRoot.eachChild( function(childNode) {
                //alternatively dont remove hovedtema without match but dont expand either
                //filter(childNode);
                var childLeafMatch = filter(childNode);
                if ( childLeafMatch == false ) {
                    filteredNodes.push(childNode);
                }                                   
            }); 
            Ext.each(filteredNodes, function(n) {
                n.getUI().hide();
            });  
        },
        focus : {
            fn : function(view, record, item, index, even) {
                //this.setValue("");
                var tree = Ext.getCmp('thematic_tree');
                var treeRoot = tree.getRootNode();
                Ext.each(filteredNodes, function(n) {
                    //var el = Ext.fly(tree.getView().getNodeByRecord(n));
                    var indexEl = treeRoot.indexOf(n);
                    var el = treeRoot.item(indexEl);
                    if (el != null) {
                        el.getUI().show();
                    }
                });
                treeRoot.collapseChildNodes( true );
            }
        }
    };
};
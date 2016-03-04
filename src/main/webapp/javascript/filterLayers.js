var closureForFilterLayers = ( function() {
    // local variables with closure scope to filterLayers()
    var filteredNodes = [];
    var treeRoot = null;
    var hiddenTreeRootExpanded = false; //a hiddend treeRoot is always expanded=false; so add our own state variable
    //otherwise will try to expand childnodes if expand deep = true
        
    var LAYER_FILTER = "Layer:";
    var ABSTRACT_FILTER = "Abstract:";
    var filterChoosen = LAYER_FILTER;
    return function filterLayers() {
        var purgeAndShowFilteredNodes = function() {
            Ext.each(filteredNodes, function(n) {
                n.getUI().show();
            });
            filteredNodes = [];
        }
        return {
            afterrender:function(rec) { //set default value
                Ext.getCmp("filterLayers").setValue( LAYER_FILTER );
                var tree = Ext.getCmp('thematic_tree');
                treeRoot = tree.getRootNode();  
                var filterChoosen = LAYER_FILTER; //default filter is "Layer:"
            },
            select:function (combo, selection) {
                purgeAndShowFilteredNodes();
            },
            keydown: function(form, e) {
                if ( hiddenTreeRootExpanded == false ) {
                    treeRoot.expandChildNodes(true);
                    hiddenTreeRootExpanded = true;
                }
            },
            keyup: function(form, e) {
                var query = this.getRawValue();
                if ( query.indexOf( LAYER_FILTER ) > -1 ) {
                    if ( filterChoosen != LAYER_FILTER && query != LAYER_FILTER) {
                        purgeAndShowFilteredNodes();
                        filterChoosen = LAYER_FILTER;
                    }
                    query = query.replace(LAYER_FILTER, "");    
                } else if ( query.indexOf( ABSTRACT_FILTER ) > -1 ) {
                    if ( filterChoosen != ABSTRACT_FILTER && query != ABSTRACT_FILTER ) {
                        purgeAndShowFilteredNodes();
                        filterChoosen = ABSTRACT_FILTER;
                    }
                    query = query.replace(ABSTRACT_FILTER, ""); 
                }
                
                if ( e.keyCode == 8 || query == "" ) {
                    purgeAndShowFilteredNodes();
                }
                if ( query == "" ) {
                    treeRoot.collapseChildNodes( true );
                    hiddenTreeRootExpanded = false;
                    return;
                }
            
                if ( query.length < 2 ) {
                    return;
                }
                var re = new RegExp(Ext.escapeRe( query ), 'i');
                //console.log("re:"+query);
                
                var getNodeValue = function( anode ) {
                    if ( filterChoosen == LAYER_FILTER ) {
                        if ( anode.attributes != null && anode.attributes.text != null ) {
                            return anode.attributes.text;
                        } else if ( anode.layer!= null && anode.layer.name != null ) {
                            return anode.layer.name;
                        } 
                        console.warn("no value found for node:"+anode);
                        return "";
                    } else if ( filterChoosen == ABSTRACT_FILTER ) {
                        if ( anode.attributes != null && anode.attributes.qtip != null ) {
                            return anode.attributes.qtip;
                        } 
                        console.warn("no value found for node:"+anode);
                        return "";
                    }
                }                               
            
                var filter = function(node) { // descends into child nodes recursivly)
                    if ( re.test( getNodeValue(node) ) ) { //basecase 1: If match on folder name - ignore children
                        return true;
                    }
                    var thisLeafMatch = false;
                    if ( node.hasChildNodes() ) {
                        for ( var i=0; i < node.childNodes.length; i++ ) {
                            var childNode = node.childNodes[i];
                            if( childNode.isLeaf() ) {
                                if ( re.test( getNodeValue(childNode) ) ) {
                                    return true;
                                }
                            } else { 
                                var childLeafMatch = filter(childNode);
                                if ( childLeafMatch == false ) {
                                    filteredNodes.push(childNode);
                                } else if ( node != treeRoot ) { //dont set thisLeafMatch=true if childNode is a hovedtema
                                    thisLeafMatch = true;
                                }
                            }
                        }
                    }
                    return thisLeafMatch;
                }
                filter( treeRoot );
                Ext.each(filteredNodes, function(n) {
                    n.getUI().hide();
                });  
            }
        };
    }
})();
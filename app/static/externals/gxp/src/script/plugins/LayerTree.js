/**
 * Copyright (c) 2008-2011 The Open Planning Project
 * 
 * Published under the BSD license.
 * See https://github.com/opengeo/gxp/raw/master/license.txt for the full text
 * of the license.
 */

/**
 * @requires plugins/Tool.js
 */

/** api: (define)
 *  module = gxp.plugins
 *  class = LayerTree
 */

/** api: (extends)
 *  plugins/Tool.js
 */
Ext.namespace("gxp.plugins");

/** api: constructor
 *  .. class:: LayerTree(config)
 *
 *    Plugin for adding a tree of layers to a :class:`gxp.Viewer`. Also
 *    provides a context menu on layer nodes.
 */   
gxp.plugins.LayerTree = Ext.extend(gxp.plugins.Tool, {
    
    /** api: ptype = gxp_layertree */
    ptype: "gxp_layertree",

    /** api: config[rootNodeText]
     *  ``String``
     *  Text for root node of layer tree (i18n).
     */
    rootNodeText: "Kartlagsliste",

    /** api: config[overlayNodeText]
     *  ``String``
     *  Text for overlay node of layer tree (i18n).
     */
    overlayNodeText: "Kartlag",

    /** api: config[baseNodeText]
     *  ``String``
     *  Text for baselayer node of layer tree (i18n).
     */
    baseNodeText: "Bakgrunnskart",
    
    /** api: config[groups]
     *  ``Object`` The groups to show in the layer tree. Keys are group names,
     *  and values are either group titles or an object with ``title`` and
     *  ``exclusive`` properties. ``exclusive`` means that nodes will have
     *  radio buttons instead of checkboxes, so only one layer of the group can
     *  be active at a time. Optional, the default is
     *
     *  .. code-block:: javascript
     *
     *      groups: {
     *          "default": "Overlays", // title can be overridden with overlayNodeText
     *          "background": {
     *              title: "Base Layers", // can be overridden with baseNodeText
     *              exclusive: true
     *          }
     *      }
     */
    groups: null,
    
    /** api: config[defaultGroup]
     *  ``String`` The name of the default group, i.e. the group that will be
     *  used when none is specified. Defaults to ``default``.
     */
    defaultGroup: "default",
    
    /** private: method[constructor]
     *  :arg config: ``Object``
     */
    constructor: function(config) {
        gxp.plugins.LayerTree.superclass.constructor.apply(this, arguments);
        if (!this.groups) {
            this.groups = {
                "default": this.overlayNodeText,
                "background": {
                    title: this.baseNodeText,
                    exclusive: true
                }
            };
        }
    },
    
    /** private: method[addOutput]
     *  :arg config: ``Object``
     */
    addOutput: function(config) {

        var target = this.target, me = this;
        var addListeners = function(node, record) {
            if (record) {            	
                target.on("layerselectionchange", function(rec) {
                    if (!me.selectionChanging && rec === record) {
                        node.select();
                    }
                });
                if (record === target.selectedLayer) {
                    node.on("rendernode", function() {
                        node.select();
                    });
                }
            }
        };
        
        // create our own layer node UI class, using the TreeNodeUIEventMixin
        var LayerNodeUI = Ext.extend(GeoExt.tree.LayerNodeUI,
            new GeoExt.tree.TreeNodeUIEventMixin());
        
        var treeRoot = new Ext.tree.TreeNode({
            text: this.rootNodeText,
            expanded: true,
            isTarget: false,
            allowDrop: false
        });
        
        var groupConfig, defaultGroup = this.defaultGroup;
        for (var group in this.groups) {
            groupConfig = typeof this.groups[group] == "string" ?
                {title: this.groups[group]} : this.groups[group];
        	if ( group=="default") {
        		var featureInfoEvents = [];
            	var thisMapPanel = Ext.ComponentMgr.all.find(function(c) {
            		return c instanceof GeoExt.MapPanel;
            	});
                treeRoot.appendChild(new GeoExt.tree.LayerContainer({
                    text: this.overlayNodeText,
                    iconCls: "gx-folder",
                    expanded: true,
                    loader: new GeoExt.tree.LayerLoader({
                        store: thisMapPanel.layers,
                        filter: function(record) {
                        	/** Add event for getFeatureInfo */
                        	function setHTML(response) {
                        		var from = response.responseText.indexOf("<body>");
                        		var to = response.responseText.indexOf("</body>");
                        		var bodyStr = response.responseText.substring(from, to);
                        		/** Ugly - fix by not sending request when click outside layer */
                        		if ( response.responseText != null && response.responseText != "" && bodyStr.length > 14 ) {
        							Ext.MessageBox.show( 'Feature Info', response.responseText );
//        							{title: 'Feature Info',msg: response.responseText, width:600}
                        		}
        	            	};
                        	var tmpMap = thisMapPanel.map;
        					if (record.get("layer").url!=null && !(record.get("layer") instanceof OpenLayers.Layer.Vector) && 
        						record.get("layer").url.indexOf( 'http://maps.imr.no/geoserver/wms' ) != -1 ) {
        						
        						var isRegFlag = 0;
        						if ( featureInfoEvents != [] ) {
                    				for ( var i=0, len=featureInfoEvents.length; i<len; i++ ) {
        								if ( featureInfoEvents[i] == record.get("layer") ) {
        									isRegFlag = 1;
        								}
                    				}
        						}
        						
        						if ( isRegFlag == 0 ) {
        							featureInfoEvents.push( record.get("layer") ); 
        							tmpMap.events.register('click', record.get("layer"), function (e) {
        								
        								if ( record.get("layer").getVisibility() ) {
        									var params = {
        		                        		REQUEST: "GetFeatureInfo",
        			                        	EXCEPTIONS: "application/vnd.ogc.se_xml",
        			                        	BBOX: tmpMap.getExtent().toBBOX(),
        			                        	X: e.xy.x,
        			                        	Y: e.xy.y,
        			                        	INFO_FORMAT: 'text/html',
        			                        	QUERY_LAYERS: record.get("layer").params['LAYERS'],
        			                        	FEATURE_COUNT: 50,
        			                        	Layers: record.get("layer").params['LAYERS'],
        			                        	Styles: '',
        			                        	Srs: 'EPSG:32633',
        			                        	WIDTH: tmpMap.size.w,
        			                        	HEIGHT: tmpMap.size.h,
        			                        	format: 'image/jpeg'
        		                    		};
        		                    		var returned = OpenLayers.loadURL("http://maps.imr.no/geoserver/wms", params, this, setHTML, setHTML);
        		                    		returned.abort(); //to avoid two popups
        		                    		OpenLayers.Event.stop(e);
        								}
        	                    	});
        						}
        					}
                            return !record.get("group") && record.get("layer").displayInLayerSwitcher == true && !(record.get("layer") instanceof OpenLayers.Layer.GML);
                        },
                        createNode: function(attr) {
                            var layer = attr.layer;
                            var store = attr.layerStore;
                            if (layer && store) {
                                var record = store.getAt(store.findBy(function(r) {
                                    return r.get("layer") === layer;
                                }));
                                if (record && !record.get("queryable") ) {
                                	var url = "";
        							if ( layer instanceof OpenLayers.Layer.WMS )
                                		url = layer.url;
        							attr.iconCls = getLayerIcon(url);
                                }
                            }
                            return GeoExt.tree.LayerLoader.prototype.createNode.apply(this, [attr]);
                        }
                    }),
                    singleClickExpand: true,
                    allowDrag: false,
                    listeners: {
                        append: function(tree, node) {
                            node.expand();
                        }
                    }
                }));        		
        	} else {
	            treeRoot.appendChild(new GeoExt.tree.LayerContainer({
	                text: groupConfig.title,
	                iconCls: "gxp-folder",
	                expanded: true,
	                group: group == defaultGroup ? undefined : group,
	                loader: new GeoExt.tree.LayerLoader({
	                    baseAttrs: groupConfig.exclusive ?
	                        {checkedGroup: group} : undefined,
	                    store: this.target.mapPanel.layers,
	                    filter: (function(group) {
	                        return function(record) {
	                            return (record.get("group") || defaultGroup) == group &&
	                                record.getLayer().displayInLayerSwitcher == true;
	                        };
	                    })(group),
	                    createNode: function(attr) { //this creature - attr - is a TreeNode
	                        attr.uiProvider = LayerNodeUI;
	                        var layer = attr.layer;
	                        var store = attr.layerStore;
	                        if (layer && store) {
	                            var record = store.getAt(store.findBy(function(r) {
	                                return r.getLayer() === layer;
	                            }));
	                            if (record) {
	                                if (!record.get("queryable")) {
	                                	var url = "";
	    								if ( layer instanceof OpenLayers.Layer.WMS )
	                                		url = layer.url;
	    								attr.iconCls = getLayerIcon(url);
	                                }
	                                if (record.get("fixed")) {
	                                    attr.allowDrag = false;
	                                }
	                            }
	                        }
	                        var node = GeoExt.tree.LayerLoader.prototype.createNode.apply(this, arguments);
	                        addListeners(node, record);
	                        return node;
	                    }
	                }),
	                singleClickExpand: true,
	                allowDrag: false,
	                listeners: {
	                    append: function(tree, node) {
	                        node.expand();
	                    }
	                }
	            }));
        	}
        }
        
        config = Ext.apply({
            xtype: "treepanel",
            root: treeRoot,
            rootVisible: false,
            border: false,
            enableDD: true,
            selModel: new Ext.tree.DefaultSelectionModel({
                listeners: {
                    beforeselect: function(selModel, node) {
                        var changed = true;
                        var layer = node && node.layer;
                        if (layer) {
                            var store = node.layerStore;
                            var record = store.getAt(store.findBy(function(r) {
                                return r.getLayer() === layer;
                            }));
                            this.selectionChanging = true;
                            changed = this.target.selectLayer(record);
                            this.selectionChanging = false;
                        }
                        return changed;
                    },
                    scope: this
                }
            }),
            listeners: {
                contextmenu: function(node, e) {
                    if(node && node.layer) {
                        node.select();
                        var tree = node.getOwnerTree();
                        if (tree.getSelectionModel().getSelectedNode() === node) {
                            var c = tree.contextMenu;
                            c.contextNode = node;
                            c.items.getCount() > 0 && c.showAt(e.getXY());
                        }
                    }
                },
                beforemovenode: function(tree, node, oldParent, newParent, i) {
                    // change the group when moving to a new container
                    if(oldParent !== newParent) {
                        var store = newParent.loader.store;
                        var index = store.findBy(function(r) {
                            return r.getLayer() === node.layer;
                        });
                        var record = store.getAt(index);
                        record.set("group", newParent.attributes.group);
                    }
                },                
                scope: this
            },
            contextMenu: new Ext.menu.Menu({
                items: []
            })
        }, config || {});
        
        var layerTree = gxp.plugins.LayerTree.superclass.addOutput.call(this, config);
        
        return layerTree;
    }
        
});

Ext.preg(gxp.plugins.LayerTree.prototype.ptype, gxp.plugins.LayerTree);

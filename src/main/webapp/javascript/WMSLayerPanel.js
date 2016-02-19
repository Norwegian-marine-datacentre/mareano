Ext.ns("Mareano");

Mareano.WMSLayerPanel = Ext.extend(Ext.form.FormPanel, {

    nameLabel: "Layer Name",
    titleLabel: "Layer Title",
    abstractLabel: "Layer Abstract",
    keywordLabel: "Keywords",
    urlLabel: "GetCapabilities URL",
    scaleMin: "Scale Min",
    scaleMax: "Scale Max",
    bodyStyle: "padding: 5px",
    border: false,
    plain: true,
    labelWidth: 75,

    initComponent: function() {
        Mareano.WMSLayerPanel.superclass.initComponent.call(this);
        var record = this.layerRecord;
        var layer = record.getLayer();
        if (layer instanceof OpenLayers.Layer.WMS) {
            var params = {
                "REQUEST": "GetCapabilities",
                "SERVICE": "wms",
                "VERSION": "1.1.1"
            };
            var url = gxp.plugins.WMSSource.prototype.trimUrl(layer.url, params);
            url = Ext.urlAppend(url, Ext.urlEncode(params));
            var keywords = [];
            if (layer.metadata.keyword) {
                keywords = layer.metadata.keyword.split(",");
                for (var i=0, ii=keywords.length; i<ii; ++i) {
                    keywords[i] = Ext.util.Format.trim(keywords[i]);
                }
            }
            this.add([{
                xtype: 'textfield',
                width: '100%', 
                anchor: '99%',
                readOnly: true,
                fieldLabel: this.nameLabel,
                value: layer.params.LAYERS
            }, {
                xtype: 'textfield',
                width: '100%', 
                anchor: '99%',
                readOnly: true,
                fieldLabel: this.titleLabel,
                value: layer.name
            }, {
                xtype: 'textfield',
                width: '100%',
                anchor: '99%',
                readOnly: true,
                fieldLabel: this.urlLabel,
                value: url
            }, {
                xtype: 'textfield',
                width: '100%',
                anchor: '99%',
                readOnly: true,
                fieldLabel: this.scaleMin,
                value: layer.minScale
            }, {
                xtype: 'textfield',
                width: '100%',
                anchor: '99%',
                readOnly: true,
                fieldLabel: this.scaleMax,
                value: layer.maxScale
            },{
                xtype: "gx_opacityslider",
                name: "opacity",
                anchor: "99%",
                isFormField: true,
                fieldLabel: this.opacityText,
                listeners: {
                    change: function() {
                        this.fireEvent("change");
                    },
                    scope: this
                },
                layer: this.layerRecord
            }]);
            if (layer.metadata["abstract"]) {
                this.add({
                    xtype: 'textarea',
                    width: '100%',
                    anchor: '99%',
                    readOnly: true,
                    fieldLabel: this.abstractLabel,
                    value: layer.metadata["abstract"]
                });
            }
            if (keywords.length > 0) {
                this.add([{
                    xtype: 'textarea',
                    readOnly: true,
                    width: '100%', 
                    anchor: '99%', 
                    fieldLabel: this.keywordLabel,
                    value: keywords.join('\n')
                }]);
            }
        }
    }

});

Ext.reg('mareano_wmslayerpanel', Mareano.WMSLayerPanel);

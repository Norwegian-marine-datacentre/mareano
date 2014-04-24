
/**
 * Deprecated - use https://github.com/boundlessgeo/gxp/blob/master/src/script/plugins/WMSGetFeatureInfo.js
 * @param record
 * @param mapPanel
 * @param gruppeNavn
 * @param app
 * @param layerName
 */
function featureInfoImpl(record, mapPanel, gruppeNavn, app, layerName) {
    var featureInfoEvents = [];
    /** Add event for getFeatureInfo */
    function setThisHTML(response) {
        var from = response.responseText.indexOf("<body>");
        var to = response.responseText.indexOf("</body>");
        var bodyStr = response.responseText.substring(from, to);
        /** Ugly - fix by not sending request when click outside layer */
        if ( response.responseText != null && response.responseText != "" && bodyStr.length > 14 ) {
            //Ext.MessageBox.show( 'Feature Info', response.responseText );
            winPanel = new Ext.Window({title: 'Feature Info',autoHeight: true,width:300,html: response.responseText});
            winPanel.show();
            //Ext.MessageBox.show( {title: 'Feature Info', msg: response.responseText, setAutoScroll:true} );
        }
        gfiCache = {};
    };
    var tmpMap = mapPanel.map;
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
                var pressed = false;
                for (var key in app.tools) {
                    if (app.tools[key].ptype === 'gxp_wmsgetfeatureinfo') {
                        pressed = app.tools[key].actions[0].items[0].pressed;
                        break;
                    }
                }
                if (pressed && !gfiCache[record.get("layer").id] && record.get("layer").getVisibility() ) {
                    gfiCache[record.get("layer").id] = true;
                    var params = {
                            REQUEST: "GetFeatureInfo",
                            EXCEPTIONS: "application/vnd.ogc.se_xml",
                            BBOX: tmpMap.getExtent().toBBOX(),
                            X: parseInt(e.xy.x),
                            Y: parseInt(e.xy.y),
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
                    var returned = OpenLayers.Request.GET({url: "http://maps.imr.no/geoserver/wms", params: params, callback: setThisHTML});
                    //returned.abort(); //to avoid two popups
                    OpenLayers.Event.stop(e);
                }
            });
        }
    }
} 
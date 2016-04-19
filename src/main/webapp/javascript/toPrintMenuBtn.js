var toPrintMenuButton = function printImageHelper() {
    var myMask = new Ext.LoadMask(Ext.getBody(), {msg:"Please wait..."});
    myMask.show();
    
    var width = this.mapPanel.map.getSize().w;
    var height = this.mapPanel.map.getSize().h;
    
    var layerObjectArray = new Array();
    layersToJSON.call(this, layerObjectArray);
    addLegendsToJSON_AndAddHiddenLayersWithLegend(layerObjectArray);

    var scaleLineTxt = jQuery('.olControlScaleLineTop').css('width');
    var scaleLine = parseInt(scaleLineTxt);
    var scaleLineText = jQuery('.olControlScaleLineTop').text();
    
    var currentLegends = getCurrentLegendFragment();
    
    jQuery.ajax({
        type: 'post',
        url: "spring/postMapImage",
        data: JSON.stringify({ 
            'printlayers': layerObjectArray,
            'width': this.mapPanel.map.getSize().w,
            'height': this.mapPanel.map.getSize().h,
            'scaleLine': scaleLine,
            'scaleLineText': scaleLineText
        }),
        dataType: "json",
        contentType: "application/json; charset=utf-8",
        success:function(data) {
            var mapImageLink = jQuery("<a id='downloadPrintMap' href='spring/getMapImage?printFilename="+data.filename+"' download hidden></a>");
            jQuery('body').append(mapImageLink);
            var formDocument = document.getElementById("downloadPrintMap");
            formDocument.click();
            formDocument.remove();
            myMask.hide();
        },
        error: function (request, status, error) {
            alert("The request failed: " + request.responseText);
        }
    });       
    
}

var getBBoxFromGriddedLayer = function getBBoxFromGriddedLayerAsFlattendArray(layer) {
    isSendt = true;
    var sizeFlatenArray = layer.grid.length * layer.grid[0].length;

    var gridUrls = new Array( sizeFlatenArray );
    var flatArray = 0;
    for ( var i=0; i < layer.grid.length; i++ ) {
        for ( var j=0; j < layer.grid[i].length; j++) {
            var gridObj = layer.grid[i][j];
            gridUrls[flatArray++] = gridObj.bounds;
        }
    }   
    return gridUrls;
};

/**
 * Adds all the visible layers from the map
 * like background layer and all visible overlays
 * but does not add legend info 
 * @param layerObjectArray
 */
var layersToJSON = function addVisibleLayersToJSON( layerObjectArray ) {
    this.mapPanel.layers.each(function(record) {
        var layer = record.getLayer();
        if ( record.getLayer().getVisibility() ) {
            var layer = record.getLayer();
            var params = layer.params;
            
            if ( params != undefined ) {
                var isSendt = false;
                var gridUrls = [];
                if ( isSendt == false && layer.grid != '' && layer.grid.length > 1) {
                    gridUrls = getBBoxFromGriddedLayer.call( this, layer );
                } 
                var layerId = "";
                var layerTitle = "";
                if ( layer.metadata != null && layer.metadata['kartlagId'] != undefined) {
                    var layerId = layer.metadata['kartlagId'] + "";            
                }
                if ( layer.metadata != null && layer.metadata['kartlagTitle'] != undefined) {
                    layerTitle = layer.metadata['kartlagTitle'];
                }
                if (layer.grid[0] != null ) {
                	var urlOfBackgroundLayer = layer.grid[0][0].url;
                	if ( urlOfBackgroundLayer == null ) {
                		urlOfBackgroundLayer = layer.url;
                		urlOfBackgroundLayer += "&LAYERS="+params.LAYERS;
                		urlOfBackgroundLayer += "&FORMAT="+params.FORMAT;
                		urlOfBackgroundLayer += "&TRANSPARENT="+params.TRANSPARENT;
                		urlOfBackgroundLayer += "&SERVICE="+params.SERVICE;
                		urlOfBackgroundLayer += "&VERSION="+params.VERSION;
                		urlOfBackgroundLayer += "&REQUEST="+params.REQUEST;
                		urlOfBackgroundLayer += "&STYLES="+params.STYLES;
                		
                		if ( params.WIDTH != null) {
                			urlOfBackgroundLayer += "&WIDTH="+params.WIDTH;	
                		} else if ( layer.grid[0][0].size != null) 
                			urlOfBackgroundLayer += "&WIDTH="+layer.grid[0][0].size.w;
                		else {
                			urlOfBackgroundLayer += "&WIDTH=256";
                		}

                		if ( params.HEIGHT != null) {
                			urlOfBackgroundLayer += "&HEIGHT="+params.HEIGHT;	
                		} else if ( layer.grid[0][0].size != null) 
                			urlOfBackgroundLayer += "&HEIGHT="+layer.grid[0][0].size.h;
                		else {
                			urlOfBackgroundLayer += "&HEIGHT=256";
                		}
                		
                		urlOfBackgroundLayer += "&SRS="+params.SRS;
                	}
                    var layerObject = {
                            url: urlOfBackgroundLayer,
                            gridBoundingBoxes: gridUrls,
                            columnSize: layer.grid[0].length,
                            position: [layer.grid[0][0].position.x, layer.grid[0][0].position.y],
                            kartlagId: layerId,
                            kartlagTitle: layerTitle,
                            visible: true,
                            legend: []
                    }
                    layerObjectArray.push( layerObject );
                } else console.log("not pushing:"+layerTitle);
            }
        }
    }, this);
}

/**
 * Adds legend info to already added layers that are visible
 * and adds layer info and legend info to hidden layers.
 * That is layers that are not visible in that zoom resolution
 *  
 * @param layerObjectArray
 */
function addLegendsToJSON_AndAddHiddenLayersWithLegend(layerObjectArray) {
    var idsFromVisibleLayers = jQuery.map(layerObjectArray, function(v, i){
        return v.kartlagId;
    });
    var legendLayers = getlayerIdHash();
    var idsFromLegend = jQuery.map(legendLayers, function(v, i){
        return v.kartlagId;
    });
    
    var layerObject;
    for ( var i=0; i < idsFromLegend.length; i++) {
        var layerId = idsFromLegend[i];
        
        var kartlagTitle = jQuery.map(legendLayers, function(v, i){
            if ( v.kartlagId == layerId) {
                return v.kartlagTitle;
            }
        });
        kartlagTitle = kartlagTitle.toString(); //object is array;
        
        if ( idsFromVisibleLayers.indexOf(layerId) == -1) {
            // overlay layer has not been added because it is not visible
            layerObject = {
                    url: '',
                    gridBoundingBoxes: [],
                    columnSize: 0,
                    position:  [0,0],
                    kartlagId: layerId,
                    kartlagTitle: kartlagTitle, 
                    visible: false,
                    legend: []
            }
            layerObjectArray.push( layerObject );
        } else {
            layerObject = jQuery.map(layerObjectArray, function(v, i){
                if ( v.kartlagId == layerId) {
                    return v;
                }
            });    
            layerObject = layerObject[0];
        }
        //overlay or background layer already added - append legend too
        var legendObj = jQuery.map(legendLayers, function(v, i){
            if ( v.kartlagId == layerId) {
                return v.legend;
            }
        });
        layerObject['legend'] = legendObj;
    }    
}
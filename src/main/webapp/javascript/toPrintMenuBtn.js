var toPrintMenuButton = function printImageHelper() {
    var myMask = new Ext.LoadMask(Ext.getBody(), {msg:"Please wait..."});
    myMask.show();
    
    var width = this.mapPanel.map.getSize().w;
    var height = this.mapPanel.map.getSize().h;
    
    var layerObjectArray = new Array();
    this.mapPanel.layers.each(function(record) {
        var layer = record.getLayer();
        if ( record.getLayer().getVisibility() ) {
            var layer = record.getLayer();
            var params = layer.params;
            if ( params != undefined ) {
                var isSendt = false;
                var gridUrls = [];
                if ( isSendt == false && layer.grid != '' && layer.grid.length > 1) {
                    gridUrls = sendGridedLayer.call( this, layer );
                } 
                if (layer.grid[0] != null ) {
                    layerObject = {
                            url: layer.grid[0][0].url,
                            gridBoundingBoxes: gridUrls,
                            columnSize: layer.grid[0].length,
                            position: [layer.grid[0][0].position.x, layer.grid[0][0].position.y]
                    }
                    layerObjectArray.push( layerObject );
                }
            }
        }
    }, this);   
    
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
            'layers': getlayerIdHash(),
            'scaleLine': scaleLine,
            'scaleLineText': scaleLineText
        }),
        dataType: "json",
        contentType: "application/json; charset=utf-8",
        success:function(data) {
            var mapImageLink = jQuery("<a id='downloadPrintMap' href='spring/getMapImage?printFilename="+data.filename+"' download hidden></a>");
            jQuery('body').append(mapImageLink);
            //mapImageLink.click();
            document.getElementById("downloadPrintMap").click();
            myMask.hide();
        },
        error: function (request, status, error) {
            alert("The request failed: " + request.responseText);
        }
    });       
    
}

var sendGridedLayer = function drawGridHelper(layer) {
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
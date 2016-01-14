var toPrintMenuButton = function printImageHelper() {
    var extent = this.mapPanel.map.getExtent() + "";
    
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

    var jsonString = JSON.stringify({ 
        'printlayers': layerObjectArray,
        'width': this.mapPanel.map.getSize().w,
        'height': this.mapPanel.map.getSize().h,
        'layers': getlayerIdHash(),
        'scaleLine': scaleLine,
        'scaleLineText': scaleLineText});
    
    jsonString = encodeURI(jsonString);
    var mapForm = jQuery('<form id="mapform" action="spring/getMapImage" method="post"></form>');
    mapForm.append('<input type="hidden" name="printImage"  value="'+jsonString+'" />');
    jQuery('body').append(mapForm);
    mapForm.submit();                 
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
}

function b64toBlob(b64, onsuccess, onerror) {
    var img = new Image();

    img.onerror = onerror;

    img.onload = function onload() {
        var canvas = document.createElement('canvas');
        canvas.width = img.width;
        canvas.height = img.height;

        var ctx = canvas.getContext('2d');
        ctx.drawImage(img, 0, 0, canvas.width, canvas.height);

        canvas.toBlob(function(blob) {
            var newImg = document.createElement("img"),
                url = URL.createObjectURL(blob);

            newImg.onload = function() {
              // no longer need to read the blob so it's revoked
              URL.revokeObjectURL(url);
            };

            newImg.src = url;
            document.body.appendChild(newImg);
          });
        
        canvas.toBlob(onsuccess);
    };

    img.src = b64;
}

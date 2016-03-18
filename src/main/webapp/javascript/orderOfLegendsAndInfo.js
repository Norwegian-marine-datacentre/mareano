function insertLegendAtIndex(currentLegend, kartlagId) {
    //snag when working with jQuery fragements - jQuery doesnt read outmost tag
    //so have to append <div> ... </div> (<html> ... </html> doesnt work as it is removed by clean())
    //See: http://stackoverflow.com/questions/3445680/is-it-possible-to-get-jquery-objects-from-an-html-string-thats-not-in-the-dom
    var divs = jQuery('<div>'+currentLegend+'</div>').find('div');
    var currentId = -1;
    var currentIdIndex = -1;
    var kartlagIdIndex = -1;
    var insertAfterCurrentIdIndex = -1;
    for ( var j=0; j <layersInPicture.length; j++ ) {
      if ( layersInPicture[j] == kartlagId ) {
        kartlagIdIndex = j;
        break;
      }
    }
    var listIds = [];
    for ( var i = 0; i < divs.length; i++ ) {
        listIds.push(divs[i].id);
        currentId = divs[i].id;
        currentIdIndex = -1;
        for ( var j=0; j < layersInPicture.length; j++ ) {
            if ( layersInPicture[j] == currentId ) {
                currentIdIndex = j;
                break;
            }
        }
        if ( currentIdIndex+1 == kartlagIdIndex ) {
            insertAfterCurrentIdIndex = currentIdIndex;
            break;
        }
    }
    return insertAfterCurrentIdIndex;
}

/** 
 * build array of Legend divs
 * 
 * @param current legends as html fragment
 * @returns Array - of html fragments divided by divs
 */
function getArrayOfLegendDivs(currentLegend) {
    
    currentLegend = currentLegend.replace(' class=\" out-of-scale\"','');
    var arrayCurrentLegend = currentLegend.split("<div id=");
    for ( var i = 0; i < arrayCurrentLegend.length; i++ ) {
        if ( i > 0 ) {
            arrayCurrentLegend[i-1] = "<div id=" + arrayCurrentLegend[i];
        }
    }
    if ( i > 0) {
        arrayCurrentLegend.splice(arrayCurrentLegend.length-1, 1);
    }
    return arrayCurrentLegend;
}

/** 
 * build array of Info divs
 * 
 * @param current Info as html fragment
 * @returns Array - of html fragments divided by divs
 */
function getArrayOfInfoDivs( currentInfo ) {
    
    var arrayCurrentInfo = currentInfo.split( "<div id=" );
    for ( var i = 0; i < arrayCurrentInfo.length; i++ ) {
        if ( i > 0 ) {
            arrayCurrentInfo[i-1] = "<div id =" + arrayCurrentInfo[i];
        }
    }
    if ( i > 0) {
        arrayCurrentInfo.splice( arrayCurrentInfo.length-1, 1 );
    }
    return arrayCurrentInfo;
}

function createNewInfoFragment(layer, data) {

    var kartlagId = layer.metadata['kartlagId'];

    var getMapUrl = "";
    var layers = app.mapPanel.map.layers;
    for (var i=0; i<layers.length; i++) {
        var alayer = layers[i];
        var id = alayer.metadata['kartlagId'];
        if ( id != null && id == kartlagId ) {
            var is = (alayer.CLASS_NAME == "OpenLayers.Layer.WMS")
            if (is) {
                tParams = alayer.params;
                tParams = OpenLayers.Util.extend(tParams, {
                    BBOX: alayer.maxExtent.toArray(),
                    WIDTH: alayer.map.size.w, 
                    HEIGHT: alayer.map.size.h
                });
                getMapUrl = alayer.getFullRequestString(tParams);
            }
            break;
        }
    }

    //add a href to urls
    var infoWithLinks = closureForAddHrefsToUrl(data.kartlagInfo.text);
    
    var layers = layer.params.LAYERS;
    var infoHTML = '<div id="'+kartlagId+'tips" style="margin-bottom: 0.1cm;"><font style="font-size: 12px;"><b>'+ 
    data.kartlagInfo.kartlagInfoTitel+'</b>' + ':<br />' + 
    infoWithLinks + '<br />' +
    '<a href="' + getMapUrl + '" target="_blank">getMap:'+layers+'</a></font></div>';

    return infoHTML;
}

function closureForAddHrefsToUrl(infoText) {
    if ( infoText == null ) {
        return infoText;
    } else {
        var re = new RegExp(Ext.escapeRe( '<a href' ), 'i')
        if ( re.test(infoText) ) {
            //already contains a href
            return infoText;
        }
    }
    var updatedInfoText = "";
    addHrefsToUrl(infoText);
    function addHrefsToUrl( infoTextFragment ) {
        var indexHttp = infoTextFragment.indexOf("http");
        if ( indexHttp > -1 ) {
            addHrefString( indexHttp, infoTextFragment );
        } else {
            updatedInfoText += infoTextFragment;
        }
    }
    
    function addHrefString( indexHttp, infoTextFragment ) {
        updatedInfoText += infoTextFragment.substring(0, indexHttp );
        var lengthStr = infoTextFragment.length;
        //console.log("length:"+lengthStr+" fragment:"+updatedInfoText)
        var notUpdatedInfoText = infoTextFragment.substring(indexHttp, lengthStr );
        var endOfWordToLink = notUpdatedInfoText.indexOf(" ");
        var endOfWordToLinkNonAscii = notUpdatedInfoText.indexOf("<");
        if ( endOfWordToLink == -1 ) {
            endOfWordToLink = lengthStr;
        } else if ( endOfWordToLinkNonAscii < endOfWordToLink ) {
            endOfWordToLink = endOfWordToLinkNonAscii;
        }
        //console.log("begin substring:"+indexHttp+"index substring:"+endOfWordToLink+" subString:"+notUpdatedInfoText)
        var theLink = notUpdatedInfoText.substring(0, endOfWordToLink);
        //console.log("link:"+theLink.link(theLink))
        updatedInfoText += "<a href='"+theLink+"' target='_target'>"+theLink+"</a>";
        //console.log("recurse on:"+infoTextFragment.substring(endOfWordToLink, lengthStr))
        addHrefsToUrl( notUpdatedInfoText.substring(endOfWordToLink, notUpdatedInfoText.length) );        
    }
    return updatedInfoText;
}

var layerIdHash = [];
function getlayerIdHash() {
    return layerIdHash;
}

function removeKartlagFromHash(kartlagIdRemoved) {
    for(var i = layerIdHash.length - 1; i >= 0; i--) {
        if(layerIdHash[i].kartlagId === kartlagIdRemoved) {
           layerIdHash.splice(i, 1);
           break;
        }
    }
}

function createNewLegendFragment(kartlagId, kartlagTitle, data) {
    
    var newLegendFragment = '<div id="'+kartlagId+'">';
    var legendHash = [];
    var lengendHashLength = legendHash.length; 
    for ( var i=0; i < data.legends.length; i++ ) {
        legendHash[ i + lengendHashLength] = {"url": data.legends[i].url, "text": data.legends[i].text};
        if ( i > 0 ) {
            newLegendFragment += '<div>';     
        }
        if ( data.legends[i].url != '') {
            newLegendFragment += '<table><tr><td><img src="' + data.legends[i].url + '"/></td>';
            newLegendFragment += '<td id="legend_text">' + data.legends[i].text + '</td></tr></table>';
        } else {
            newLegendFragment += data.legends[i].text;
        }
        if ( i > 0 ) {
            newLegendFragment += '</div>';     
        }
    } 
    newLegendFragment += '</div>';
    
    layerIdHash.push( { "kartlagId": kartlagId, "legend":legendHash, "kartlagTitle": kartlagTitle } );
    
    return newLegendFragment;
}
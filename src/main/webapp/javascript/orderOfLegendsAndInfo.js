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
            	var width = alayer.map.size.w;
            	var height = alayer.map.size.h;
            	if ( alayer.url.indexOf("geonorge.no") || 
            			alayer.url.indexOf("opencache.statkart.no") || 
            			alayer.url.indexOf("maps.imr.no/geoserver/gwc") ) {
                	var width = 256;
                	var height = 256;
            	}
                tParams = alayer.params;
                tParams = OpenLayers.Util.extend(tParams, {
                    BBOX: alayer.maxExtent.toArray(),
                    WIDTH: width, 
                    HEIGHT: height
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
    
    var shape_date = getShapeDate(alayer);
    if (alayer.url.indexOf("maps.imr.no/geoserver/") > -1) {
        if ( shape_date != null && shape_date != "" ) {
            infoHTML += "<font style='font-size:12px'>Shape_date:" +shape_date+" </font>";
        }
    }
    if (alayer.url.indexOf("geo.ngu.no") > -1) {
        if ( shape_date != null && shape_date != "" ) {
            infoHTML += "<font style='font-size:12px'>Dato:" +shape_date+" </font>";
        }
    }
    
    return infoHTML;
}

//Also add date of when layer was last updated
//Currently implemented for IMR layers
//Todo: implement for NGU layers
function getShapeDate(alayer) {
  
  var shape_date = null;
  if (alayer.url.indexOf("maps.imr.no/geoserver/") > -1 ) { // /gwc or /wms
      
	  var layerWithShapeDate = alayer.params.LAYERS;
      var baseWFSurl = "https://maps.imr.no/geoserver/wms?service=WFS&version=2.0.0&request=GetFeature&typeName=";
      var update_ = "&propertyName=update_";
      var update = "&propertyName=update";
      var version = "&propertyName=version";
      var versjon = "&propertyName=VERSJON";
      var date_shape_property = "&propertyName=date_shape";
      
      
      var wfsDate_shape = baseWFSurl + layerWithShapeDate + date_shape_property;
      var wfsUpdate_ = baseWFSurl + layerWithShapeDate + update_;
      var wfsUpdate = baseWFSurl + layerWithShapeDate + update;
      var wfsVersion = baseWFSurl + layerWithShapeDate + version;
      var wfsVersjon = baseWFSurl + layerWithShapeDate + versjon;
  
      //corals is layer_group and only coralreefs in group has shape_date
      if ( layerWithShapeDate == "corals" ) { 
    	  layerWithShapeDate = "coralreefs";
      }
      
    function checkIfXmlContainDate( url ) {
      var xmlHttp = new XMLHttpRequest();
      xmlHttp.open( "GET", url, false ); // false for synchronous request
      xmlHttp.send( null );            
      if (xmlHttp.status==200) {
          var xmlDoc = xmlHttp.responseXML;
          var feature_collection = xmlDoc.childNodes[0];
          var last_member = feature_collection.childNodes[feature_collection.childNodes.length -1];
          if (last_member.childNodes[0] != undefined) {
              shape_date = last_member.childNodes[0].textContent;
              return shape_date;
          }
      }
      return null;
    }

      shape_date = checkIfXmlContainDate( wfsDate_shape );
      if ( shape_date == null )
      	shape_date = checkIfXmlContainDate( wfsUpdate_ );
      if ( shape_date == null )
          shape_date = checkIfXmlContainDate( wfsUpdate );
      if ( shape_date == null )
          shape_date = checkIfXmlContainDate( wfsVersion );
      if ( shape_date == null )
          shape_date = checkIfXmlContainDate( wfsVersjon );
      
      //console.log("shape_date:"+shape_date);
  }
  if (alayer.url.indexOf("http://geo.ngu.no") > -1) {

      //NGU uses older WFS version 1.0.0
      var baseWFSurl = alayer.url + "?service=WFS&version=1.0.0&request=GetFeature";
      
      var partsOfStr = alayer.params.LAYERS.split(',')
      for (i=0; i < partsOfStr.length; i++) {
          baseWFSurl += "&typeName=" + partsOfStr[i];
      }
      var dato_shape_property = "&propertyName=Dato";
      
      var wfsDate_shape = baseWFSurl + dato_shape_property;
      
      function checkIfXmlContainDate( url ) {
        var xmlHttp = new XMLHttpRequest();
        xmlHttp.open( "GET", url, false ); // false for synchronous request
        xmlHttp.send( null );            
        if (xmlHttp.status==200) {
          var xmlDoc = xmlHttp.responseXML;
          var feature_collection = xmlDoc.childNodes[0];
          if (feature_collection != undefined && 
             feature_collection.childNodes[3] != null &&
             feature_collection.childNodes[3].childNodes[1] != null &&
             feature_collection.childNodes[3].childNodes[1].childNodes[1] != null ) {
              dato = feature_collection.childNodes[3].childNodes[1].childNodes[1].innerHTML
              return dato;
          }
        }
        return null;
      }
    
      shape_date = checkIfXmlContainDate( wfsDate_shape );
  }
  
  return shape_date;
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
function insertLegendAtIndex(currentLegend, kartlagId) {
    //snag when working with jQuery fragements - jQuery doesnt read outmost tag
    //so have to append <div> ... </div> (<html> ... </html> doesnt work as it is removed by clean()
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

//build an array of divs
function getArrayOfCurrentDivs(currentLegend) {
    
    var arrayCurrentLegend = currentLegend.split("<div id=");
    for ( var i = 0; i < arrayCurrentLegend.length; i++ ) {
        if ( i > 0 ) {
            arrayCurrentLegend[i-1] = "<div id =" + arrayCurrentLegend[i];
        }
    }
    if ( i > 0) {
        arrayCurrentLegend.splice(arrayCurrentLegend.length-1, 1);
    }
    return arrayCurrentLegend;
}

function createNewLegendFragment(kartlagId, data) {
    var newLegendFragment = '<div id="'+kartlagId+'">';
    for ( var i=0; i < data.legends.length; i++ ) {
        if ( i > 0 ) {
            newLegendFragment += '<div>';     
        }
        if ( data.legends[i].url != '') {
            newLegendFragment += '<table><tr><td><img src="' + data.legends[i].url + '"/></td>';
            newLegendFragment += '<td>' + data.legends[i].text + '</td></tr></table>';
        } else {
            newLegendFragment += data.legends[i].text;
        }

        if ( i > 0 ) {
            newLegendFragment += '</div>';     
        }
    } 
    newLegendFragment += '</div>';
    return newLegendFragment;
}
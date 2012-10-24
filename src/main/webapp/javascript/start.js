/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
var map;

jQuery(document).ready(function(){

   //legger til søkeboksen som ligger like under menyen
   jQuery(".top_below").append("<div class='search'>"+
        "<form action='http://www.imr.no/sok/nb-no' method='get'>"+
        "<div>"+
        "<input type='text' class='searchBoxString' name='searchString:utf8:ustring'/>"+
        "<input type='submit' value='Søk' class='earchBoxButton'/>"+
        "</div>"+
        "</form>"+
        "</div>");



    // lager en brødsmule sti. Det er bare å fortsette nedover dersom det skulle være ønskelig

    var url = window.location.pathname;
    jQuery(".top_below").append("<div class='breadcrumbs'>Her er du:<a href='http://www.imr.no/nb-no'>Forside</a>"+
        "<div class='split'>&gt;</div><a href='http://www.imr.no/forskning/nb-no'>Forskning</a>"+
        "<div class='split'>&gt;</div><span><a href='http://www.imr.no/forskning/forskningsdata/nb-no'>Forskningsdata</a></span>"+
 //       "<div class='split'>&gt;</div><span><a href='"+url + "/index.html'>DOKIPY metadata</a></span>"+
   //     "<div class='split'>&gt;</div><span><a href='"+url + "/listmetadata.html'>Dataset list</a></span>"+
        "<div class='split'>&gt;</div><span>Dataset</span></div");

    // når man har en undermeny (som på Forskningsdata sidene) må man utvide høyden på .menu elementet slik at man får riktig utseende
    // ellers vil brødsmulene og søkefeltet havne under undermenyen
    jQuery(".top_below .menu").css("height","51px");
    jQuery("#choices").dialog({
        width: "667px",
        position: "right"
    });

    var options = {
        maxExtent: new OpenLayers.Bounds(-1067531,-2516126,2096218,8142),
        projection: "EPSG:3995",
        controls: [new OpenLayers.Control.LayerSwitcher(), new OpenLayers.Control.PanZoom(),
        new OpenLayers.Control.DragPan(),new OpenLayers.Control.Navigation()],
        maxResolution: 'auto'
    };
    map = new OpenLayers.Map("map", options);

    var wms = new OpenLayers.Layer.WMS(
        "Gebco",
        "http://crius.nodc.no:8080/geoserver/wms?",
        {
            layers: 'gebco:gebco'
        }
        );
    //    map.addLayer(wms);




/*    var wms2 = new OpenLayers.Layer.WMS(
        "Image test",
        "http://crius.nodc.no:8080/geoserver/wms?",
        {
            layers: "gn:imgtest",
            transparent: true
        },
        {
            isBaseLayer: false

        }
        );*/
    map.addLayer(wms);

    map.zoomToMaxExtent();
    map.setBaseLayer(wms);



});

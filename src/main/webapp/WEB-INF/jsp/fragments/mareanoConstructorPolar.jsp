<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
/**
 * 1. Create Mareano.Composer constructor into app var and define background maps.
 * 2. Create hovedtema, kartbilde, and kartlag structure in left layer panel 
 * 3. Create Generelle folder in right most layer panel
 */
app = new Mareano.Composer({
    <!-- authStatus: < status >, -->
    proxy: "proxy/?url=",
    printService: null,
    about: {
        title: "Mareano",
        "abstract": "<spring:message code="projectAbstract" text="Abstract" />",
        contact: "<spring:message code="contact" text="Contact" />"
    },
    defaultSourceType: "gxp_wmscsource",
    sources: {
        ol: {
            ptype: "gx_olsource"
        }
    },
    map: {
        projection: "EPSG:3575",
        maxExtent:[ -4889334.802954878,-4889334.802954878,4889334.802954878,4889334.802954878 ],        
        units: "m",
        maxResolution: 38197.92815, //new for EPSG:3575
        numZoomLevels: 18,
        wrapDateLine: false,
        layers: [
        {
            source: "ol",
            type: "OpenLayers.Layer.WMS",
            group: "background",
            args: [
                    "barentswatch_grunnkart hardkodet",
                    "http://opencache.statkart.no/gatekeeper/gk/gk.open",
                    {layers: "barentswatch_grunnkart", format: "image/png", transparent: true, isBaseLayer: true},
                    {singleTile:false}
                ]
            }                  
       ],
       center: [-433382.43932, -2457833.949055],
       zoom: 2
    }
});

//Global vars
var layers = [];
var generelleLayers = [];
var generelleLayerNames = {};
var OLRecord;

var hovedtemaer=[];  //Non generelle and non background
var generelle=[];
var bakgrunn=[]; 

//JSON of all layers
var alleHovedtemaer=${hovedtemaer_json};
//TODO discuss how background layers should be flagged
var backgroundGroupName ="background";
var backgroundSeaGroupName ="backgroundSea";

var hovedtema,gruppe;




/**
 * Creates layers for background layers.
 * This could be merged into generic function that createLayerRecord could use
 * to create layer part of layer record. Would need to check extra atttributes that
 * createLayerRecord adds are valid for background layers as well
*/
function createBackgroundLayerObject(layer)
{
    return  {source: "ol",
         type: "OpenLayers.Layer.WMS",
         group: "background",
         args: [
         layer.title,
         layer.url,
         {layers: layer.layers,
          format: layer.format,
          transparent: true,
          isBaseLayer: true},
         {
             metadata: {
             keyword: layer.keyword,
             'kartlagId': layer.id
                     },
             singleTile:false
         }
    ]};
}


function createLayerRecord(panelGroup,isVisible,layer)
{
    return gxp.plugins.OLSource.prototype.createLayerRecord({
        source: "ol",
        type: "OpenLayers.Layer.WMS",
        group: panelGroup,
        queryable: layer.queryable,
        visibility: isVisible,
        properties: "mareano_wmslayerpanel",            
        args: [
            layer.title,
        layer.url,
            {layers: layer.layers, format: layer.format, transparent: true},
            {
                opacity: 1,
                metadata: {
                    keyword: layer.keyword,
                    'kartlagId': ''+layer.id
                },
                minScale: layer.scalemax*(96/0.0254),
                maxScale: (layer.scalemin > 0) ? layer.scalemin*(96/0.0254) : 0.001,
                units: "m",
                maxExtent: [
                    layer.exGeographicBoundingBoxWestBoundLongitude,
                    layer.exGeographicBoundingBoxSouthBoundLatitude,
                    layer.exGeographicBoundingBoxEastBoundLongitude,
                    layer.exGeographicBoundingBoxNorthBoundLatitude
                ],
                singleTile:true,
                buffer: 0, //getting no boarder around image - so panning will get a new image.
                ratio: 1 //http://dev.openlayers.org/releases/OpenLayers-2.12/doc/apidocs/files/OpenLayers/Layer/Grid-js.html#OpenLayers.Layer.Grid.ratio                                        
            }
        ]
    });
}


for (var i=0;i<alleHovedtemaer.length;i++)
{
    hovedtema=alleHovedtemaer[i];

    
    //Split into three groups so have lists of each type of layers for later use
    //Test is a bit sloppy as it assumes each tema does not have mix of groups
    if (hovedtema.bilder[0].gruppe == backgroundGroupName) {
    bakgrunn.push(hovedtema);
    } else if (hovedtema.bilder[0].gruppe == "generelle") {
    generelle.push(hovedtema);
    }
    else
    {
    hovedtemaer.push(hovedtema);
    }
    
    for (var j=0;j<hovedtema.bilder.length;j++)
    {
    gruppe=hovedtema.bilder[j];
    for (var k=0;k<gruppe.kart.length;k++)
    {
        layer = gruppe.kart[k];
        if (gruppe.gruppe == backgroundGroupName || gruppe.gruppe == backgroundSeaGroupName) {
        app.map.layers.push(createBackgroundLayerObject(layer));
        } else {
        OLRecord = createLayerRecord(gruppe.gruppe,gruppe.visible,layer);
        if (gruppe.gruppe == "generelle") {
            generelleLayers.push(OLRecord);
            generelleLayerNames[layer.title]=true;

        } else {
            layers.push(OLRecord);
        }
        }
    }
    }
}

var store = new GeoExt.data.LayerStore();
store.add(layers);   
store.add(generelleLayers);

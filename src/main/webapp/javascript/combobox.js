function gridchange(){
	
    jQuery.ajax({
        url: "spring/parameter.html",
        data:{
            grid : "gridname",
            grid_value : jQuery("#grid :selected").val()
        },
        method: "post",
        success:function(data){
        	Ext.Msg.updateText(data);
        	rememberState(newHelp); 
        }
    });
}
function datasetchange(){
    jQuery.ajax({
        url:"spring/parameter.html",
        data:{
            dataset : "datasetname",
            dataset_value : jQuery("#dataset :selected").val(),
            grid : "gridname",
            grid_value : jQuery("#grid :selected").val()
        },
        method: "post",
        success:function(data){
        	Ext.Msg.updateText(data);
        	if (notSelectedDataset()) {
            	jQuery("#parameter").removeAttr("disabled");
            }
        	rememberState(newHelp);        	
        }
    });
}
function paramchange(){
    jQuery.ajax({
        url: "spring/parameter.html",
        data:{
            parameter : "parametername",
            parameter_value : jQuery("#parameter :selected").val(),
            dataset : "datasetname",
            dataset_value : jQuery("#dataset :selected").val(),
            grid : "gridname",
            grid_value : jQuery("#grid :selected").val()
        },
        method: "post",
        success:function(data){
        	var msgBox = Ext.Msg.updateText(data);

        	jQuery("#choices").html(data);
            jQuery("#external_metadata").html(jQuery("#metadata").html());
            jQuery("#scrollbarLayerInfo").show();
            jQuery("#metadata").hide();
        	
            if (notSelectedParameter() && notSelectedDataset()) {
            	jQuery("#parameter").removeAttr("disabled");
            	jQuery("#depthlayer").removeAttr("disabled");
            	jQuery("#period").removeAttr("disabled");
            	jQuery("#displaytype").removeAttr("disabled");
            	
            	jQuery("#makemap").removeAttr("disabled");
            	jQuery("#createpdf").removeAttr("disabled");
            }
            rememberState(newHelp);
        }
    });
}

var felayer;
function drawmap(){
	jQuery.support.cors = true;

    // create sld 
	var layername = viewChoosen();
	var displayType = "";
	if (layername == "test:pointvalue") displayType = "punktvisning";
	else displayType = "arealvisning";
    jQuery.ajax({
        url:"spring/createsld.html",
        data:{
            grid : jQuery("#grid :selected").val(),
            parameter: jQuery("#parameter :selected").val(),
            time: jQuery("#period :selected").val(),
            depth: jQuery("#depthlayer :selected").val(),
            displaytype: displayType
        },
        method: "post",
        success: function(message){
            var mapp = Ext.ComponentMgr.all.find(function(c) {
                return c instanceof GeoExt.MapPanel;
            });
            // add layer to map
            if(felayer != null){
                mapp.map.removeLayer(felayer);
            }
            
            addLayerToMap(layername, message, mapp);
        },
        error: function(req, status, errThrown) {
    		alert("ie error req:"+req+" status:"+status+" errThrown:"+errThrown);
    	}
    })
}

function viewChoosen() {
    var punktVisning = jQuery("#punkt").is(':checked');
    var layername = "";
    if(punktVisning){
        layername = "test:pointvalue";
    }else{
        layername = "test:areavalue";
    }  
    return layername;
}

function addLayerToMap(layername, message, mapp) {
    var baseUrl = location.href.substring(0,location.href.lastIndexOf('/'));
    baseUrl = baseUrl + "/spring/";
    felayer = new OpenLayers.Layer.WMS.Post(
        "FishExchangeGrid",
        "http://maps.imr.no/geoserver/wms?",
        {
            layers: layername,
            transparent: true,
            sld: baseUrl + "getsld.html?file=" + message
        },
        {
            isBaseLayer: false
        }
        );
    mapp.map.addLayer(felayer);
    
    /** dns redirect to crius.nodc.no/geoserver/wms */
    var src = "http://maps.imr.no/geoserver/wms?service=WMS&version=1.1.1&request=GetLegendGraphic&layer="+
    	layername+"&width=22&height=24&format=image/png&SLD="+baseUrl + "getsld.html?file=" + message;
    jQuery("#legend").attr("src",src);      
}

function getHTTPObject() {
    if (typeof XMLHttpRequest != 'undefined') {
        return new XMLHttpRequest();
    }
    try {
        return new ActiveXObject("Msxml2.XMLHTTP");
    } catch (e) {
        try {
            return new ActiveXObject("Microsoft.XMLHTTP");
        } catch (e) {}
    }
    return false;
}

function createPDF(){
	var showLayers = showVisibleLayers();
	var layername = viewChoosen();
	var displayType = "";
	if (layername == "test:pointvalue") displayType = "punktvisning";
	else displayType = "arealvisning";
    jQuery.ajax({
        url: "spring/createsld.html",
        data:{
            grid : jQuery("#grid :selected").val(),
            parameter: jQuery("#parameter :selected").val(),
            time: jQuery("#period :selected").val(),
            depth: jQuery("#depthlayer :selected").val(),
            displaytype: displayType
        },
        method: "post",
        success: function(message){
        	var mapp = Ext.ComponentMgr.all.find(function(c) {
        		return c instanceof GeoExt.MapPanel;
        	});

            var pdfUrl = "spring/createpdfreport.html?bbox="+mapp.map.getExtent().toBBOX()+
            	"&sld="+"http://talos.nodc.no:8080/geodata/spring/"+
            	"getsld.html?file="+message+
            	"&srs="+mapp.map.getProjection()+
            	"&layer="+layername+
            	"&layerson="+showLayers+
            	"&width="+mapp.map.getSize().w+
            	"&height="+mapp.map.getSize().h;        
            
            document.getElementById("hidden_pdf").action = pdfUrl;
            jQuery("#hidden_pdf").submit();
        }
    })
}

function showVisibleLayers() {
	var mapp = Ext.ComponentMgr.all.find(function(c) {
		return c instanceof GeoExt.MapPanel;
	});
	var tmpLayers = mapp.map.layers;
	var strLayers = "";
	for(var i=0; i<tmpLayers.length; i++) {
		var aLayer = tmpLayers[i];
		if ( aLayer.getVisibility() == true && !(aLayer instanceof OpenLayers.Layer.Vector) 
				&& aLayer.name != "FishExchangeGrid" 
					&& aLayer.name != "Europakart" ) {
			var tmpUrl = aLayer.getFullRequestString({});
			tmpUrl = escape(tmpUrl);
			strLayers += tmpUrl + "-";
		}
	}
//	alert("strLayers:"+strLayers);
	return strLayers;
}

function rememberState(showSpan) {
	if ( isAdvanced ) showHiddenSelect();
	hideShow(showSpan);
}
/* used in parameter.js to hide/show help feature*/
var newHelp='';
function hideShow(showSpan) {
	newHelp = showSpan;
	if ( newHelp == '') {
		jQuery('.hiddenFirstHelptext').show('fast');
	} else {
		jQuery('.hiddenFirstHelptext').hide('fast');
		jQuery('.hiddenGridHelp').hide('fast');
		jQuery('.hiddenSpeciesHelp').hide('fast');
		jQuery('.hiddenSubgroupHelp').hide('fast');
		jQuery('.hiddenDepthHelp').hide('fast');
		jQuery('.hiddenPeriodHelp').hide('fast');
		jQuery('.'+showSpan).show('fast');
	}
}

var isAdvanced=false;
function showHiddenSelect() {
	jQuery("#advanced").hide('fast');
	jQuery("#simple").show('fast');
	jQuery("#gridColumn").show('fast');
	jQuery("#depthLayerColumn").show('fast');
	jQuery("#periodColumn").show('fast');
	isAdvanced=true;
}

function hideSelect() {
	jQuery("#advanced").show("fast");
	jQuery("#simple").hide('fast');
	jQuery("#gridColumn").hide('fast');
	jQuery("#depthLayerColumn").hide('fast');
	jQuery("#periodColumn").hide('fast');
	isAdvanced=false;
}

function notSelectedDataset(){
	return jQuery('#dataset').val()!="Select value" && jQuery('#dataset').val()!="Velg verdi";
} 

function notSelectedParameter(){
	return jQuery('#parameter').val()!="Select value" && jQuery('#parameter').val()!="Velg verdi";
}
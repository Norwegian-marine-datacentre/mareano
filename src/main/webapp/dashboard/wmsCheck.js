


console.log("Init ");
var app =function() {
    
    var imageWidth=256;
    var imageHeight=256;

    var mapMaxExtent="-2500000.0,3500000.0,3045984.0,9045984.0";
    var polarMapMaxExtent="2444667.4014774,-2444667.4014775,4889334.8029549,0";

    var srsID = "EPSG:32633";
    var polarID = "EPSG:3575";
    

    
    //var testTile="-1113504,6272992,272992,7659488";
    var testTile="-1113504,6272992,272992,7659488";
    var polarTestTile="0,-2444667.4014775,2444667.4014774,0";


    
    var jsonURL= "../mareanoJson"
    var polarJsonURL= "../mareanoPolarJson"
    
    var layerCount=0;
    var themeID=0;
    var themeRow=0;

    if (document.location.href.indexOf("Polar") != -1) {
	console.log("have polar");

	testTile = polarTestTile;
	jsonURL = polarJsonURL;
	srsID = polarID;
    }


    return {
	createThemePanel: function(name){
	    var newPanel = $(".template").clone();
	    newPanel.removeClass("template");
	    newPanel.addClass("panel-primary");
	    newPanel.find(".panel-title").text(name);
	    newPanel.find(".panel-collapse").attr("id", "theme"+themeID).removeClass("collapse");
	    $("body").append($("<div>",{class:"col-md-4"}).append(newPanel));
	    //$("#themes"+themeRow).append($("<div>",{class:"col-md-3"}).append(newPanel));

	    /*themeID++;
	    if(themeID%4==0){
		themeRow++;
		var newRow = $("<div>", {id: "themes"+themeRow, class: "row"});
		newRow.insertAfter($("#themes"+(themeRow-1)));
		console.log("add row");
	    }*/
	   
	    return newPanel;
	},
	loadSingleLayer: function() {
	    $.getJSON( "test1.json", $.proxy(function( data) {
		this.addWMSLayer(data);
	    },this)); 
	},
       loadLayers: function() {
	    $.getJSON( jsonURL, $.proxy(function(data) {
		//this.addWMSLayer(data);
		data.forEach( function(theme) {
		    console.log(theme.hovedtema);
		    var themePanel = this.createThemePanel(theme.hovedtema);
		    
		    theme.bilder.forEach( function(layerGroup) {
		//	var startBounds = [layerGroup.startextentMinx,layerGroup.startextentMiny,layerGroup.startextentMaxx,layerGroup.startextentMaxy];
			layerGroup.kart.forEach( function(layer) {
			    this.addWMSLayer(layer,themePanel,(theme.hovedtema=="Bakgrunnskart"));
			   // this.addWMSLayer(layer,startBounds,themePanel,false);
			},this);
		    },this);
		},this);
	    },this)); 
       },
	addWMSLayer: function(data,containerPanel,useTestTile){
	  
//	  var panelID ="layer"+data.id;
	  var panelID ="layer"+(layerCount++);
	   
	   var newPanel = $(".template").clone();
	   newPanel.removeClass("template");
	   newPanel.find("a").attr("data-target","#"+panelID).text(data.title);
	   newPanel.find(".panel-collapse").attr("id", panelID);
	   containerPanel.append(newPanel);

	    var boundsString;
	    var boundsUsed;
	    if (useTestTile) {
		boundsString = testTile;
	      boundsUsed ="Test tile";
	  } else if (data.exGeographicBoundingBoxWestBoundLongitude!=0)  {
	      boundsString =data.exGeographicBoundingBoxWestBoundLongitude+","+data.exGeographicBoundingBoxSouthBoundLatitude+","+data.exGeographicBoundingBoxEastBoundLongitude+","+data.exGeographicBoundingBoxNorthBoundLatitude;
	      boundsUsed ="Mareno admin bounding box for this layer";
	  } else {
	      boundsUsed ="Default Mareano bounding box (zoom level 2)";
	      boundsString=this.mapMaxExtent;
	  };

	   var wmsParams = {layers:data.layers,
			    format:data.format,
			    transparent:true,
			    isbaselayer:true,
			    service:"WMS",
			    version:"1.1.1",
			    request:"GetMap",
			    srs:srsID,
			    bbox:boundsString,
			    width:imageWidth,
			    height:imageHeight};
	   var wmsQuery =$.param(wmsParams);
	   
	    var imgURL = data.url+(data.url.includes('?')?"&":"?")+wmsQuery;
	   newPanel.find(".panel-body").find("a").attr("href",imgURL).text("WMS request");
            newPanel.find(".panel-body").append($('<div/>', {text:"Using "+boundsUsed}));
	    
	    //var randomNum = Math.round(Math.random() * 10000);
	   var start = new Date();
	   var wmsImage=new Image();
	   wmsImage.src=imgURL;
	   wmsImage.onload=function(){
	       var end = new Date();
	       console.log('Image succesfully loaded!')
	       newPanel.addClass("panel-success");
	       //newPanel.find(".panel-heading").append(wmsImage);
	       newPanel.find(".panel-body").append($('<div/>', {text:"Time:"+(end-start)+"ms"}));
	       newPanel.find(".panel-body").append(wmsImage);
	    
	   
	   }
	   wmsImage.onerror=function(e){
	       var end = new Date();
	       newPanel.addClass("panel-danger");
	       console.log('Image error',e,wmsImage)

	       
	       newPanel.find(".panel-body").append($('<div/>', {text:"Error loading"}));
	       newPanel.find(".panel-body").append($('<div/>', {text:"Time:"+(end-start)+"ms"}));
	       //newPanel.find(".panel-body").append($('<iframe/>', {src:imgURL}));
	   }
       }
    };
}();

//app.loadSingleLayer();
app.loadLayers();



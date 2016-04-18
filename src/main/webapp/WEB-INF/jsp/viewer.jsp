<html>
    <head>
        <title>Mareano</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="shortcut icon" href="../lib/imr/img/mareanoLogo.png">

        <!-- Ext resources -->
        <link rel="stylesheet" type="text/css" href="../lib/geoexplorer/ext/ext-all.css">
        <link rel="stylesheet" type="text/css" href="../lib/geoexplorer/ext/xtheme-gray.css">
        <script type="text/javascript" src="../lib/geoexplorer/ext/ext-base.js"></script>
        <script type="text/javascript" src="../lib/geoexplorer/ext/ext-all.js"></script>

        <!-- OpenLayers resources -->
        <link rel="stylesheet" type="text/css" href="../lib/geoexplorer/openlayers/theme/default/style.css">

        <!-- GeoExt resources -->
        <link rel="stylesheet" type="text/css" href="../lib/geoexplorer/geoext/resources/css/popup.css">
        <link rel="stylesheet" type="text/css" href="../lib/geoexplorer/geoext/resources/css/layerlegend.css">
        <link rel="stylesheet" type="text/css" href="../lib/geoexplorer/geoext/resources/css/gxtheme-gray.css">

        <!-- gxp resources -->
        <link rel="stylesheet" type="text/css" href="../lib/geoexplorer/gxp/src/theme/all.css">
        
        <!-- GeoExplorer resources -->
    	<link rel="stylesheet" type="text/css" href="../lib/geoexplorer/geoexplorer.css" />     
    	<script type="text/javascript" src="../lib/geoexplorer/GeoExplorer.js"></script>

    <script>
        Ext.BLANK_IMAGE_URL = "../theme/app/img/blank.gif";
        OpenLayers.ImgPath = "../theme/app/img/";
        var app = new GeoExplorer.Viewer({
            proxy: "../proxy/?url=",
            printService: null,
            about: {
        		title: "Mareano",
        		contact: "For mer informasjon kontakt <a href='http://www.imr.no'>Havforskningsinstituttet</a>."
            },
            defaultSourceType: "gxp_wmscsource",
        	sources: {
        	    ol: {
        	        ptype: "gx_olsource"
        	    }
            },
            map: {
                layers: [{
                    name: "world",
                    title: "World",
                    source: "suite",
                    group: "background"
                }],
                center: [0, 0],
                zoom: 2
            }
        });
    </script>
    </head>
    <body></body>
</html>


    

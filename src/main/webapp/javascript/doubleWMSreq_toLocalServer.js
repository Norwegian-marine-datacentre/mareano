/**
 * Whenever a layer is turned on or off - send a request to local server (this server) to see
 * if layer also should include Spesialpunkt from Mareano.
 ***/
jQuery(document).ready(function() {
    jQuery('body').change(function(event) {
        if ( jQuery(event.target).is(':checkbox') ) {
            jQuery.ajax({
                type: 'get',
                url: "spesialpunkt",
                data: this.mapPanel.map.getExtent()                
            });
        }
    });
});		
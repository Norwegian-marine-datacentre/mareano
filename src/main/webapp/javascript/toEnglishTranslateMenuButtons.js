jQuery(document).ready(function() {
	jQuery('.icon-english').click(function(event) {
		jQuery(".x-btn-text").each(function(index) {
	    	if (jQuery(this).text() == "Gå til koordinat") {
	    		jQuery(this).html("Go to coordinat");
	    	} else if (jQuery(this).text() == "Koordinater (WGS84):") {
	    		jQuery(this).html("Coordinates (WGS84):");
	    	}
	    });
	    jQuery(".x-form-field").each(function(index) {
	    	jQuery(this).val("Go to area");
		});
	    jQuery(".x-combo-list-item").each(function(index) {
	    	if ( jQuery(this).text() == "Barentshavet") {
	    		jQuery(this).html("Barent Sea");
	        } else if ( jQuery(this).text() == "Norskehavet") {
				jQuery(this).html("Norwegian Sea");
	        } else if ( jQuery(this).text() == "Nordsjøen") {
				jQuery(this).html("North Sea");
	        }
		});
	});			
});
jQuery(document).ready(function() {
	jQuery('.icon-norsk').click(function(event) {
		jQuery(".x-btn-text").each(function(index) {
	    	if (jQuery(this).text() == "Go to coordinat") { 
		    	jQuery(this).html("Gå til koordinat");
	    	} else if (jQuery(this).text() == "Coordinates (WGS84):") {
	        	jQuery(this).html("Koordinater (WGS84):");
	        }
		});
	    jQuery(".x-form-field").each(function(index) {
	    	jQuery(this).val("G\u00e5 til omr\u00e5de");
		});
	    jQuery(".x-combo-list-item").each(function(index) {
	    	 if ( jQuery(this).text() == "Barent Sea") {
	    	   jQuery(this).html("Barentshavet");
	    	} else if ( jQuery(this).text() == "Norwegian Sea") {
				jQuery(this).html("Norskehavet");
	        } else if ( jQuery(this).text() == "North Sea") {
				jQuery(this).html("Nordsjøen");
	        }
		});       	
	});
});
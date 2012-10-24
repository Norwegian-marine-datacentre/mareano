/**
 * Disable fishexchange knapp til id=parameter i dropdown er valgt
 * Part of the functionality of this script is found in combobox.js - paramchange()
 ***/
jQuery(document).ready(function() {
    jQuery('body').change(function(event) {
        if ( jQuery(event.target).is('#grid') ) {
            gridchange();
        }				
        if ( jQuery(event.target).is('#dataset') ) {
            datasetchange();
        }
        if ( jQuery(event.target).is('#parameter') ) {
            paramchange();
        }
        if (jQuery(event.target).is('#parameter') && (jQuery(event.target).val()=="Velg verdi" ||jQuery(event.target).val()=="Select value")) {
            jQuery("#makemap").attr("disabled", "disabled");
            jQuery("#createpdf").attr("disabled","disabled");
        }
    });
});		
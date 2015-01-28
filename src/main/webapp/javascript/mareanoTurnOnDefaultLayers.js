/** Code that copies selected layers and moves them to 
 * Overlays folder when map is loaded first time.
 * @param that
 * @param store
 */
function turnOnDefaultLayers(that, store ) {
    that.mapPanel.layers.each(function(record) {
         if (record.get('visibility') === true && record.getLayer().metadata['kartlagId'] !== undefined) {
             displayLegendGraphics(record.getLayer().metadata['kartlagId']);
         }
    });                	

    store.each(function(record) {
    	if (record.getLayer().visibility === true) {
                var clone = record.clone();
                clone.set("group", "default");
                clone.getLayer().metadata['kartlagId'] = record.getLayer().metadata['kartlagId'];
                var idx = that.mapPanel.layers.findBy(function(r) {
                    return (record.getLayer().metadata['kartlagId'] === r.getLayer().metadata['kartlagId']);
                });
                if (idx === -1) {
                    that.mapPanel.layers.add(clone);
//                    displayLegendGraphics(clone.getLayer().metadata['kartlagId']);
                }
    	}
    }, that);
}
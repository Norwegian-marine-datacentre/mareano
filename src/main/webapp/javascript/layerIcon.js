function getLayerIcon(url) {
	if (url != null) {
		if (url.indexOf("npd.no") != -1) {
			return "gx-tree-rasterlayer-icon-OD";
		} else if (url.indexOf("crius.nodc") != -1
				|| url.indexOf("atlas.nodc") != -1
				|| url.indexOf("maps.nodc") != -1
				|| url.indexOf("maps.imr.no") != -1
				|| url.indexOf("talos.nodc.no") != -1) {
			return "gx-tree-rasterlayer-icon-HI";
		} else if (url.indexOf("ngu.no") != -1) {
			return "gx-tree-rasterlayer-icon-NGU";
		} else if (url.indexOf("dirnat.no") != -1) {
			return "gx-tree-rasterlayer-icon-DN";
		} else if (url.indexOf("fiskeridir.no") != -1) {
			return "gx-tree-rasterlayer-icon-FD";
		} else if (url.indexOf("geonorge.no") != -1
				|| url.indexOf("opencache.statkart.no") != -1) {
			return "gx-tree-rasterlayer-icon-SK";
		} else if (url.indexOf("kart.kystverket.no") != -1) {
			return "gx-tree-rasterlayer-icon-KV";
		} else {
			return "gx-tree-rasterlayer-icon";
		}
	}
}
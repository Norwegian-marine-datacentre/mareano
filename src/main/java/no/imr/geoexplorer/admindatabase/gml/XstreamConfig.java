package no.imr.geoexplorer.admindatabase.gml;

import no.imr.geoexplorer.admindatabase.gml.pojo.Attributes;
import no.imr.geoexplorer.admindatabase.gml.pojo.BoundedBy;
import no.imr.geoexplorer.admindatabase.gml.pojo.FeatureCollection;
import no.imr.geoexplorer.admindatabase.gml.pojo.FeatureMember;
import no.imr.geoexplorer.admindatabase.gml.pojo.Point;

import org.springframework.stereotype.Component;

import com.thoughtworks.xstream.XStream;

/**
 * Configuration of gml pojos to create valid gml
 * 
 * @author endrem
 * 
 */
@Component
public class XstreamConfig {

	private XStream xstream;

	public XstreamConfig() {
		xstream = new XStream();
		//xmlns:gml=\"http://www.opengis.net/gml/3.2 does not work in firefox - use xmlns:gml=\"http://www.opengis.net/gml
		xstream.alias("FeatureCollection xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:gml=\"http://www.opengis.net/gml\"", FeatureCollection.class);
		xstream.addImplicitCollection(FeatureCollection.class, "featureMember");
		xstream.alias("gml:featureMember", FeatureMember.class);
		xstream.aliasField("attributes", FeatureMember.class, "attributes");
		xstream.aliasField("gml:boundedBy", FeatureCollection.class, "boundedBy");
		xstream.aliasField("gml:Box", BoundedBy.class, "box");
		xstream.aliasField("name", Attributes.class, "name");
		xstream.aliasField("pid", Attributes.class, "pid");
		xstream.aliasField("description", Attributes.class, "description");
		xstream.aliasField("time", Attributes.class, "time");
		xstream.aliasField("id", Attributes.class, "id");
		xstream.aliasField("gml:Point", Attributes.class, "point");
		xstream.aliasField("gml:coordinates", Point.class, "coordinates");
		xstream.useAttributeFor(Point.class, "srsName");
	}

	public String toXML(FeatureCollection collection) {
		return xstream.toXML(collection);
	}
}

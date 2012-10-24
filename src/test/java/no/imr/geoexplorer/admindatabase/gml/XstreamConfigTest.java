package no.imr.geoexplorer.admindatabase.gml;

import no.imr.geoexplorer.admindatabase.gml.pojo.Attributes;
import no.imr.geoexplorer.admindatabase.gml.pojo.BoundedBy;
import no.imr.geoexplorer.admindatabase.gml.pojo.FeatureCollection;
import no.imr.geoexplorer.admindatabase.gml.pojo.FeatureMember;
import no.imr.geoexplorer.admindatabase.gml.pojo.Point;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:springmvc-servlet.xml"})
public class XstreamConfigTest {

	@Autowired(required = true)
	private XstreamConfig xstream;

	@Test
	public void testGMLWriter() {
		String gml = exampleOfValidGML();
		assertNotNull(gml);
	}

	@Test
	public void testXstreamConfig() {
		FeatureCollection features = new FeatureCollection();
		BoundedBy bounds = new BoundedBy();
		Point point = new Point("-9.205,10.795,59.39800000000001,79.39800000000001", "EPSG:32633");
		bounds.setBox(point);
		features.setBoundedBy(bounds);
		FeatureMember member = new FeatureMember();
		Attributes attr = new Attributes();
		attr.setDescription("desc");
		attr.setId("id");
		attr.setName("name");
		attr.setPid("pid");
		attr.setType("bilder");
		Point point2 = new Point("0.795,69.39800000000001", "EPSG:32633");
		attr.setPoint(point2);
		member.setAttributes(attr);
		features.addMember(member);

		String xml = xstream.toXML(features);
		assertNotNull(xml);
	}

	private String exampleOfValidGML() {
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version=\"1.0\" encoding=\"utf-8\" ?> " + "<FeatureCollection " + "	xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " + "	xmlns:gml=\"http://www.opengis.net/gml\"> " + "	<gml:boundedBy>" + "		<gml:Box srsName='EPSG:32633'>"
				+ "			<gml:coordinates>-9.205,10.795,59.39800000000001,79.39800000000001</gml:coordinates>" + "		</gml:Box>" + "	</gml:boundedBy>" + "	<gml:featureMember>" + "		<xsi:attributes>" + "			<xsi:name>6900799</xsi:name>" + "			<xsi:pid>FE8241BD120AE6ECC296FD89AF379DF4</xsi:pid>"
				+ "			<xsi:description>74</xsi:description>" + "			<xsi:time>23.05.2012</xsi:time>" + "			<xsi:id>D261E1CF10EB823B2A9585A2EB951D8E</xsi:id>" + "			<gml:Point srsName='EPSG:4326'>" + "				<gml:coordinates>0.795,69.39800000000001</gml:coordinates>" + "			</gml:Point>"
				+ "		</xsi:attributes>" + "	</gml:featureMember>" + "</FeatureCollection>");
		return sb.toString();
	}
}

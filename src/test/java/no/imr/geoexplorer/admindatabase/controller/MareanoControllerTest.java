package no.imr.geoexplorer.admindatabase.controller;

import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.Map;

import no.imr.geoexplorer.admindatabase.gml.XstreamConfig;
import no.imr.geoexplorer.admindatabase.gml.pojo.Attributes;
import no.imr.geoexplorer.admindatabase.gml.pojo.BoundedBy;
import no.imr.geoexplorer.admindatabase.gml.pojo.FeatureCollection;
import no.imr.geoexplorer.admindatabase.gml.pojo.FeatureMember;
import no.imr.geoexplorer.admindatabase.gml.pojo.Point;
import no.imr.geoexplorer.admindatabase.jsp.pojo.HovedtemaVisning;
import no.imr.geoexplorer.admindatabase.jsp.pojo.LegendsInfo;
import no.imr.geoexplorer.admindatabase.jsp.pojo.SpesialpunktStatus;
import no.imr.geoexplorer.admindatabase.mybatis.pojo.Legend;
import no.imr.geoexplorer.admindatabase.mybatis.pojo.Spesialpunkt;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.servlet.ModelAndView;

/**
 * 
 * @author endrem
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:springmvc-servlet.xml"})
public class MareanoControllerTest {

	@Autowired(required = true)
	private MareanoController mareanoController;

	@Autowired(required = true)
	private XstreamConfig xstream;

	private String kartlag = "MAREANO-bilder";
	private String kartbilde = "Satellittbilder";
	private long kartlagId = 259;

	@SuppressWarnings("unchecked")
	@Test
	public void getHovedtemaer() {
		ModelAndView mav = mareanoController.getMareanoTest(new MockHttpServletResponse());
		Map<String, Object> mavMap = mav.getModel();
		List<HovedtemaVisning> hovedtemaer = (List<HovedtemaVisning>) mavMap.get("hovedtemaer");
		assertNotNull(hovedtemaer);
	}

	@Test
	public void getSpesialpunktForKartlagOgKartbilde() {
		List<Spesialpunkt> punkter = mareanoController.getSpesialpunkt(259l);
		assertNotNull(punkter);
		//assertTrue(punkter.size() > 0);
	}

	@Test
	public void getSpesialpunkt() throws Exception {
		String extent = "-524140,7672817,2701088,8506881";
		mareanoController.getSpesialpunktAsGML(extent, kartlagId+"", new MockHttpServletRequest(), new MockHttpServletResponse());
	}

	@Test
	public void spesialPunktPojosToGMLPojos() {
		List<Spesialpunkt> spesialpunkt = mareanoController.getSpesialpunkt(kartlagId);
		String extent = "-524140,7672817,2701088,8506881";
		FeatureCollection features = mareanoController.toGMLPojos(spesialpunkt, extent);
		String xml = xstream.toXML(features);
		assertNotNull(xml);
	}

	@Test
	public void testWriteGmlToFile() throws Exception {
		MockHttpServletRequest req = new MockHttpServletRequest();
		String xml = dummyGML("extent");
		mareanoController.writeGmlToFile(xml, req);
	}
	
	@Test
	public void getLegendTest() {
		mareanoController.getLegend("287", "norsk");
	}
	
	@Test
	public void getLegendsInfoTest() {
		SpesialpunktStatus status = mareanoController.getLegendsInfo("287", "norsk");
		for ( LegendsInfo aLegend : status.getLegends() ) {
			System.out.println("Url:"+aLegend.getUrl()+" \t text:"+aLegend.getText());
		}
	}
	
	@Test
	public void getKartlagInfoTest() {
		mareanoController.getKartlagInfo("287", new SpesialpunktStatus(), "norsk");;
	}
	
	@Test 
	public void getMeareanoHeading() {
		String html = mareanoController.getMareanoHeading("");
		System.out.println(html);
	}

	private String dummyGML(String boundingBox) {
		FeatureCollection features = new FeatureCollection();
		BoundedBy bounds = new BoundedBy();
		Point point = new Point(boundingBox, "EPSG:32633");
		bounds.setBox(point);
		features.setBoundedBy(bounds);
		FeatureMember member = new FeatureMember();
		Attributes attr = new Attributes();
		attr.setDescription("desc");
		attr.setId("id");
		attr.setName("name");
		attr.setPid("pid");
		attr.setType("bilder");
		Point point2 = new Point("729673,7897450", "EPSG:32633");
		attr.setPoint(point2);
		member.setAttributes(attr);
		features.addMember(member);

		return xstream.toXML(features);
	}
}

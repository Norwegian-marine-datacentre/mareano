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
	public void getHovedtemaer() throws Exception{
		ModelAndView mav = mareanoController.getMareanoTest(new MockHttpServletResponse());
		Map<String, Object> mavMap = mav.getModel();
		List<HovedtemaVisning> hovedtemaer = (List<HovedtemaVisning>) mavMap.get("hovedtemaer");
		assertNotNull(hovedtemaer);
	}
	
	@Test 
	public void getMeareanoHeading() {
		String html = mareanoController.getMareanoHeading("");
		System.out.println(html);
	}
	
	@Test
	public void getListOrganizedToBrowser() throws Exception {
		mareanoController.listOrganizedToBrowser("en");
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

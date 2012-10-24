package no.imr.fishexchange.atlas.controller;

import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 
 * @author endrem
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:springmvc-servlet.xml"})
public class CreateSLDControllerTest {

	@Autowired
	private CreateSLDController createSLDController;
	
	@Test
	public void testCreateSLD() throws Exception {
		HttpServletResponse resp = new MockHttpServletResponse();
		createSLDController.createsld(
				"FishExChange", 
				"Cod_survey_trawl_ecosystem_20-24cm", 
				"Y2003", "W", "arealvisning", resp );
		System.out.println(resp);
		
	}
	
	@Test
	public void testCreateSLD2() throws Exception {
		HttpServletResponse resp = new MockHttpServletResponse();
		createSLDController.createsld(
				"FishExChange", 
				"Herring_survey_trawl_autumn_0-group", 
				"Y2002", "W", "arealvisning", resp );
		System.out.println(resp);
		
	}	
	
}

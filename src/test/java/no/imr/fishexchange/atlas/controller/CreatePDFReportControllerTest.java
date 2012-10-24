package no.imr.fishexchange.atlas.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 
 * @author endrem
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:springmvc-servlet.xml"})
public class CreatePDFReportControllerTest {

	@Autowired( required = true )
	private CreatePDFReportController createPDFReportController;
	
	@Test
	public void getGrids() throws Exception {
		MockHttpServletRequest req = new MockHttpServletRequest();
		MockHttpServletResponse resp = new MockHttpServletResponse();
		req.setParameter("bbox", "1701951.6057143,-669804.26285714,2625112.4742857,253356.60571429");
		req.setParameter("sld", "http://talos.nodc.no:8080/geodata/spring/getsld.html?file=cod_25-29cm_y2003_depthW.sld");
		req.setParameter("srs", "EPSG32633");
		req.setParameter("layer", "test:pointvalue");
		req.setParameter("layerson", "");
		req.setParameter("width", "256");
		req.setParameter("height", "256");
		
		DefaultResourceLoader resourceLoader = new DefaultResourceLoader( createPDFReportController.getClass().getClassLoader() );		
		MockServletContext servletContext = new MockServletContext( resourceLoader );
		MockHttpSession session = new MockHttpSession(servletContext);
		req.setSession(session);
		createPDFReportController.createpdfreport( req, resp );
	}
}

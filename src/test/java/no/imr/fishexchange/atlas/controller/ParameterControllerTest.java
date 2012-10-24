package no.imr.fishexchange.atlas.controller;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import no.imr.fishexchange.atlas.controller.ParameterController;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.servlet.ModelAndView;

/**
 * 
 * @author endrem
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:springmvc-servlet.xml"})
public class ParameterControllerTest {

	@Autowired( required = true )
	private ParameterController parameterController;
	
	@Test
	public void getGrids() throws Exception {
		MockHttpServletRequest req = new MockHttpServletRequest();
		req.setParameter("grid", "gridname");
		req.setParameter("grid_value", "FishExChange");
		ModelAndView mav = parameterController.parameter( req );
		Map<String, Object> mavMap = mav.getModel();
		assertsForGridAndSpecies( mavMap );
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void getSpeciesSubgroupForCod() throws Exception {
		MockHttpServletRequest req = new MockHttpServletRequest();
		req.setParameter("grid", "gridname");
		req.setParameter("grid_value", "FishExChange");
		req.setParameter("dataset", "datasetname");
		req.setParameter("dataset_value", "cod");
		ModelAndView mav = parameterController.parameter( req );
		
		Map<String, Object> mavMap = mav.getModel();
		assertsForGridAndSpecies( mavMap );
		
		String datasets_value = (String) mavMap.get( "dataset_value_selected" );
		assertEquals( "cod", datasets_value );
		
		List<String> parameters = (List<String>) mavMap.get( "parameters" );
		for ( String speciesSubgroup : parameters ) {
//			System.out.println(speciesSubgroup);
			assertTrue( speciesSubgroup.toLowerCase().contains( "cod" ) );
		}
	}	
	
	@SuppressWarnings(value="unchecked")
	private void assertsForGridAndSpecies( Map<String, Object> mavMap ) {
		List<String> grids = (List<String>) mavMap.get("grids");
		assertNotNull( grids );
		
		String grid_value_selected = (String) mavMap.get("grid_value_selected");
		assertEquals( "FishExChange", grid_value_selected );
		
		List<String> datasets = (List<String>) mavMap.get("datasets");
		for ( String aSpecies : datasets ) {
//			System.out.println(aSpecies);
			assertNotNull( aSpecies );
		}
	}
}

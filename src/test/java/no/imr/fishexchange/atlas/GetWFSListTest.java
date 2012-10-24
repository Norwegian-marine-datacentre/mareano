package no.imr.fishexchange.atlas;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.imr.fishexchange.atlas.controller.ParameterController;
import no.imr.fishexchange.atlas.pojo.FishExchangePojo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertTrue;

/**
 * 
 * @author endrem
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:springmvc-servlet.xml"})
public class GetWFSListTest {

	@Autowired( required = true )
	private GetWFSList wfsList = new GetWFSList();
	
	@Test
	public void getMaxMinLegendFromWFSForTemperatureStandardDepths() throws Exception {
		
		FishExchangePojo queryObj = new FishExchangePojo(
				"FishExChange", 
				"Temperature_Atlas_InterpolatedObservations_StandardDepths_BarentsSea", 
				"P0035.00:00045.00", 
				"M197008" );
        Map<String, String> input = queryObj.createQueryMap();        
		List<String> returnedFromDb = 
			wfsList.getWFSList("maxval", input, ParameterController.BASE_URL_REQUEST + "typeName=test:temperature_maxmin" );
//		System.out.println("maxval:"+returnedFromDb.get(0));
		assertTrue( returnedFromDb.size() > 0 );
		returnedFromDb = 
			wfsList.getWFSList("minval", input, ParameterController.BASE_URL_REQUEST + "typeName=test:temperature_maxmin" );
//		System.out.println("minval:"+returnedFromDb.get(0));
		assertTrue( returnedFromDb.size() > 0 );
	}
	
	@Test
	public void getMaxMinLegendFromWFSForCod() throws Exception {
		
		FishExchangePojo queryObj = new FishExchangePojo(
				"FishExChange", 
				"Cod_survey_trawl_winter_20-24cm", 
				"W", 
				"Y2003" );
        Map<String, String> input = queryObj.createQueryMap();  

		List<String> returnv = 
			wfsList.getWFSList("maxval", input, ParameterController.BASE_URL_REQUEST + "typeName=test:temperature_maxmin" );
		assertTrue( returnv.size() > 0 );
		System.out.println("maxval:"+returnv.get(0));
		returnv = 
			wfsList.getWFSList("minval", input, ParameterController.BASE_URL_REQUEST + "typeName=test:temperature_maxmin" );
		System.out.println("minval:"+returnv.get(0));		
		assertTrue( returnv.size() > 0 );
	}		
	
	@Test
	public void getPeriodnameFromWFSForCod() throws Exception {
		
        Map<String, String> input = new HashMap<String, String>();
        input.put( "parametername", "Cod_survey_trawl_winter_0-4cm" );
        input.put( "gridname", "FishExChange" );

		List<String> returnv = 
			wfsList.getWFSList("periodname", input, ParameterController.BASE_URL_REQUEST + "typeName=test:grid_parameter_time" );
		assertTrue( returnv.size() > 0 );
	}
}

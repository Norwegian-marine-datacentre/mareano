package no.imr.geoexplorer.admindatabase.controller;

import no.imr.geoexplorer.admindatabase.jsp.pojo.SpesialpunktStatus;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * 
 * @author endrem
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:test-context-dont-check-inn.xml"})
public class JsonDataControllerTest {
    
	@Autowired( required = true )
	private JsonDataController jsonDatacontroller;
	
	@Test
	public void getLegendTest() throws Exception {
		
		MockHttpServletRequest req = new MockHttpServletRequest();
		SpesialpunktStatus status2 = 
				jsonDatacontroller.getLegendAndSpesialpunkt( "-1391440,6826964,2291440,8273036", "37", "norsk", req );
		System.out.println( status2 );
	}
	
	@Test
	public void getLengendAndSpesialpunktTest() throws Exception {
		
		MockHttpServletRequest req = new MockHttpServletRequest();
		SpesialpunktStatus status2 = 
				jsonDatacontroller.getLegendAndSpesialpunkt( "-1391440,6826964,2291440,8273036", "37", "norsk", req );
		
		SpesialpunktStatus status4 = 
				jsonDatacontroller.getLegendAndSpesialpunkt( "-1585062,6994860,2485062,8105140", "244", "norsk", req );
		System.out.println( status4 );
	}
}

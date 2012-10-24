package no.imr.fishexchange.atlas;

import java.util.List;

import no.imr.fishexchange.atlas.pojo.FishExchangePojo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 
 * @author endrem
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:springmvc-servlet.xml"})
public class SLDFileTest {
	
	@Autowired( required = true )
	private SLDFile sldfile;
	
	@Test
	public void makeValueRangesTest() {
		List<List<Float>> returned = sldfile.makeValueRanges(-1.9f, 9.4f, 10);
		for ( List<Float> listSteps : returned ) {
			System.out.println("");
			for ( Float range : listSteps ) {
				System.out.print( range+ ", ");
			}
		}
	}
	
	@Test
	public void getColorRuleTest() {
		FishExchangePojo pojo = new FishExchangePojo("FishExchange", "parameter", "depth", "time");
		List<String> colors = HSVtoRGB.makeHexColorScale(0.3f, 10);
		System.out.println("color rule 0:"+colors.get(0));
		String returned = sldfile.getColorRule("-1.9", "9.4", colors.get(0), pojo );
		System.out.println(returned);
	}
	
	@Test
	public void getColorRulesTest() {
		FishExchangePojo pojo = new FishExchangePojo("FishExchange", "parameter", "depth", "time");
		pojo.setMaxLegend(20f);
		pojo.setMinLegend(0f);
		List<String> colors = HSVtoRGB.makeHexColorScale(0.3f, 10);
		System.out.println("color rule 0:"+colors.get(0));
		String returned = sldfile.getColorRules(0.3f, 10, pojo );
		System.out.println(returned);
	}
	
	@Test
	public void getZeroRuleAreaDisplayTest() {
		FishExchangePojo pojo = new FishExchangePojo("FishExchange", "parameter", "depth", "time");
		String returned = sldfile.getZeroRuleAreaDisplay(pojo);
		System.out.println(returned);
	}
	
	@Test
	public void getStepSizeRulePointTest() {
		FishExchangePojo pojo = new FishExchangePojo("FishExchange", "parameter", "depth", "time");
		String returned = sldfile.getStepSizeRulePoint("0", "10", "1", pojo);
		System.out.println(returned);
	}
	
	@Test
	public void getStepSizeRulePointsTest() {
		FishExchangePojo pojo = new FishExchangePojo("FishExchange", "parameter", "depth", "time");
		pojo.setMinLegend(0f);
		pojo.setMaxLegend(20f);
		String returned = sldfile.getSteppedSizeRulePoints(3, 2, 10, pojo);
		System.out.println(returned);
	}
	
	@Test
	public void getSizeRulePointDisplayTest() {
		FishExchangePojo pojo = new FishExchangePojo("FishExchange", "parameter", "depth", "time");
		String returned = sldfile.getSizeRulePointDisplay(pojo);
		System.out.println(returned);
	}
}

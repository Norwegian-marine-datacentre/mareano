package no.imr.geoexplorer.admindatabase.mybatis.pojo;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 
 * @author endrem
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:springmvc-servlet.xml"})
public class KartlagEnNoTest {

	@Autowired( required = true )
	public SqlSessionTemplate template;
	
	@SuppressWarnings("unchecked")
	@Test
	public void getKartlagEn() {
		List<KartlagEnNo> kartlagText = (List<KartlagEnNo>) template.selectList("getKartlagEn", 328l);
		System.out.println( "kartlagtext:" + kartlagText.get(0).getTitle() );
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void getKartbilderEn() {
		List<KartBilderEnNo> kartbilderText = (List<KartBilderEnNo>) template.selectList("getKartbilderEn", 108l);
		System.out.println( "bilder text:" + kartbilderText.get(0).getTitle() );
	}	
	
	@SuppressWarnings("unchecked")
	@Test
	public void getHovedtemaEn() {
		List<HovedtemaEnNo> hovedtemaText = (List<HovedtemaEnNo>) template.selectList("getHovedtemaEn", 6l);
		System.out.println( "bilder text:" + hovedtemaText.get(0).getAlternateTitle() );
	}		
}

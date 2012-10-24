package no.imr.geoexplorer.admindatabase.mybatis.pojo;

import java.util.ArrayList;
import java.util.List;

import no.imr.geoexplorer.admindatabase.jsp.pojo.HovedtemaVisning;
import no.imr.geoexplorer.admindatabase.jsp.pojo.KartbilderVisning;
import no.imr.geoexplorer.admindatabase.jsp.pojo.KartlagVisning;
import no.imr.geoexplorer.admindatabase.mybatis.pojo.Hovedtema;
import no.imr.geoexplorer.admindatabase.mybatis.pojo.Kartbilder;
import no.imr.geoexplorer.admindatabase.mybatis.pojo.Kartlag;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertNotNull;

/**
 * 
 * @author endrem
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:springmvc-servlet.xml"})
public class HovedtemaDaoTest {
	
	@Autowired( required = true )
	public SqlSessionTemplate template;
	
	@SuppressWarnings("unchecked")
	@Test
	public void getHovedtema() {
		List<Hovedtema> hList = (List<Hovedtema>) template.selectList("getHovedtemaer");
		assertNotNull( hList );
	}

	@SuppressWarnings("unchecked")
	@Test
	public void listAllKartbilderOfHovedtema() {
		List<Hovedtema> hovedtemaer = (List<Hovedtema>) template.selectList("getHovedtemaer");
		for ( Hovedtema hovedtema : hovedtemaer ) {
			assertNotNull( hovedtema.getGenericTitle() );
			for ( Kartbilder kartbilde : hovedtema.getKartbilder() ) {
				assertNotNull( kartbilde.getGenericTitle() );
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void listOrganizedToBrowser() {
		List<Hovedtema> hovedtemaer = (List<Hovedtema>) template.selectList("getHovedtemaer");
		List<HovedtemaVisning> hovedtemaVisninger = new ArrayList<HovedtemaVisning>();
		for ( Hovedtema hovedtema : hovedtemaer ) {
			HovedtemaVisning hovedtemaVisning = new HovedtemaVisning();
			hovedtemaVisning.setHovedtema( hovedtema.getGenericTitle() );

			for ( Kartbilder kartbilde : hovedtema.getKartbilder() ) {
				KartbilderVisning kartbilderVisining = new KartbilderVisning(); 
				kartbilderVisining.setGruppe( kartbilde.getGenericTitle() );
				
				List<Kartlag> kartlagene  = (List<Kartlag>) template.selectList("getKartlagFromKartbilder", kartbilde.getKartbilderId());
				for ( Kartlag kartlag  : kartlagene ) {
					assertNotNull( kartlag.getGenericTitle() );
					KartlagVisning kart = new KartlagVisning();
					kart.setLayers( kartlag.getLayers() );
					kart.setTitle(kartlag.getGenericTitle() );
					kart.setUrl( kartlag.getKarttjeneste().getUrl() );
					kartbilderVisining.addKart(kart);
				}
				hovedtemaVisning.addBilder( kartbilderVisining );
			}
			if ( hovedtemaVisning.getBilder().size() > 0 )
				hovedtemaVisninger.add( hovedtemaVisning );
		}
	}
}

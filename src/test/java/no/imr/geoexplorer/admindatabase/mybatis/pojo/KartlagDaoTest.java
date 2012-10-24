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
public class KartlagDaoTest {

	@Autowired( required = true )
	public SqlSessionTemplate template;
	
	@SuppressWarnings("unchecked")
	@Test
	public void getKartlagOgKartbilder() {
		List<Kartlag> kartlagene = (List<Kartlag>) template.selectList("getKartlag");
		for ( Kartlag kartlag : kartlagene ){
			assertNotNull( kartlag );
			assertNotNull(kartlag.getKartbilder());
			for ( Kartbilder kartbilde : kartlag.getKartbilder() ) {
				assertNotNull( kartbilde.getGenericTitle() );
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void getKartlagOgKarttjenester() {
		List<Kartlag> kartlagene = (List<Kartlag>) template.selectList("getKartlag");
		for ( Kartlag kartlag : kartlagene ){
			assertNotNull( kartlag );
			assertNotNull(kartlag.getKarttjeneste());
		}
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void getKartbilderFraEtKartbilde() {
		List<Kartlag> kartlagene = (List<Kartlag>) template.selectList("getKartlag");
		assertTrue( kartlagene.size() > 0);
		
		for ( Kartlag etKartlag : kartlagene ) {
			List<Kartbilder> kartbilder = 
				(List<Kartbilder>) template.selectList("getKartbilderFromKartlag", etKartlag.getKartlagId() );
			for ( Kartbilder k : kartbilder ) {
				assertNotNull( k.getGenericTitle() ); 
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void getKartlagIdFromKartlagNavn() {
		List<Kartlag> kartlagene = (List<Kartlag>) template.selectList("getKartlagId", "MAREANO-bilder");
		assertNotNull(kartlagene);
		assertTrue( kartlagene.size() == 1 );
		assertTrue( kartlagene.get(0).getKartlagId() == 84 );
	}
}

package no.imr.geoexplorer.admindatabase.mybatis.pojo;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import no.imr.geoexplorer.admindatabase.mybatis.pojo.Kartlag;
import no.imr.geoexplorer.admindatabase.mybatis.pojo.Karttjenester;

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
public class KarttjenesterDaoTest {

	@Autowired( required = true )
	private SqlSessionTemplate template;
	
	@SuppressWarnings("unchecked")
	@Test
	public void getKarttjeneste() {
		List<Karttjenester> karttjenester = (List<Karttjenester>) template.selectList("getKarttjenester");
		assertNotNull(karttjenester);
		for ( Karttjenester karttjeneste : karttjenester ) {
			assertNotNull( karttjeneste.getKartlag() );
			assertNotNull( karttjeneste.getGenericTitle() );
			for ( Kartlag kartlag : karttjeneste.getKartlag() ) {
				assertNotNull( kartlag );
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	@Test	
	public void removeParamsExceptServiceNameFromURL() {
		List<Karttjenester> karttjenester = (List<Karttjenester>) template.selectList("getKarttjenester");
		assertNotNull(karttjenester);
		for ( Karttjenester karttjeneste : karttjenester ) {
			if ( karttjeneste.getKarttjenesterId() == 40 ) {
				assertNotNull( karttjeneste.getUrl() );
				List<Kartlag> k = karttjeneste.getKartlag();
				assertNotNull( k );
			}
		}
	}
}

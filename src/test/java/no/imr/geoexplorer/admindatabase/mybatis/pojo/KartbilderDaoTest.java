package no.imr.geoexplorer.admindatabase.mybatis.pojo;

import java.util.List;

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
public class KartbilderDaoTest {
	
	@Autowired( required = true )
	public SqlSessionTemplate template;
	
	@SuppressWarnings("unchecked")
	@Test
	public void getKartbilderOgHovedtema() {
		List<Kartbilder> kartbilder = (List<Kartbilder>) template.selectList("getKartbilder");
		for ( Kartbilder kartbilde : kartbilder ){
			assertNotNull( kartbilde.getGenericTitle() );
			assertNotNull( kartbilde.getHovedtema().getGenericTitle() );
		}
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void getKartbilderOgKartlag() {
		List<Kartbilder> kartbilder = (List<Kartbilder>) template.selectList("getKartbilder");
		for ( Kartbilder kartbilde : kartbilder ){
			assertNotNull(kartbilde.getGenericTitle());
			assertNotNull(kartbilde.getKartlag());
			for ( Kartlag kartlag : kartbilde.getKartlag() ) {
				assertNotNull( kartlag.getGenericTitle() );
			}
		}
	}
}

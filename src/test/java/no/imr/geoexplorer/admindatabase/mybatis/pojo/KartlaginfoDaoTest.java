package no.imr.geoexplorer.admindatabase.mybatis.pojo;

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
@ContextConfiguration(locations = {"classpath:springmvc-servlet.xml"})
public class KartlaginfoDaoTest {

	@Autowired(required = true)
	private SqlSessionTemplate template;

	@Test
	public void getKartlagInfo() {
		KartlagInfo kartlagInfo = (KartlagInfo) template.selectOne("getKartlagInfoNO", 1l);
		System.out.println("id:"+kartlagInfo.getKartlagId());
		System.out.println("title:"+kartlagInfo.getTitle());
		System.out.println("alt title:"+kartlagInfo.getAlternateTitle());
		System.out.println("abstract:"+kartlagInfo.getAbstracts());
		
		kartlagInfo = (KartlagInfo) template.selectOne("getKartlagInfoEN", 1l);
		System.out.println("id:"+kartlagInfo.getKartlagId());
		System.out.println("title:"+kartlagInfo.getTitle());
		System.out.println("alt title:"+kartlagInfo.getAlternateTitle());
		System.out.println("abstract:"+kartlagInfo.getAbstracts());
		
	}
}

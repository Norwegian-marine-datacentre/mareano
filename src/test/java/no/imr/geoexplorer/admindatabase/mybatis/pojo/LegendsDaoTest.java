package no.imr.geoexplorer.admindatabase.mybatis.pojo;

import java.util.List;

import no.imr.geoexplorer.admindatabase.dao.MareanoAdminDbDao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * 
 * @author endrem
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:springmvc-servlet.xml"})
public class LegendsDaoTest {

	@Autowired(required = true)
	private SqlSessionTemplate template;
	
	@Autowired(required=true)
	private MareanoAdminDbDao dao;

	@Test
	public void getLegends() {
		List<Legend> legends = (List<Legend>) template.selectList("getLegends");
		assertNotNull(legends);
	}
	
	@Test
	public void getALegend() {
		List<Legend> legends = (List<Legend>) template.selectList("getLegend", 97);
		assertTrue(legends.size() >= 1);
		assertTrue(legends.get(0).getUrl().equals("http://www.ngu.no/gd_images/symb/mareano/GT_haug_ngu.png"));
		
	}
	
	@Test
	public void getLegendBildeForMareanoBilder() {
//		List<Legend> legends = dao.getALegend("MAREANO-bilder", "");
		List<Legend> legends = dao.getALegend(259, "norsk");
		assertNotNull(legends);
		System.out.println(legends.size());
		System.out.println(legends.get(0).getGenericTitle()+" "+legends.get(0).getKartlagId());
		
		legends = dao.getALegend(259, "en");
		assertNotNull(legends);
		System.out.println(legends.size());
		System.out.println(legends.get(0).getGenericTitle()+" "+legends.get(0).getKartlagId());		
	}
}

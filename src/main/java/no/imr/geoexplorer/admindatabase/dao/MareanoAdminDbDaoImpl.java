package no.imr.geoexplorer.admindatabase.dao;

import java.util.List;

import no.imr.geoexplorer.admindatabase.mybatis.pojo.Hovedtema;
import no.imr.geoexplorer.admindatabase.mybatis.pojo.HovedtemaEnNo;
import no.imr.geoexplorer.admindatabase.mybatis.pojo.KartBilderEnNo;
import no.imr.geoexplorer.admindatabase.mybatis.pojo.Kartlag;
import no.imr.geoexplorer.admindatabase.mybatis.pojo.KartlagEnNo;
import no.imr.geoexplorer.admindatabase.mybatis.pojo.KartlagInfo;
import no.imr.geoexplorer.admindatabase.mybatis.pojo.Legend;
import no.imr.geoexplorer.admindatabase.mybatis.pojo.Spesialpunkt;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @see no.imr.geoexplorer.admindatabase.dao.MareanoAdminDbDao
 * @author endrem
 */
@Repository("MareanoAdminDbDao")
public class MareanoAdminDbDaoImpl implements MareanoAdminDbDao {

	@Autowired(required = true)
	private SqlSessionTemplate template;

	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Hovedtema> getHovedtemaer() {
		return (List<Hovedtema>) template.selectList("getHovedtemaer");
	}

	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Kartlag> getKartlagene(long kartbilderId) {
		return (List<Kartlag>) template.selectList("getKartlagFromKartbilder", kartbilderId);
	}
	
	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<KartlagEnNo> getKartlagEn(long kartbilderId) {
		return (List<KartlagEnNo>) template.selectList("getKartlagEn", kartbilderId);
	}
	
	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<KartlagEnNo> getKartlagNo(long kartbilderId) {
		return (List<KartlagEnNo>) template.selectList("getKartlagNo", kartbilderId);
	}

	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<KartBilderEnNo> getKartbilderEn(long kartbilderId) {
		return (List<KartBilderEnNo>) template.selectList("getKartbilderEn", kartbilderId);
	}
	
	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<KartBilderEnNo> getKartbilderNo(long kartbilderId) {
		return (List<KartBilderEnNo>) template.selectList("getKartbilderNo", kartbilderId);
	}
	
	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<HovedtemaEnNo> getHovedtemaEn(long hovedtemaId) {
		return (List<HovedtemaEnNo>) template.selectList("getHovedtemaEn", hovedtemaId);
	}
	
	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<HovedtemaEnNo> getHovedtemaNo(long hovedtemaId) {
		return (List<HovedtemaEnNo>) template.selectList("getHovedtemaNo", hovedtemaId);
	}

	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Spesialpunkt> getSpesialpunkt(long kartlagId) {
		return (List<Spesialpunkt>) template.selectList("getSpesialpunktFraKart", kartlagId);
	}
	
	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Legend> getALegend( long id, String language ) {
		List<Legend> legends = null;
		if ( language.equals("norsk") ) {
			legends = (List<Legend>) template.selectList("getLegendNo", id);
		} else {
			legends = (List<Legend>) template.selectList("getLegendEn", id);
		}
		return legends;
	}
	
	/** {@inheritDoc} */
	@Transactional(readOnly = true)
	public KartlagInfo getKartlagInfo( long kartlagId, String noOrEn ) {
		KartlagInfo kartlagInfo = null;
		if ( noOrEn.equals("norsk") ) {
			kartlagInfo= (KartlagInfo) template.selectOne("getKartlagInfoNO", kartlagId);
		} else {
			kartlagInfo= (KartlagInfo) template.selectOne("getKartlagInfoEN", kartlagId);
		}
		return kartlagInfo;
	}
}

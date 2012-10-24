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

/**
 * Data Access Object to access mareano admin database
 * 
 * @author endrem
 */
public interface MareanoAdminDbDao {

	/**
	 * Get all Hovedtema
	 * 
	 * @return List<Hovedtema> and the belonging kartbilder
	 */
	List<Hovedtema> getHovedtemaer();

	/**
	 * Given a kartbilderId return {@literal List<Kartlag>}
	 * 
	 * @param kartbilderId
	 * @return List<Kartlag> and the belonging KartTjeneste
	 */
	List<Kartlag> getKartlagene(long kartbilderId);
	
	List<KartlagEnNo> getKartlagEn(long kartlagId);
	
	List<KartlagEnNo> getKartlagNo(long kartlagId);
	
	List<KartBilderEnNo> getKartbilderEn(long kartbilderId);
	
	List<KartBilderEnNo> getKartbilderNo(long kartbilderId);
	
	List<HovedtemaEnNo> getHovedtemaEn(long hovedtemaId);
	
	List<HovedtemaEnNo> getHovedtemaNo(long hovedtemaId);

	/**
	 * Get the spesialpunkt for the given kartlagId
	 * 
	 * @param long kartlagId
	 * @return
	 */
	List<Spesialpunkt> getSpesialpunkt( long  kartlagId );
	
	List<Legend> getALegend( long kartlagId, String language );
	
	KartlagInfo getKartlagInfo( long kartlagId, String noOrEn );

}

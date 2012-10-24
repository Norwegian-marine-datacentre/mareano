package no.imr.fishexchange.atlas.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import no.imr.fishexchange.atlas.GetWFSList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author trondwe
 */
@Controller
public class ParameterController{
	
	private String grid;
	private String gridValue;
	private String dataset;
	private String datasetValue;
	private String parameter;
	private String parameterValue;
	
	private final static String GRID = "grid";
	private final static String GRID_VALUE = "grid_value";
	private final static String DATASET = "dataset";
	private final static String DATASET_VALUE = "dataset_value";
	private final static String PARAMETER = "parameter";
	private final static String PARAMETER_VALUE = "parameter_value";
	
	private final static String GRIDS = "grids";
	private final static String DATASETS = "datasets";
	private final static String PARAMETERS = "parameters";
	private final static String METADATA = "metadata";
	
	private final static String MAIN_URL = "http://maps.imr.no/geoserver/ows?";
    private final static String SERVICE = "service=WFS";
    private final static String VERSION = "version=1.0.0";
    private final static String REQUEST_TYPE = "request=GetFeature";
    private final static String TYPENAME = "typeName=test:grid_parameter_name";
    public final static String BASE_URL_REQUEST = MAIN_URL + SERVICE + "&" + VERSION + "&" + REQUEST_TYPE + "&";
    
    @Autowired( required = true )
    private GetWFSList gwfs  = null;

    /**
     * FILLS THE COMBOBOX FOR GRIDS:
     * @param request
     * @return
     * @throws Exception
     */
	@RequestMapping("/parameter")
    public ModelAndView parameter(HttpServletRequest request) throws Exception {
		
		grid = request.getParameter( GRID );
		gridValue = request.getParameter( GRID_VALUE );
		dataset = request.getParameter( DATASET );
		datasetValue = request.getParameter( DATASET_VALUE );
		parameter = request.getParameter( PARAMETER );
		parameterValue = request.getParameter( PARAMETER_VALUE );
						
        ModelAndView mav = new ModelAndView("parameters");
        String urlRequest = BASE_URL_REQUEST + TYPENAME; 

        List<String> grids = gwfs.getWFSList("gridname", null, urlRequest );
        mav.addObject(GRIDS, grids);

        List<String> speciesSubgroups = getSpeciesSubgroupFromWFSlist( urlRequest );
        addSpecies( mav, speciesSubgroups );
        addSpeciesSubgroups( mav, speciesSubgroups );
        periodDepthlayersAndMetadata( mav, urlRequest, request );
        return mav;
    }
	
	private void addSpecies( ModelAndView mav, List<String> speciesSubgroups ) throws Exception {
        if ( grid != null && gridValue != null ) {
            mav.addObject("grid_value_selected", gridValue );
            List<String> species = new ArrayList<String>();
            for(int i=0; i<speciesSubgroups.size(); i++) {
            	String speciesName = getWFSSpeciesName( speciesSubgroups, i );
                if(!species.contains( speciesName )){
                	species.add( speciesName );
                }
            }
            mav.addObject(DATASETS, species);
        }
	}
	
	private void addSpeciesSubgroups( ModelAndView mav, List<String> speciesSubgroups ) throws Exception {
        if ( dataset != null ) {
            mav.addObject("dataset_value_selected", datasetValue);
            List <String> thisSpeciesSubgroup = new ArrayList<String>();
            for(int i=0; i<speciesSubgroups.size(); i++) {
            	String speciesName = getWFSSpeciesName( speciesSubgroups, i );
                String speciesChoosen = datasetValue;
                if (speciesChoosen.equalsIgnoreCase( speciesName ) && !thisSpeciesSubgroup.contains( speciesName)) {
                	thisSpeciesSubgroup.add(speciesSubgroups.get(i));
                }
            }
            mav.addObject(PARAMETERS, thisSpeciesSubgroup);
        }
	}
	
	private List<String> getSpeciesSubgroupFromWFSlist( String urlRequest ) throws Exception {
        Map<String, String> input = new HashMap<String, String>();
        input.put( grid, gridValue );
        return gwfs.getWFSList( "parametername", input, urlRequest );
	}
	
	private String getWFSSpeciesName( List<String> params, int i ) {
    	String WFSSpeciesName = params.get( i );
    	String[] theSpeciesNameWithAllSylables = WFSSpeciesName.split( "_" );
    	String theFirstNameOfSpecie = theSpeciesNameWithAllSylables[0];
    	return theFirstNameOfSpecie.toLowerCase();
	}
	
	private void periodDepthlayersAndMetadata( ModelAndView mav, String urlRequest, HttpServletRequest request ) throws Exception {
        if ( parameter != null ) {
            mav.addObject( "parameter_value_selected", parameterValue );
            Map<String, String> input = new HashMap<String, String>();
            input.put( parameter, parameterValue );
            input.put( grid, gridValue );

            List<String> descriptions = gwfs.getWFSList("description", input, urlRequest);
            if ( descriptions.size() > 0 ) {
            	mav.addObject(METADATA,descriptions.get(0));
            	request.getSession().setAttribute(METADATA, descriptions.get(0));
            } else {
            	request.getSession().setAttribute( METADATA, "Fant ingen metadata" );
            }
            
            urlRequest = BASE_URL_REQUEST + "typeName=test:grid_parameter_time";
            List<String> params = gwfs.getWFSList( "periodname", input, urlRequest );
            mav.addObject("periods", params);

            urlRequest = BASE_URL_REQUEST + "typeName=test:grid_parameter_depth";
            params = gwfs.getWFSList( "layername", input, urlRequest );
            mav.addObject("depthlayers", params);
        }
	}

	@RequestMapping("/parameterdummy")
    public ModelAndView parameterdummy(HttpServletRequest request) throws Exception {
		gridValue = request.getParameter("grid_value");
		datasetValue = request.getParameter("dataset_value");
		parameterValue = request.getParameter("parameter_value");
						
        ModelAndView mav = new ModelAndView("parameterdummy");

        mav.addObject("grid_value_selected", gridValue );

        List<String> grids = new ArrayList<String>();
        grids.add( "FishExChange" );
        mav.addObject(GRIDS, grids);

        mav.addObject("dataset_value_selected", datasetValue);
        List<String> species = new ArrayList<String>();
        species.add("cod");
        species.add("haddock");
        species.add("capelin");
        mav.addObject(DATASETS, species);

        mav.addObject( "parameter_value_selected", parameterValue );
        List<String> speciesSubgroup = new ArrayList<String>();
        if ( datasetValue != null ) {
	        if ( datasetValue.contains("cod")) {
	        	speciesSubgroup.add("Cod_survey_trawl_ecosystem_total2009_Q3");
	        	speciesSubgroup.add("Cod_survey_trawl_ecosystem_total2010_Q3");
	        } else if ( datasetValue.contains("haddock")) {
	        	speciesSubgroup.add("Haddock_survey_trawl_ecosystem_total2009_Q3");
	        	speciesSubgroup.add("Haddock_survey_trawl_ecosystem_total2010_Q3");
	        } else if ( datasetValue.contains("capelin")) {
	        	speciesSubgroup.add("Capelin_survey_acoustic_autumn_1-5yr_2009");
	        	speciesSubgroup.add("Capelin_survey_acoustic_autumn_1-5yr_2010");
	        }
        }
        mav.addObject(PARAMETERS, speciesSubgroup);
        String metadata = "";
        if ( parameterValue != null ) {
	        if ( parameterValue.contains("Cod") || parameterValue.contains("Haddock") ) {
	        	metadata ="The joint ecosystem survey has been conducted annually in the Barents Sea since 2003 by the Institute of Marine Research, (IMR), Norway, and the Polar Research Institute of Marine Fisheries and Oceanography (PINRO), Russia. The data covers the period 2003- 2009 (ongoing), and are sampled over a period of two months each year between July and October. Totally 5 vessels participates in the survey, three Norwegians and two Russians. The Norwegian vessels survey the western, north- western and the central parts of the Barents Sea, while the Russian vessels survey the eastern, north- eastern and central parts of the Barents Sea with degrees of overlap between Russian and Norwegian vessels varying from year to year. The survey encompasses various surveys that previously have been carried out jointly or at national basis. The survey design is a combination of different designs inherited from the various surveys carried out previously as special investigations, but coordinated to a certain degree. The demersal fish stocks are covered by a bottom trawl survey, Campelen 2283-02, with a predetermined station grid. Since the ecosystem survey is based on predefined stations, cod and haddock is sampled whenever the species is represented in the trawl catch, in addition trawling is carried out whenever the echo sounder recordings changes their characteristics and/or the need for biological data made it necessary. The data represents number of fish per square nautical mile from bottom trawl catches within each FishExChange square. In cases of several sampling stations within the same square the values are averaged. The following species are included: Northeast Arctic cod (Gadus morhua L.), and Northeast Arctic haddock (Melanogrammus aeglefinus L.). The parameter name is: Species_survey_trawl_ecosystem_total = COMBINED"; 
	        } else if (parameterValue.contains("Capelin") ) {
	        	metadata ="The Norwegian-USSR acoustic survey of the Barents Sea has been conducted annually since 1972 between August and October. In 1975 the Polar Research Institute of Marine Fisheries and Oceanography (PINRO), Russia, participated for the first time, and since then the survey has been carried out as co-operative projects between IMR and PINRO. In 2003 the survey became a part of the joint Norwegian-Russian ecosystem survey. The number of vessels participating in the survey has varied from three to seven, this has affected the amount of effort spent on the survey.The main aim of the survey is to estimate the sizes of pelagic fish stocks in the Barents Sea, and study their biology and geographical distribution. The survey targets mainly capelin (Mallotus villosus) and polar cod (Boreogadus saida). One year old capelin are heavily underestimated prior to 1980. The survey design from 1972- 1999 was based on acoustic investigations complemented with trawl stations. The vessels started working in the eastern part of the Barents Sea, running north- south courses, and covered the area with transects along every second degree longitude. The main distribution area of capelin was covered with transects one degree longitude apart. The North- and southward extensions of the course lines were adjusted according to the distribution of capelin. From 2000 the survey grid was changed to run east-west transects either 15 or 20 nautical miles apart, fluctuating whether it started in the south or in the north. Prior to 1991 all vessels participating were operating digital echo-integrators. The data represents number of fish (N) per square nautical mile within each FishExChange square, written as N*10 Exp-7. In cases of several sampling stations within the same square, the values are averaged. The following species are included: Capelin, Mallotus villosus, and Polar cod, Boreogadus saida. The parameter name is: Species_survey_acoustic_autumn_age.";
	        }
	        mav.addObject(METADATA, metadata );
        }
        
        return mav;		
		
	}
}

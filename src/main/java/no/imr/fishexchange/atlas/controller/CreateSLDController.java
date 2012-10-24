package no.imr.fishexchange.atlas.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLStreamException;

import no.imr.fishexchange.atlas.GetWFSList;
import no.imr.fishexchange.atlas.SLDFile;
import no.imr.fishexchange.atlas.StAXreader.StAXreaderException;
import no.imr.fishexchange.atlas.pojo.FishExchangePojo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author trondwe
 */
@Controller
public class CreateSLDController {

	private final static String PUNKTVISNING = "punktvisning";
	
    @Autowired( required = true )
    private GetWFSList gwfs;
    
    @Autowired( required = true )
    private SLDFile sldFile;
	
	@RequestMapping("/createsld")
    public void createsld(
    		@RequestParam("grid") String grid,
    		@RequestParam("parameter") String parameter,
    		@RequestParam("time") String time,
    		@RequestParam("depth") String depth,
    		@RequestParam("displaytype") String displaytype,  
            HttpServletResponse resp) throws Exception {

        boolean areadisplay = isAreadisplay( displaytype );
        
        
        FishExchangePojo queryFishEx = new FishExchangePojo( grid, parameter, depth, time );
        getMaxMinLegendValues( queryFishEx );
        
        String sld = sldFile.getSLDFile( queryFishEx, areadisplay);
        String filename = "sld_".concat(String.valueOf(Math.random() * 10000 % 1000)).concat(".sld");
        writeSldFileToTmpdir( sld, filename );
        writeFilenameToResponse( resp, filename );
    }

	private void getMaxMinLegendValues( FishExchangePojo queryFishEx ) throws IOException, StAXreaderException, XMLStreamException {
        Map<String, String> input = queryFishEx.createQueryMap(); 
        String urlRequest = ParameterController.BASE_URL_REQUEST + "typeName=test:temperature_maxmin";
        List<String> maxvals = gwfs.getWFSList( "maxval", input, urlRequest );
        List<String> minvals = gwfs.getWFSList( "minval", input, urlRequest );
        float max = getMaxvalue( maxvals );
        float min = getMinvalue( minvals );
        queryFishEx.setMaxLegend( max );
        queryFishEx.setMinLegend( min );
	}
	
	private float getMaxvalue( List<String> maxvals ) {
		Float max = null;
		if ( maxvals.size() == 0 ) {
			max = Float.MIN_VALUE;
		} else if ( maxvals.size() == 1 ) {
			max = stringToFloat( maxvals.get( 0 ), Float.MIN_VALUE );	
		} else {
			for ( String next : maxvals ) {
				Float nextVal = stringToFloat( next, Float.MIN_VALUE );
				if ( nextVal > max ) {
					max = nextVal;
				}
			}
		}
        return max; 
	}
	
	private float getMinvalue( List<String> minvals ) {
		Float min = null;
		if ( minvals.size() == 0 ) {
			min = Float.MAX_VALUE;
		} else if ( minvals.size() == 1 ) {
			min = stringToFloat( minvals.get( 0 ), Float.MAX_VALUE );	
		} else {
			for ( String next : minvals ) {
				Float nextVal = stringToFloat( next, Float.MAX_VALUE );
				if ( nextVal > min ) {
					min = nextVal;
				}
			}
		}
        return min;
	}
	
	private Float stringToFloat( String val, Float exceptionValue ) {
		Float nextVal = null;
		try{
			nextVal = Float.parseFloat( val );
		} catch ( NumberFormatException e ) {
			nextVal = exceptionValue;
		}
		return nextVal;
	}
	
	private boolean isAreadisplay( String displaytype ) {
        if (displaytype.contains(PUNKTVISNING)) {
            return false;
        } else {
            return true;
        }		
	}
	
	private void writeSldFileToTmpdir( String sld, String filename ) throws IOException {
        File output = new File(System.getProperty("java.io.tmpdir").concat(System.getProperty("file.separator")).concat(filename));
        OutputStream fos = new FileOutputStream(output);
        fos.write(sld.getBytes());
        fos.close();		
	}
	
	private void writeFilenameToResponse( HttpServletResponse resp, String filename ) throws IOException {
        resp.setContentType("text/plain");
        OutputStream out = resp.getOutputStream();
        out.write(filename.getBytes());
        out.close();
	}
}

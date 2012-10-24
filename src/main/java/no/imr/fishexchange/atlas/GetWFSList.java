package no.imr.fishexchange.atlas;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import no.imr.fishexchange.atlas.StAXreader.StAXreaderException;

/**
 *
 * @author trondwe
 */
@Component
public class GetWFSList {

	@Autowired( required = true ) 
	private GetWFSParameterList gfs;
	
    public List<String> getWFSList(
    		String searchProperty, 
    		Map<String, String> inputvalues, 
    		String mainUrl) throws UnsupportedEncodingException, StAXreaderException, XMLStreamException, IOException {

        String filterStart = "<ogc:Filter xmlns:ogc=\"http://ogc.org\" xmlns:gml=\"http://www.opengis.net/gml\">";
        String andOperatorStart = "<ogc:And>";
        String andOperatorEnd = "</ogc:And>";
        String likeOperatorStart = "<ogc:PropertyIsLike escape=\"?\" singleChar=\"_\" wildCard=\"*\">";
        String propertyStart = "<ogc:PropertyName>";
        String propertyEnd = "</ogc:PropertyName>";
        String valueStart = "<ogc:Literal>";
        String valueEnd = "</ogc:Literal>";
        String likeOperatorEnd = "</ogc:PropertyIsLike>";
        String filterEnd = "</ogc:Filter>";

        String filter = null;

        String propertyName = "propertyName=" + searchProperty;
        String urlRequest = mainUrl + "&" + propertyName;

        if (inputvalues != null) {
            List<String> keys = new ArrayList<String>(inputvalues.keySet());
            if (keys.size() > 1) {
                filter = filterStart;
                filter += andOperatorStart;
                for (String filter_property : keys) {
                    String value = inputvalues.get(filter_property);
                    filter += likeOperatorStart + propertyStart + filter_property + propertyEnd + valueStart
                            + value + valueEnd + likeOperatorEnd;

                }
                filter += andOperatorEnd;
                filter += filterEnd;
            } else {
                filter = filterStart + likeOperatorStart + propertyStart + keys.get(0) + propertyEnd + valueStart
                        + inputvalues.get(keys.get(0)) + valueEnd + likeOperatorEnd + filterEnd;
            }
            if (filter != null) {
                filter = URLEncoder.encode(filter, "UTF-8");
                urlRequest = urlRequest + "&filter=" + filter;
            }
        }

        gfs.readXML(urlRequest, searchProperty);
        List<String> params = gfs.getList();
        gfs.deleteList();
        return params;
    }
}

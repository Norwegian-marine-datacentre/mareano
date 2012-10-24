package no.imr.fishexchange.atlas;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import no.imr.fishexchange.atlas.pojo.FishExchangePojo;

/**
 * @author trondwe
 */
@Component
public class SLDFile {

    private String sSLDFile = null;
    
    public String getSLDFile( FishExchangePojo queryFishEx, Boolean areadisplay ) {

        // List<Color> theColors = HSVtoRGB.makeColorScale(180.0f, Integer.parseInt("10"));

        sSLDFile = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>";
        sSLDFile += "<StyledLayerDescriptor version=\"1.0.0\" xmlns=\"http://www.opengis.net/sld\" xmlns:ogc=\"http://www.opengis.net/ogc\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.opengis.net/sld http://schemas.opengis.net/sld/1.0.0/StyledLayerDescriptor.xsd\">";
        sSLDFile += "<NamedLayer>";
        if (areadisplay) {
            sSLDFile += "<Name>test:areavalue</Name>";
        } else {
            sSLDFile += "<Name>test:pointvalue</Name>";
        }
        sSLDFile += getUserStyle(areadisplay, queryFishEx );
        sSLDFile += "</NamedLayer>";
        sSLDFile += "</StyledLayerDescriptor>";
        
        return sSLDFile;
    }

    protected String getUserStyle(Boolean areadisplay, FishExchangePojo queryFishEx ) {

        String sUserStyle = "<UserStyle>";
        sUserStyle += "<FeatureTypeStyle>";
        if (areadisplay) {
        	if ( !queryFishEx.getParameter().contains("Temperature") ) {
        		sUserStyle += getZeroRuleAreaDisplay( queryFishEx );
        		if ( queryFishEx.getMinLegend() == 0.0f ) {
        			queryFishEx.setMinLegend( 0.1f );
        		}
        	}
            // HUE is between 0 and 360 according to wikipedia,
            // BUT java.lang color MAPS this to a value between 0 and 1
            // Number of steps is here set to 4 - this number may increase
            // when the SLD_BODY text is taken from a file instead
            // the buffer of the http-request is limited to a few thousand bytes
            sUserStyle += getColorRules( 0.3f, 10, queryFishEx );
        } else { // pointdisplay
        	if ( !queryFishEx.getParameter().contains("Temperature") ) {
        		sUserStyle += getZeroRulePointDisplay( queryFishEx );
            	if ( queryFishEx.getMinLegend() == 0.0f ) {
            		queryFishEx.setMinLegend( 0.1f );
        		}
            }
            sUserStyle += getSteppedSizeRulePoints( 3, 2, 10, queryFishEx );
        }

        sUserStyle += "</FeatureTypeStyle>";
        sUserStyle += "</UserStyle>";
//        System.out.println(sUserStyle);
        return sUserStyle;
    }

    protected String getSelectionRule( FishExchangePojo queryFishEx) {

        String sRule = "";
        sRule += "<ogc:And>";
        sRule += "<ogc:PropertyIsEqualTo>";
        sRule += "<ogc:PropertyName>gridname</ogc:PropertyName>";
        sRule += "<ogc:Literal>" + queryFishEx.getGrid() + "</ogc:Literal>";
        sRule += "</ogc:PropertyIsEqualTo>";
        sRule += "<ogc:PropertyIsEqualTo>";
        sRule += "<ogc:PropertyName>parametername</ogc:PropertyName>";
        sRule += "<ogc:Literal>" + queryFishEx.getParameter() + "</ogc:Literal>";
        sRule += "</ogc:PropertyIsEqualTo>";
        sRule += "<ogc:PropertyIsEqualTo>";
        sRule += "<ogc:PropertyName>depthlayername</ogc:PropertyName>";
        sRule += "<ogc:Literal>" + queryFishEx.getDepth() + "</ogc:Literal>";
        sRule += "</ogc:PropertyIsEqualTo>";
        sRule += "<ogc:PropertyIsEqualTo>";
        sRule += "<ogc:PropertyName>periodname</ogc:PropertyName>";
        sRule += "<ogc:Literal>" + queryFishEx.getTime() + "</ogc:Literal>";
        sRule += "</ogc:PropertyIsEqualTo>";

        return sRule;
    }

    /**
     * ONE RULE FOR ALL VALUES GREATER THAN ZERO THE SYMBOL SIZE
     * IS PROPORTIONAL TO THE VALUE AT THE POINT
     * @return
     */
    protected String getSizeRulePointDisplay( FishExchangePojo queryFishEx) {

        String sRule = "<Rule>";
        sRule += "<ogc:Filter>";
        sRule += getSelectionRule( queryFishEx );
        sRule += "<ogc:PropertyIsNotEqualTo>";
        sRule += "<ogc:PropertyName>value</ogc:PropertyName>";
        sRule += "<ogc:Literal>0</ogc:Literal>";
        sRule += "</ogc:PropertyIsNotEqualTo>";
        sRule += "</ogc:And>";
        sRule += "</ogc:Filter>";
        sRule += "<PointSymbolizer>";
        sRule += "<Graphic>";
        sRule += "<Mark>";
        sRule += "<WellKnownName>circle</WellKnownName>";
        sRule += "<Fill>";
        sRule += "<CssParameter name=\"fill\">#FF0000</CssParameter>";
        sRule += "</Fill>";
        sRule += "</Mark>";
        sRule += "<Size>";
        sRule += "<ogc:Div>";
        sRule += "<ogc:PropertyName>value</ogc:PropertyName>";
        sRule += "<ogc:Literal>1</ogc:Literal>";
        sRule += "</ogc:Div>";
        sRule += "</Size>";
        sRule += "</Graphic>";
        sRule += "</PointSymbolizer>";
        sRule += "</Rule>";

        return sRule;
    }

    protected String getSteppedSizeRulePoints(Integer minsymbolsize, Integer stepsymbolsize, Integer nstep, FishExchangePojo queryFishEx ) {
        String stepRules = "";
        List<List<Float>> valueranges = makeValueRanges(queryFishEx.getMinLegend(), queryFishEx.getMaxLegend(), nstep);
        Integer intervalsymbolsize = 0;
        Integer istep = -1;
        for (List<Float> valuerange : valueranges) {
            istep++;
            intervalsymbolsize = minsymbolsize + stepsymbolsize * istep;
            stepRules += getStepSizeRulePoint(
            		valuerange.get(0).toString(), valuerange.get(1).toString(), 
        			intervalsymbolsize.toString(), queryFishEx );
        }
        return stepRules;
    }

    protected String getStepSizeRulePoint( String minLocal, String maxLocal, String symbolsize, FishExchangePojo queryFishEx) {
        String sRule = "<Rule>";
        sRule += "<Title>" + minLocal + "-" + maxLocal + "</Title>";
        sRule += "<ogc:Filter>";
        sRule += "<ogc:And>";
        sRule += "<ogc:PropertyIsEqualTo>";
        sRule += "<ogc:PropertyName>gridname</ogc:PropertyName>";
        sRule += "<ogc:Literal>" + queryFishEx.getGrid() + "</ogc:Literal>";
        sRule += "</ogc:PropertyIsEqualTo>";
        sRule += "<ogc:PropertyIsEqualTo>";
        sRule += "<ogc:PropertyName>parametername</ogc:PropertyName>";
        sRule += "<ogc:Literal>" + queryFishEx.getParameter() + "</ogc:Literal>";
        sRule += "</ogc:PropertyIsEqualTo>";
        sRule += "<ogc:PropertyIsEqualTo>";
        sRule += "<ogc:PropertyName>depthlayername</ogc:PropertyName>";
        sRule += "<ogc:Literal>" + queryFishEx.getDepth() + "</ogc:Literal>";
        sRule += "</ogc:PropertyIsEqualTo>";
        sRule += "<ogc:PropertyIsEqualTo>";
        sRule += "<ogc:PropertyName>periodname</ogc:PropertyName>";
        sRule += "<ogc:Literal>" + queryFishEx.getTime() + "</ogc:Literal>";
        sRule += "</ogc:PropertyIsEqualTo>";
        sRule += "<ogc:PropertyIsGreaterThanOrEqualTo>";
        sRule += "<ogc:PropertyName>value</ogc:PropertyName>";
        sRule += "<ogc:Literal>" + minLocal + "</ogc:Literal>";
        sRule += "</ogc:PropertyIsGreaterThanOrEqualTo>";
        sRule += "<ogc:PropertyIsLessThan>";
        sRule += "<ogc:PropertyName>value</ogc:PropertyName>";
        sRule += "<ogc:Literal>" + maxLocal + "</ogc:Literal>";
        sRule += "</ogc:PropertyIsLessThan>";
        sRule += "</ogc:And>";
        sRule += "</ogc:Filter>";
        sRule += "<PointSymbolizer>";
        sRule += "<Graphic>";
        sRule += "<Mark>";
        sRule += "<WellKnownName>circle</WellKnownName>";
        sRule += "<Fill>";
        sRule += "<CssParameter name=\"fill\">#FF0000</CssParameter>"; //RED
        sRule += "</Fill>";
        sRule += "</Mark>";
        sRule += "<Size>" + symbolsize + "</Size>";
        sRule += "</Graphic>";
        sRule += "</PointSymbolizer>";
        sRule += "</Rule>";
        return sRule;
    }

    protected String getZeroRulePointDisplay( FishExchangePojo queryFishEx ) {
        String sRule = "<Rule>";
        sRule += "<Title>0 - Zero</Title>";
        sRule += "<ogc:Filter>";
        sRule += getSelectionRule( queryFishEx );
        sRule += "<ogc:PropertyIsEqualTo>";
        sRule += "<ogc:PropertyName>value</ogc:PropertyName>";
        sRule += "<ogc:Literal>0</ogc:Literal>";
        sRule += "</ogc:PropertyIsEqualTo>";
        sRule += "</ogc:And>";
        sRule += "</ogc:Filter>";
        sRule += "<PointSymbolizer>";
        sRule += "<Graphic>";
        sRule += "<Mark>";
        sRule += "<WellKnownName>circle</WellKnownName>";
        sRule += "<Fill>";
        sRule += "<CssParameter name=\"fill\">#999999</CssParameter>"; // Grey color
        sRule += "</Fill>";
        sRule += "</Mark>";
        sRule += "<Size>3</Size>";
        sRule += "</Graphic>";
        sRule += "</PointSymbolizer>";
        sRule += "</Rule>";
        return sRule;
    }

    protected String getZeroRuleAreaDisplay( FishExchangePojo queryFishEx ) {

        String sRule = "<Rule>";
        sRule += "<Title>0 - Zero</Title>";
        sRule += "<ogc:Filter>";
        sRule += "<ogc:And>";
        sRule += "<ogc:PropertyIsEqualTo>";
        sRule += "<ogc:PropertyName>gridname</ogc:PropertyName>";
        sRule += "<ogc:Literal>" + queryFishEx.getGrid() + "</ogc:Literal>";
        sRule += "</ogc:PropertyIsEqualTo>";
        sRule += "<ogc:PropertyIsEqualTo>";
        sRule += "<ogc:PropertyName>parametername</ogc:PropertyName>";
        sRule += "<ogc:Literal>" + queryFishEx.getParameter() + "</ogc:Literal>";
        sRule += "</ogc:PropertyIsEqualTo>";
        sRule += "<ogc:PropertyIsEqualTo>";
        sRule += "<ogc:PropertyName>depthlayername</ogc:PropertyName>";
        sRule += "<ogc:Literal>" + queryFishEx.getDepth() + "</ogc:Literal>";
        sRule += "</ogc:PropertyIsEqualTo>";
        sRule += "<ogc:PropertyIsEqualTo>";
        sRule += "<ogc:PropertyName>periodname</ogc:PropertyName>";
        sRule += "<ogc:Literal>" + queryFishEx.getTime() + "</ogc:Literal>";
        sRule += "</ogc:PropertyIsEqualTo>";
        sRule += "<ogc:PropertyIsEqualTo>";
        sRule += "<ogc:PropertyName>value</ogc:PropertyName>";
        sRule += "<ogc:Literal>0</ogc:Literal>";
        sRule += "</ogc:PropertyIsEqualTo>";
        sRule += "</ogc:And>";
        sRule += "</ogc:Filter>";
        sRule += "<PolygonSymbolizer>";
        sRule += "<Fill>";
        sRule += "<CssParameter name=\"fill\">#999999</CssParameter>"; // Grey color
        sRule += "</Fill>";
        sRule += "</PolygonSymbolizer>";

        sRule += "</Rule>";
        return sRule;
    }

    protected String getColorRules(float hue, Integer nstep, FishExchangePojo queryFishEx ) {
        String colorRules = "";
        List<String> colors = HSVtoRGB.makeHexColorScale(hue, nstep);
        List<List<Float>> valueranges = makeValueRanges(queryFishEx.getMinLegend(), queryFishEx.getMaxLegend(), nstep);

        int i = 0;
        for (List<Float> valuerange : valueranges) {
            colorRules += getColorRule(
            		valuerange.get(0).toString(), valuerange.get(1).toString(), 
            		colors.get(i++), queryFishEx);
        }
        return colorRules;
    }

    protected String getColorRule( String minLocal, String maxLocal, String hexcolor, FishExchangePojo queryFishEx ) {

        String sRule = "<Rule>";
        sRule += "<Title>" + minLocal + "-" + maxLocal + "</Title>";
        sRule += "<ogc:Filter>";
        sRule += "<ogc:And>";
        sRule += "<ogc:PropertyIsEqualTo>";
        sRule += "<ogc:PropertyName>gridname</ogc:PropertyName>";
        sRule += "<ogc:Literal>" + queryFishEx.getGrid() + "</ogc:Literal>";
        sRule += "</ogc:PropertyIsEqualTo>";
        sRule += "<ogc:PropertyIsEqualTo>";
        sRule += "<ogc:PropertyName>parametername</ogc:PropertyName>";
        sRule += "<ogc:Literal>" + queryFishEx.getParameter() + "</ogc:Literal>";
        sRule += "</ogc:PropertyIsEqualTo>";
        sRule += "<ogc:PropertyIsEqualTo>";
        sRule += "<ogc:PropertyName>depthlayername</ogc:PropertyName>";
        sRule += "<ogc:Literal>" + queryFishEx.getDepth() + "</ogc:Literal>";
        sRule += "</ogc:PropertyIsEqualTo>";
        sRule += "<ogc:PropertyIsEqualTo>";
        sRule += "<ogc:PropertyName>periodname</ogc:PropertyName>";
        sRule += "<ogc:Literal>" + queryFishEx.getTime() + "</ogc:Literal>";
        sRule += "</ogc:PropertyIsEqualTo>";
        sRule += "<ogc:PropertyIsGreaterThanOrEqualTo>";
        sRule += "<ogc:PropertyName>value</ogc:PropertyName>";
        sRule += "<ogc:Literal>" + minLocal + "</ogc:Literal>";
        sRule += "</ogc:PropertyIsGreaterThanOrEqualTo>";
        sRule += "<ogc:PropertyIsLessThan>";
        sRule += "<ogc:PropertyName>value</ogc:PropertyName>";
        sRule += "<ogc:Literal>" + maxLocal + "</ogc:Literal>";
        sRule += "</ogc:PropertyIsLessThan>";
        sRule += "</ogc:And>";
        sRule += "</ogc:Filter>";
        sRule += "<PolygonSymbolizer>";
        sRule += "<Fill>";
        sRule += "<CssParameter name=\"fill\">#" + hexcolor + "</CssParameter>";
        sRule += "</Fill>";
        sRule += "</PolygonSymbolizer>";
        sRule += "</Rule>";
        return sRule;
    }

    protected List<List<Float>> makeValueRanges(Float minvalue, Float maxvalue, Integer nstep) {

        List<List<Float>> thelist = new ArrayList<List<Float>>();
        Float step = (maxvalue - minvalue) / nstep;
        Float value = minvalue;

        for (int i = 0; i < nstep; i++) {
            List<Float> range = new ArrayList<Float>();
            range.add(value);
            range.add(value + step);
            thelist.add(range);
            value = value + step;
        }
        return thelist;
    }
}

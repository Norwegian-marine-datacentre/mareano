package no.imr.geoexplorer.printmap;

public class HTMLstring {
    
    private final static String BOLD = "<b>";
    private final static String BOLD_END = "<b>";
    private final static String NEWLINE = "<br>";
    private final static String NEWLINE2 = "<BR>";
    private final static String NEWLINE_END = "</br>";
    private final static String NEWLINE_END2 = "</BR>";
    
    public String removeHTMLfromString(String legendText) {

        legendText = removeString( legendText, BOLD);
        legendText = removeString( legendText, BOLD_END);
        legendText = removeString( legendText, NEWLINE);
        legendText = removeString( legendText, NEWLINE_END);
        legendText = removeString( legendText, NEWLINE2);
        legendText = removeString( legendText, NEWLINE_END2);
        return legendText;
    }
        
    private String removeString( String theString, String htmlAnnotation) {
        int index = theString.indexOf(htmlAnnotation);
        if ( index != -1 ) {
            theString = theString.substring(0, index) + theString.substring(index + htmlAnnotation.length());
        }
        return theString;
    }
}

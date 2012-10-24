package no.imr.fishexchange.atlas;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

/** StAX reader.
 *  Use this class when reading large xml files sequentially.
 * @author aasmunds
 */
public abstract class StAXreader {
	
	protected String tagtolookfor = "";
	protected XMLEventReader eventReader;
	protected XMLEvent event;
    
    public static class StAXreaderException extends Exception {

		private static final long serialVersionUID = -6292741192523324723L;

		public StAXreaderException(Throwable cause) {
            super(cause);
        }

        public StAXreaderException(String message) {
            super(message);
        }
    }

    public String getCurrentElementValue() throws XMLStreamException{
    	event = eventReader.nextEvent();
    	if (event.isCharacters()) {
    		return event.asCharacters().getData();
    	}
        return "";
    }

    public String getCurrentAttributeValue(String attname) {
        return event.asStartElement().getAttributeByName(QName.valueOf(attname)).getValue();
    }

    public abstract void onElementStart(String Element) throws XMLStreamException;

    public Boolean isFinished() {
        return !eventReader.hasNext();
    }

    public void readXML(String UrlRequest, String tagtolookfor) throws StAXreaderException, IOException, XMLStreamException {
        this.tagtolookfor = tagtolookfor;
        // First create a new XMLInputFactory
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();

        // Setup a new eventReader
        BufferedReader in = null;
        Boolean hasElements = false;
        try {
            URL url = new URL(UrlRequest);
            in = new BufferedReader(new InputStreamReader(url.openStream()));

            in = new BufferedReader(new InputStreamReader(url.openStream()));
            eventReader = inputFactory.createXMLEventReader(in);
            // Read the XML document
            while (!isFinished()) {
                event = eventReader.nextEvent();
                if (event.isStartElement()) {
                    hasElements = true;
                    onElementStart(event.asStartElement().getName().getLocalPart());
                }
            }
        } finally {
            if (!hasElements) {
                throw (new StAXreaderException("This file seems not to be a properly xml file. No tags are found!"));
            }
            in.close();
        }
    }
}

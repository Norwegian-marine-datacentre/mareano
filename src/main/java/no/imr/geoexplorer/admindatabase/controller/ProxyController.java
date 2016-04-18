package no.imr.geoexplorer.admindatabase.controller;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import org.apache.commons.io.IOUtils;

@Controller
public class ProxyController {
    
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(ProxyController.class);
    
    @RequestMapping(value = "/proxy", method = RequestMethod.GET)
    public void simpleProxy(@RequestParam(value = "url") String sourceURL,
            HttpServletRequest request,
            HttpServletResponse response) {
//        System.out.println("proxy request:"+sourceURL);
        HttpURLConnection proxyRequest = createProxyRequest(sourceURL);

        if (proxyRequest != null) {
            try {
               InputStream input = proxyRequest.getInputStream();
               for (String header:proxyRequest.getHeaderFields().keySet()) {
                   if (!(header!= null && header.equals("Transfer-Encoding"))) {
                       response.setHeader(header,proxyRequest.getHeaderField(header));
                   }
               }
                IOUtils.copy(input, response.getOutputStream());
                input.close();
            } catch (IOException ex) {
                LOG.error("Error proxying " + sourceURL);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private HttpURLConnection createProxyRequest(String requestURL) {
        HttpURLConnection result = null;
        try {
            URL url = new URL(requestURL);
            result = (HttpURLConnection) url.openConnection();
        } catch (MalformedURLException ex) {
            LOG.error("Incorrect url syntax", ex);
        } catch (IOException ex) {
            LOG.error("Unable to connect", ex);
        }
        return result;
    }
}

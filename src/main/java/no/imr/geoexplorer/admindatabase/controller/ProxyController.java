package no.imr.geoexplorer.admindatabase.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

@Controller
public class ProxyController {
    
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(ProxyController.class);
    
    @Autowired
    private ApplicationContext ctx;
    
    private final static String HOST_WHITELIST = "whitelist_proxy_urls.properties";
    
    @RequestMapping(value = "/proxy", method = RequestMethod.GET)
    public void simpleProxy(@RequestParam(value = "url") String sourceURL,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        
        if ( !isHostWhiteListUrl(sourceURL) ) {
            return;
        }
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
    
    private boolean isHostWhiteListUrl(String sourceURL) throws IOException {
        Resource template = ctx.getResource("classpath:"+HOST_WHITELIST );
        File whitelistFile = template.getFile();
        
        URL aUrl = new URL(sourceURL);
        boolean isFound = FileUtils.readFileToString(whitelistFile).contains(aUrl.getHost());
        
        return isFound;
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

package no.imr.fishexchange.atlas.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author trondwe
 */
@Controller
public class GetSLDController  {

	/**
	 * tmp sld files are stored at - /usr/share/tomcat5/temp/
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("/getsld")
    public void getsld(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String filename = (String) request.getParameter("file");
        String tmpSldFilepath = System.getProperty("java.io.tmpdir").concat(System.getProperty("file.separator")).concat(filename);
        File output = new File( tmpSldFilepath );
        writeSldToResponse( output, response );
    }
	
	@Autowired
	private ApplicationContext ctx;
	
	@RequestMapping("/getcodsld25_29cm_2003y")
	public void getcodsld25_29cm_2003y(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		getSldResource("no/imr/fishexchange/atlas/sld/cod_25-29cm_y2003_depthW.sld", req, resp);
	}
	
	@RequestMapping("/getcodsld25_29cm_2010y")
	public void getcodsld25_29cm_2010y(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		getSldResource("no/imr/fishexchange/atlas/sld/cod_25-29cm_y2010_depthW.sld", req, resp);
	}
	
	@RequestMapping("/gethaddocksld25_29cm_2003y")
	public void gethaddcoksld25_29cm_2003y(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		getSldResource("no/imr/fishexchange/atlas/sld/haddock_25-29cm_y2003_depthW.sld", req, resp);
	}
	
	@RequestMapping("/gethaddocksld25_29cm_2010y")
	public void gethaddocksld25_29cm_2010y(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		getSldResource("no/imr/fishexchange/atlas/sld/haddock_25-29cm_y2010_depthW.sld", req, resp);
	}
	
	@RequestMapping("/getcodtotal2009Q3")
	public void getcodtotal2009Q3(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		getSldResource("no/imr/fishexchange/atlas/sld/cod_ecosystem_total_Q20093.sld", req, resp);
	}
	@RequestMapping("/getcodtotal2009Q3punkt")
	public void getcodtotal2009Q3punkt(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		getSldResource("no/imr/fishexchange/atlas/sld/cod_ecosystem_total_Q20093punkt.sld", req, resp);
	}	
	
	@RequestMapping("/getcodtotal2010Q3")
	public void getcodtotal2010Q3(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		getSldResource("no/imr/fishexchange/atlas/sld/cod_ecosystem_total_Q20103.sld", req, resp);
	}
	@RequestMapping("/getcodtotal2010Q3punkt")
	public void getcodtotal2010Q3punkt(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		getSldResource("no/imr/fishexchange/atlas/sld/cod_ecosystem_total_Q20103punkt.sld", req, resp);
	}
	
	@RequestMapping("/gethaddocktotal2009Q3")
	public void gethaddocktotal2010Q3(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		getSldResource("no/imr/fishexchange/atlas/sld/haddock_ecosystem_total_Q20093.sld", req, resp);
	}
	@RequestMapping("/gethaddocktotal2009Q3punkt")
	public void gethaddocktotal2010Q3punkt(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		getSldResource("no/imr/fishexchange/atlas/sld/haddock_ecosystem_total_Q20093punkt.sld", req, resp);
	}
	
	@RequestMapping("/gethaddocktotal2010Q3")
	public void gethaddocktotal2009Q3(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		getSldResource("no/imr/fishexchange/atlas/sld/haddock_ecosystem_total_Q20103.sld", req, resp);
	}
	@RequestMapping("/gethaddocktotal2010Q3punkt")
	public void gethaddocktotal2009Q3punkt(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		getSldResource("no/imr/fishexchange/atlas/sld/haddock_ecosystem_total_Q20103punkt.sld", req, resp);
	}	
	
	@RequestMapping("/getcapelintotal2010")
	public void getcapelintotal2010(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		getSldResource("no/imr/fishexchange/atlas/sld/capelin_ecosystem_total_2010.sld", req, resp);
	}	
	@RequestMapping("/getcapelintotal2010punkt")
	public void getcapelintotal2010punkt(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		getSldResource("no/imr/fishexchange/atlas/sld/capelin_ecosystem_total_2010punkt.sld", req, resp);
	}	
	
	@RequestMapping("/getcapelintotal2009")
	public void getcapelintotal2009(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		getSldResource("no/imr/fishexchange/atlas/sld/capelin_ecosystem_total_2009.sld", req, resp);
	}	
	@RequestMapping("/getcapelintotal2009punkt")
	public void getcapelintotal2009punkt(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		getSldResource("no/imr/fishexchange/atlas/sld/capelin_ecosystem_total_2009punkt.sld", req, resp);
	}	
	
	private void getSldResource( String sldResource, HttpServletRequest request, HttpServletResponse response ) throws IOException {
		Resource template = ctx.getResource("classpath:"+sldResource );
        File output = template.getFile();
        writeSldToResponse( output, response );
	}
	
	
	private void writeSldToResponse( File sldOutput, HttpServletResponse response ) throws IOException {
        BufferedReader inReader = new BufferedReader(new FileReader(sldOutput.getAbsoluteFile()));
        Writer respWriter = response.getWriter();

        try {
	        String thisLine;
	        StringBuffer sldXml = new StringBuffer();
	        while ((thisLine = inReader.readLine()) != null) { 
	        	sldXml.append(thisLine);
	        }      
        	respWriter.write( sldXml.toString() );
        } finally {
        	respWriter.close();
        	inReader.close();
        }
	}
}

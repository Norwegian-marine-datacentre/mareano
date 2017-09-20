package no.imr.geoexplorer.admindatabase.controller;

import java.io.File;
import java.io.IOException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.ServletConfigAware;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class SaveMapsController implements ServletConfigAware {
	
	private final static String SAVED_MAPS_DB = "geoexplorer.db";
	
    private final static String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS maps (id INTEGER PRIMARY KEY ASC, config BLOB);";
    private final static String READ_MAP_LIST = "SELECT id, config FROM maps;";
    private final static String READ_MAP = "SELECT config FROM maps WHERE id = ?;";
    private final static String SAVE_MAP = "INSERT INTO maps (config) VALUES (?);";
    private final static String SAVED_MAP_GET_ID = "SELECT last_insert_rowid() AS id;";
    
    private Connection conn = null;
    private String jdbcUrl = "";
    
    private ServletConfig servletConfig;

    public void setServletConfig(ServletConfig servletConfig) {
        this.servletConfig = servletConfig;
    }
    
    public void init( HttpServletRequest req ) throws ClassNotFoundException, SQLException {
    	
    	if ( conn == null ) {
	    	String dataDir = "";
	    	if ( req != null ) {
	    		dataDir = servletConfig.getInitParameter("GEOEXPLORER_DATA");
	    		System.out.println("dataDir:"+dataDir);
	    	} else {
	    		throw new Error("Empty request sent. Cant open database with saved maps.");
	    	}

	    	System.out.println("GEOEXPLORER_DATA:"+servletConfig.getInitParameter("GEOEXPLORER_DATA"));
	    	Enumeration en = req.getSession().getServletContext().getInitParameterNames();
	    	while (en.hasMoreElements()) {
	    		System.out.println("Aname:"+en.nextElement().toString());
	    	}
	    	
	    	String dbFile = dataDir + File.separator + SAVED_MAPS_DB;
	        String jdbc = "jdbc:sqlite:";
	        jdbcUrl = jdbc + dbFile;
	        String driver = "org.sqlite.JDBC";
	        Class.forName(driver);
	
	        try {
	        	conn = DriverManager.getConnection(jdbcUrl);
	        	conn.setAutoCommit(true);
	        } catch (SQLException sqle) {
	        	throw new Error("Can't open '" + jdbcUrl + "' for writing.  Set GEOEXPLORER_DATA to a writable directory:"+sqle.getMessage());
	        }    	
	        
	    	Statement st = conn.createStatement();
	
	    	//getDB
	    	int isOk = st.executeUpdate(CREATE_TABLE);
	    	System.out.println("isOk:"+isOk);
    	}
    }
    
    @PreDestroy
    public void cleanup() {
    	try {
			conn.close();	
    	} catch( SQLException e) {
    		throw new Error("Error closing connection to:"+jdbcUrl);
    	}
    }
    
    @RequestMapping(value = "/maps", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody JsonNode  saveMap( HttpServletRequest req) throws JsonProcessingException, IOException, ClassNotFoundException, SQLException {
    	
    	init(req);
    	int id = -1;
        try {
    		//createMap
    		String jsonBody = IOUtils.toString( req.getInputStream());
        		
    		long now = System.currentTimeMillis();
			String formattedNow = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(now);
			jsonBody = jsonBody.substring(0, jsonBody.length()-1)+", \"created\":\""+formattedNow+"\", \"modified\":\""+formattedNow+"\"}";
			PreparedStatement prep = conn.prepareStatement( SAVE_MAP );
	        prep.setString(1, jsonBody);
	        prep.executeUpdate();
	        // get the map id
	        Statement prep2 = conn.createStatement();
	        ResultSet results = prep2.executeQuery( SAVED_MAP_GET_ID );
	        results.next();
	        id = new Integer(results.getInt("id"));        	
        } catch (SQLException e) {
        	e.printStackTrace();
        	throw new Error("Error updating table with sql:"+e.getMessage());
        } 
        
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree("{\"id\":"+id+"}");
    }
    
    @RequestMapping(value = "/maps/{mapId}", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody JsonNode  getMap( @PathVariable Integer mapId, HttpServletRequest req) throws JsonProcessingException, IOException, ClassNotFoundException, SQLException {
    	
    	init(req);
    	//getReadMap
    	
    	System.out.println("pathInfo():"+req.getPathInfo());
    	String pathInfo = req.getPathInfo();
    	System.out.println("pathInfo:"+pathInfo);
    	System.out.println("pathInfo splitt:"+pathInfo.split("/"));
    	String[] paths = req.getPathInfo().split("/"); 
    	String id = paths[1];
    	System.out.println("id:"+id);
    	
    	
//    	if ( id == null ) return readMapList();
    	
    	PreparedStatement prep = conn.prepareStatement( READ_MAP );
//        prep.setInt(1, new Integer(id));
    	prep.setInt(1, new Integer(mapId));
        ResultSet results = prep.executeQuery();
        String jsonConfig = results.getString("config");
        System.out.println( "jsonConfig:" + jsonConfig.toString() );
        
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(jsonConfig.toString());
    }
    
    public JsonNode readMapList() throws JsonProcessingException, IOException, SQLException {
    	Statement st = conn.createStatement();
    	ResultSet results = st.executeQuery( READ_MAP_LIST ); 
    	ArrayList items = new ArrayList();
    	String config;
    	while ( results.next() ) {
    		config = results.getString("config");
    		System.out.println("result config:"+config);
    		items.add("id:"+results.getInt("id") +","+config);
    	}
    	
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree("{\"maps\":"+items.toString()+"}");
    }    
}

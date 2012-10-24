package no.imr.fishexchange.atlas.pojo;

import java.util.HashMap;
import java.util.Map;

/**
 * Contains attributes used to query fishexchange db.
 * Methods on object are null-safe.
 * @author endrem
 */
public class FishExchangePojo {
	
	public static final String GRIDNAME = "gridname";
	public static final String PARAMETERNAME = "parametername";
	public static final String DEPTHLAYERNAME = "depthlayername";
	public static final String PERIODNAME = "periodname";
	
	private String grid = "";
	private String parameter = "";
	private String time = "";
	private String depth = "";
	
	private float maxLegend;
	private float minLegend;
	
	public FishExchangePojo( String grid, String parameter, String depth, String time ) {
		setGrid( grid );
		setParameter( parameter );
		setTime( time );
		setDepth( depth );
	}
	
	public Map<String, String> createQueryMap() {
	    Map<String, String> input = new HashMap<String, String>();
	    if ( !grid.equals("") )
	    	input.put( GRIDNAME, grid  );
		if ( !parameter.equals("") )
			input.put( PARAMETERNAME, parameter );
		if ( !depth.equals("") )
			input.put( DEPTHLAYERNAME, depth );
		if ( !time.equals("") )
			input.put( PERIODNAME, time );
		return input;
	}
 
	public String getGrid() {
		return grid;
	}
	public void setGrid(String grid) {
		if ( grid == null ) {
			this.grid = "";
		} else {
			this.grid = grid;
		}
	}
	public String getParameter() {
		return parameter;
	}
	public void setParameter(String parameter) {
		if ( parameter == null ) {
			this.parameter = "";
		} else {
			this.parameter = parameter;
		}
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		if ( time == null ) {
			this.time = "";
		} else {
			this.time = time;
		}
	}
	public String getDepth() {
		return depth;
	}
	public void setDepth(String depth) {
		if ( depth == null ) {
			this.depth = "";
		} else {
			this.depth = depth;
		}
	}

	public float getMaxLegend() {
		return maxLegend;
	}

	public void setMaxLegend(float maxLegend) {
		this.maxLegend = maxLegend;
	}

	public float getMinLegend() {
		return minLegend;
	}

	public void setMinLegend(float minLegend) {
		this.minLegend = minLegend;
	}
}

package org.fco.gdelt.mysql;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;

public class LocationTable extends Table {
	
	DecimalFormat df; 

	/**
	 * Define Table and subtable characteristics. 
	 *  
	 * @param connection
	 */
	public LocationTable( final Connection connection ) {
		super( "gdelt.Location", new String[]{ "LocationId", "Latitude", "Longitude" }, connection, false );
		
		df = new DecimalFormat( "#.0000" ); 
	}
	
	/**
	 * Returns key for the given data set. 
	 * 
	 * Recursively retieves keys for subdata-sets stored in subtables. 
	 * 
	 * Returns key on combination of subtable keys. 
	 */
	@Override
	public String getKey(String[] tableEntry) throws SQLException { 
		
		//Add fake value for regions without location info. 
		final String[] entries = new String[]{ "200", "200" };
		if( !tableEntry[ 0 ].equals( "" )  ) {  			
			entries[ 0 ] = String.valueOf( df.format( Double.parseDouble( tableEntry[ 0 ] ) ) ); 
		}
		 
		if( !tableEntry[ 1 ].equals( "" ) ) {			
			entries[ 1 ] = String.valueOf( df.format( Double.parseDouble( tableEntry[ 1 ] ) ) );
		}
		 
		return super.getKey( entries );
	}

}

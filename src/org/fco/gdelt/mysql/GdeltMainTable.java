package org.fco.gdelt.mysql;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Main table for the gdelt database. 
 * Holds subtables for the values. 
 * 
+----------------+---------------------+------+-----+---------+----------------+
| Field          | Type                | Null | Key | Default | Extra          |
+----------------+---------------------+------+-----+---------+----------------+
| Id             | bigint(20) unsigned | NO   | PRI | NULL    | auto_increment |
| GLOBALEVENTID  | bigint(20) unsigned | NO   |     | NULL    |                |
| DateId         | int(10) unsigned    | YES  | MUL | NULL    |                |
| Actor1Id       | int(10) unsigned    | YES  | MUL | NULL    |                |
| Actor2Id       | int(10) unsigned    | YES  | MUL | NULL    |                |
| EventId        | int(10) unsigned    | YES  | MUL | NULL    |                |
| MediaId        | int(10) unsigned    | YES  | MUL | NULL    |                |
| Actor1GeoId    | int(10) unsigned    | YES  | MUL | NULL    |                |
| Actor2GeoId    | int(10) unsigned    | YES  | MUL | NULL    |                |
| ActionGeoId    | int(10) unsigned    | YES  | MUL | NULL    |                |
| AddedSQLDateId | int(10) unsigned    | YES  | MUL | NULL    |                |
| SourceId       | int(10) unsigned    | YES  | MUL | NULL    |                |
+----------------+---------------------+------+-----+---------+----------------+
 * @author fernando carrillo (fernando@carrillo.at)
 *
 */
public class GdeltMainTable extends Table {

	private boolean useSourceUrl; 
	
	private DateTable date; 
	private ActorTable actor;
	private EventTable event; 
	private MediaTable media; 
	private GeoTable geo; 
	private DateAddedTable dateAdded;
	private SourceTable source; 

	/**
	 * Define Table and subtable characteristics. 
	 *  
	 * @param connection
	 */
	public GdeltMainTable( final Connection connection, final boolean useSourceUrl ) {
		super("gdelt.GdeltMain", new String[]{
				"Id", 
				"GLOBALEVENTID", 
				"DateId", 
				"Actor1Id", "Actor2Id", 
				"EventId", "MediaId", 
				"Actor1GeoId", "Actor2GeoId", "ActionGeoId", 
				"AddedSQLDateId", "SourceId"
		}, connection, false );
		
		this.useSourceUrl = useSourceUrl; 
		
		date = new DateTable( connection );
		actor = new ActorTable( connection );
		event = new EventTable( connection );
		media = new MediaTable( connection ); 
		geo = new GeoTable( connection ); 
		dateAdded = new DateAddedTable( connection ); 
		source = new SourceTable( connection ); 
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
		
		final String[] keys = new String[ columnNames.length -1  ]; 
		
		//System.out.println( Arrays.toString( tableEntry ) ); 
		
		keys[ 0 ] = tableEntry[ 0 ];  
		keys[ 1 ] = date.getKey( getDateEntries( tableEntry ) );
		
		keys[ 2 ] = actor.getKey( getActor1Entries( tableEntry ) );
		keys[ 3 ] = actor.getKey( getActor2Entries( tableEntry ) );
		
		keys[ 4 ] = event.getKey( getEventEntries( tableEntry ) ); 
		keys[ 5 ] = media.getKey( getMediaEntries( tableEntry ) );
	
		keys[ 6 ] = geo.getKey( getActor1GeoEntries( tableEntry ) );
		keys[ 7 ] = geo.getKey( getActor2GeoEntries( tableEntry ) );
		keys[ 8 ] = geo.getKey( getActionGeoEntries( tableEntry ) );
		//System.out.println( keys[ 6 ] );
		
		keys[ 9 ] = dateAdded.getKey( getDateAddedEntries( tableEntry ) );
		keys[ 10 ] = source.getKey( getSourceUrlEntries( tableEntry ) );
		
		String update = "INSERT INTO " + this.id + " " + getColumnString()
				+ " VALUES " + getValueString( keys ) + ";";  
		
		insertEntry( keys );
		return null; 
	}
	
	//Methods to split data-set into subdata. 
	
	private String[] getDateEntries( final String[] entries ) {
		return new String[]{ entries[ 1 ], entries[ 2 ], entries[ 3 ], entries[ 4 ] }; 
	}
	
	private String[] getActor1Entries( final String[] entries ) {
		//System.out.println( Arrays.toString(  new String[]{ entries[ 5 ], entries[ 6 ], entries[ 7 ], entries[ 8 ], entries[ 9 ], entries[ 10 ], entries[ 11 ], entries[ 12 ], entries[ 13 ], entries[ 14 ] } ) );  
		return new String[]{ entries[ 5 ], entries[ 6 ], entries[ 7 ], entries[ 8 ], entries[ 9 ], entries[ 10 ], entries[ 11 ], entries[ 12 ], entries[ 13 ], entries[ 14 ] };
	}
	
	private String[] getActor2Entries( final String[] entries ) {
		//System.out.println( Arrays.toString(  new String[]{ entries[ 15 ], entries[ 16 ], entries[ 17 ], entries[ 18 ], entries[ 19 ], entries[ 20 ], entries[ 21 ], entries[ 22 ], entries[ 23 ], entries[ 24 ] } ) );
		return new String[]{ entries[ 15 ], entries[ 16 ], entries[ 17 ], entries[ 18 ], entries[ 19 ], entries[ 20 ], entries[ 21 ], entries[ 22 ], entries[ 23 ], entries[ 24 ] };
	}
	
	private String[] getEventEntries( final String[] entries ) {
		//System.out.println( Arrays.toString( new String[]{ entries[ 25 ], entries[ 26 ], entries[ 27 ], entries[ 28 ], entries[ 29 ], entries[ 30 ] } ) ); 
		return new String[]{ entries[ 25 ], entries[ 26 ], entries[ 27 ], entries[ 28 ], entries[ 29 ], entries[ 30 ] };
	}
	
	private String[] getMediaEntries( final String[] entries ) {
		//System.out.println( Arrays.toString( new String[]{ entries[ 31 ], entries[ 32 ], entries[ 33 ], entries[ 34 ] }  ) ); 
		return new String[]{ entries[ 31 ], entries[ 32 ], entries[ 33 ], entries[ 34 ] };
	}
	
	private String[] getActor1GeoEntries( final String[] entries ) {
		//System.out.println( Arrays.toString( new String[]{ entries[ 35 ], entries[ 36 ], entries[ 37 ], entries[ 38 ], entries[ 39 ], entries[ 40 ], entries[ 41 ] } ) ); 
		return new String[]{ entries[ 35 ], entries[ 36 ], entries[ 37 ], entries[ 38 ], entries[ 39 ], entries[ 40 ], entries[ 41 ] };
	}
	
	private String[] getActor2GeoEntries( final String[] entries ) {
		return new String[]{ entries[ 42 ], entries[ 43 ], entries[ 44 ], entries[ 45 ], entries[ 46 ], entries[ 47 ], entries[ 48 ] };
	}
	
	private String[] getActionGeoEntries( final String[] entries ) {
		return new String[]{ entries[ 49 ], entries[ 50 ], entries[ 51 ], entries[ 52 ], entries[ 53 ], entries[ 54 ], entries[ 55 ] };
	}
	
	private String[] getDateAddedEntries( final String[] entries ) {
		return new String[]{ entries[ 56 ] };
	}
	
	private String[] getSourceUrlEntries( final String[] entries ) {
		if( useSourceUrl ) {
			return new String[]{ entries[ 57 ] };
		} else {
			return new String[] { "" }; 
		}
	}
	
	
}

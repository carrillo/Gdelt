package org.fco.gdelt.mysql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
/**
 * Event table refers to subtables to hold values of: 
 * 
+-----------------+----------------------+------+-----+---------+----------------+
| Field           | Type                 | Null | Key | Default | Extra          |
+-----------------+----------------------+------+-----+---------+----------------+
| EventId         | int(10) unsigned     | NO   | PRI | NULL    | auto_increment |
| IsRootEvent     | tinyint(1)           | YES  |     | NULL    |                |
| EventCodeId     | smallint(5) unsigned | YES  | MUL | NULL    |                |
| BaseEventCodeId | smallint(5) unsigned | YES  | MUL | NULL    |                |
| RootEventCodeId | smallint(5) unsigned | YES  | MUL | NULL    |                |
| QuadClassId     | tinyint(3) unsigned  | YES  | MUL | NULL    |                |
+-----------------+----------------------+------+-----+---------+----------------+ * 
 * goldsteinscale
 * 
 * @author fernando carrillo (fernando@carrillo.at)
 *
 */
public class EventTable extends Table {

	private Table code; 
	private Table quadClass; 
	
	/**
	 * Define Table and subtable characteristics. 
	 *  
	 * @param connection
	 */
	public EventTable( Connection connection ) {
		super( "gdelt.Event" , new String[]{ 
				"EventId", "IsRootEvent", 
				"EventCodeId", "BaseEventCodeId", "RootEventCodeId", 
				"QuadClassId" } , connection, false );
		
		this.code = new Table( "gdelt.EventCode", new String[] {"EventCodeId", "CAMEOEventCode" }, connection, false );
		this.quadClass= new Table( "gdelt.QuadClass", new String[] {"QuadClassId", "QuadClass" }, connection, false );
	}
	
	/**
	 * Returns key for the given data set. 
	 * 
	 * Recursively retieves keys for subdata-sets stored in subtables. 
	 * 
	 * Returns key on combination of subtable keys. 
	 */
	@Override
	public String getKey( final String[] tableEntry ) throws SQLException {
		final String[] keys = new String[ tableEntry.length - 1 ];  
		
		//is root event
		keys[ 0 ] = tableEntry[ 0 ];
		
		//event codes
		keys[ 1 ] = code.getKey( new String[]{ tableEntry[ 1 ] } );
		keys[ 2 ] = code.getKey( new String[]{ tableEntry[ 2 ] } );
		keys[ 3 ] = code.getKey( new String[]{ tableEntry[ 3 ] } );
		
		//quadClass
		keys[ 4 ] = quadClass.getKey( new String[]{ tableEntry[ 4 ] } );
				
		return super.getKey( keys );
	}


}

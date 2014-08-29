package org.fco.gdelt.mysql;

import java.sql.Connection;
import java.sql.SQLException;
/**
 * 
 * Holds values of 
+----------+------------------+------+-----+---------+----------------+
| Field    | Type             | Null | Key | Default | Extra          |
+----------+------------------+------+-----+---------+----------------+
| SourceId | int(10) unsigned | NO   | PRI | NULL    | auto_increment |
| URL      | varchar(45)      | YES  |     | NULL    |                |
+----------+------------------+------+-----+---------+----------------+

 * @author fernando carrillo (fernando@carrillo.at)
 *
 */
public class SourceTable extends Table {
	
	private int count; 

	/**
	 * Define Table and subtable characteristics. 
	 *  
	 * @param connection
	 */
	public SourceTable( final Connection connection ) {
		super("gdelt.Source", new String[]{ "SourceId", "URL" }, connection, false );
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
		
		if( count == 1000 ) {
			this.keyHash.clear(); 
			count = 0; 
		}
		count++;
		
		return super.getKey( new String[]{ tableEntry[ 0 ].replace( "'", "" ) } );  
	}

}

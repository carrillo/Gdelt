package org.fco.gdelt.mysql;

import java.sql.Connection;
import java.sql.SQLException;
/**
 * Hold values and subtables for
 * 
+--------------+-----------------------+------+-----+---------+----------------+
| Field        | Type                  | Null | Key | Default | Extra          |
+--------------+-----------------------+------+-----+---------+----------------+
| DateId       | int(10) unsigned      | NO   | PRI | NULL    | auto_increment |
| SQLDateId    | int(10) unsigned      | NO   | UNI | NULL    |                |
| MonthYear    | int(6) unsigned       | NO   |     | NULL    |                |
| Year         | year(4)               | NO   |     | NULL    |                |
| FractionDate | decimal(8,4) unsigned | NO   |     | NULL    |                |
+--------------+-----------------------+------+-----+---------+----------------+
 * 
 * @author fernando carrillo (fernando@carrillo.at)
 *
 */
public class DateTable extends Table {
	
	private Table sqlDate; 
	
	/**
	 * Define Table and subtable characteristics. 
	 *  
	 * @param connection
	 */
	public DateTable( final Connection connection ) {
		super( "gdelt.Date", new String[]{ 
				"DateId", "SQLDateId", "MonthYear", "Year", "FractionDate"
				}, connection, false );
		this.sqlDate = new Table("gdelt.SQLDate", new String[]{ "SQLDateId", "SQLDate" } , connection, false ); 
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
		
		tableEntry[ 0 ] = sqlDate.getKey( new String[]{ tableEntry[ 0 ] } ); 
		
		return super.getKey(tableEntry);
	}
}

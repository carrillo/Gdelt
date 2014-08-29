package org.fco.gdelt.mysql;

import java.sql.Connection;

/**
+-----------+------------------+------+-----+---------+----------------+
| Field     | Type             | Null | Key | Default | Extra          |
+-----------+------------------+------+-----+---------+----------------+
| SQLDateId | int(10) unsigned | NO   | PRI | NULL    | auto_increment |
| SQLDate   | date             | YES  | UNI | NULL    |                |
+-----------+------------------+------+-----+---------+----------------+
 * @author fernando carrillo (fernando@carrillo.at)
 *
 */
public class DateAddedTable extends Table {

	/**
	 * Define Table characteristics. 
	 *  
	 * @param connection
	 */
	public DateAddedTable( final Connection connection ) {
		super("gdelt.SQLDate", new String[]{ "SQLDateId", "SQLDate" } , connection, false );
	}

}

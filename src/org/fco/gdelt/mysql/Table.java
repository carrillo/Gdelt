package org.fco.gdelt.mysql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import array.tools.StringArrayTools;


/**
 * Class to specify MySQL tables and subtables. 
 * 
 * It stores table specific information including 
 * - tableName (id) 
 * - columnNames
 * 
 * It implements a simple method for efficient key lookup given a data set.
 * If recursion over subtables is required override this method to retrieve keys of subtables, first. 
 * An example of this recursive method is implemented in MediaTable. 
 *  
 * @author fernando carrillo (fernando@carrillo.at)
 *
 */
public class Table 
{
	//Stores keys already loaded
	protected HashMap<String, String> keyHash = new HashMap<String, String>();
	
	protected String id;
	protected String[] columnNames;
	protected Connection connection; 
	protected int offset = 0; 
	
	/**
	 * Define Table characteristics. 
	 *  
	 * @param connection
	 */
	public Table( final String id, final String[] columnNames, final Connection connection, final boolean keyPartOfData ) {
		this.id = id; 
		this.columnNames = columnNames;
		this.connection = connection; 
		
		if( !keyPartOfData ) {
			offset = 1; 
		}
	}
	
	/**
	 * Return the key for the current table entry. 
	 * If the key was queried before return from hashmap. If not, check if it was added in another session 
	 * by querying the database.  
	 * @param tableEntry
	 * @return
	 */
	public String getKey( final String[] tableEntry ) throws SQLException {
		
		final String key = StringArrayTools.arrayToString( tableEntry, "," ); 
		 
		if( !keyHash.containsKey( key ) ) {	
			 
			try {
				
				ResultSet keyQueryResult = queryForKey( tableEntry ); 
				
				//Insert into table if not present.
				if( !keyQueryResult.first() ) {
					
					insertEntry( tableEntry );
					
					
					keyQueryResult =  queryForKey( tableEntry );
					keyQueryResult.next();
				}
				 
				 
				keyHash.put( key, keyQueryResult.getString( 1 ) ); 
				
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
		return keyHash.get( key ); 
	}
	
	/**
	 * Formulates the MySQL query for a given data-set. 
	 * 
	 * @param tableEntry
	 * @return
	 * @throws SQLException
	 */
	protected ResultSet queryForKey( final String[] tableEntry ) throws SQLException {
		String query = "SELECT " + this.columnNames[ 0 ] + " FROM " + this.id + " WHERE ";
		
		for( int i = offset; i < columnNames.length; i++ ) {
			if( i == offset ) {
				query += columnNames[ i ] + " = '" + tableEntry[ i - offset ] + "'"; 
			} else {				
				query += " and " + columnNames[ i ] + " = '" + tableEntry[ i  - offset ] + "'"; 
			}
		}
		query += ";";   
		
		//System.out.println( query ); 
		
		return connection.createStatement().executeQuery(  query ); 
	}
	
	/**
	 * Formulates the MySQL insert statement for the given data-set. 
	 * 
	 * @param tableEntry
	 * @throws SQLException
	 */
	protected void insertEntry( final String[] tableEntry ) throws SQLException {
		String update = "INSERT INTO " + this.id + " " + getColumnString()
				+ " VALUES " + getValueString( tableEntry ) + ";";  
		
		try {			
			connection.createStatement().executeUpdate( update ); 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns the column names formated for insert and update statements. 
	 * 
	 * @return
	 */
	protected String getColumnString() {
		String out = "("; 
		for( int i =  offset; i < columnNames.length; i++ ) {
			
			if( i == offset ) {
				out += columnNames[ i ];
			} else {
				out += "," + columnNames[ i ];
			}
			
		}
		out += ")"; 
		return out;
	}
	
	/**
	 * Returns the data-set formated for insert and update statements. 
	 * 
	 * @return
	 */
	protected String getValueString( final String[] entries ) {
		String out = "("; 
		for( int i = 0; i < entries.length; i++ ) {
			
			if( i == 0 ) {
				out += "'" + entries[ i ] + "'";
			} else {
				out += ",'" + entries[ i ] + "'";
			}
			
		}
		out += ")"; 
		return out; 
	}
	
}

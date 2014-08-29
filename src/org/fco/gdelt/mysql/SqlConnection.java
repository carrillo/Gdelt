package org.fco.gdelt.mysql;

import java.sql.Connection;
import java.sql.SQLException;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

/**
 * Manipulating SQL connections. 
 *  
 * @author fernando carrillo (fernando@carrillo.at)
 *
 */
public class SqlConnection 
{
	/**
	 * Open connection to a SQL database/  
	 * 
	 * @param url: Address of the database
	 * @param user: UserId
	 * @param passwd: Password
	 * @return Established connection
	 * @throws SQLException
	 */
	public static Connection getConnection( final String url, final String user, final String passwd ) throws SQLException {
		MysqlDataSource dataSource = new MysqlDataSource(); 
		
		dataSource.setUrl( url );
		dataSource.setUser( user );
		dataSource.setPassword( passwd );
		
		return dataSource.getConnection(); 
	}
	
	/**
	 * Close connection
	 * 
	 * @param connection
	 * @throws SQLException
	 */
	public static void closeConnection( final Connection connection ) throws SQLException {
		connection.close();  
	}
	
}

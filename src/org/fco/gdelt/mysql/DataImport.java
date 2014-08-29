package org.fco.gdelt.mysql;

import inputOutput.TextFileAccess;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Copied and modified from http://www.vogella.com/tutorials/MySQLJava/article.html
 * @author carrillo
 *
 */
public class DataImport 
{
	private java.sql.Connection connection = null;
	private Statement statement = null;
	private ResultSet resultSet = null;
	
	private GdeltMainTable table; 
	
	public void testStatements() throws SQLException
	{	
		// statements allow to issue SQL queries to the database
		statement = connection.createStatement();
		
		// resultSet gets the result of the SQL query
		try {			
			statement.executeUpdate( "INSERT INTO test.year (value) VALUES ( 1982  );" ); 
		} catch (Exception e) {
			e.printStackTrace();
		}
	    resultSet = statement.executeQuery("select * from test.year");
	    
	    while( resultSet.next() ){
            System.out.println( "Year=" + resultSet.getInt("value") );
        }
		
	}
	
	public void importData( final File dataFile ) throws IOException { 
		
		table = new GdeltMainTable( connection, false ); 
		
		BufferedReader in = TextFileAccess.openFileRead( dataFile );
		int line = 0; 
		try {
			while( in.ready() ) {
				if( line % 1000  == 0 ) {
					System.out.println( "Line " + line + " of file " + dataFile.getName() ); 
				}
				
				importLine( in.readLine().split("\t") );
				
				line++; 
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	/**
	 * Import line of gdelt entry to database; 
	 * 
	 * each line of gdelt has the following fields: 
	 * 
	 *  Id 0:            GLOBALEVENTID	
	 *  Date 1-4:        SQLDATE	MonthYear	Year	FractionDate	
	 *  Actor1 5-14:     Actor1Code	Actor1Name	Actor1CountryCode	Actor1KnownGroupCode	Actor1EthnicCode	Actor1Religion1Code	Actor1Religion2Code	Actor1Type1Code	Actor1Type2Code	Actor1Type3Code	
	 *  Actor2 15-24:    Actor2Code	Actor2Name	Actor2CountryCode	Actor2KnownGroupCode	Actor2EthnicCode	Actor2Religion1Code	Actor2Religion2Code	Actor2Type1Code	Actor2Type2Code	Actor2Type3Code	
	 *  Event 25-30:     IsRootEvent	EventCode	EventBaseCode	EventRootCode	QuadClass	GoldsteinScale	
	 *  Media 31-34:     NumMentions	NumSources	NumArticles	AvgTone	
	 *  Actor1Geo 35-41  Actor1Geo_Type	Actor1Geo_FullName	Actor1Geo_CountryCode	Actor1Geo_ADM1Code	Actor1Geo_Lat	Actor1Geo_Long	Actor1Geo_FeatureID	
	 *  Actor2Geo 42-48  Actor2Geo_Type	Actor2Geo_FullName	Actor2Geo_CountryCode	Actor2Geo_ADM1Code	Actor2Geo_Lat	Actor2Geo_Long	Actor2Geo_FeatureID	
	 *  ActionGeo 49-55  ActionGeo_Type	ActionGeo_FullName	ActionGeo_CountryCode	ActionGeo_ADM1Code	ActionGeo_Lat	ActionGeo_Long	ActionGeo_FeatureID	
	 *  DateAdded 56:    DATEADDED	
	 *  SourceUrl 57:    SOURCEURL
	 * @param entries
	 */
	private void importLine( final String[] entries ) throws SQLException { 
		
		//assert ( entries.length == 58 ) : new String( "Unexpected number of features: " + Arrays.toString( entries ) ) ; 
		
		if( entries.length == 58 ) {
			table.getKey( entries );
		} else if ( entries.length == 57 ) {
			table.getKey( addEmptyColumn( entries ) ); 
		}
		
		
	}
	
	/**
	 * Patch empty columns for entries without source column. 
	 * 
	 * @param entries
	 * @return
	 */
	private String[] addEmptyColumn( final String[] entries ) {
		final String[] out = new String[ entries.length + 1 ];
		
		for( int i = 0; i < entries.length; i++)
		{
			out[ i ] = entries[ i ]; 
		}
		out[ out.length - 1 ] = ""; 
		
		return out; 
	}
	
		
	/**
	 * Open Connection to SQL database 
	 * @throws SQLException
	 */
	private void openConnection() throws SQLException {
		
		//final String url = "jdbc:mysql://172.29.13.226:3306";
		final String ipAndPort = "172.29.13.226:3306"; 
		final String user = "root"; 
		final String password = "password"; 
		
		openConnection( ipAndPort, user, password );
		//connection = SqlConnection.getConnection( url, user, password ); 
	}
	private void openConnection( final String ipAndPort, final String user, final String password ) throws SQLException { 
		
		final String url = "jdbc:mysql://" + ipAndPort; 
		connection = SqlConnection.getConnection( url, user, password ); 
	}
	
	
	public static void printSQLException(SQLException ex) {

	    for (Throwable e : ex) {
	        if (e instanceof SQLException) {
	            

	                e.printStackTrace(System.err);
	                System.err.println("SQLState: " +
	                    ((SQLException)e).getSQLState());

	                System.err.println("Error Code: " +
	                    ((SQLException)e).getErrorCode());

	                System.err.println("Message: " + e.getMessage());

	                Throwable t = ex.getCause();
	                while(t != null) {
	                    System.out.println("Cause: " + t);
	                    t = t.getCause();
	                }
	            
	        }
	    }
	}
	
	private void writeResultSet(ResultSet resultSet) throws SQLException {
	    // resultSet is initialised before the first data set
	    while (resultSet.next()) {
	      // it is possible to get the columns via name
	      // also possible to get the columns via the column number
	      // which starts at 1
	      // e.g., resultSet.getSTring(2);
	      String user = resultSet.getString("myuser");
	      String website = resultSet.getString("webpage");
	      String summary = resultSet.getString("summary");
	      Date date = resultSet.getDate("datum");
	      String comment = resultSet.getString("comments");
	      System.out.println("User: " + user);
	      System.out.println("Website: " + website);
	      System.out.println("Summary: " + summary);
	      System.out.println("Date: " + date);
	      System.out.println("Comment: " + comment);
	    }
	  }
	
	public static void main(String[] args) throws Exception 
	{
		final long time = System.currentTimeMillis(); 
		
		final DataImport dataImport = new DataImport();
		
		 
		String file = ""; 
		if ( args.length == 1 ) {			
			dataImport.openConnection();
			file = args[ 0 ]; 
		} else if ( args.length == 4 ) {
			final String host = args[ 0 ]; 
			final String user = args[ 1 ]; 
			final String password = args[ 2 ]; 
			dataImport.openConnection(host, user, password);
			
			file = args[ 3 ]; 
		} else {
			final String info = "Import data into initiated gdelt mysql database.\n" + 
					"Please provide the following arguments in the exact order.\n" + 
					"host - ip and port of the mysql database (e.g. 172.29.13.226:3306)\n" +
					"user - username with granted INSERT rights\n" +
					"password - the password for the given username\n" + 
					"inputFile - the gdelt data you want to insert.\n" + 
					"An example for a valid import call is:\n" + 
					"java -jar -Xms2g -Xmx4g dataImport.jar 172.29.13.226:3306 myUser myPassword 20131107.export.CSV";
			System.err.println( info ); 
			System.exit( 1 );
		}
		
		dataImport.importData( new File( file ) );
	
		System.out.println( "Done in " + ( System.currentTimeMillis() - time )/1000 + "s" ); 
		
		SqlConnection.closeConnection( dataImport.connection );	 
	}
}

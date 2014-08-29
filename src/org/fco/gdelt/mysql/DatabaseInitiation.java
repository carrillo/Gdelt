package org.fco.gdelt.mysql;

import inputOutput.TextFileAccess;

import java.io.BufferedReader;
import java.io.File;
import java.sql.Connection;

/**
 * Initiates the database with static tables. 
 * 
 * Values stored in these tables are not expected to be updated during the 
 * main data population.
 * 
 *  
 * @author fernando carrillo (fernando@carrillo.at)
 *
 */
public class DatabaseInitiation 
{
	private Connection connection; 
	private String resourceDirectory; 
	
	public DatabaseInitiation( final Connection connection, final String resourceDirectory )  {
		this.connection = connection;   
		this.resourceDirectory = resourceDirectory; 
	}
	
	/**
	 * Preload tables stored in the resource directory. 
	 * 
	 * These include: 
	 * - Gdelt.Country
	 * - Gdelt.Ethnic
	 * - Gdelt.EventCode
	 * - Gdelt.GoldsteinScale
	 * - Gdelt.KnownGroup 
	 * - Gdelt.Religion 
	 * - Gdelt.Type
	 * - Gdelt.QuadClass 
	 * - Gdelt.GeoType
	 * 
	 * It primes the database with a few lines of real data, to check if everything goes smooth.  
	 * 
	 * @throws Exception
	 */
	public void preLoadTables() throws Exception {
		
		System.out.println( "Loading gdelt.Country" ); 
		new TableFromFile( "gdelt.Country", new String[]{ "CountryId", "CAMEOCountryCode", "Label" }, connection, 
				new File( resourceDirectory + "CAMEO.country.txt" ), false ); 
		
		System.out.println( "Loading gdelt.Ethnic" ); 
		new TableFromFile( "gdelt.Ethnic", new String[]{ "EthnicId", "CAMEOEthnicCode", "Label" }, connection, 
				new File( resourceDirectory + "CAMEO.ethnic.txt" ), false ); 
		
		System.out.println( "Loading gdelt.EventCode" ); 
		new TableFromFile( "gdelt.EventCode", new String[]{ "EventCodeId", "CAMEOEventCode", "Label" }, connection, 
				new File( resourceDirectory + "CAMEO.eventcodes.txt" ), false );
		
		System.out.println( "Loading gdelt.GoldsteinScale" ); 
		new TableFromFile( "gdelt.GoldsteinScale", new String[]{ "GoldsteinScaleId", "CAMEOEventCode", "Value" }, connection, 
				new File( resourceDirectory + "CAMEO.goldsteinscale.txt" ), false );
		
		System.out.println( "Loading gdelt.KnownGroup" ); 
		new TableFromFile( "gdelt.KnownGroup", new String[]{ "KnownGroupId", "CAMEOKnownGroupCode", "Label" }, connection, 
				new File( resourceDirectory + "CAMEO.knowngroup.txt" ), false );
		
		System.out.println( "Loading gdelt.Religion" ); 
		new TableFromFile( "gdelt.Religion", new String[]{ "ReligionId", "CAMEOReligionCode", "Label" }, connection, 
				new File( resourceDirectory + "CAMEO.religion.txt" ), false );
		
		System.out.println( "Loading gdelt.Type" ); 
		new TableFromFile( "gdelt.Type", new String[]{ "TypeId", "CAMEOTypeCode", "Label" }, connection, 
				new File( resourceDirectory + "CAMEO.type.txt" ), false );
		
		System.out.println( "Loading gdelt.QuadClass" ); 
		new TableFromFile( "gdelt.QuadClass", new String[]{ "QuadClassId", "QuadClass", "Label" }, connection, 
				new File( resourceDirectory + "quadClass.txt" ), false );
		
		System.out.println( "Loading gdelt.GeoType" ); 
		new TableFromFile( "gdelt.GeoType", new String[]{ "GeoTypeId", "GeoType", "Label" }, connection, 
				new File( resourceDirectory + "geoType.txt" ), false );
	
		primeDatabase( new File( resourceDirectory + "databasePrimer.csv" ) );
	}
	
	private void primeDatabase( final File primingData ) throws Exception {
		GdeltMainTable table = new GdeltMainTable( connection, false ); 
		BufferedReader read = TextFileAccess.openFileRead( primingData ); 
		while( read.ready() ) {
			table.getKey( read.readLine().split("\t") ); 
		}
		
		connection.createStatement().executeUpdate( "DELETE FROM gdelt.GdeltMain" ); 
	}
	
	//Getter 
	public Connection getConnection(){ return this.connection; } 
	
	public static void main(String[] args) throws Exception
	{
		if( args.length == 4 ) {
			//final String url = "jdbc:mysql://172.29.13.226:3306";
			//final String user = "root"; 
			//final String passwd = "password";
			//final String resourceDirectory = "/Users/carrillo/workspace/Testing/resourcesSql/";
			
			final String host = "jdbc:mysql://" + args[ 0 ];
			final String user = args[ 1 ]; 
			final String passwd = args[ 2 ];
			final String resourceDirectory = args[ 3 ];
			
			DatabaseInitiation dbi = new DatabaseInitiation( SqlConnection.getConnection( host, user, passwd ), resourceDirectory );
			dbi.preLoadTables(); 
			SqlConnection.closeConnection( dbi.connection );
			
		} else {
			final String info = "Initiate gdelt database.\n" + 
					"Please provide the following arguments in the exact order.\n" + 
					"host - ip and port of the mysql database (e.g. 172.29.13.226:3306)\n" +
					"user - username with granted INSERT rights\n" +
					"password - the password for the given username\n" + 
					"inputDir - the directory containing the static tables to import.\n" + 
					"An example for a valid initiation call is:\n" + 
					"java -jar databaseInitiation.jar 172.29.13.226:3306 myUser myPassword /Users/myUser/resourcesSql/";
			System.err.println( info ); 
			System.exit( 1 );
		}
	}

}

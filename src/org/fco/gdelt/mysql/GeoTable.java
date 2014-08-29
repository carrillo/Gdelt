package org.fco.gdelt.mysql;

import java.sql.Connection;
import java.sql.SQLException;
/**
 * Geo data holds the following subtables
 *  
+--------------+----------------------+------+-----+---------+----------------+
| Field        | Type                 | Null | Key | Default | Extra          |
+--------------+----------------------+------+-----+---------+----------------+
| GeoId        | int(10) unsigned     | NO   | PRI | NULL    | auto_increment |
| GeoTypeId    | tinyint(3) unsigned  | YES  | MUL | NULL    |                |
| GeoNameId    | int(10) unsigned     | YES  | MUL | NULL    |                |
| GeoCountryId | smallint(5) unsigned | YES  | MUL | NULL    |                |
| ADM1CodeId   | int(10) unsigned     | YES  | MUL | NULL    |                |
| LocationId   | int(10) unsigned     | YES  | MUL | NULL    |                |
| FeatureId    | int(10) unsigned     | YES  | MUL | NULL    |                |
+--------------+----------------------+------+-----+---------+----------------+
 *
 * @author fernando carrillo (fernando@carrillo.at)
 *
 */
public class GeoTable extends Table {

	private Table type; 
	private Table name; 
	private Table country; 
	private Table adm1Code;
	private Table location; 
	private Table feature; 
	
	/**
	 * Define Table and subtable characteristics. 
	 *  
	 * @param connection
	 */
	public GeoTable( final Connection connection ) {
		super( "gdelt.Geo", getColumnNames(), connection, false );
		
		this.type = new Table( "gdelt.GeoType", new String[]{ "GeoTypeId", "GeoType" }, connection, false );
		this.name = new Table( "gdelt.GeoName", new String[]{ "GeoNameId", "Label" }, connection, false );
		this.country = new Table( "gdelt.GeoCountry", new String[]{ "GeoCountryId", "Code" }, connection, false );
		this.adm1Code = new Table( "gdelt.ADM1Code", new String[]{ "AdmCodeId", "Code" }, connection, false );
		this.location = new LocationTable( connection );  
		this.feature = new Table("gdelt.GeoFeature", new String[]{ "GeoFeatureId", "Value" }, connection, false ); 
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
		final String[] keys = new String[ tableEntry.length - 1];
		
		keys[ 0 ] = type.getKey( new String[]{ tableEntry[ 0 ] } );
		keys[ 1 ] = name.getKey( new String[]{ tableEntry[ 1 ].replace("'", "" ) } );
		keys[ 2 ] = country.getKey( new String[]{ tableEntry[ 2 ] } );
		keys[ 3 ] = adm1Code.getKey( new String[]{ tableEntry[ 3 ] } );
		keys[ 4 ] = location.getKey( new String[]{ tableEntry[ 4 ], tableEntry[ 5 ] } ); 
		keys[ 5 ] = feature.getKey( new String[]{ tableEntry[ 6 ] } ); 
		 
		return super.getKey( keys ); 
	}

	
	private static String[] getColumnNames() {
		return new String[]{ "GeoId", "GeoTypeId", "GeoNameId", "GeoCountryId", "ADM1CodeId", "LocationId", "FeatureId"  }; 
	}

}

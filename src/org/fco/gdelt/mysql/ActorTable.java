package org.fco.gdelt.mysql;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Actor table refers to subtables to hold values of: 
 * 
+--------------+----------------------+------+-----+---------+----------------+
| Field        | Type                 | Null | Key | Default | Extra          |
+--------------+----------------------+------+-----+---------+----------------+
| ActorId      | int(10) unsigned     | NO   | PRI | NULL    | auto_increment |
| ActorCodeId  | int(10) unsigned     | YES  | MUL | NULL    |                |
| ActorNameId  | int(10) unsigned     | YES  | MUL | NULL    |                |
| CountryId    | smallint(5) unsigned | YES  | MUL | NULL    |                |
| KnownGroupId | smallint(5) unsigned | YES  | MUL | NULL    |                |
| EthnicId     | smallint(5) unsigned | YES  | MUL | NULL    |                |
| Religion1Id  | smallint(5) unsigned | YES  | MUL | NULL    |                |
| Religion2Id  | smallint(5) unsigned | YES  | MUL | NULL    |                |
| Type1Id      | smallint(5) unsigned | YES  | MUL | NULL    |                |
| Type2Id      | smallint(5) unsigned | YES  | MUL | NULL    |                |
| Type3Id      | smallint(5) unsigned | YES  | MUL | NULL    |                |
+--------------+----------------------+------+-----+---------+----------------+
 *
 * @author fernando carrillo (fernando@carrillo.at)
 *
 */
public class ActorTable extends Table {

	private Table code;  
	private Table name; 
	private Table country; 
	private Table knownGroup; 
	private Table ethnic;
	private Table religion; 
	private Table type; 
	
	/**
	 * Define Table and subtable characteristics. 
	 *  
	 * @param connection
	 */
	public ActorTable( Connection connection ) {
		
		super( "gdelt.Actor", new String[] { 
				"ActorId",
				"ActorCodeId", "ActorNameId", "CountryId", "KnownGroupId", "EthnicId", 
				"Religion1Id", "Religion2Id", 
				"Type1Id", "Type2Id", "Type3Id"
				}, 
				connection, false );
		
		code = new Table( "gdelt.ActorCode", new String[]{ "ActorCodeId", "ActorCode" }, connection, false );
		name = new Table( "gdelt.ActorName", new String[]{ "ActorNameId", "Value" }, connection, false );
		country = new Table( "gdelt.Country", new String[]{ "CountryId", "CAMEOCountryCode" }, connection, false );
		knownGroup = new Table( "gdelt.KnownGroup", new String[]{ "KnownGroupId", "CAMEOKnownGroupCode" }, connection, false );
		ethnic = new Table( "gdelt.Ethnic", new String[]{ "EthnicId", "CAMEOEthnicCode" }, connection, false );
		religion = new Table( "gdelt.Religion", new String[]{ "ReligionId", "CAMEOReligionCode" }, connection, false );
		type = new Table( "gdelt.Type", new String[]{ "TypeId", "CAMEOTypeCode" }, connection, false );
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
		
		final String[] keys = new String[ tableEntry.length ]; 
		
		keys[ 0 ] = code.getKey( new String[]{ tableEntry[ 0 ] } );
		keys[ 1 ] = name.getKey( new String[]{ tableEntry[ 1 ].replace("'", "") } );
		keys[ 2 ] = country.getKey( new String[]{ tableEntry[ 2 ] } );
		keys[ 3 ] = knownGroup.getKey( new String[]{ tableEntry[ 3 ] } );
		keys[ 4 ] = ethnic.getKey( new String[]{ tableEntry[ 4 ] } );
		keys[ 5 ] = religion.getKey( new String[]{ tableEntry[ 5 ] } );
		keys[ 6 ] = religion.getKey( new String[]{ tableEntry[ 6 ] } );
		keys[ 7 ] = type.getKey( new String[]{ tableEntry[ 7 ] } );
		keys[ 8 ] = type.getKey( new String[]{ tableEntry[ 8 ] } );
		keys[ 9 ] = type.getKey( new String[]{ tableEntry[ 9 ] } );
		
		return super.getKey( keys );
	}

}

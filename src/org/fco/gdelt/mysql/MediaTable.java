package org.fco.gdelt.mysql;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Media table hold subtables for: 
 * 
+---------------+------------------+------+-----+---------+----------------+
| Field         | Type             | Null | Key | Default | Extra          |
+---------------+------------------+------+-----+---------+----------------+
| MediaId       | int(10) unsigned | NO   | PRI | NULL    | auto_increment |
| NumMentionsId | int(10) unsigned | YES  | MUL | NULL    |                |
| NumSourcesId  | int(10) unsigned | YES  | MUL | NULL    |                |
| NumArticlesId | int(10) unsigned | YES  | MUL | NULL    |                |
| AvgToneId     | int(10) unsigned | YES  | MUL | NULL    |                |
+---------------+------------------+------+-----+---------+----------------+
 * 
 * The getKey method overrides the super method in order to implement recursive key retrieval. 
 * 
 * @author fernando carrillo (fernando@carrillo.at)
 *
 */
public class MediaTable extends Table {
	
	private Table numMentions;
	private Table numSources;
	private Table numArticles; 
	private Table avgTone; 

	/**
	 * Define Table and subtable characteristics. 
	 *  
	 * @param connection
	 */
	public MediaTable( final Connection connection ) {
		super( "gdelt.Media", new String[] {
				"MediaId", "NumMentionsId", "NumSourcesId", "NumArticlesId", "AvgToneId" }
		, connection, false);
		
		numMentions = new Table( "gdelt.NumMentions" , new String[]{ "NumMentionsId", "Value" }, connection, false );
		numSources = new Table( "gdelt.NumSources" , new String[]{ "NumSourcesId", "Value" }, connection, false );
		numArticles = new Table( "gdelt.NumArticles" , new String[]{ "NumArticlesId", "Value" }, connection, false );
		avgTone = new Table( "gdelt.AvgTone" , new String[]{ "AvgToneId", "Value" }, connection, false );
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
		
		keys[ 0 ] = numMentions.getKey( new String[]{ tableEntry[ 0 ] } );
		keys[ 1 ] = numSources.getKey( new String[]{ tableEntry[ 1 ] } );
		keys[ 2 ] = numArticles.getKey( new String[]{ tableEntry[ 2 ] } );
		
		final double avgToneVal = Math.round( Double.parseDouble( tableEntry[ 3 ] ) * 100 ) / (double) 100;
		keys[ 3 ] = avgTone.getKey( new String[]{ String.valueOf( avgToneVal ) } );
		
		return super.getKey( keys ); 
	}

	
}

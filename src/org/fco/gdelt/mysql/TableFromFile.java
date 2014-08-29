package org.fco.gdelt.mysql;

import inputOutput.TextFileAccess;

import java.io.BufferedReader;
import java.io.File;
import java.sql.Connection;

/**
 * Populates table from file. 
 * 
 * @author fernando carrillo (fernando@carrillo.at)
 *
 */
public class TableFromFile extends Table {

	/**
	 * Define Table characteristics. 
	 * 
	 * @param id
	 * @param columnNames
	 * @param connection
	 * @param inputFile
	 * @param keyPartOfData
	 */
	public TableFromFile(String id, String[] columnNames, Connection connection, final File inputFile, final boolean keyPartOfData ) {
		super(id, columnNames, connection, keyPartOfData );
		loadFromFile( inputFile, true, "\t" );
	}
	
	/**
	 * Populate database with data from file. 
	 * 
	 * @param inputFile Path to the input file. Input must be in ascii.  
	 * @param header True if header is present 
	 * @param sep Separator character used in the ascii table. 
	 */
	private void loadFromFile( final File inputFile, final boolean header, final String sep ) {
		
		BufferedReader in = TextFileAccess.openFileRead( inputFile ); 
		
		try {
			String entries[];
			int count = 0; 
			while( in.ready() ) {
				entries = in.readLine().replace( "'", "" ).split( sep ); 
				 
				count++; 
				
				if( header && count == 1 )
					continue; 
				
				getKey( entries ); 
			}
			
		} catch (Exception e) {
			e.printStackTrace(); 
		}
	}
}

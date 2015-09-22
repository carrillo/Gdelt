package org.fco.gdelt.hadoop;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class MapperTest extends Mapper<LongWritable, Text, Text, IntWritable> {

	
	@Override
	protected void map(LongWritable key, Text value, Context context )
	
		throws IOException, InterruptedException {
		
		final String[] entries = value.toString().split("\t");
		
		final String year = entries[ 3 ]; 
		final int quadClass = Integer.parseInt( entries[ 30 ] ); 
		
		context.write( new Text( year ) , new IntWritable( quadClass ) );
	}
	
}

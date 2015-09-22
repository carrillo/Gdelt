package org.fco.gdelt.hadoop;

import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import com.mysql.jdbc.util.Base64Decoder.IntWrapper;

/**
 * Example from http://hadoop.apache.org/docs/current/hadoop-mapreduce-client/hadoop-mapreduce-client-core/MapReduceTutorial.html
 * 
 * @author fernando carrillo (fernando@carrillo.at)
 *
 */
public class WordCount1 
{
	public static class TokenizerMapper extends Mapper<Object, Text, Text, IntWritable> {
		
		private final static IntWritable one = new IntWritable( 1 );
		private Text word = new Text(); 
		
		/**
		 * Map words occuring in the input object. This method does not count the words, just catalogues 
		 * them. 
		 */
		@Override
		public void map( Object key, Text value, Context context ) throws IOException, InterruptedException {
			StringTokenizer itr = new StringTokenizer( key.toString() ); 
			while( itr.hasMoreTokens() ) {
				word.set( itr.nextToken() );
				context.write( word, one );
			}
		}
	}
	
	public static class IntSumReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
		
		private IntWritable result = new IntWritable();
		
		/**
		 * Sum over count for a key
		 */
		public void reduce( Text key, Iterable<IntWritable> values, org.apache.hadoop.mapreduce.Mapper.Context context ) 
				throws IOException, InterruptedException {
			int sum = 0; 
			for( IntWritable val : values ) {
				sum += val.get(); 
			}
			result.set( sum );
			context.write( key, result );
		}
	}
	
	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration(); 
		Job job = Job.getInstance( conf, "word count" ); 
		job.setJarByClass( WordCount1.class );
		job.setMapperClass( TokenizerMapper.class );
		job.setCombinerClass( IntSumReducer.class );
		job.setReducerClass( IntSumReducer.class );
		job.setOutputKeyClass( Text.class );
		job.setOutputValueClass( IntWrapper.class );
		
		FileInputFormat.addInputPath( job, new Path( args[ 0 ] ) );
		FileOutputFormat.setOutputPath( job, new Path( args[ 1 ] ) );
		
		System.exit( job.waitForCompletion( true ) ? 0 : 1 );
	}
}

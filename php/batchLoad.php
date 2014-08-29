#!/usr/bin/php
<?php
//var_dump($argv);
if( count( $argv ) != 6 ) {
	echo "\n Script loads all csv files in the current directory in a specified mysql database.";
	echo "\n Please provide host (e.g. 172.29.13.232 arg1), database (e.g. gdelt, arg2), table (e.g. GDELT_HISTORICAL, arg3), userid (arg4) and passwd (arg5)\n\n";  
} else {
	$host = $argv[1];
	$dbase = $argv[2]; 
	$table = $argv[3]; 
	$user = $argv[4]; 
	$pass = $argv[5]; 

	//Establish connection
	$conn = mysqli_init();
	mysqli_options($conn, MYSQLI_OPT_LOCAL_INFILE, true);
	mysqli_real_connect($conn,$host,$user,$pass,$dbase);

	//Load files 
	
	$files = glob('*.csv');
	foreach($files as $file) {
        	echo "Loading $file into table $table of database $dbase hosted on $host\n";
        $query = "LOAD DATA LOCAL INFILE '".$file."' INTO TABLE ".$table."";
        mysqli_query($conn, $query)  or die(mysqli_error($conn) );
	}
		
}
?>

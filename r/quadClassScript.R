################################
# Plot quad class evolution for nations. 
# 1. Load data
# 2. 
################################

setwd('~/Projects/Homepage/content/gdeltMysql/results/')

require( ggplot2 ); require( scales ); require( plyr ); require( reshape )


# Read quad-class count script and return a data.frame formated with Date type for easy plotting. 
#
# @param countryId the countryId string used to specify the filename. Typically the three letter country code
#
# 1. Load data
#    1a. Specify the filename from the passed parameter countryId
#    1b. Read file into data fram 
# 2. Reformat data for proper typing. 
# 3. Type each column for later plotting
# 4. Replace numeric quadclass code to self-explanatory labels. 
getData <- function( countryId ) {
  
  
  fileName  <- paste( c( countryId, ".txt"), collapse="", sep="" ) 
  df <- read.table( fileName, sep="\t", colClasses=c("character","numeric") )
  
  # Reformat an entry into a Date class, a quadClass and a count  
  extractValues <- function( row ) {
    
    yearQuadClass <- unlist( strsplit( as.character( row[ 1 ] ), "," ) )
    date <- paste( c(yearQuadClass[ 1 ],"6","1"), sep="-", collapse="-" )
    
    return( c( date, yearQuadClass[ 2 ], row[ 2 ] ) )
  }
  
  # Get data.frame with split values for year and date. 
  df <- data.frame( t( apply( df, MARGIN=1, extractValues ) ) )
  names( df ) <- c( "date", "quadClass", "count")

  # Type variables. Be careful with typing factor to numeric. 
  df$date <- as.Date( df$date, "%Y-%m-%d" )
  df$quadClass <- as.character( df$quadClass )
  df$count <- as.numeric( as.character( df$count ) )
  
  # Replace quad class by labels 
  df$quadClass[ df$quadClass == 1 ]  <- "1 - Verbal Cooperation"
  df$quadClass[ df$quadClass == 2 ]  <- "2 - Material Cooperation"
  df$quadClass[ df$quadClass == 3 ]  <- "3 - Verbal Conflict"
  df$quadClass[ df$quadClass == 4 ]  <- "4 - Material Conflict"
  
  df$quadClass <- as.factor( df$quadClass )
  
  return( df )  
}

# Makes quadcount relative to event count per year to enable interyear and intercountry comparison. 
makeQuadCountRelative <- function( df ) {
  
  # Count events per year. The for loop is not good R style, but very short. 
  countsPerYear <- data.frame( date=as.Date( unique( df$date ) ), sumCount=rep(-1,length( unique( df$date ) ) ) )
  for( i in 1:nrow( countsPerYear ) ) {
    countsPerYear$sumCount[ i ] <- sum( df[ df$date == countsPerYear$date[ i ], 3 ] )
  }
  
  # Merge df and countsperyear 
  df <- merge( df, countsPerYear, by="date")
  df$fraction <- df$count / df$sumCount
  return( df )
}

#
# Merge two quadclass summary (count per year) into one
# 
# @param data1 quadClass summary provided by getData 
# @param data2 quadClass summary provided by getData 
#
# 1. Merge data1 and data2 on date and quadclass
# 2. Replace missing values with 0 
# 3. Sum counts for both data1 and data2 and return new data.frame
#
mergeQuadClassSummary <- function( data1, data2 ) {
  m <- merge( data1, data2, by=c("date","quadClass"), all.x=T, all.y=T )
  m[ is.na(m) ]  <- 0
  return( data.frame( date=m$date, quadClass=m$quadClass, count=(m$count.x + m$count.y) ) )
}

# Collapses the quadclass code from 4 to 2. Joining verbal and material cooperation or conflict respectively. 
#
# 1. Rename Quadclass 
# 2. Sum counts for identical Year quadClass combinations. 
collapseQuadClassCode <- function( data ) {
  data$quadClass <- as.character( data$quadClass )
  data$quadClass[ grep( "Cooperation", x=data$quadClass ) ]  <- "Cooperation"
  data$quadClass[ grep( "Conflict", x=data$quadClass ) ]  <- "Conflict"
  
  ids <- paste( data$date, data$quadClass )
  uniqueIds  <- unique( ids ) 
  i <- 1 
  df <- data.frame( date=data[ which( ids == uniqueIds[ i ] ), 1 ][ 1 ], quadClass=data[ which( ids == uniqueIds[ i ] ), 2 ][ 1 ],  count=sum( data[ which( ids == uniqueIds[ i ] ), 3 ] ) )
  for( i in 2:length( uniqueIds ) ) {
    df <- rbind( df, data.frame( date=data[ which( ids == uniqueIds[ i ] ), 1 ][ 1 ], quadClass=data[ which( ids == uniqueIds[ i ] ), 2 ][ 1 ],  count=sum( data[ which( ids == uniqueIds[ i ] ), 3 ] ) ) )
  }
  
  return( df )
}

# Plot absolute quadclass count by year 
#
# @param data quadclass summary provided by getData or mergeQuadClassSummary
# @param label label for the title of the plot. 
#
# 1. Specify which aesthetic are plotted. 
# 2. Specify title and y-axis name as well as legend position
# 3. Invoke quadClassPlot to generate the plot 
#
plotAbsolutQuadClassEvolution <- function( data, label ) {
  aes <- aes( x=date, y=count, colour=quadClass )
  title <- paste( c(label, ": Events per year per Quad-Class"), collapse="" )
  yName  <- 'Number of events (Log10 scale)'
  legendPos  <-  c(0,1)
  return( quadClassPlot( data, aes, yName, legendPos, title ) )
} 

# Plot relative quadclass count by year as fraction of total events 
#
# @param data quadclass summary provided by getData or mergeQuadClassSummary
# @param label label for the title of the plot. 
# @param yScale optional limits for y-axis
#
# 1. Specify which aesthetic are plotted. 
# 2. Specify title and y-axis name as well as legend position
# 3. Invoke quadClassPlot to generate the plot 
#
plotRelativeQuadClassEvolution <- function( data, label, yScale=NA ) {
  aes <- aes( x=date, y=fraction, colour=quadClass )
  title <- paste( c(label, ": Fraction of events per year per Quad-Class"), collapse="" )
  yName  <- 'Fraction of events (Log10 scale)'
  legendPos  <-  c(0,0)
  return( quadClassPlot( data, aes, yName, legendPos, title, yScale ) )
} 

# Plot quadclass vs year. 
#
# 1. Define color palette for matching colors for similar classification. 
#
quadClassPlot <- function( data, aes, yName, legendPos, title, yScale=NA ) {
  # Define colors
  if( length( unique( as.character( data$quadClass ) ) ) == 2 ) {
    cbPalette <- c("blue", "red" )
  } else {
    cbPalette <- c("dodgerblue", "blue", "red", "red4" )
  }
  
  # Plot data
  p <- ggplot( data, aes )
  p <- p + geom_line()
  p <- p + geom_point()
  p <- p + scale_colour_manual(values=cbPalette)
  
  # Format axis
  p <- p + scale_x_date(name='Year')
  p <- p + labs(title=title )
  
  if( is.na( yScale ) ) {
    p <- p + scale_y_log10( labels = comma, name=yName )
  } else {
    p <- p + scale_y_log10( labels = comma, name=yName, limits=yScale )
  }
  p <- p + annotation_logticks(sides = "l")
  
  # Position legend
  p <- p + theme(legend.justification=legendPos, legend.position=legendPos )
  
  return( p )
}

# Generate quadclass summary for Isalnd and Somalia. For each country, merge data for country being Actor1 and Actor2 
isl <- mergeQuadClassSummary( getData( countryId="isl1" ), getData( countryId="isl2" ) )
som <- mergeQuadClassSummary( getData( countryId="som1" ), getData( countryId="som2" ) )


# Plot absolute quadclass count per year for island and somalia
ggsave( plotAbsolutQuadClassEvolution( isl, "Island" ), filename="/Users/carrillo/Projects/Homepage/content/gdeltMysql/results/quadclassIslandAbs.png", width=7, height=7  )
ggsave( plotAbsolutQuadClassEvolution( som, "Somalia" ), filename="/Users/carrillo/Projects/Homepage/content/gdeltMysql/results/quadclassSomaliaAbs.png", width=7, height=7  )

# Make data relative to count per year 
isl_rel <- makeQuadCountRelative( isl )
som_rel <- makeQuadCountRelative( som )

# Plot relative quadclass count per yeawr for island and somalia 
ggsave( plotRelativeQuadClassEvolution( isl_rel, "Island", yScale=c(0.0001,1) ), filename="/Users/carrillo/Projects/Homepage/content/gdeltMysql/results/quadclassIslandRel.png", width=7, height=7  )
ggsave( plotRelativeQuadClassEvolution( som_rel, "Somalia", yScale=c(0.0001,1) ), filename="/Users/carrillo/Projects/Homepage/content/gdeltMysql/results/quadclassSomaliaRel.png", width=7, height=7  )


# Simplifiy quadclass 
isl_simple <- makeQuadCountRelative( collapseQuadClassCode( isl ) )
som_simple <- makeQuadCountRelative( collapseQuadClassCode( som ) )

ggsave( plotRelativeQuadClassEvolution( isl_simple, "Island", yScale=c(0.0001,1) ), filename="/Users/carrillo/Projects/Homepage/content/gdeltMysql/results/quadclassIslandRelSimple.png", width=7, height=7  )
ggsave( plotRelativeQuadClassEvolution( som_simple, "Somalia", yScale=c(0.0001,1) ), filename="/Users/carrillo/Projects/Homepage/content/gdeltMysql/results/quadclassSomaliaRelSimple.png", width=7, height=7  )

mean( isl_simple[ isl_simple$quadClass == "Conflict", ]$fraction )
mean( som_simple[ som_simple$quadClass == "Conflict", ]$fraction )

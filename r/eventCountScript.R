##########################
# Plot event count summaries 
##########################

# Dependencies for plotting
require( ggplot2 ); require( scales )

# Load data
# 1. Read data from file 
# 2. Name columns (MySQL does not print header by default)
# 3. Remove 2014 entries. 
setwd('~/Projects/Homepage/content/gdeltMysql/results/')
read.table( 'eventsPerYear.txt', sep='\t' ) -> eventCount
names( eventCount ) <- c("year","mean","sd","min","max" )
eventCount <- eventCount[ eventCount$yea != 2014, ]

# Reformat year into R Date class
getDate <- function( row ) { return( paste( c(row[1],"6","1"), sep="-", collapse="-" ) ) }
eventCount$date <- apply(X=eventCount,MARGIN=1,getDate)
eventCount$date <- as.Date( eventCount$date, "%Y-%m-%d" )

# Plot mean and STDEV of mean. 
quartz()
p <- ggplot( eventCount, aes( x=date, y=mean ) )
p <- p + scale_x_date(name='Year')
p <- p + labs(title="Mean events per day")
p <- p + scale_y_log10( labels = comma, name='Events (Log10 scale)' )
p <- p + annotation_logticks(sides = "l")
p  <- p + geom_line(color='red')
p <- p + geom_line(mapping = aes(y = (mean+sd)), lty = "dashed")
p <- p + geom_line(mapping = aes(y = (mean-sd)), lty = "dashed")
#p <- p + geom_line(mapping = aes(y = (max)), lty = "dotted")
#p <- p + geom_line(mapping = aes(y = (min)), lty = "dotted")
ggsave(p, file="eventsperyear.png")
show( p )

package com.pugh.sockso.music.scheduling;

import com.pugh.sockso.Constants;
import com.pugh.sockso.Properties;
import com.pugh.sockso.music.indexing.Indexer;

import java.util.Date;
import java.util.Calendar;

import org.apache.log4j.Logger;

/**
 *  This scheduler accepts a cron style specification for when it should run
 *
 *  Example:
 *
 *  scheduler.cron.tab 20 * * * *
 *
 *  Will run at 20 minutes past every hour.
 *
 */

public class CronScheduler extends BaseScheduler {

    private static final Logger log = Logger.getLogger( CronScheduler.class );
    
    private static final String DEFAULT_TAB = "*/30 * * * *";

    private final Properties p;

    /**
     *  Constructorm creates a new scheduler which runs via a cron spec
     *
     *  @param indexer
     *  @param p
     *
     */

    public CronScheduler( final Indexer indexer, final Properties p ) {
        
        super( indexer );
        
        this.p = p;
        
    }

    /**
     *  Returns a boolean indicating if the indiexer should be run on the date
     *
     *  @param date
     *
     *  @return
     *
     */

    public boolean shouldRunAt( final Date date ) {

        try {

            final String spec = p.get( Constants.SCHED_CRON_TAB, DEFAULT_TAB );
            final String[] patterns = getPatterns( spec );
            final Calendar cal = Calendar.getInstance();

            cal.setTime( date );

            return

            // normal cron rules

            (
                matchesPattern( cal.get(Calendar.MINUTE), patterns[0] )                                 // minute
                && matchesPattern( cal.get(Calendar.HOUR), patterns[1] )                                // hour
                && matchesPattern( cal.get(Calendar.DAY_OF_MONTH), patterns[2] )                        // day of month
                && matchesPattern( cal.get(Calendar.MONTH) + 1, getMonth(patterns[3]) )                 // month
                && matchesPattern( cal.get(Calendar.DAY_OF_WEEK) - 1, getDayOfWeek(patterns[4]) )       // day of week
            )

            ||

            // exception: if both "day of month" and "day of week" are restricted
            // (not "*"), then either the "day of month" field (3) or the "day of
            // week" field (5) must match the current day (even though the other
            // of the two fields need not match the current day).
            // http://en.wikipedia.org/wiki/Cron#crontab_syntax

            (
                !patterns[2].equals("*") && !patterns[4].equals("*") &&
                ( matchesPattern( cal.get(Calendar.DAY_OF_MONTH), patterns[2] )
                    || matchesPattern( cal.get(Calendar.DAY_OF_WEEK) - 1, getDayOfWeek(patterns[4]) ) )
            );

        }

        catch ( final Exception e ) {
            e.printStackTrace();
            return false;
        }
        
    }

    /**
     *  Returns true if we have a match for the specified part of the spec
     *
     *  @param toMatch the number to match (eg minute, hour, day)
     *  @param pattern eg. "*", "1,2,3"
     *
     *  @return
     *
     */

    private boolean matchesPattern( final int toMatch, final String pattern ) {

        // every unit: *

        if ( pattern.equals("*") ) {
            return true;
        }

        // every period of units: */10

        else if ( pattern.contains("/") ) {
            final int divisor = Integer.parseInt( pattern.substring( 2 ) );
            return toMatch % divisor == 0;
        }

        // between certain units: 1-6
        
        else if ( pattern.contains("-") ) {
            final int dashIndex = pattern.indexOf( "-" );
            final int from = Integer.parseInt( pattern.substring(0,dashIndex) );
            final int to = Integer.parseInt( pattern.substring(dashIndex+1) );
            for ( int i=from; i<=to; i++ ) {
                if ( toMatch == i ) {
                    return true;
                }
            }
        }

        // on certain units: 1,2,3

        else if ( pattern.contains(",") ) {
            final String[] units = pattern.split( "," );
            for ( final String unit : units ) {
                if ( toMatch == Integer.parseInt(unit) ) {
                    return true;
                }
            }
        }

        // on a unit: 1

        else if ( toMatch == Integer.parseInt(pattern) ) {
            return true;
        }

        // no match

        return false;

    }

    /**
     *  Given a specification for a month translates it to numbers.  For example
     *  it could be "jan", which will become 1.
     *
     *  @param month
     *
     *  @return
     *
     */

    private String getMonth( final String month ) {
        
        final String replacements[] = {
            "jan", "1",
            "feb", "2",
            "mar", "3",
            "apr", "4",
            "may", "5",
            "jun", "6",
            "jul", "7",
            "aug", "8",
            "sep", "9",
            "oct", "10",
            "nov", "11",
            "dec", "12"
        };

        return replaceWords( month, replacements );
        
    }

    /**
     *  Given a specification for a day of the week translates it to numbers.
     *  For example it could be "mon", which will become 1.
     *
     *  @param dayOfWeek
     *
     *  @return
     *
     */

    private String getDayOfWeek( final String dayOfWeek ) {
        
        final String replacements[] = {
            "sun", "0",
            "mon", "1",
            "tue", "2",
            "wed", "3",
            "thu", "4",
            "fri", "5",
            "sat", "6"
        };

        return replaceWords( dayOfWeek, replacements )
            .replaceAll( "7", "0" ); // sunday can be 0 or 7
        
    }

    /**
     *  Replaces words in the phrase with the value of the next item (ie. they
     *  should be in pairs in the array)
     *
     *  @param phrase
     *  @param replacements
     *
     *  @return
     *
     */
    
    private String replaceWords( final String phrase, final String[] replacements ) {
        
        String newPhrase = phrase;

        for ( int i=0; i<replacements.length; i+=2 ) {
            newPhrase = newPhrase.replaceAll(
                replacements[ i ],
                replacements[ i + 1 ]
            );
        }
        
        return newPhrase;
        
    }

    /**
     *  Returns an array of patterns from the cron spec.  The spec can optionally
     *  contain any of the special @name words (eg. @yearly)
     *
     *  @param spec
     *
     *  @return
     *
     */

    private String[] getPatterns( final String spec ) {

        final String[] replacements = {
            "@yearly", "0 0 1 1 *",
            "@annually", "0 0 1 1 *",
            "@monthly", "0 0 1 * *",
            "@weekly", "0 0 * * 0",
            "@daily", "0 0 * * *",
            "@midnight", "0 0 * * *",
            "@hourly", "0 * * * *",
        };

        return replaceWords( spec, replacements ).split( " " );

    }
    
}

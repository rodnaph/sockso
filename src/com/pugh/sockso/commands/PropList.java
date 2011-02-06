
package com.pugh.sockso.commands;

import com.pugh.sockso.Properties;
import java.util.Arrays;

public class PropList extends BaseCommand {

    private final Properties p;
    
    public PropList( final Properties p ) {
        
        this.p = p;
        
    }

    public String getName() {
        
        return "proplist";
        
    }
    
    public String getDescription() {
        
        return "Lists properties";
        
    }

    /**
     *  handles the CMD_PROPLIST command to list the
     *  applications properties
     *
     */

    public String execute( String[] args ) {

        final StringBuffer sb = new StringBuffer();
        final String pattern = ( args.length > 1 ) ? args[1] : null;
        final String[] props = p.getProperties();
        final int longest = getLongestStringLength( props );

        Arrays.sort( props );

        // print header

        sb.append( " NAME" +getPadding(4,longest)+ "  VALUE\n" );
        if ( pattern != null ) {
            sb.append( "\n (containing '" +pattern+ "')\n" );
        }
        sb.append( "\n" );

        // print properties

        for ( final String prop : props ) {

            // if we have a pattern, check this property matches
            if ( pattern != null && !prop.contains(pattern) ) {
                continue;
            }

            sb.append( " " +prop + getPadding(prop.length(),longest)+ "  " + p.get(prop) );
            sb.append( "\n" );

        }

        return sb.toString();

    }

    /**
     *  returns a string of space characters enough to pad a string of the
     *  given length to be the same as a string of the longest length
     *
     *  eg.
     *  longest = "asdhasgdhjaghdj"
     *  shorter = "asd            " (with padding)
     *
     *  @param length
     *  @param longest
     *
     *  @return
     *
     */

    protected String getPadding( final int length, final int longest ) {

        String padding = "";

        for ( int i=length; i<longest; i++ )
            padding += " ";

        return padding;

    }

    /**
     *  returns the length of the longest string in the array.  if an empty array
     *  is passed in then will return 0.
     *
     *  @param strings
     *  @return
     *
     */

    protected int getLongestStringLength( final String[] strings ) {

        int longest = 0;

        for ( String string : strings ) {
            final int length = string.length();
            if ( length > longest ) {
                longest = length;
            }
        }

        return longest;

    }

    @Override
    public String[] getArguments() {

        return new String[] { "FILTER" };
        
    }

}


package com.pugh.sockso.commands;

import java.util.Vector;

public class CommandParser {
    
    /**
     *  Returns the arguments to use for the command
     *
     *  @param command
     *
     *  @return
     *
     */

    public String[] parseCommand( final String command ) {
    	
    	final Vector<String> args = new Vector<String>();
        final StringBuffer currentArgument = new StringBuffer();

        boolean inString = false;

    	for ( char c: command.toCharArray() ) {

            if ( Character.isWhitespace(c)) {
                if ( inString ) {
                    currentArgument.append( c );
                }
                else if ( currentArgument.length() > 0 ) {
                    args.add( currentArgument.toString() );
                    currentArgument.setLength( 0 );
                }
                continue;
            }

            if ( inString && c == '"' ) {
                args.add( currentArgument.toString() );
                currentArgument.setLength( 0 );
                inString = false;
                continue;
            }

            if ( c == '"' ) {
                inString = true;
                continue;
            }

            currentArgument.append( c );

    	}

    	if (currentArgument.length() > 0) {
            args.add( currentArgument.toString() );
        }

        return args.toArray(new String[0]);

    }

 }

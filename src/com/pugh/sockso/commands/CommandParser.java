
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

    	String arg = new String();
        boolean previousEscape = false;
        boolean inString = false;

    	for ( char c: command.toCharArray() ) {

            if ( Character.isWhitespace(c)) {
                if ( inString ) {
                    arg += c;
                }
                else if ( previousEscape ) {
                    arg += c;
                }
                else if ( arg.length() > 0 ) {
                    args.add( arg );
                    arg = "";
                }
                continue;
            }

            if ( c == '\\' && !previousEscape) {
                previousEscape = true;
                continue;
            }

            if ( inString && c == '"' ) {
                args.add( arg );
                arg = "";
                inString = false;
                continue;
            }

            if ( c == '"' ) {
                inString = true;
                continue;
            }

            arg += c;

            previousEscape = false;

    	}

    	if (arg.length() > 0) {
            args.add( arg );
        }

        return args.toArray(new String[0]);

    }

 }


package com.pugh.sockso.commands;

import com.pugh.sockso.tests.SocksoTestCase;

public class CommandParserTest extends SocksoTestCase {

    private CommandParser parser;

    @Override
    protected void setUp() {
        parser = new CommandParser();
    }
    public void testArgumentsAreSplitUsingWhitespace() {
        String[] args = parser.parseCommand( "propset foo bar" );
        assertEquals( "propset", args[0] );
        assertEquals( "foo", args[1] );
        assertEquals( "bar", args[2] );
    }

    public void testQuotesCanBeUsedToUseWhitespaceInArguments() {
        String[] args = parser.parseCommand( "propset \"foo foo\" \"bar bar\"" );
        assertEquals( "foo foo", args[1] );
        assertEquals( "bar bar", args[2] );
    }

    public void testAnyWhiteSpaceCanSeperateArguments() {
        String[] args = parser.parseCommand( "propset    foo\t    bar" );
        assertEquals( "propset", args[0] );
        assertEquals( "foo", args[1] );
        assertEquals( "bar", args[2] );
    }
    
}

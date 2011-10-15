
package com.pugh.sockso.commands;

import com.pugh.sockso.Properties;
import com.pugh.sockso.StringProperties;
import com.pugh.sockso.db.Database;

import com.pugh.sockso.tests.SocksoTestCase;
import com.pugh.sockso.tests.TestDatabase;
import com.pugh.sockso.tests.TestLocale;
import com.pugh.sockso.web.User;

public class CommandExecuterTest extends SocksoTestCase {

    private CommandExecuter cmd;
    
    private Properties p;
    
    private Database db;

    private TestLocale locale;

    @Override
    public void setUp() {
        p = new StringProperties();
        db = new TestDatabase();
        locale = new TestLocale();
        locale.setString( "con.desc.commands", "Usage:" );
        locale.setString( "con.msg.propertySaved", "property saved" );
        cmd = new CommandExecuter( db, p, null, locale );
    }


    public void testCommandsCanBeExecutedByName() throws Exception {
        cmd.execute( "propset foo bar" );
        assertEquals( "bar", p.get("foo") );
    }

    public void testCommandsCanBePassedParameters() throws Exception {
        cmd.execute( "propset foo bar" );
        assertEquals( "bar", p.get("foo") );
    }

    public void testErrorReportedWhenExtactNumberOfRequiredArgumentsIsNotSpecified() throws Exception {
        assertContains( cmd.execute("propset foo"), "requires" );           // too few
        assertContains( cmd.execute("propset foo bar baz"), "requires" );   // too many

    }

    public void testCommandResultIsReturnedWhenACommandIsRun() throws Exception {
        assertEquals( "property saved", cmd.execute("propset foo bar") );
    }

    public void testUsageIsReturnedWhenAnUnknownCommandIsSpecified() throws Exception {
        assertContains( cmd.execute("bloopper"), "Usage:" );
    }

    public void testCommandNamesListedInUsageReturned() throws Exception {
        assertContains( cmd.execute("help"), "propset" );
    }

    public void testCommandArgumentsListedInUsageReturned() throws Exception {
        assertContains( cmd.execute("help"), "NAME VALUE" );
    }

    public void testCommandDescriptionListedInUsageReturned() throws Exception {
        assertContains( cmd.execute("help"), "Sets a property" );
    }

    public void testABackslashCanBeUsedToEscapeSpaces() throws Exception {
        cmd.execute( "useradd r\\ od pass rod@pu-gh.com 1" );
        User user = User.find( db, 0 );
        assertEquals( "r od", user.getName() );
    }

    public void testQuotesCanBeUsedInArguments() throws Exception {
        cmd.execute( "propset foo \"bah bah\"" );
        assertEquals( "bah bah", p.get("foo") );
        cmd.execute( "propset \"foo foo\" \"baz baz baz\"" );
        assertEquals( "baz baz baz", p.get("foo foo") );
    }

}

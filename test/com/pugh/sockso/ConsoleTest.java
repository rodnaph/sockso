
package com.pugh.sockso;

import com.pugh.sockso.tests.SocksoTestCase;
import com.pugh.sockso.tests.TestLocale;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class ConsoleTest extends SocksoTestCase {

    private Properties p;
    
    @Override
    public void setUp() {
        p = new StringProperties();
        String command = "propset foo bazzle\n";
        InputStream in = new ByteArrayInputStream(command.getBytes() );
        Console c = new Console( null, p, null, System.out, in, new TestLocale());
        c.open();
    }

    public void testCommandsCanBeExecutedFromInputStream() {
        assertEquals( "bazzle", p.get("foo") );
    }

    public void testCommandOutputIsSentToTheOutputStream() {

    }

    public void testLatestVersionIsPrintedToOutputWhenItsReceived() {

    }

    public void testConsoleCanReceiveLatestVersionNotifications() {

    }

}


package com.pugh.sockso.mail;

import com.pugh.sockso.tests.SocksoTestCase;

public class MailerTest extends SocksoTestCase {
    
    public void testConstructor() {
        
        final Mailer m = new Mailer( null );
        
        assertNotNull( m );
        
    }

}

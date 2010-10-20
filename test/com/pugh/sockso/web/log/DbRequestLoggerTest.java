
package com.pugh.sockso.web.log;

import com.pugh.sockso.db.Database;
import com.pugh.sockso.tests.SocksoTestCase;
import com.pugh.sockso.tests.TestDatabase;

public class DbRequestLoggerTest extends SocksoTestCase {

    public void testLoggingARequestAddsARecordToTheDatabase() throws Exception {
        final Database db = new TestDatabase();
        final RequestLogger logger = new DbRequestLogger( db );
        final String ipAddress = "1.2.3.4",
                     requestUrl = "some resource",
                     userAgent = "this user agent",
                     referer = "some referer",
                     cookies = "some cookies";
        assertTableSize( db, "request_log", 0 );
        logger.log( null, ipAddress, requestUrl, userAgent, referer, cookies );
        assertTableSize( db, "request_log", 1 );
        assertRowExists( db, "request_log", "ip_address", ipAddress );
        assertRowExists( db, "request_log", "request_url", requestUrl );
        assertRowExists( db, "request_log", "user_agent", userAgent );
        assertRowExists( db, "request_log", "referer", referer );
        assertRowExists( db, "request_log", "cookies", cookies );
    }

}

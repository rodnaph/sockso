
package com.pugh.sockso;

import com.pugh.sockso.tests.MyHttpURLConnection;
import com.pugh.sockso.tests.SocksoTestCase;
import java.net.HttpURLConnection;

public class CommunityUpdaterTest extends SocksoTestCase {

    private CommunityUpdater cu;

    private Properties p;

    private String key;

    @Override
    public void setUp() {
        p = new StringProperties();
        p.set( Constants.COMMUNITY_ENABLED, p.YES );
        cu = new MyCommunityUpdater( p );
        key = Utils.getRandomString( 32 );
    }

    public void testServerKeyIsGeneratedIfThereIsntOneAlready() {
        cu.check();
        assertNotNull( p.get(Constants.SERVER_KEY,null) );
    }

    public void testGeneratedServerKeyIs32CharsLong() {
        cu.check();
        assertEquals( p.get(Constants.SERVER_KEY).length(), 32 );
    }

    public void testServerKeyIsNotChangedWhenItAlreadyExists() {
        p.set( Constants.SERVER_KEY, key );
        cu.check();
        assertEquals( key, p.get(Constants.SERVER_KEY) );
    }

    public void testRequiredInfoIsSentViaJsonWithPing() throws Exception {
        p.set( Constants.SERVER_KEY, key );
        p.set( Constants.SERVER_PORT, 1234 );
        p.set( Constants.SERVER_BASE_PATH, "/foo" );
        cu.check();
        String json = cu.getUrlConnection( "" )
                        .getOutputStream()
                        .toString();
        assertContains( json, "skey" );
        assertContains( json, key );
        assertContains( json, "port" );
        assertContains( json, "1234" );
        assertContains( json, "basepath" );
        assertContains( json, "/foo" );
    }

    public void testPingUrlDefaultsToPublicWebsite() {
        assertContains( cu.getPingUrl(), Constants.WEBSITE_URL );
    }

    public void testPingUrlCanBeSetViaProperty() throws Exception {
        String url = "http://foo.com/ping";
        p.set( Constants.COMMUNITY_PING_URL, url );
        assertEquals( url, cu.getPingUrl() );
    }
    
}

class MyCommunityUpdater extends CommunityUpdater {
    
    private MyHttpURLConnection urlCnn;

    public MyCommunityUpdater( Properties p ) {
        super( p );
    }

    @Override
    protected HttpURLConnection getUrlConnection( final String url ) {
        if ( urlCnn == null ) {
            urlCnn = new MyHttpURLConnection( "ok" );
        }
        return urlCnn;
    }

}

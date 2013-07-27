
package com.pugh.sockso.web.action;

import com.pugh.sockso.music.Artist;
import com.pugh.sockso.music.Track;
import com.pugh.sockso.music.stream.AbstractMusicStream;
import com.pugh.sockso.music.stream.MusicStream;
import com.pugh.sockso.tests.SocksoTestCase;
import com.pugh.sockso.web.Response;

import java.io.DataOutputStream;
import java.io.IOException;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.matches;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

public class MusicStreamTest extends SocksoTestCase {
    
    private Track tr;
    private MusicStream ms;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        tr = new Track.Builder()
                .artist(new Artist(1, "Usher"))
                .path("/music/usher-yeah.mp3")
                .name("yeah")
                .build();

        ms = new AbstractMusicStream(tr) {
            public void sendAudioStream( DataOutputStream client ) throws IOException {
                // Do nothing
            }
        };
    }

    public void testSetHeaders() {

        final Response res = createMock( Response.class );

        res.addHeader( matches("Content-Type"), (String) anyObject() );
        res.addHeader( matches("Content-Disposition"), (String) anyObject() );

        replay( res );

        ms.setHeaders( res );

        verify( res );
    }

}

package com.pugh.sockso.web.action;

import com.pugh.sockso.music.stream.MusicStream;
import com.pugh.sockso.Constants;
import com.pugh.sockso.Properties;
import com.pugh.sockso.Utils;
import com.pugh.sockso.db.Database;
import com.pugh.sockso.music.Track;
import com.pugh.sockso.music.encoders.BuiltinEncoder;
import com.pugh.sockso.music.encoders.CustomEncoder;
import com.pugh.sockso.music.encoders.Encoder;
import com.pugh.sockso.music.encoders.Encoders;
import static com.pugh.sockso.music.encoders.Encoders.Type.BUILTIN;
import static com.pugh.sockso.music.encoders.Encoders.Type.CUSTOM;
import com.pugh.sockso.music.stream.ChunkedStream;
import com.pugh.sockso.music.stream.RangeStream;
import com.pugh.sockso.music.stream.RangeStream.Range;
import com.pugh.sockso.music.stream.SimpleStream;
import com.pugh.sockso.web.BadRequestException;
import com.pugh.sockso.web.Request;
import com.pugh.sockso.web.Response;
import com.pugh.sockso.web.User;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

public class Streamer extends BaseAction {

    private static final Logger log = Logger.getLogger( Streamer.class );


    /**
     *  handles a request
     * 
     */
    
    public void handleRequest() throws SQLException, IOException, BadRequestException {
        
        final Request req = getRequest();
        final int trackId = Integer.parseInt( req.getUrlParam( 1 ) );

        final Track track = Track.find( getDatabase(), trackId );

        if ( track == null ) {
            throw new BadRequestException( "Invalid track ID", 404 );
        }

        final MusicStream stream = getStream( track );

        playTrack( track, stream );

    }
    
    /**
     *  if users are required to login then authentication will be done via session
     *  information send in the playlists.
     * 
     */
    
    @Override
    public boolean requiresLogin() {

        final Properties p = getProperties();

        return p.get( Constants.STREAM_REQUIRE_LOGIN ).equals( Properties.YES );

    }

    protected MusicStream getStream( final Track track ) throws IOException, BadRequestException {

        MusicStream stream;

        // check if we're using an encoder first
        Encoder encoder = getEncoder( track );

        if ( encoder != null ) {
            // use Chunked stream strategy
            stream = new ChunkedStream( track, encoder, getProperties() );

        }
        else if ( hasRangeHeader() ) {
            // stream from range
            Range range = processRangeRequest(track);
            stream = new RangeStream( track, range );
        }
        else {
            // stream normally (unaltered)
            stream = new SimpleStream( track );
        }

        return stream;

    }

    /**
     *  streams a particular track to the response object
     *
     *  @param trackId the track id to playTrack
     * 
     *  @throws SQLException
     *  @throws IOException
     *  @throws BadRequestException
     * 
     */

    protected void playTrack( final Track track, final MusicStream stream ) throws SQLException, IOException, BadRequestException {

        logTrackPlayed( track );

        final Response response = getResponse();
        stream.setHeaders( response );
        response.sendHeaders();
        stream.sendAudioStream( new DataOutputStream(response.getOutputStream()) );
            
    }


    protected Encoder getEncoder( final Track track ) throws IOException {

        final Properties p = getProperties();
        final String ext = Utils.getExt(track.getPath());

        final String type = p.get(Constants.PROP_ENCODERS_PREFIX + ext);

        if ( type.equals("") ) {
            return null;
        }

        Encoders.Type encoderType;

        try {
            encoderType = Encoders.Type.valueOf(type);
        } catch (IllegalArgumentException e) {
            log.error("Encoder type not found", e);
            return null;
        }

        switch (encoderType) {

            // 1. use a builtin encoder?
            case BUILTIN:

                final String name = p.get(Constants.PROP_ENCODERS_PREFIX + ext + ".name");
                final BuiltinEncoder encoder = Encoders.getBuiltinEncoderByName(name).getEncoder();

                if ( encoder != null ) {

                    return encoder;
                }

            // 2. use a custom command to encode?
            case CUSTOM:

                final String command = p.get(Constants.PROP_ENCODERS_PREFIX + ext + ".command");

                if ( !command.equals("") ) {
                    
                    return new CustomEncoder(command);
                }

            // 3. otherwise stream unaltered (no encoder)
            default:
                
                return null;
        }
        
    }
    
    /**
     * Check if the header "Range" exists in the HTTP Request
     *
     * @return
     */
    private boolean hasRangeHeader() {

        final String rangeHeader = getRequest().getHeader("Range");

        return !rangeHeader.isEmpty();
        
    }

    /**
     *
     * Process range request headers to seek to positions in the audio stream
     *
     * GET /2390/2253727548_a413c88ab3_s.jpg HTTP/1.1
     * Host: farm3.static.flickr.com
     * Range: bytes=1000-
     *
     * HTTP/1.0 206 Partial Content
     *
     * Content-Length: 2980
     * Content-Range: bytes 1000-3979/3980
     *
     */
    protected Range processRangeRequest( final Track track ) throws IOException, BadRequestException {

        final String rangeHeader = getRequest().getHeader("Range");

        final Pattern pattern = Pattern.compile("bytes=(\\d+)-(\\d+)?");
        final Matcher matcher = pattern.matcher(rangeHeader);

        if ( !matcher.matches() ) {
            log.error("Bad \"Range\" header: " + rangeHeader);
            throw new BadRequestException("Invalid range", 416);
        }

        try {
            
            long beginPos = Long.parseLong(matcher.group(1));
            long endPos = -1;

            String endMatch = matcher.group(2);

            if ( endMatch != null ) {
                endPos = Long.parseLong(endMatch);
            }

            final long trackLength = new File(track.getPath()).length();

            if ( endPos < 0 ) {
                endPos = trackLength - 1;
            }

            if ( beginPos < 0 || beginPos >= trackLength
                 || endPos >= trackLength || endPos <= beginPos ) {
                log.error("Bad \"Range\" values: " + beginPos + "-" + endPos);
                throw new BadRequestException("Invalid range", 416);
            }

            return new Range(beginPos, endPos);

        } catch (NumberFormatException e) {
            log.error("Bad \"Range\" header", e);
            throw new BadRequestException("Invalid range", 416);
        }

    }

    /**
     *  @TODO Create a dao class for PlayLog
     * 
     *  logs the fact that a track has been played
     * 
     *  @param trackId the track id
     *  @param path the path to the file
     *  
     */
    
    protected void logTrackPlayed( final Track track ) {
        
        log.debug( "Track: " + track.getPath() );
        
        PreparedStatement st = null;
                
        try {

            final Database db = getDatabase();
            final User user = getUser();
            final String sql = " insert into play_log ( track_id, date_played, user_id ) " +
                               " values ( ?, current_timestamp, ? ) ";
            
            st = db.prepare( sql );
            st.setInt( 1, track.getId() );
            
            if ( user != null )
                st.setInt( 2, user.getId() );
            else
                st.setNull( 2, Types.INTEGER );

            st.execute();

        }

        catch ( SQLException e ) {
            log.error( e );
        }

        finally {
            Utils.close( st );
        }
        
    }

}

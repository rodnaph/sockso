package com.pugh.sockso.web.action;

import com.pugh.sockso.db.Database;
import com.pugh.sockso.Properties;
import com.pugh.sockso.Constants;
import com.pugh.sockso.Utils;
import com.pugh.sockso.music.Track;
import com.pugh.sockso.music.encoders.Encoders;
import com.pugh.sockso.music.encoders.BuiltinEncoder;
import com.pugh.sockso.web.Response;
import com.pugh.sockso.web.Request;
import com.pugh.sockso.web.BadRequestException;
import com.pugh.sockso.web.User;

import java.io.IOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.File;
import java.io.EOFException;

import java.net.SocketException;

import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Types;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

public class Streamer extends BaseAction {
    
    private static final int STREAM_BUFFER_SIZE = 1024 * 8; // 8Kb

    private static final Logger log = Logger.getLogger( Streamer.class );
   
    /**
     *  handles a request
     * 
     */
    
    public void handleRequest() throws SQLException, IOException, BadRequestException {
        
        final Request req = getRequest();
        final String trackId = req.getUrlParam( 1 );

        playTrack(
            Integer.parseInt( trackId )
        );

    }
    
    /**
     *  if users are required to login then authentication will be done via session
     *  information send in the playlists.
     * 
     */
    
    @Override
    public boolean requiresLogin() {

        final Properties p = getProperties();

        return p.get( Constants.STREAM_REQUIRE_LOGIN ).equals( p.YES );

    }
    
    /**
     *  streams a particular track to the response object
     *
     *  @param res the response object
     *  @param trackId the track id to playTrack
     * 
     *  @throws SQLException
     *  @throws IOException
     *  @throws BadRequestException
     * 
     */
    
    protected void playTrack( final int trackId ) throws SQLException, IOException, BadRequestException {

        final Track track = Track.find( getDatabase(), trackId );

        if ( track == null ) {
            throw new BadRequestException( "Invalid track ID", 404 );
        }
        
        final MusicStream ms = getMusicStream( track );

        logTrackPlayed( track );
        sendTrackHeaders( track, ms.getMimeType() );

        playMusicStream( ms );

    }

    /**
     *  returns a data input stream for reading audio encoded using a custom command
     * 
     *  @param track the track to playTrack
     *  @param command the custom command to use
     * 
     *  @return audio data stream for the client
     * 
     *  @throws IOException
     * 
     */
    
    private DataInputStream getAudioStreamFromCommand( final Track track, final String command ) throws IOException {

        // break up users command into it's parts, then add the track we're
        // going to play, and the output redirection to the end.
        
        final String[] cmdArgs = command.split( " " );
        final String[] allArgs = new String[ cmdArgs.length + 2 ];
        
        for ( int i=0; i<cmdArgs.length; i++ )
            allArgs[ i ] = cmdArgs[ i ];
        allArgs[ cmdArgs.length ] = track.getPath();
        allArgs[ cmdArgs.length + 1 ] = "-";

        log.debug( "Encoding with custom command: " + Arrays.toString(allArgs) );

        final ProcessBuilder pb = new ProcessBuilder( allArgs );
        final Process process = pb.start();
        
        return new DataInputStream( process.getInputStream() );
 
    }
    
    /**
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

    /**
     *  returns the music stream to read from for this track.  this is determined
     *  by the encoding options the user has set up.  the music stream then contains
     *  info like the audio stream to send to the output, and the mime type.
     * 
     *  @param track the track to get the stream for
     * 
     *  @return the music stream
     * 
     *  @throws IOException
     * 
     */
    
    private MusicStream getMusicStream( final Track track ) throws IOException {
        
        final Properties p = getProperties();
        final String ext = Utils.getExt( track.getPath() );
        final String type = p.get( "encoders." +ext );
        
        // 1. use a builtin encoder?
        if ( Encoders.Type.BUILTIN.name().equals(type) ) {

            final String name = p.get( "encoders." +ext+ ".name" );
            final String bitrate = p.get( "encoders." +ext+ ".bitrate" );
            final BuiltinEncoder encoder = Encoders.getBuiltinEncoderByName( name ).getEncoder();
            
            if ( encoder != null )
                return new MusicStream(
                    encoder.getAudioStream(
                        track,
                        bitrate.equals("")
                            ? encoder.getDefaultBitrate()
                            : Integer.valueOf(bitrate)
                    ),
                    encoder.getOutputMimeType()
                );

        }
        
        // 2. use a custom command to encode?
        if ( Encoders.Type.CUSTOM.name().equals(type) ) {
            final String command = p.get( "encoders." +ext+ ".command" );
            if ( !command.equals("") )
                return new MusicStream(
                    getAudioStreamFromCommand( track, command ),
                    "" // @TODO - we don't know the type...  best to say nothing?
                );
        }

        // 3. otherwise stream unaltered
        log.debug( "Streaming unaltered" );
        return new MusicStream(
            new DataInputStream(
                new FileInputStream( track.getPath() )
            ),
            FileServer.getMimeType( track.getPath() )
        );

    }

    /**
     *  Sends the music stream to the client, optionally handling range requests
     * 
     *  @param ms
     * 
     *  @return
     * 
     *  @throws java.io.IOException
     * 
     */
    
    protected boolean playMusicStream( final MusicStream ms ) throws IOException {
        
        DataInputStream audio = null;
        byte[] buffer = new byte[ STREAM_BUFFER_SIZE ];

        try {

            final DataOutputStream client = new DataOutputStream( getResponse().getOutputStream() );

            audio = ms.getAudioStream();
            int bytesRead = 0;
            
            processRangeRequest( audio );

            while ( true ) {
                bytesRead = audio.read( buffer );
                client.write( buffer, 0, bytesRead );
            }

        }

        catch ( final EOFException e ) {}
        /* if the client disconnected then we didn't finish playing the track */
        catch ( final SocketException e ) { return false; }

        finally {
            Utils.close( audio );
            return true;
        }

    }

    /**
     *  Process range request headers to seek to positions in the audio stream
     * 
     */
    
    protected void processRangeRequest( final DataInputStream audio ) throws IOException {
        
        final String range = getRequest().getHeader( "Range" );
        
        if ( range.length() > 0 ) {
            
            final Pattern pattern = Pattern.compile( "bytes=(\\d+)-\\d+" );
            final Matcher matcher = pattern.matcher( range );
            
            if ( matcher.matches() ) {
                
                final int seekTo = Integer.parseInt( matcher.group(1) );
                
                audio.skipBytes( seekTo );
                
            }
            
        }
        
    }
    
    /**
     *  sends the HTTP headers needed before sending streaming audio content
     * 
     *  @param track the track that will be streamed
     *  @param mimeType the audio mime type
     * 
     */
    
    protected void sendTrackHeaders( final Track track, final String mimeType ) {

        final Response res = getResponse();
        final String contentLength = Long.toString( new File(track.getPath()).length() );
        final String filename = track.getArtist().getName() + " - " + track.getName();

        res.addHeader( "Content-Type", mimeType );
        res.addHeader( "Content-Length", contentLength );
        res.addHeader( "Content-Disposition", "filename=\"" + filename + "\"" );
        res.sendHeaders();

    }
    
}


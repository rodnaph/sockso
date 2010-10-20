/*
 * AudioTag.java
 * 
 * Created on Jun 8, 2007, 10:30:10 PM
 * 
 *  This class is designed to be an abstraction over various different tagging
 *  types.  We only need some simple information, just use the static getTag()
 *  method and hopefully you'll get a tag object back you can use!  :P
 * 
 */

package com.pugh.sockso.music.tag;

import com.pugh.sockso.Utils;

import java.io.File;
import java.io.IOException;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.apache.log4j.Logger;

public abstract class AudioTag implements Tag {

    private static final Logger log = Logger.getLogger( AudioTag.class );
    
    protected String artistTitle = "", albumTitle = "", trackTitle = "";
    protected int trackNumber = 0;

    public String getArtist() { return artistTitle; }
    public String getAlbum() { return albumTitle; }
    public String getTrack() { return trackTitle; }
    public int getTrackNumber() { return trackNumber; }

    /**
     *  this method should be used when trying to read the tags from an
     *  audio file.  the type of file is checked and the details of
     *  what it actually is should be nicely hidden away.
     * 
     *  @param file the file to fetch tags for
     *  @return this files tags
     * 
     *  @throws IOException
     *  @throws InvalidTagException
     * 
     */
    
    public static Tag getTag( final File file ) throws IOException, InvalidTagException {
        
        final String ext = Utils.getExt( file );
        AudioTag tag = null;
        
        // determine type of file by extension
        if ( ext.equals("mp3") ) tag = new Mp3Tag();
        else if ( ext.equals("ogg") ) tag = new OggTag();
        else if ( ext.equals("wma") || ext.equals("asf") ) tag = new WmaTag();
        else if ( ext.equals("flac") ) tag = new FlacTag();
        else if ( ext.equals("m4a")) tag = new AACTag();

        else throw new InvalidTagException( file );

        tag.parse( file );

        // please, please no nullness...
        if ( tag.artistTitle == null ) tag.artistTitle = "";
        if ( tag.albumTitle == null ) tag.albumTitle = "";
        if ( tag.trackTitle == null ) tag.trackTitle = "";

        // remove leading/trailing space
        tag.albumTitle = tag.albumTitle.trim();
        tag.artistTitle = tag.artistTitle.trim();
        tag.trackTitle = tag.trackTitle.trim();

        // set defaults if we have nothing
        if ( tag.artistTitle.equals("") ) tag.artistTitle = guessArtist( file );
        if ( tag.albumTitle.equals("") ) tag.albumTitle = guessAlbum( file );
        if ( tag.trackTitle.equals("") ) tag.trackTitle = guessTrack( file );
        if ( tag.trackNumber == 0 ) tag.setTrackNumber( guessTrackNumber(file) );
                
        return tag;
        
    }

    /**
     *  takes a string that is *possibly* a track number and sets it as the
     *  track number if it really is.
     * 
     *  @param strNumber
     * 
     */
    
    protected void setTrackNumber( final String strNumber ) {

        try {
            trackNumber = Integer.parseInt(
                checkTrackNumberForTotal( strNumber )
            );
        }

        catch ( final NumberFormatException e ) {} // swallow
        
    }

    /**
     *  checks if a track number isn't something like "6/9" (ie. track 6 of 9)
     *  if it is then it'll just return the first part, otherwise it'll return
     *  whatever was passed into the methog
     * 
     *  @param trackNumber
     * 
     *  @return
     * 
     */
    
    protected static String checkTrackNumberForTotal( final String trackNumber ) {

        Pattern p = Pattern.compile( "(\\d+)/\\d+" );
        Matcher m = p.matcher( trackNumber );
        
        return m.matches()
            ? m.group( 1 )
            : trackNumber;

    }
    
    /**
     *  tries to guess the name of the artist from the file name/path
     * 
     *  @param file filename to guess from
     *  @return artist name
     *
     */
    
    protected static String guessArtist( final File file ) {
        
        final File parent = file.getParentFile();
        
        if ( parent != null ) {
            
            final String parentName = parent.getName();
            final String[] splits = { " - ", "_-_" };

            // see if there's a parent folder that seems to have
            // an "artist - album" type name
            for ( int i=0; i<splits.length; i++ )
                if ( parentName.indexOf(splits[i]) != -1 )
                    return parentName.substring( 0, parentName.indexOf(splits[i]) );

            // otherwise try and get the parent of the parent (artist/album/track)
            if ( parent.getParent() != null )
                return parent.getParentFile().getName();

        }
        
        return "(Unknown Artist)";

    }
    
    /**
     *  tries to guess the name of the album from the file name/path
     * 
     *  @param file filename to guess from
     *  @return album name
     *
     */
    
    protected static String guessAlbum( final File file ) {

        final File parent = file.getParentFile();
        
        if ( parent != null ) {
        
            final String parentName = parent.getName();
            final String[] splits = { " - ", "_-_" };

            // see if there's a parent folder that seems to have
            // an "artist - album" type name
            for ( int i=0; i<splits.length; i++ )
                if ( parentName.indexOf(splits[i]) != -1 )
                    return parentName.substring( parentName.indexOf(splits[i]) + splits[i].length(), parentName.length() );

            // otherwise use parent folder name
            return parent.getName();

        }
        
        return "(Unknown Album)";
        
    }
    
    /**
     *  tries to guess the name of the track from the file name
     * 
     *  @param file filename to guess from
     *  @return track name
     *
     */

    protected static String guessTrack( final File file ) {

        // possible regexps to match
        final String[] regexs = {
            "\\d+ - (.*)\\.\\w+",
            "\\d+_-_(.*)\\.\\w+",
            "\\d+-(.*)\\.\\w+",
            "(.*)\\.\\w"
        };
        final String name = file.getName();

        return matchRegex( name, regexs, file.getName() ); 
        
    }
    
    /**
     *  tries to match a bunch of regular expressions against
     *  a string, the first match is returned
     * 
     *  @param str the string to match against
     *  @param regexs array of regular expressions
     *  @param defaultValue the default to return if no matches
     *  @return the first match, or the default if nothing
     * 
     */
    
    private static String matchRegex( final String str, final String[] regexs, final String defaultValue ) {

        for ( int i=0; i<regexs.length; i++ ) {
            
            final String regex = regexs[ i ];
            final Pattern p = Pattern.compile( regex );
            final Matcher m = p.matcher( str );
            
            if ( m.matches() )
                return m.group( 1 );

        }
        
        return defaultValue;

    }
    
    /**
     *  tries to guess the name of the track number from the file name/path
     * 
     *  @param file filename to guess from
     * 
     *  @return track number
     *
     */
    
    protected static String guessTrackNumber( final File file ) {
        
        // possible regexps to match
        final String[] regexs = {
            "(\\d+).*",
        };
        final String name = file.getName();  
        
        return matchRegex( name, regexs, "0" );
        
    }
    
}

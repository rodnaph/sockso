/*
 * WmaTag.java
 * 
 * Created on Jun 9, 2007, 1:17:17 PM
 * 
 * This code was originally taken from Limewire: http://limewire.com, which in
 * turn got it from some other places, follow the trail...
 * 
 */

package com.pugh.sockso.music.tag;

import com.pugh.sockso.Utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.UnsupportedEncodingException;
import java.io.FilterInputStream;
import java.io.EOFException;

import java.util.Arrays;

import org.apache.log4j.Logger;

public class WmaTag extends AudioTag {

    private static final Logger log = Logger.getLogger( WmaTag.class );
    
    // data types we know about in the extended content description.
    // THESE ARE WRONG (but close enough for now)
    private static final int TYPE_STRING = 0;
    private static final int TYPE_BINARY = 1;
    private static final int TYPE_BOOLEAN = 2;
    private static final int TYPE_INT = 3;
    private static final int TYPE_LONG = 4;
   
    private String _album, _artist, _title, _year, _copyright,
                   _rating, _genre, _comment, _drmType;
    private short _track = -1;
    private int _bitrate = -1, _length = -1, _width = -1, _height = -1;
    private boolean _hasAudio, _hasVideo;
    
    @Override
    public String getAlbum() { return notnull(_album); }
    @Override
    public String getArtist() { return notnull(_artist); }
    @Override
    public String getTrack() { return notnull(_title); }
    @Override
    public String getGenre() { return notnull(_genre); }
    
    String getTitle() { return _title; }
    String getYear() { return _year; }
    String getCopyright() { return _copyright; }
    String getRating() { return _rating; }
    String getComment() { return _comment; }
    int getBitrate() { return _bitrate; }
    int getLength() { return _length; }
    int getWidth() { return _width; }
    int getHeight() { return _height; }
        
    boolean hasAudio() { return _hasAudio; }
    boolean hasVideo() { return _hasVideo; }

    /**
     *  Takes a string and if it's null returns the empty string
     *
     *  @param str
     *
     *  @return
     *
     */

    protected String notnull( final String str ) {

        return ( str == null ) ? "" : str;

    }

    /**
     * Parses the given file for metadata we understand.
     */
    public void parse( final File f ) throws IOException {
        
        InputStream is = null;
        
        try {
            is = new BufferedInputStream( new FileInputStream(f) );
            parse( is );
        }
                
        finally {
            Utils.close( is );
        }

    }
    
    /**
     * Parses a ASF input stream's metadata.
     * This first checks that the marker (16 bytes) is correct, reads the data offset & object count,
     * and then iterates through the objects, reading them.
     * Each object is stored in the format:
     *   ObjectID (16 bytes)
     *   Object Size (4 bytes)
     *   Object (Object Size bytes)
     */

    private void parse( final InputStream is ) throws IOException {

        final CountingInputStream counter = new CountingInputStream(is);
        final DataInputStream ds = new DataInputStream(counter);
        
        final byte[] marker = new byte[ IDs.HEADER_ID.length ];
        ds.readFully( marker );
        if ( !Arrays.equals(marker,IDs.HEADER_ID) )
            throw new IOException( "not an ASF file" );
       
        final long dataOffset = ByteOrder.leb2long( ds );
        final int objectCount = ByteOrder.leb2int( ds );

        ensureSkip( ds, 2 );

        if ( dataOffset < 0 )
            throw new IOException( "ASF file is corrupt. Data offset negative:" +dataOffset );
        if ( objectCount < 0 )
            throw new IOException( "ASF file is corrupt. Object count unreasonable:" +ByteOrder.uint2long(objectCount) );
        if ( objectCount > 100 )
            throw new IOException( "object count very high: " + objectCount );

        final byte[] object = new byte[ 16 ];

        for ( int i=0; i<objectCount; i++ ) {

            ds.readFully(object);

            final long size = ByteOrder.leb2long(ds) - 24;

            if ( size < 0 )
                throw new IOException( "ASF file is corrupt.  Object size < 0 :" +size );

            counter.clearAmountRead();
            readObject( ds, object, size );

            final int read = counter.getAmountRead();
            
            if ( read > size )
                throw new IOException( "read (" +read+ ") more than size (" +size+ ")" );
            else if ( read != size ) {
                ensureSkip( ds, size - read );
            }
            
        }
        
    }
    
    /**
     * Reads a single object from a ASF metadata stream.
     * The objectID has already been read.  Each object is stored differently.
     */
    
    private void readObject( final DataInputStream ds, final byte[] id, final long size ) throws IOException {
        
        if( Arrays.equals(id, IDs.FILE_PROPERTIES_ID) )
            parseFileProperties(ds);
        else if( Arrays.equals(id, IDs.STREAM_PROPERTIES_ID) )
            parseStreamProperties(ds);
        else if( Arrays.equals(id, IDs.EXTENDED_STREAM_PROPERTIES_ID) )
            parseExtendedStreamProperties(ds);
        else if( Arrays.equals(id, IDs.CONTENT_DESCRIPTION_ID) )
            parseContentDescription(ds);
        else if( Arrays.equals(id, IDs.EXTENDED_CONTENT_DESCRIPTION_ID) )
            parseExtendedContentDescription(ds);
        else if( Arrays.equals(id, IDs.CONTENT_ENCRYPTION_ID) )
            parseContentEncryption(ds);
        else {
            // for debugging.
            final byte[] temp = new byte[ (int) size ];
            ds.readFully( temp );
            log.debug("id: " + string(id) + ", data: " + string(temp) );
        }

    }

    private static long ensureSkip( final InputStream in, final long length ) throws IOException {
        
    	long skipped = 0;
        
    	while( skipped < length ) {
            final long current = in.skip(length - skipped);
    	    if ( current == -1 || current == 0 )
    	        throw new EOFException( "eof" );
    	    else
    	        skipped += current;
    	}
        
    	return skipped;
        
    }

    /**
     * Parses known information out of the file properties object.
     */
    
    private void parseFileProperties( final DataInputStream ds ) throws IOException {

        ensureSkip( ds, 48 );
        
        final int duration = (int)(ByteOrder.leb2long(ds) / 10000000);
        
        if ( duration < 0 )
            throw new IOException( "ASF file corrupt.  Duration < 0:" +duration );
        
        _length = duration;
        ensureSkip( ds, 20 );
        
        final int maxBR = ByteOrder.leb2int(ds);
        
        if ( maxBR < 0 )
            throw new IOException("ASF file corrupt.  Max bitrate > 2 Gb/s:" +ByteOrder.uint2long(maxBR) );

        _bitrate = maxBR / 1000;

    }
    
    /**
     * Parses stream properties to see if we have audio or video data.
     */
    
    private void parseStreamProperties( final DataInputStream ds ) throws IOException {
        
        final byte[] streamID = new byte[16];
        
        ds.readFully(streamID);
        
        if ( Arrays.equals(streamID, IDs.AUDIO_STREAM_ID) ) {
            _hasAudio = true;
        }

        else if ( Arrays.equals(streamID, IDs.VIDEO_STREAM_ID) ) {
            _hasVideo = true;
            ensureSkip( ds, 38 );
            _width = ByteOrder.leb2int( ds );
            if ( _width < 0 )
                throw new IOException( "ASF file corrupt.  Video width excessive:" +ByteOrder.uint2long(_width) );
            _height = ByteOrder.leb2int( ds );
            if ( _height < 0 )
                throw new IOException( "ASF file corrupt.  Video height excessive:" +ByteOrder.uint2long(_height) );
        }
        
        // we aren't reading everything, but we'll skip over just fine.

    }
    
    /**
     * Parses known information out of the extended stream properties object.
     */
    
    private void parseExtendedStreamProperties( final DataInputStream ds ) throws IOException {

        ensureSkip(ds, 56);
        
        final int sampleRate = ByteOrder.leb2int(ds);
        final int byteRate = ByteOrder.leb2int(ds);
        
        if ( sampleRate < 0 )
            throw new IOException( "ASF file corrupt.  Sample rate excessive:" +ByteOrder.uint2long(sampleRate) );

        if ( byteRate < 0 )
            throw new IOException( "ASF file corrupt.  Byte rate excessive:" +ByteOrder.uint2long(byteRate) );
        
        if ( _bitrate == -1 )
            _bitrate = byteRate * 8 / 1000;

    }
    
    /**
     * Parses the content encryption object, to determine if the file is protected.
     * We parse through it all, even though we don't use all of it, to ensure
     * that the object is well-formed.
     */
    
    private void parseContentEncryption( final DataInputStream ds ) throws IOException {
        
        long skipSize = ByteOrder.uint2long(ByteOrder.leb2int(ds)); // data
        ensureSkip(ds, skipSize);
        
        final int typeSize = ByteOrder.leb2int( ds ); // type
        if ( typeSize < 0 )
            throw new IOException("ASF file is corrupt.  Type size < 0: "+typeSize);
        
        final byte[] b = new byte[ typeSize ];
        ds.readFully( b );
        _drmType = new String( b ).trim();
        
        skipSize = ByteOrder.uint2long(ByteOrder.leb2int(ds)); // data
        ensureSkip( ds, skipSize );
        
        skipSize = ByteOrder.uint2long(ByteOrder.leb2int(ds)); // url
        ensureSkip( ds, skipSize );

    }   
    
    /**
     * Parses known information out of the Content Description object.
     * The data is stored as:
     *   10 bytes of sizes (2 bytes for each size).
     *   The data corresponding to each size.  The data is stored in order of:
     *   Title, Author, Copyright, Description, Rating.
     */
    
    private void parseContentDescription( final DataInputStream ds ) throws IOException {

        final int[] sizes = { -1, -1, -1, -1, -1 };
        
        for( int i=0; i<sizes.length; i++ )
            sizes[i] = ByteOrder.ushort2int( ByteOrder.leb2short(ds) );
        
        final byte[][] info = new byte[5][];
        for( int i=0; i<sizes.length; i++ )
            info[i] = new byte[ sizes[i] ];
                
        for ( int i=0; i<info.length; i++ )
            ds.readFully( info[i] );
        
        _title = string( info[0] );
        _artist = string( info[1] );
        _copyright = string( info[2] );
        _comment = string( info[3] );
        _rating = string( info[4] );
            
    }
    
    /**
     * Reads the extended Content Description object.
     * The extended tag has an arbitrary number of fields.  
     * The number of fields is stored first, as:
     *      Field Count (2 bytes)
     *
     * Each field is stored as:
     *      Field Size (2 bytes)
     *      Field      (Field Size bytes)
     *      Data Type  (2 bytes)
     *      Data Size  (2 bytes)
     *      Data       (Data Size bytes)
     */
    
    private void parseExtendedContentDescription( final DataInputStream ds ) throws IOException {

        final int fieldCount = ByteOrder.ushort2int(ByteOrder.leb2short(ds));

        for ( int i=0; i<fieldCount; i++ ) {

            final int fieldSize = ByteOrder.ushort2int( ByteOrder.leb2short(ds) );
            final byte[] field = new byte[fieldSize];
            
            ds.readFully( field );
            
            final String fieldName = string( field );
            final int dataType = ByteOrder.ushort2int( ByteOrder.leb2short(ds) );
            final int dataSize = ByteOrder.ushort2int( ByteOrder.leb2short(ds) );
            
            switch ( dataType ) {
                case TYPE_STRING:
                    parseExtendedString( fieldName, dataSize, ds );
                    break;
                case TYPE_BINARY:
                    parseExtendedBinary( dataSize, ds );
                    break;
                case TYPE_BOOLEAN:
                    parseExtendedBoolean( dataSize, ds );
                    break;
                case TYPE_INT:
                    parseExtendedInt( fieldName, dataSize, ds );
                    break;
                case TYPE_LONG:
                    parseExtendedInt( fieldName, dataSize, ds );
                    break;
                default: 
                    ensureSkip( ds, dataSize );
            }
            
        }
        
    }
    
    /**
     * Parses a value from an extended tag, assuming the value is of the 'string' dataType.
     * 
     */
    
    private void parseExtendedString( final String field, final int size, final DataInputStream ds ) throws IOException {
        
        final byte[] data = new byte[ Math.min(250, size) ];
        ds.readFully( data );
        final int leftover = Math.max(0, size - 250);
        ensureSkip( ds, leftover );        
        final String info = string( data );
        
        if ( Extended.WM_TITLE.equals(field) && _title == null )
            _title = info;
        else if ( Extended.WM_AUTHOR.equals(field) && _artist == null )
            _artist = info;
        else if ( Extended.WM_ALBUMTITLE.equals(field) && _album == null )
            _album = info;
        else if ( Extended.WM_TRACK_NUMBER.equals(field) && _track == -1 )
            _track = toShort(info);
        else if ( Extended.WM_YEAR.equals(field) && _year == null )
            _year = info;
        else if ( Extended.WM_GENRE.equals(field) && _genre == null )
            _genre = info;
        else if ( Extended.WM_DESCRIPTION.equals(field) && _comment == null )
            _comment = info;

    }
    
    /**
     * Parses a value from an extended tag, assuming the value is of the 'boolean' dataType.
     * 
     */

    private void parseExtendedBoolean( final int size, final DataInputStream ds) throws IOException {

        ensureSkip( ds, size );

    }
    
    /**
     * Parses a value from an extended tag, assuming the value is of the 'int' dataType.
     * 
     */
    
    private void parseExtendedInt( final String field, final int size, final DataInputStream ds ) throws IOException {

        if ( size != 4 ) {
            ensureSkip( ds, size );
            return;
        }
        
        final int value = ByteOrder.leb2int(ds);
            
        if ( Extended.WM_TRACK_NUMBER.equals(field) && _track == -1 ) {
            final short shortValue = (short)value;
            if ( shortValue < 0 )
                throw new IOException( "ASF file reports negative track number " +shortValue );
            _track = shortValue;
        }

    }
    
    /**
     * Parses a value from an extended tag, assuming the value is of the 'binary' dataType.
     * 
     */
    
    private void parseExtendedBinary( final int size, final DataInputStream ds ) throws IOException {
            
        ensureSkip( ds, size );
        
    }
        
    /**
     *  Converts a String to a short, if it can.
     * 
     */
    
    private short toShort( final String x ) {
        
        try {
            return Short.parseShort( x );
        }
        
        catch( final NumberFormatException nfe ) {
            return -1;
        }

    }
    
    /**
     * Returns a String uses ASF's encoding (WCHAR: UTF-16 little endian).
     * If we don't support that encoding for whatever, hack out the zeros.
     * 
     */
    
    private String string( final byte[] x ) throws IOException {
        
        if ( x == null )
            return null;
            
        try {
            return new String( x, "UTF-16LE" ).trim();
        }
        
        catch( final UnsupportedEncodingException uee ) {
            // hack.
            int pos = 0;
            for ( int i=0; i<x.length; i++ ) {
                if ( x[i] != 0 )
                    x[pos++] = x[i];
            }
            return new String( x, 0, pos, "UTF-8" );
        }
        
    }
    
    private static class IDs {
        
        private static final byte HEADER_ID[] =
            { (byte)0x30, (byte)0x26, (byte)0xB2, (byte)0x75, (byte)0x8E, (byte)0x66, (byte)0xCF, (byte)0x11,
              (byte)0xA6, (byte)0xD9, (byte)0x00, (byte)0xAA, (byte)0x00, (byte)0x62, (byte)0xCE, (byte)0x6C };
            
        private static final byte FILE_PROPERTIES_ID[] =
            { (byte)0xA1, (byte)0xDC, (byte)0xAB, (byte)0x8C, (byte)0x47, (byte)0xA9, (byte)0xCF, (byte)0x11,
              (byte)0x8E, (byte)0xE4, (byte)0x00, (byte)0xC0, (byte)0x0C, (byte)0x20, (byte)0x53, (byte)0x65 };
              
        private static final byte STREAM_PROPERTIES_ID[] =
            { (byte)0x91, (byte)0x07, (byte)0xDC, (byte)0xB7, (byte)0xB7, (byte)0xA9, (byte)0xCF, (byte)0x11,
              (byte)0x8E, (byte)0xE6, (byte)0x00, (byte)0xC0, (byte)0x0C, (byte)0x20, (byte)0x53, (byte)0x65 };
            
        private static final byte EXTENDED_STREAM_PROPERTIES_ID[] =
            { (byte)0xCB, (byte)0xA5, (byte)0xE6, (byte)0x14, (byte)0x72, (byte)0xC6, (byte)0x32, (byte)0x43,
              (byte)0x83, (byte)0x99, (byte)0xA9, (byte)0x69, (byte)0x52, (byte)0x06, (byte)0x5B, (byte)0x5A };
            
        private static final byte CONTENT_DESCRIPTION_ID[] =
            { (byte)0x33, (byte)0x26, (byte)0xB2, (byte)0x75, (byte)0x8E, (byte)0x66, (byte)0xCF, (byte)0x11,
              (byte)0xA6, (byte)0xD9, (byte)0x00, (byte)0xAA, (byte)0x00, (byte)0x62, (byte)0xCE, (byte)0x6C };
            
        private static final byte EXTENDED_CONTENT_DESCRIPTION_ID[] =
            { (byte)0x40, (byte)0xA4, (byte)0xD0, (byte)0xD2, (byte)0x07, (byte)0xE3, (byte)0xD2, (byte)0x11,
              (byte)0x97, (byte)0xF0, (byte)0x00, (byte)0xA0, (byte)0xC9, (byte)0x5E, (byte)0xA8, (byte)0x50 };
            
        private static final byte CONTENT_ENCRYPTION_ID[] =
            { (byte)0xFB, (byte)0xB3, (byte)0x11, (byte)0x22, (byte)0x23, (byte)0xBD, (byte)0xD2, (byte)0x11,
              (byte)0xB4, (byte)0xB7, (byte)0x00, (byte)0xA0, (byte)0xC9, (byte)0x55, (byte)0xFC, (byte)0x6E };
            
        private static final byte EXTENDED_CONTENT_ENCRYPTION_ID[] =
            { (byte)0x14, (byte)0xE6, (byte)0x8A, (byte)0x29, (byte)0x22, (byte)0x26, (byte)0x17, (byte)0x4C,
              (byte)0xB9, (byte)0x35, (byte)0xDA, (byte)0xE0, (byte)0x7E, (byte)0xE9, (byte)0x28, (byte)0x9C };
            
        @SuppressWarnings("unused")
        private static final byte CODEC_LIST_ID[] =
            { (byte)0x40, (byte)0x52, (byte)0xD1, (byte)0x86, (byte)0x1D, (byte)0x31, (byte)0xD0, (byte)0x11,
              (byte)0xA3, (byte)0xA4, (byte)0x00, (byte)0xA0, (byte)0xC9, (byte)0x03, (byte)0x48, (byte)0xF6 };
              
        private static final byte AUDIO_STREAM_ID[] =
            { (byte)0x40, (byte)0x9E, (byte)0x69, (byte)0xF8, (byte)0x4D, (byte)0x5B, (byte)0xCF, (byte)0x11, 
              (byte)0xA8, (byte)0xFD, (byte)0x00, (byte)0x80, (byte)0x5F, (byte)0x5C, (byte)0x44, (byte)0x2B };
              
        private static final byte VIDEO_STREAM_ID[] = 
           { (byte)0xC0, (byte)0xEF, (byte)0x19, (byte)0xBC, (byte)0x4D, (byte)0x5B, (byte)0xCF, (byte)0x11, 
             (byte)0xA8, (byte)0xFD, (byte)0x00, (byte)0x80, (byte)0x5F, (byte)0x5C, (byte)0x44, (byte)0x2B };
        
    }
    
    
    private static class Extended {
        
        /** the title of the file */
        private static final String WM_TITLE = "WM/Title";
        
        /** the author of the file */
        private static final String WM_AUTHOR = "WM/Author";
        
        /** the title of the album the file is on */
        private static final String WM_ALBUMTITLE = "WM/AlbumTitle";
        
        /** the zero-based track of the song */
        @SuppressWarnings("unused")
        private static final String WM_TRACK = "WM/Track";
        
        /** the one-based track of the song */
        private static final String WM_TRACK_NUMBER = "WM/TrackNumber";
        
        /** the year the song was made */
        private static final String WM_YEAR = "WM/Year";
        
        /** the genre of the song */
        private static final String WM_GENRE = "WM/Genre";
        
        /** the description of the song */
        private static final String WM_DESCRIPTION = "WM/Description";
        
        /** the lyrics of the song */
        @SuppressWarnings("unused")
        private static final String WM_LYRICS = "WM/Lyrics";
        
        /** whether or not this is encoded in VBR */
        @SuppressWarnings("unused")
        private static final String VBR = "IsVBR";
        
        /** the unique file identifier of this song */
        @SuppressWarnings("unused")
        private static final String WM_UNIQUE_FILE_IDENTIFIER = "WM/UniqueFileIdentifier";
        
        /** the artist of the album as a whole */
        @SuppressWarnings("unused")
        private static final String WM_ALBUMARTIST = "WM/AlbumArtist";
        
        /** the encapsulated ID3 info */
        @SuppressWarnings("unused")
        private static final String ID3 = "ID3";
        
        /** the provider of the song */
        @SuppressWarnings("unused")
        private static final String WM_PROVIDER = "WM/Provider";
        
        /** the rating the provider gave this song */
        @SuppressWarnings("unused")
        private static final String WM_PROVIDER_RATING = "WM/ProviderRating";
        
        /** the publisher */
        @SuppressWarnings("unused")
        private static final String WM_PUBLISHER = "WM/Publisher";
        
        /** the composer */
        @SuppressWarnings("unused")
        private static final String WM_COMPOSER = "WM/Composer";
        
        /** the time the song was encoded */
        @SuppressWarnings("unused")
        private static final String WM_ENCODING_TIME = "WM/EncodingTime";
        
    }

}

class CountingInputStream extends FilterInputStream {
    
    private int _count = 0;
    
    public CountingInputStream( final InputStream in ) {
        super(in);
    }
    
    @Override
    public int read() throws IOException {
        
        final int read = super.read();
        
        if ( read != -1 ) {
            _count++;
        }
        
        return read;
        
    }
    
    @Override
    public int read( final byte[] b, final int off, final int len ) throws IOException {
        
        int read;
        
        try {
            read = super.read(b, off, len);
        }
        
        catch( final ArrayIndexOutOfBoundsException aioob ) {
            // happens.
            throw new IOException();
        }
        
        if ( read != -1 )
            _count += read;
        
        return read;
        
    }
    
    @Override
    public long skip( final long n ) throws IOException {
        
        final long skipped = super.skip(n);
        
        _count += (int) skipped;
        
        return skipped;
        
    }
    
    public void close() throws IOException {
        in.close();
    }
    
    public int getAmountRead() {
        return _count;
    }
    
    public void clearAmountRead() {
        _count = 0;
    }
    
    
}

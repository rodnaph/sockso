
package com.pugh.sockso.music;

import com.pugh.sockso.tests.SocksoTestCase;

import java.awt.datatransfer.DataFlavor;

public class MusicItemTest extends SocksoTestCase {
    
    public void testConstructors() {
        
        final String type = MusicItem.ALBUM, name = "my item";
        final int id = 123;
        
        assertNotNull( new MusicItem(type,id,name) );
        
    }

    public void testGetters() {

        final String type = MusicItem.ALBUM, name = "my item";
        final int id = 123;
        final MusicItem item =  new MusicItem(type,id,name);

        assertEquals( type, item.getType() );
        assertEquals( id, item.getId() );
        assertEquals( name, item.getName() );

        assertEquals( "al", new MusicItem(MusicItem.ALBUM,-1,"").getShortType() );
        assertEquals( "ar", new MusicItem(MusicItem.ARTIST,-1,"").getShortType() );
        assertEquals( "tr", new MusicItem(MusicItem.TRACK,-1,"").getShortType() );
        assertEquals( "pl", new MusicItem(MusicItem.PLAYLIST,-1,"").getShortType() );
        
    }

    public void testToString() {
        
        final String name = "some kind of name";
        final MusicItem item = new MusicItem( "", -1, name );
        
        assertEquals( name, item.toString() );
        
    }
    
    public void testGetTransferData() {
        
        final MusicItem item = new MusicItem( "", -1, "" );
        
        assertEquals( item, item.getTransferData(null) );
        
    }
    
    public void testIsDataFlavorSupported() {
        
        final MusicItem item = new MusicItem( "", -1, "" );
        final DataFlavor goodFlavor = MusicItem.MUSIC_ITEM_FLAVOR;
        final DataFlavor badFlavor = new DataFlavor( "bad/mime", "Something Else" );
        
        assertTrue( item.isDataFlavorSupported(goodFlavor) );
        assertFalse( item.isDataFlavorSupported(badFlavor) );

    }
    
    public void testGetTransferDataFlavors() {
        
        final MusicItem item = new MusicItem( "", -1, "" );
        final DataFlavor[] flavors = item.getTransferDataFlavors();
        
        assertNotNull( flavors );
        assertEquals( 1, flavors.length );
        
    }
}

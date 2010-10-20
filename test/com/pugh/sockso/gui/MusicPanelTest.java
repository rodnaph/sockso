
package com.pugh.sockso.gui;

import static com.pugh.sockso.tests.TestUtils.*;

import com.pugh.sockso.resources.Resources;
import com.pugh.sockso.resources.Locale;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JScrollPane;

import junit.framework.TestCase;

import static org.easymock.EasyMock.*;

public class MusicPanelTest extends TestCase {

    public void testConstructor() {
        
        final MusicPanel mp = new MusicPanel( null, null, null, null );
        
        assertNotNull( mp );
        
    }
    
    public void testGetTabbedPane() {
        
        final Locale locale = createMock( Locale.class );
        expect( locale.getString((String)anyObject()) ).andReturn( "" ).times( 2 );
        replay( locale );
        
        final Resources r = createMock( Resources.class );
        expect( r.getCurrentLocale() ).andReturn( locale ).times( 1 );
        expect( r.getImage((String)anyObject()) ).andReturn( getBlankImage() ).times( 2 );
        replay( r );
        
        final MusicPanel mp = new MusicPanel( null, null, null, r );
        final JTabbedPane tp = mp.getTabbedPane( null, null );
        
        assertEquals( 2, tp.getComponentCount() );
        assertEquals( JScrollPane.class, tp.getComponent(0).getClass() );
        assertEquals( JScrollPane.class, tp.getComponent(1).getClass() );
        
        verify( r );
        verify( locale );
        
    }
    
    public void testGetSitePlaylistsPanel() {

        final Locale locale = createMock( Locale.class );
        expect( locale.getString((String)anyObject()) ).andReturn( "" ).times( 1 );
        replay( locale );
        
        final Resources r = createMock( Resources.class );
        expect( r.getCurrentLocale() ).andReturn( locale ).times( 1 );
        expect( r.getImage((String)anyObject()) ).andReturn( getBlankImage() ).times( 1 );
        replay( r );
                
        final MusicPanel mp = new MusicPanel( null, null, null, r );
        final JPanel p = mp.getSitePlaylistsPanel( null, null );

        assertEquals( 2, p.getComponentCount() );

        verify( r );
        verify( locale );

    }
    
    public void testGetUserPlaylistsPanel() {

        final Locale locale = createMock( Locale.class );
        expect( locale.getString((String)anyObject()) ).andReturn( "" ).times( 1 );
        replay( locale );
        
        final Resources r = createMock( Resources.class );
        expect( r.getCurrentLocale() ).andReturn( locale ).times( 1 );
        expect( r.getImage((String)anyObject()) ).andReturn( getBlankImage() ).times( 1 );
        replay( r );
        
        final MusicPanel mp = new MusicPanel( null, null, null, r );
        final JPanel p = mp.getUserPlaylistsPanel( null );

        assertEquals( 2, p.getComponentCount() );
        
        verify( r );
        verify( locale );
        
    }
    
}

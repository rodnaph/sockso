
package com.pugh.sockso.resources;

import com.pugh.sockso.tests.SocksoTestCase;

public class LocaleFactoryTest extends SocksoTestCase {
    
    private LocaleFactory factory;
    
    @Override
    protected void setUp() {
        factory = new LocaleFactory( new FileResources() );
        factory.init( "en" );
    }
    
    public void testGetlocaleReturnsRequestedLocale() {
        Locale locale = factory.getLocale( "en" );
        assertEquals( "en", locale.getLangCode() );
    }
    
    public void testLocaleReturnedByGetlocaleIsLoadedWithLocaleStrings() {
        Locale locale = factory.getLocale( "en" );
        assertEquals( "Stack Trace", locale.getString("www.error.stackTrace") );
    }
    
    public void testGetdefaultlocaleReturnsTheDefaultLocaleConfigured() {
        Locale locale = factory.getDefaultLocale();
        assertEquals( "en", locale.getLangCode() );
    }
    
}

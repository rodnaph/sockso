
package com.pugh.sockso.inject;

import com.pugh.sockso.resources.Locale;
import com.pugh.sockso.resources.LocaleFactory;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class LocaleProvider implements Provider<Locale> {
    
    private final LocaleFactory localeFactory;
    
    @Inject
    public LocaleProvider( LocaleFactory localeFactory ) {
        
        this.localeFactory = localeFactory;
        
    }
    
    public Locale get() {
        
        return localeFactory.getDefaultLocale();
        
    }
    
}

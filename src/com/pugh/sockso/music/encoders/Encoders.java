/*
 * A class to do with using different built-ins within sockso
 * 
 */

package com.pugh.sockso.music.encoders;

import java.util.ArrayList;
import java.util.List;

public class Encoders {

    // different types of encoding 
    public enum Type {
        NONE,
        BUILTIN,
        CUSTOM
    };
    
    public enum Builtin {
        
        //
        // add built-ins here
        //
        Lame(new LameEncoder()),
        OggDecToLame(new OggDecToLame()),
        FlacToLame(new FlacToLame()),
        FfmpegToLame(new FfmpegToLame());
        
        private BuiltinEncoder encoder;

        private Builtin( BuiltinEncoder encoder ) {
            this.encoder = encoder;
        }

        public BuiltinEncoder getEncoder() {
            return encoder;
        }

        @Override
        public String toString() { 
            return encoder.toString();
        }

    };
    
    /**
     *  returns all the built-in built-ins we have that support the specified
     *  format.
     * 
     *  @param format the format (mp3, ogg, etc...)
     *  @return built-ins that support this format
     * 
     */
    
    public static Builtin[] getBuiltinEncoders( String format ) {
        
        List<Builtin> builtins = new ArrayList<Builtin>();
        
        for ( Builtin b : Builtin.values() )
            for ( String supportedFormat : b.getEncoder().getSupportedFormats() )
                if ( format.toLowerCase().equals(supportedFormat.toLowerCase()) ) {
                    builtins.add( b );
		}
        
        // messy generics... :(
        return builtins.toArray( new Builtin[builtins.size()] );

    }
    
    /**
     *  tries to fetch a built-in b by name, if none are found
     *  then returns null
     * 
     *  @param name the name of the b to find
     *  @return b if found, null otherwise
     * 
     */
    
    public static Builtin getBuiltinEncoderByName( String name ) {
        
        for ( Builtin b : Builtin.values() ) {
            if ( b.name().equals(name) ) {
                return b;
            }
        }
        
        return null;
        
    }
    
}

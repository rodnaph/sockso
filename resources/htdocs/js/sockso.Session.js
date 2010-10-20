
/**
 *  Allows storing name/value pairs in the users session.
 *
 *  The current implementation uses cookies, but in the future this could use
 *  client-side storage.
 *
 *  NB: Currently because we are using cookies, this means all instances of this
 *  object will share the same info.
 *
 *  var sess = new sockso.Session();
 *  var val = sess.get( 'name' );
 *  
 *  sess.set( 'name', 'value' );
 *
 */

sockso.Session = function() {

    /**
     *  Sets a named piece of information in the users session
     *  
     *  @param key
     *  @param value
     *  
     */

    this.set = function( key, value ) {

        var path = '/';
        var expires = new Date( new Date().getTime() + (30*24*60*60*1000) );

        document.cookie = key + '=' + encodeURI( value ) + '; ' +
            'path=' + path + '; ' +
            'expires=' + expires.toGMTString() + ';';

    };

    /**
     *  Tried to fetch a named value from the users session.  If the value is
     *  not found then null is returned.
     *
     *  @return String
     *
     */

    this.get = function( key ) {

        // Get cookie string and separate into individual cookie phrases:
        var cookie_string = '' + document.cookie;
        var cookie_array = cookie_string.split("; ");

        // Scan for desired cookie:
        for ( var i=0; i<cookie_array.length; ++i ) {

            var single_cookie = cookie_array [i].split('=');
            if ( single_cookie.length != 2 )
                    continue;
            var name  = decodeURI ( single_cookie[0] );
            var value = decodeURI ( single_cookie[1] );

            // Return cookie if found:
            if ( key == name )
                return value;

        }

        // Cookie was not found:
        return null;

    };

};

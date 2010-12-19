
/**
 *  Allows storing name/value pairs in the users session via persistsjs.
 *
 *  var sess = new sockso.Session();
 *
 *  sess.get( 'name', function(data) { ... } );
 *  sess.set( 'name', 'value' );
 *
 */

sockso.Session = function() {

    var store = new Persist.Store( 'Sockso' );

    /**
     *  Sets a named piece of information in the users session
     *  
     *  @param key
     *  @param value
     *  
     */

    this.set = function( key, value ) {

        store.set( key, value );

    };

    /**
     *  Tried to fetch a named value from the users session.  If the value is
     *  not found then null is returned.
     *
     *  @return String
     *
     */

    this.get = function( key, callback ) {

        return store.get( key, function(ok,data) {
            callback( data );
        });

    };

};

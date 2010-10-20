
/**
 *  Implements a basic hash table.
 *
 */

sockso.Hashtable = function() {

    var items = [];

    /**
     *  Gets a named property, using the default if it's not set
     *
     *  @param name
     *  @param defaultValue
     *
     *  @return Object
     *
     */

    this.get = function( name, defaultValue ) {

        var value = items[ name ];

        return value
            ? value
            : ( defaultValue ? defaultValue : '' );

    };

    /**
     *  Sets a property to a value
     *
     *  @param name
     *  @param value
     *
     */

    this.set = function( name, value ) {

        items[ name ] = value;

    };

    /**
     *  Sets the data to be used for properties
     *
     *  p.setData({ foo: 'bar' });
     *
     *  @param data
     *
     */

    this.setData = function( data ) {

        items = data;

    };

};

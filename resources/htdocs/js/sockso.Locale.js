
/**
 *  An object for storing and accessing locale data
 *
 */

sockso.Locale = function() {

    var data = {};

    /**
     *  Sets the locale text data to use
     *  
     *  l.setData({ foo: 'bar', baz: 'bazzle' });
     *  
     *  @param newData
     *  
     */

    this.setData = function( newData ) {

        data = newData;

    };

    /**
     *  Returns a locale string, optionally with some replacements made
     *  
     *  @param key
     *  @param reps
     *  
     */

    this.getString = function( key, reps ){

        var text = data[ key ] || '';

        if ( reps ) {
            $.each(reps,function(i,rep){
                text = text.replace( '%'+(i+1), rep, 'g' );
            });
        }
        
        return text;
        
    };

};


/**
 *  Base class for all sockso objects
 *
 */

sockso.Base = function( options ) {
    
    $.extend( this, options );

};

$.extend( sockso.Base.prototype, {

    /**
     * Returns the current user
     *
     */
    getUser: function() {

        return this.user;

    },
    
    /**
     * Returns the specified handler bound to this object
     * 
     */
    bind: function( handler, options ) {
        
        var self = this;
        
        return function() {
            return self[ handler ].apply(
                self,
                options || arguments
            );
        }
        
    },

    /**
     * Provide the jquery ajax method (this can then be overridden)
     * 
     */
    ajax: $.ajax

});

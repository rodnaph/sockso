
/**
 *  Base class for all sockso objects
 *
 */

sockso.Base = function( options ) {

    var self = this;
    var user = options.user || null;

    /**
     * Returns the current user
     *
     */
    self.getUser = function() {

        return user;

    };

    /**
     * Provide the jquery ajax method (this can then be overridden)
     * 
     */
    self.ajax = $.ajax;

    return self;

};

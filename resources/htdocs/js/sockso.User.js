
sockso.User = function( options ) {

    var self = new sockso.Base( options );
    var id = options.id;
    var name = options.name;

    /**
     *  Returns the users id
     *  
     *  @return int
     *  
     */

    self.getId = function() {
        
        return id;

    };

    /**
     *  Returns the users name
     *  
     *  @return String
     *  
     */

    self.getName = function() {
        
        return name;

    };

    return self;

};

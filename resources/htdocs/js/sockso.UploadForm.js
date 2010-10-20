
/**
 *  Allows adding validation to an upload form
 * 
 *  var form = new sockso.UploadForm( 'formId' );
 *  form.init();
 *   
 */

sockso.UploadForm = function( formId ) {

    var self = this;

    /**
     *  An error has occurred, do something
     *
     *  @param message
     *
     */

    self.error = function( message ) {

        alert( message );

    };

    /**
     *  validates the upload form to make sure all is ok
     *
     *  @return boolean true if all ok, false otherwise
     *
     */

    self.validate = function() {

        var isValid = true;

        // check required fields
        $( '#' +formId+ ' input[type=text]' ).each(function(i,oField) {
            if ( isValid && oField.value == '' ) {
                self.error( 'You missed a field' );
                isValid = false;
                return false;
            }
        });

        return isValid;

    };

    /**
     *  Initializes the form with validation
     *  
     */

    self.init = function() {

        $( '#uploadForm :input[type=submit]' )
            .click( self.validate );

    };

    return self;

};

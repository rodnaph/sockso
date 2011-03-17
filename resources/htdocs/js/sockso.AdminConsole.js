
sockso.AdminConsole = function() {};

sockso.AdminConsole.prototype = {

    /**
     * Output element
     */
    output: null,
    
    /**
     * Input field
     */
    input: null,

    /**
     * Initialise the console and any links to it
     * 
     */
    init: function() {
        
        this.ajax = $.ajax;

        this.initConsoleLinks();
        this.initConsole();

    },

    /**
     * Inits any links to open the admin console
     *
     */
    initConsoleLinks: function() {

        $( '.admin-console-link' ).click(function() {
            window.open(
                Properties.getUrl('/admin/console'),
                'AdminConsole',
                'width=800,height=600,toolbars=no'
            ).focus();
            return false;
        });

    },

    /**
     * Inits the admin console interface
     *
     */
    initConsole: function() {

        var self = this;

        $( 'body.admin-console' ).each(function() {
            self.initInterface();
        });

    },

    /**
     * Init the console interface
     * 
     */
    initInterface: function() {

        this.input = $( '.admin-console-input' );
        this.output = $( '<pre></pre>' )
                            .addClass( 'admin-console-output' )
                            .prependTo( 'form' );

        $( 'form' ).submit(
            this.onSubmit.bind( this )
        );

    },

    /**
     * Handler for when the console form is submitted
     *
     */
    onSubmit: function() {

        this.ajax({
            method: 'POST',
            url: Properties.getUrl('/admin/console/send'),
            data: {
                command: this.input.val()
            },
            success: this.onCommandResult.bind( this )
        });

        this.input.val( '' );

        return false;

    },

    /**
     * Handles the return of an executed command
     *
     * @param text String The command output
     *
     */
    onCommandResult: function( text ) {

        this.output.append( text + "\n" );

        this.output[0].scrollTop = this.output[0].scrollHeight;

    }

};

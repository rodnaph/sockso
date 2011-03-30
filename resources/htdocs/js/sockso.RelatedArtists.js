
sockso.RelatedArtists = function( options ) {

    var self = new sockso.Base( options );
    var properties = options.properties;
    var currentRequest = null;

    /**
     * Runs the widget
     *
     */
    self.init = function() {

        if ( self.isEnabled() ) {

            $.each( self.getArtistIds(), function(i,artistId) {
                self.getRelatedArtists( artistId, function(relatedArtists) {
                    self.updateArtist( artistId, relatedArtists );
                });
            });

        }

    };

    /**
     * loads similar artists, and executes the callback when done
     *
     * @param artistId
     * @param callback
     */
    self.getRelatedArtists = function( artistId, callback ) {

        if ( currentRequest ) {
            currentRequest.abort();
        }

        currentRequest = $.ajax({
            type: 'GET',
            url: Properties.getUrl('/json/similarArtists/' +artistId),
            success: function( responseText ) {
                currentRequest = null;
                var artists = null;
                eval( 'artists = ' +responseText );
                callback( artists );
            }
        });

    };

    /**
     * Updates the artist with the array of related artists
     *
     * @param artistId
     * @param relatedArtists Array
     */
    self.updateArtist = function( artistId, relatedArtists ) {

        var list = $( '<ul></ul>' );

        $.each( relatedArtists, function(i,related) {

            var relatedId = related.id.substring(2);

            $( '<li/>' )
               .addClass( 'related-artist related-artist-' +relatedId )
               .append(
                   $( '<a/>' )
                       .attr({ href: Properties.getUrl('/browse/artist/' +relatedId) })
                       .html( related.name )
               )
               .appendTo( list );

        });

        $( '.artist-' +artistId )
            .empty()
            .append( list );

        options.ajaxer.attach( list );

        self.addPlayLink( list, relatedArtists );

    };

    /**
     *  Adds a link which allows playing all related artists
     *
     *  @param list
     *  @param relatedArtists
     */
    self.addPlayLink = function( list, relatedArtists ) {

        if ( relatedArtists.length == 0 ) return;

        var url = '';
        var img = $( '<img></img>' )
                        .attr({ src: Properties.getUrl('/<skin>/images/play.png') });

        $.each( relatedArtists, function(i,related) {
            url += related.id + '/';
        });

        var link = $( '<a></a>' )
            .addClass( 'play-related' )
            .attr({
                href: 'javascript:player.play("' +url+ '","orderBy=random");',
                title: 'Play similar artists'
            })
            .append( img );

        $( '<li></li>' )
            .append( link )
            .prependTo( list );

    };

    /**
     * Returns an array of the artist IDs found on the page
     *
     * @return Array
     */
    self.getArtistIds = function() {

        var found = {};
        var ids = [];

        $( '.related' ).each(function(){

            var classes = $( this ).attr('class').split( ' ' );

            for ( var i=0; i<classes.length; i++ ) {
                var name = classes[ i ];
                var info = name.match( /artist-(\d+)/ );
                if ( info ) {
                    var id = info[ 1 ];
                    if ( !found[id] ) {
                        ids.push( id );
                        found[ id ] = true;
                    }
                }
            }

        });

        return ids;

    };

    /**
     * Indicates if the related artist functionality is enabled or not
     *
     * @return boolean
     */
    self.isEnabled = function() {

        return properties.get( 'www.similarArtists.disable' ) != 'yes';

    };

    return self;

};

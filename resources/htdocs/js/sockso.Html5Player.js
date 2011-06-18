
var html5player = null;

/**
 *  A javascript player using the HTML 5 <audio> tag.
 *  Playlist management is similar to the JS player
 *  
 *
 */
sockso.Html5Player = function() {

    var self = this,
        playlist = [],
        playing = null,
        artworkDiv = null,
        controlsDiv = null,
        playlistDiv = null,
        infoDiv = null,
    	audioElt = null;

    /**
     *  selects the specified item as being the current playing
     *
     *  @param item
     *
     */

    function selectItem( item ) {

        // set playlist styling
        $( 'li', playlistDiv ).removeClass( 'current' );
        $( '#item' +item.id ).addClass( 'current' );

        artworkDiv
            .empty()
            .append(
                $( '<img></img>' )
                    .attr({
                        src: Properties.getUrl('/file/cover/al' +item.album.id)
                    })
            );

        infoDiv.html( item.artist.name+ ' - ' +item.name );

    }

    /**
     *  redraws the playlist on the page
     *
     */

    this.refresh = function() {

        playlistDiv.empty();

        $.each( playlist, function(i,item) {

            $( '<li></li>' )
                .attr({ id: 'item' +item.id })
                .append(
                    $( '<a></a>' )
                        .html( item.artist.name+ ' - ' +item.name )
                        .attr({ href: 'javascript:;' })
                        .click(function() {
                            self.playItem( i );
                        })
                )
                .appendTo( playlistDiv );

        });

    };

    /**
     *  plays the item in the playlist at the specified index
     *
     *  @param index
     *
     */

    this.playItem = function( index ) {

        var item = playlist[ index ];

        if ( item ) {

            selectItem( item );
            
            audioElt.attr('src', Properties.getUrl('/stream/' + item.id));

            playing = index;

        }

    };

    /**
     *  Returns the index of the currently playing item
     * 
     *  @return int
     *   
     */

    this.getCurrentItem = function() {
        
        return playing;

    };

    /**
     *  plays the previous item
     *
     */

    this.playPrevItem = function() {

        if ( playing != -1 && playing > 0 ) {
            self.playItem( playing - 1 );
        }
        
    };

    /**
     *  plays the next item
     *
     */

    this.playNextItem = function() {

        if ( playing != -1 && playing < playlist.length - 1 ) {
            self.playItem( playing + 1 );
        }

    };

    /**
     *  stops anything currently playing
     *
     */

    this.stopPlaying = function() {

        audioElt.get(0).pause();
    	$( '#playlist li' ).removeClass( 'current' );
        playing = -1;

    };

    /**
     *  Adds a track to the playlist
     *
     *  @param track The track to add
     *
     */

    this.addTrack = function( track ) {

        playlist.push( track );

    };

    /**
     *  Initializes the player using the specified element id
     *  
     *  @param playerDivId Container DIV id
     *  @param skin
     *  
     */

    this.init = function( playerDivId, skin ) {

        /**
         *  Creates and returns a control for the controls panel
         *  
         *  @param onclick Click handler
         *  @param imgName name of image for control
         *  
         *  @return jQuery
         *  
         */

        function getControl( onclick, imgName ) {
            
            return $( '<a></a>' )
                        .click(function() {
                            onclick.call( self );
                        })
                        .attr({ href: 'javascript:;' })
                        .append(
                            $( '<img></img>')
                                .attr({ src: Properties.getUrl('/<skin>/images/html5player/' +imgName+ '.png') })
                        );

        }
       
        skin = skin || 'original';

        audioElt = $('<audio></audio>')
        			.attr( 'controls', 'controls')
        			.attr( 'autoplay', 'autoplay')
					.text( 'Your browser doesn\'t support HTML 5 <audio> element.' )
					.error ( function () { html5player.playNextItem(); } );
        
        var audioDiv = $( '<div></div>' )
        			.addClass( 'audio' )
        			.append( audioElt );

        artworkDiv = $( '<div></div>' )
                        .addClass( 'artwork' );

        playlistDiv = $( '<div></div>' )
                        .addClass( 'playlist' );

        controlsDiv = $( '<div></div>' )
                        .addClass( 'controls' )
                        .append( getControl( self.playPrevItem, 'prev' ) )
                        .append( getControl( self.stopPlaying, 'stop' ) )
                        .append( getControl( self.playNextItem, 'next' ) );

        infoDiv = $( '<div></div>' )
                    .addClass( 'info' )
                    .html( 'Click a track to start playing!' );
        
        
        $( '#' + playerDivId )
            .addClass( 'html5player' )
            .append( artworkDiv )
            .append( playlistDiv )
            .append( controlsDiv )
            .append( infoDiv )
            .append( audioDiv );
    };
    
    /**
     * Starts pkaying, once tracks have been
     * added
     */
    
    this.start = function () {
        this.refresh.refresh();
        this.playItem(0);    	
    },

    /**
     *  Updates the player playlist with the specified tracks
     *  
     *  @param tracks
     *  
     */

    this.update = function( tracks ) {

        var currentItem = null;

        // if we're playing something store it
        if ( playing != -1 ) {
            currentItem = playlist[ playing ];
        }

        // refresh playlist
        playlist = tracks;
        self.refresh();

        // if we have a saved item try and reload it
        if ( currentItem != null ) {
            $.each( playlist, function(i,item) {
                if ( currentItem.id == item.id ) {
                    selectItem( item ); // found!
                    playing = i;
                    return false;
                }
            });
        }

    };

};

/**
 *  attach reloader function to window
 *
 */

window.jsp_reload = function( playUrl ) {

    var url = Properties.getUrl('/json/tracks/' +playUrl);

    $.getJSON( url, {}, jsplayer.update );

};
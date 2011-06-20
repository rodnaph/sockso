
var html5player = null;

var HTML5PLAYER_MODE_NORMAL = 1;
var HTML5PLAYER_MODE_RANDOM = 2;

/**
 *  A javascript player using the HTML 5 <audio> tag.
 *  Playlist management is similar to the JS player
 *  
 *
 */
sockso.Html5Player = function() {

    this.playlist = [];
    this.mode = false;
    this.playing = null;
    this.artworkDiv = null;
    this.controlsDiv = null;
    this.playlistDiv = null;
    this.infoDiv = null;
    this.audioElt = null;

};

sockso.Html5Player.prototype = new sockso.Base();

$.extend( sockso.Html5Player.prototype, {
    
    /**
     *  selects the specified item as being the current playing
     *
     *  @param item
     *
     */

    selectItem: function( item ) {

        // set playlist styling
        $( 'li', this.playlistDiv ).removeClass( 'current' );
        $( '#item' +item.id ).addClass( 'current' );

        this.artworkDiv
            .empty()
            .append(
                $( '<img></img>' )
                    .attr({
                        src: Properties.getUrl('/file/cover/al' +item.album.id)
                    })
            );

        this.infoDiv.html( item.artist.name+ ' - ' +item.name );

    },

    /**
     *  redraws the playlist on the page
     *
     */
    
    refresh: function() {

        var self = this;

        this.playlistDiv.empty();

        $.each( this.playlist, function(i,item) {

            $( '<li></li>' )
                .attr({ id: 'item' +item.id })
                .append(
                    $( '<a></a>' )
                        .html( item.artist.name+ ' - ' +item.name )
                        .attr({ href: 'javascript:;' })
                        .click( self.bind('playItem',[i]) )
                )
                .appendTo( self.playlistDiv );

        });

    },

    /**
     *  plays the item in the playlist at the specified index
     *
     *  @param index
     *
     */

    playItem: function( index ) {

        var item = this.playlist[ index ];

        if ( item ) {

            this.selectItem( item );
            this.audioElt.attr('src', Properties.getUrl('/stream/' + item.id));
            this.playing = index;

        }

    },

    /**
     *  Returns the index of the currently playing item
     * 
     *  @return int
     *   
     */

    getCurrentItem: function() {
        
        return this.playing;

    },

    /**
     *  plays the previous item
     *
     */

    playPrevItem: function() {

        if ( this.playing != -1 && this.playing > 0 ) {
            this.playItem( this.playing - 1 );
        }
        
    },

    /**
     *  plays the next item
     *
     */

    playNextItem: function() {

        if ( this.playing != -1 && this.playing < this.playlist.length - 1 ) {
            this.playItem( this.playing + 1 );
        }
        
        else if (this.mode == HTML5PLAYER_MODE_RANDOM && this.playing >= this.playlist.length - 1) {
            // We're in random mode, refresh the page
            // in order to get more random tracks
            window.location.reload();
        }

    },

    /**
     *  stops anything currently playing
     *
     */

    stopPlaying: function() {

        this.audioElt.get(0).pause();
    	$( '#playlist li' ).removeClass( 'current' );
        this.playing = -1;

    },

    /**
     *  Adds a track to the playlist
     *
     *  @param track The track to add
     *
     */

    addTrack: function( track ) {

        this.playlist.push( track );

    },

    /**
     *  Creates and returns a control for the controls panel
     *  
     *  @param onClick Click handler
     *  @param imgName name of image for control
     *  
     *  @return jQuery
     *  
     */

    makeControl: function( onClick, imgName ) {

        return $( '<a></a>' )
                    .click( onClick.bind(this) )
                    .attr({ href: 'javascript:;' })
                    .append(
                        $( '<img></img>')
                            .attr({ src: Properties.getUrl('/<skin>/images/html5player/' +imgName+ '.png') })
                    );

    },

    /**
     *  Initializes the player using the specified element id
     *  
     *  @param playerDivId Container DIV id
     *  @param skin String for skin
     *  @param mode HTML5PLAYER_MODE_*
     */

    init: function( playerDivId, skin, mode ) {

        var self = this;

        skin = skin || 'original';
        mode = mode || HTML5PLAYER_MODE_NORMAL;

        this.audioElt = $('<audio></audio>')
        			.attr( 'controls', 'controls')
        			.attr( 'autoplay', 'autoplay')
					.text( 'Your browser doesn\'t support HTML 5 <audio> element.' )
					.error ( this.bind('playNextItem') )
        			.bind ( 'ended', this.bind('playNextItem') );
        
        var audioDiv = $( '<div></div>' )
        			.addClass( 'audio' )
        			.append( this.audioElt );

        this.artworkDiv = $( '<div></div>' )
                        .addClass( 'artwork' );

        this.playlistDiv = $( '<div></div>' )
                        .addClass( 'playlist' );

        this.controlsDiv = $( '<div></div>' )
                        .addClass( 'controls' )
                        .append( this.makeControl( self.playPrevItem, 'prev' ) )
                        .append( this.makeControl( self.stopPlaying, 'stop' ) )
                        .append( this.makeControl( self.playNextItem, 'next' ) );

        this.infoDiv = $( '<div></div>' )
                    .addClass( 'info' )
                    .html( 'Click a track to start playing!' );
        
        
        $( '#' + playerDivId )
            .addClass( 'html5player' )
            .append( this.artworkDiv )
            .append( this.playlistDiv )
            .append( this.controlsDiv )
            .append( this.infoDiv )
            .append( audioDiv );
            
    },
    
    /**
     * Starts pkaying, once tracks have been
     * added
     */
    
    start: function () {
        
        this.refresh();
        this.playItem(0);    	
        
    },

    /**
     *  Updates the player playlist with the specified tracks
     *  
     *  @param tracks
     *  
     */

    update: function( tracks ) {

        var self = this;
        var currentItem = null;

        // if we're playing something store it
        if ( this.playing != -1 ) {
            currentItem = this.playlist[ this.playing ];
        }

        // refresh playlist
        this.playlist = tracks;
        this.refresh();

        // if we have a saved item try and reload it
        if ( currentItem != null ) {
            $.each( this.playlist, function(i,item) {
                if ( currentItem.id == item.id ) {
                    self.selectItem( item ); // found!
                    self.playing = i;
                    return false;
                }
            });
        }

    }

});


/**
 *  attach reloader function to window
 *
 */

window.jsp_reload = function( playUrl ) {

    var url = Properties.getUrl( '/json/tracks/' +playUrl );

    $.getJSON( url, {}, html5player.update.bind(html5player) );

};

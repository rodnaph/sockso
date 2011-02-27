
// Because of limitations of JWPlayer only 1 instance of the JSPlayer
// is supported at a time.
var jsplayer = null;

/**
 *  A javascript player for the interface that uses a flash player to do the
 *  actual audio playing.  This means we have control over the playlist.
 *
 */

sockso.JSPlayer = function() {

    var self = this,
        playlist = [],
        playing = null,
        jwplayer = null,
        artworkDiv = null,
        controlsDiv = null,
        playlistDiv = null,
        infoDiv = null;

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
                        src: Properties.get( "server.basepath", "/" )+'file/cover/al' +item.album.id
                    })
            );

        infoDiv.html( item.artist.name+ ' - ' +item.name );

    }

    /**
     *  the state of the player has changed
     *
     *  @param obj State change object from JWPlayer
     *
     */

    this.playerStateChanged = function( obj ) {

        // if the current item has finished playing, lets check
        // to see if there are any more items in the playlist

        if ( obj.newstate == 'COMPLETED' ) {

            if ( playing < playlist.length - 1 ) {
                self.playItem( playing + 1 );
            }

            else {
                playing = -1;
                $( 'li', playlistDiv ).removeClass( 'current' );
            }

        }

    };

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

            // play the item
            jwplayer.sendEvent( 'LOAD', {
                type: 'sound',
                file: Properties.get( "server.basepath", "/" )+'stream/' +item.id
            });
            jwplayer.sendEvent( 'PLAY' );

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

        $( '#playlist li' ).removeClass( 'current' );
        jwplayer.sendEvent( 'STOP' );
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
     *  Returns the JWPlayer object we're using internally
     *
     *  @return HTMLDivElement
     *
     */

    this.getPlayerObject = function() {

        return jwplayer;
        
    };

    this.setPlayerObject = function( playerObject ) {
        
        jwplayer = playerObject;
        
    };

    /**
     *  Initializes the player using the specified element id
     *  
     *  @param playerDivId the id for the JWPlayer element
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
                                .attr({ src: Properties.get( "server.basepath", "/" )+'file/skins/' +skin+ '/images/jsplayer/' +imgName+ '.png' })
                        );

        }

        skin = skin || 'original';

        var jwplayerId = playerDivId + '-jwplayer';
        var jwDiv = $('<p></p>')
                    .attr({ id: jwplayerId })
                    .addClass( 'jwplayer' );

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
            .addClass( 'jsplayer' )
            .append( jwDiv )
            .append( artworkDiv )
            .append( playlistDiv )
            .append( controlsDiv )
            .append( infoDiv );

        var s1 = new SWFObject( Properties.get( "server.basepath", "/" )+'file/flash/jwplayer-4.2/player.swf','player','0','0','9');
        s1.addParam( 'allowfullscreen','false' );
        s1.addParam( 'allowscriptaccess','always' );
        s1.write( jwplayerId );

    };

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

    var url = Properties.get( "server.basepath", "/" )+'json/tracks/' +playUrl;

    $.getJSON( url, {}, jsplayer.update );

};

/**
 *  the state of the jwplayer has changed
 *
 *  @param obj State change object
 *  
 */

function jsp_playerStateChanged( obj ) {
    
    jsplayer.playerStateChanged( obj );

}

/**
 *  the player has intialised ok, we can set ourselves up now
 *
 *  @param obj
 *
 */

function playerReady( obj ) {

    var jwplayer = document.getElementById( obj.id );
    jwplayer.addModelListener( 'STATE', 'jsp_playerStateChanged' );

    jsplayer.setPlayerObject( jwplayer );
    jsplayer.refresh();
    jsplayer.playItem( 0 );
   
}

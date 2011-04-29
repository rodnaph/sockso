
// strings used to keep our cookie data apart
var SPLIT_PLITEM = '_:__:_';
var SPLIT_PLVARS = ':_::_:';

/**
 *  saves a playlist
 *
 */
 
function savePlaylist( options ) {

    options = options || {};

    var url = options.url || getPlaylistAsUrl();

    if ( url == '' )
        alert( Locale.getString('www.error.nothingToPlay') );

    else {
    
        var trackCount = $( '#PlaylistContents .playlistItem' ).length;
        var name = prompt( Locale.getString('www.confirm.playlistName') );
        if ( name == '' || name == null ) return;

        $.ajax({
            url: Properties.getUrl('/json/savePlaylist/' + encodeURIComponent(name) + '/'+url),
            success: function( responseText ) {
                eval( 'var result = ' + responseText );
                if ( result.match(/^\d+$/) ) {

                    var li = sockso.util.getMusicElement(
                        new MusicItem('pl'+result,name), true
                    );

                    // need to add number of tracks in playlist, and
                    // a delete link to the list item
                    li.attr({ id: 'userPlaylist' +result })
                        .append( ' by <b>you</b> (' +trackCount+ ' tracks)' )
                        .append( $('<a/>')
                                  .attr({ href: 'javascript:;' })
                                  .addClass( 'delete' )
                                  .click(function(){ deletePlaylist(result); })
                                  .append( ' ' )
                                  .append( $('<img/>').attr({ src: Properties.getUrl('<skin>/images/remove.png') }))
                        )
                        .css({ display: 'none' });

                    // clear any empty item, then add the new one
                    $( '#userPlaylists li.empty' )
                        .remove();
                    $( '#userPlaylists' )
                        .append( li );
                    li.fadeIn();

                }
                else
                    alert( 'Error: ' + result );
            }
        });

    }
    
}

/**
 *  the user has asked to delete one of their saved playlists
 * 
 *  @param playlistId
 *   
 */

function deletePlaylist( playlistId ) {
    
    if ( confirm(Locale.getString("www.confirm.deletePlaylist")) ) {
        
        $.ajax({
            url: Properties.getUrl('/json/deletePlaylist/' +playlistId),
            success: deletePlaylistHandler
        });

    }
    
}

/**
 *  handles the return of the call to delete a playlist
 *  
 *  @param result
 *  
 */

function deletePlaylistHandler( result ) {

    eval( 'result = ' +result );
    if ( result.match(/^\d+$/) ) {

        // fadeOut the item, then remove it
        $( '#userPlaylist' +result )
          .fadeOut(function(){

              $( this ).remove();

              // if it's now empty, add an empty marker item
              if ( $('#userPlaylists li').length == 0 ) {
                  $( '#userPlaylists' ).append(
                      $( '<li/>' )
                        .addClass( 'empty' )
                        .html( Locale.getString("www.text.noResults") )
                  );
              }

          });

    }

}

/**
 *  A playlist control that can be added to/downloaded/played/etc...  Just give it
 *  the ID of the parent element to attach itself to.
 *
 *  The 'player' parameter should be the sockso.Player object used for playing music
 *  
 *  @param options
 *  
 */

sockso.Playlist = function( options ) {

    var self = new sockso.Base( options ),
        parentId = options.parentId,
        player = options.player,
        session = options.session,
        ajaxer = options.ajaxer,
        items = [],
        contents = null,
        empty = null,
        nextPlaylistId = 0,
        sharer = null;

    /**
     *  Adds an item to the playlist and draws it on the page
     *  
     *  @param item
     *  
     */

    self.add = function( item ) {

        item.playlistId = nextPlaylistId++;
        items.push( item );

        if ( contents ) {
            empty.remove();
            var elem = self.getMusicElement( item ).hide();
            ajaxer.attach( elem );
            contents.append( elem );
            elem.fadeIn( 'slow' );
        }

        self.save();

    };

    /**
     *  Returns a DOM element for the music item
     *  
     *  @return jQuery
     *  
     */

    self.getMusicElement = function( item ) {

        var element = sockso.util.getMusicElement( item, false );

        return element;

    };

    /**
     *  Removes an item from the playlist using the specified playlist id
     *  
     *  @param playlistId
     *
     */

    self.remove = function( playlistId ) {

        $.each( items, function(i,item) {
            if ( item.playlistId == playlistId ) {
                items.splice( i, 1 );
                return false;
            }
        });

        $( '#playlist-item-' + playlistId )
            .slideUp( 'slow', function() {
                if ( items.length == 0 ) {
                    self.showEmpty();
                }
            });

        self.save();

    };

    /**
     *  Clears the playlist of items
     *
     */

    self.clear = function() {

        items = [];
        $( 'li', contents )
            .slideUp(function() {
                contents.empty();
                self.showEmpty();
            });

        self.save();

    };

    /**
     *  Sets the items in the playlist (no redraw is done)
     *  
     *  @param newItems
     *  
     */

    self.setItems = function( newItems ) {

        self.clear();

        $.each( newItems, function(i,item) {
            self.add( item );
        });

    };

    /**
     *  Returns the items currently in the playlist
     *
     *  @return array
     *
     */

    self.getItems = function() {

        return items;

    };

    /**
     *  Downloads the items from the playlist
     *  
     */

    self.download = function() {

        var url = self.getAsUrl();

        if ( url == '' ) {
            alert( Locale.getString('www.error.nothingToDownload') );
        }
        else {
            location.href = Properties.getUrl('/download/' + url);
        }

    };

    /**
     *  Plays the playlist with the configured player
     *  
     */

    self.play = function() {

        player.play( self.getAsUrl() );

    };

    /**
     *  Opens a popup with some code to embed a player with this playlist
     *  
     */

    self.share = function( evt ) {

        if ( !sharer ) {
            sharer = new sockso.Sharer( $(evt.target) );
            sharer.init();
            sharer.addStandardLinks();
        }
        
        sharer.setData( self.getAsUrl() );
        sharer.show();

    };

    /**
     *  Returns a string of all the playlist items that can be used in a URL
     *  back to Sockso, eg. '/tr123/ar456/al789'
     *
     *  @return String
     *
     */

    self.getAsUrl = function() {

        var url = '';

        for ( var i=0; i<items.length; i++ )
            url += '/' + items[ i ].id;

        return url.substring( 1 );

    };

    /**
     *  Loads the playlist from the users session
     *
     *  @param onLoad Function
     *
     */

    self.load = function( onLoad ) {

        session.get( 'playlist', function(data) {
            
            if ( data ) {
                var sessionItems = data.split( SPLIT_PLITEM );
                for ( var i=0; i<sessionItems.length; i++ ) {
                    var item = sessionItems[ i ];
                    var parts = item.split( SPLIT_PLVARS );
                    if ( parts.length == 3 ) {
                        items.push( new sockso.MusicItem(parts[0],parts[1],parts[2]) );
                    }
                }
            }

            if ( onLoad ) { onLoad(); }

        });

    };

    /**
     *  Saves the playlist to the session
     *  
     */

    self.save = function() {

        var data = '';

        for ( var i=0; i<items.length; i++ ) {
            var item = items[ i ];
            var s = item.id + SPLIT_PLVARS + item.name + SPLIT_PLVARS + item.playlistId;
            data += ( data == '' ? s : SPLIT_PLITEM + s );
        }

        session.set( 'playlist', data );

    };

    /**
     *  Shows the empty playlist item
     *
     */

    self.showEmpty = function() {

        empty.slideUp();
        contents.append( empty );
        empty.slideDown( 'slow' );

    };

    /**
     *  Does a complete refresh of the playlist contents.  Clears all the nodes
     *  that have been drawn then adds them again.
     *  
     */

    self.refresh = function() {

        contents.empty();

        if ( items.length == 0 ) {
            self.showEmpty();
        }
        
        else {
            $.each( items, function(i,item) {
                var elem = self.getMusicElement( item );
                ajaxer.attach( elem );
                contents.append( elem );
            });
        }

    };

    /**
     *  Initialized the control on the page, creating elements, etc...
     *  
     */

    self.init = function() {

        var skin = Properties.get( 'www.skin', 'original' );

        function makeLink( action, callback ) {

            callback = callback || self[ action ];
            
            var localeKey = 'www.link.' + action;

            return $( '<a></a>' )
                    .addClass( action )
                    .html( Locale.getString(localeKey) )
                    .attr({ href: 'javascript:;' })
                    .click( callback );

        }

        var heading = $( '<h2></h2>' )
                            .addClass( 'bg2' )
                            .html( Locale.getString('www.title.playlist') );

        var stdControls = $( '<div></div>' )
                            .addClass( 'controls' )
                            .append( makeLink('clear') )
                            .append( makeLink('play') );

        if ( Properties.get('www.disableDownloads') != 'yes' ) {
            stdControls.append( makeLink('download') );
        }

        stdControls.append( makeLink('share') );

        empty = $( '<li></li>' )
                .append( Locale.getString('www.text.emptyPlaylist',[skin]) );

        contents = $( '<ul></ul>' )
                        .addClass( 'contents' )
                        .append( empty );

        var div = $( '#' +parentId );

        div.empty()
            .addClass( 'playlist' )
            .append( heading )
            .append( stdControls )
            .append( contents );

        var user = self.getUser();

        if ( user != null ) {
            div.append(
                $( '<div></div>' )
                    .addClass( 'controls user-controls' )
                    .append( makeLink('save',function() {
                        savePlaylist({
                            url: self.getAsUrl()
                        });
                    }))
            );
        }

    };

    return self;

};

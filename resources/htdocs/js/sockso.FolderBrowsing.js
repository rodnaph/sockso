
/**
 *  represents a folder.  the name should have unicode entity encoded, and
 *  the path should be URL encoded.
 *  
 */

function Folder( name, path ) {
    this.name = name;
    this.path = decodeURIComponent(path);
    this.isFolder = true;
}

/**
 *  represents a file.  the name should have unicode entity encoded, and
 *  the path should be URL encoded.
 *  
 */

function File( name, path ) {
    this.name = name;
    this.path = decodeURIComponent(path).replace( /\\/g, '/' );
    this.isFolder = false;
}

/**
 * Folder browsing object
 *
 * @param player sockso.Player
 * @param playlist sockso.Playlist
 */
sockso.FolderBrowsing = function( player, playlist ) {

    this.player = player;
    this.playlist = playlist;

};

/**
 *  plays a file for a path
 *
 *  @param path path of file to play
 *
 */
sockso.FolderBrowsing.prototype.playFile = function( path ) {
    
    this.resolvePath(
        path,
        this.onPlayPathResolved.bind( this ),
        Locale.getString( 'www.error.trackNotFound' )
    );

};

/**
 * Handler to play some music after paths have been resolved
 *
 * @param responseText String
 */
sockso.FolderBrowsing.prototype.onPlayPathResolved = function( responseText ) {
    
    eval( 'var track = ' +responseText );
    
    this.player.play( 'tr' +track['id'] );
    
};

/**
 *  takes a string with encoded named entities (eg. &amp;) and turns
 *  them back into their normal characters
 *
 *  @param str
 *
 */
sockso.FolderBrowsing.prototype.decodeEntities = function( str ) {

    var entities = new Array(
        '&amp;', '&',
        '&lt;', '<',
        '&gt;', '>',
        '&quot;', '"',
        '&apos;', "'"
    );

    for ( var i=0; i<entities.length; i+=2 ) {
        str = str.replace( eval('/'+entities[i]+'/g'), entities[i+1] );
    }

    return str;

};

/**
 *  traverses up the DOM to work out our current path, the path returned
 *  already has it's components properly URI encoded.
 *
 */
sockso.FolderBrowsing.prototype.getPath = function( folder ) {

    var link = $( 'a', folder )[ 0 ];
    var id = folder.attr( 'id' );
    var name = encodeURIComponent(this.decodeEntities( link.firstChild.nodeValue ));

    return id.match( /^collection-\d+$/ )
        ? ''
        : this.getPath(folder.parent().parent()) + '/' + name;

};

/**
 *  traverses up the DOM to find the collection we're in
 *
 *  @param elem current element
 *
 */
sockso.FolderBrowsing.prototype.getCollectionId = function( elem ) {

    var id = elem.attr( 'id' );
    var matches = id != null
        ? id.match( /^collection-(\d+)$/ )
        : null;

    return ( matches )
        ? matches[ 1 ]
        : this.getCollectionId( elem.parent() );

};

/**
 *  makes an ajax call to resolve a path to a track id and then passes
 *  control to the specified handler function
 *
 *  @param path
 *  @param handler
 *  @param errorMessage
 *
 */
sockso.FolderBrowsing.prototype.resolvePath = function( path, handler, errorMessage ) {

    this.ajax({
        type: 'POST',
        url: Properties.getUrl('/json/resolvePath'),
        data: {
            path: path
        },
        success: handler,
        error: function(){ alert(errorMessage); }
    });

};

/**
 *  tries to resolve a path to a track then add it to the playlist
 *
 *  @param path
 *
 */
sockso.FolderBrowsing.prototype.addFileToPlaylist = function( path ) {

    this.resolvePath(
        path,
        this.onAddFileToPlaylist.bind( this ),
        Locale.getString('www.error.trackNotFound')
    );

};

/**
 * Handler for when we've resolved a track path to add to the playlist
 *
 * @param responseText String
 */
sockso.FolderBrowsing.prototype.onAddFileToPlaylist = function( responseText ) {

    eval( 'var track = ' +responseText );

    var item = new MusicItem( 'tr' + track.id, track.name );

    this.playlist.add( item );
    
};

/**
 *  looks at a files path and decides if this is a media file (mp3,
 *  ogg or wma)
 *
 *  @param file File object
 *
 *  @return boolean
 *
 */
sockso.FolderBrowsing.prototype.isMediaFile = function( file ) {

    var exts = new Array( 'mp3', 'ogg', 'wma', 'flac', 'aac' );
    var ext = file.path.toLowerCase().substring( file.path.lastIndexOf('.') + 1 );

    for ( var i=0; i<exts.length; i++ ) {
        if ( exts[i] == ext ) {
            return true;
        }
    }

    return false;

};

/**
 *  creates a folder node for the tree
 *
 *  @param folder folder object
 *
 */
sockso.FolderBrowsing.prototype.getFolderItem = function( folder ) {

    var self = this;
    var link = $( '<a></a>' )
            .attr({
                href: 'javascript:;'
            })
            .click( this.onToggleClicked.bind(this) )
            .html( folder.name );

    var play = this.getTrackAction( 'play', function() {
        var extraArgs = 'path=' +folder.path;
        self.player.play( '', extraArgs );
        return false;
    }, 'Play folder' );

    var download = null;
    if ( Properties.get('www.disableDownloads') != 'yes' )
        download = this.getTrackAction( 'download', function() {
            self.getTracksForFolder( folder, self.downloadFolder.bind(self) );
        }, 'Download folder' );

    var actions = $( '<span></span>' )
        .addClass( 'actions' )
        .append( play )
        .append( download );
        
    link.append( actions );

    return $( '<li></li>' )
        .addClass( 'folder' )
        .append( link )
        .append( $('<ul></ul>') );

};

/**
 *  creates an image wrapped in an anchor for use when playing tracks
 *
 *  @param icon
 *  @param action
 *  @param title
 *
 */
sockso.FolderBrowsing.prototype.getTrackAction = function( icon, action, title ) {

    return $( '<a></a>' )
        .attr({
            href: 'javascript:;',
            title: title
        })
        .click( action )
        .append( $('<img />').attr('src',Properties.getUrl('<skin>/images/' + icon +'.png') ))
        .append( '<span>&nbsp;</span>' );

};

/**
 *  tries to resolve a path to a tarck and then download it
 *
 *  @param path
 *
 */
sockso.FolderBrowsing.prototype.downloadFile = function( path ) {

    this.resolvePath( path, function( responseText ) {
        eval( 'var track = ' +responseText );
        self.location.href = Properties.getUrl('/download/tr' +track['id']);
    });

};

/**
 *  creates a track node for a file (assumed to be a media file)
 *
 *  @param file
 *
 *  @return LI
 *
 */
sockso.FolderBrowsing.prototype.getTrackItem = function( file ) {

    var self = this;

    var play = this.getTrackAction(
        'play',
        function() { self.playFile(file.path); },
        file.name
    );

    var playlist = this.getTrackAction(
        'add',
        function() { self.addFileToPlaylist( file.path ); },
        file.name
    );

    var download = ( Properties.get('www.disableDownloads') != 'yes' )
        ? this.getTrackAction( 'download', function() {
              self.downloadFile( file.path );
          }, 'Download: ' +file.name )
        : null;

    return $( '<li></li>' )
        .addClass( 'audioFile' )
        .append( play )
        .append( playlist )
        .append( download )
        .append( file.name );

};

/**
 *  works out the path and collection for a folder and makes an ajax request
 *  to find the tracks in it to play
 *
 *  the playUrlAction should be a function to handle the play url that will
 *  be constructed from the data in the response
 *
 *  @param folder
 *  @param handler
 *
 */
sockso.FolderBrowsing.prototype.getTracksForFolder = function( folder, handler ) {

    var url = Properties.getUrl('/json/tracksForPath?path=' +encodeURIComponent(folder.path));

    this.ajax({
        url: url,
        success: function( responseText ) {
            eval( 'var tracks = ' +responseText );
            var playUrl = '';
            $.each( tracks, function(i,track) {
                playUrl += 'tr' +track+ '/';
            });
            handler( playUrl );
        }
    });

};

/**
 *  takes a play url to download
 *
 *  @param playUrl
 *
 */
sockso.FolderBrowsing.prototype.downloadFolder = function( playUrl ) {

    self.location.href = Properties.getUrl('/download/' +playUrl);

};

/**
 *  given an array of files and folders, will return just the files that
 *  look like media files
 *
 *  @param results array of File and Folder objects
 *
 *  @return Array
 *
 */
sockso.FolderBrowsing.prototype.getTracks = function( results ) {

    var self = this;
    var files = [];

    $.each( results, function(i,result){
        if ( !result.isFolder && self.isMediaFile(result) ) {
            files.push( result );
        }
    });

    return files;

};

/**
 *  given an array of files and folders, will return just the folders
 *
 *  @param results array of File and Folder objects
 *
 *  @return Array
 *
 */
sockso.FolderBrowsing.prototype.getFolders = function( results ) {

    var folders = [];

    $.each( results, function(i,result){
        if ( result.isFolder ) {
            folders.push( result );
        }
    });

    return folders;

};

/**
 *  handles the return of the query to load a folder
 *
 *  @param folder the element we're loading
 *  @param responseText
 *
 */
sockso.FolderBrowsing.prototype.handleLoadFolder = function( folder, responseText ) {

    eval( 'var results = ' + responseText );

    var self = this;
    var children = $( $('ul',folder)[0] );
    var tracks = this.getTracks( results );
    var folders = this.getFolders( results );

    children.empty();

    // add files and folders if we have them
    if ( (tracks.length + folders.length) > 0 ) {

        // add sub folders, then files
        $.each( folders, function(i,subFolder) {
            children.append( self.getFolderItem(subFolder) );
        });
        $.each( tracks, function(i,track) {
            children.append( self.getTrackItem(track) );
        });

    }
    // otherwise mark as empty
    else children.append( $('<li></li>').addClass('empty').html('...') );

    folder.addClass( 'loaded folderOpen' );

};

/**
 * Initialise folder browsing
 *
 */
sockso.FolderBrowsing.prototype.init = function() {

    var self = this;

    $( '#folders li' ).each(function(i,folder) {

       var elem = $( folder );
       var link = $( '<a></a>' )
                    .attr({
                        href: 'javascript:;'
                    })
                    .click( self.onToggleClicked.bind(self) )
                    .html( elem.html().replace(/\\/g,'/') );
       var children = $('<ul></ul>');

       elem.empty()
            .append( link )
            .append( children );


    });

};

/**
 *  loads a folder with it's contents
 *
 *  @param folder the element to load
 *
 */
sockso.FolderBrowsing.prototype.loadFolder = function( folder ) {

    var self = this;
    var path = this.getPath( folder );
    var collectionId = this.getCollectionId( folder );
    var url = Properties.getUrl('/json/folder' +
        path+ // already URI encoded
        '?collectionId=' +encodeURIComponent(collectionId));

    // show loading gif
    $( 'ul', folder ).append(
        $( '<img />' )
            .attr({ src: Properties.getUrl('/<skin>/images/loading.gif') })
    );

    // set a small timeout so the page can refresh with the loading
    // gif before we make the ajax request (which could take a lil bit)
    setTimeout(
        function() {
            self.ajax({
                url: url,
                success: function( responseText ) {
                    self.handleLoadFolder( folder, responseText )
                }
            });
        },
        100
    );

};

/**
 * Toggles a folders expanded/collapses state, loading it's
 * children if it needs it.
 *
 * @param event jQuery.Event
 */
sockso.FolderBrowsing.prototype.onToggleClicked = function( event ) {

    var elem = $( event.target );
    var folder = elem.parent();

    if ( !folder.hasClass('loaded') ) {
        this.loadFolder( folder );
    }

    else {
        var children = $( 'ul', folder )[ 0 ];
        folder.toggleClass( 'folderOpen' );
        $( children ).toggleClass( 'collapsed' );
    }
    
    return false;

};

/**
 * For intercepting in tests
 * 
 */
sockso.FolderBrowsing.prototype.ajax = $.ajax;

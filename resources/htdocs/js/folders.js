
/**
 *  represents a folder.  the name should have unicode entity encoded, and
 *  the path should be URL encoded.
 *  
 */

function Folder( name, path ) {
    this.name = name;
    this.path = decodeURIComponent(path);
    this.isFolder = true;
};

/**
 *  represents a file.  the name should have unicode entity encoded, and
 *  the path should be URL encoded.
 *  
 */

function File( name, path ) {
    this.name = name;
    this.path = decodeURIComponent(path).replace( /\\/g, '/' );
    this.isFolder = false;
};

$(function() {

    /**
     *  takes a string with encoded named entities (eg. &amp;) and turns
     *  them back into their normal characters
     *
     *  @param str
     *
     */

    function decodeEntities( str ) {

        var entities = new Array(
            '&amp;', '&',
            '&lt;', '<',
            '&gt;', '>',
            '&quot;', '"',
            '&apos;', "'"
        );

        for ( var i=0; i<entities.length; i+=2 )
            str = str.replace( eval('/'+entities[i]+'/g'), entities[i+1] );

        return str;

    };

    /**
     *  traverses up the DOM to work out our current path, the path returned
     *  already has it's components properly URI encoded.
     *
     */

    function getPath( folder ) {

        if ( !folder.hasClass('folder') )
            return getPath( folder.parent() );

        var link = $( 'span a', folder );
        var id = folder.attr( 'id' );
        var name = encodeURIComponent(decodeEntities( link.html() ));

        if ( !id ) id = '';

        return id.match( /^collection-\d+$/ )
            ? ''
            : getPath(folder.parent()) + '/' + name;

    };

    /**
     *  traverses up the DOM to find the collection we're in
     *
     *  @param elem current element
     *
     */

    function getCollectionId( elem ) {

        var id = elem.attr( 'id' );
        var matches = id != null
            ? id.match( /^collection-(\d+)$/ )
            : null;

        return ( matches )
            ? matches[ 1 ]
            : getCollectionId( elem.parent() );

    };

    /**
     *  plays a file for a path
     *
     *  @param path path of file to play
     *
     */

    function playFile( path ) {
        resolvePath(
            path,
            function( responseText ) {
                eval( 'var track = ' +responseText );
                player.play( 'tr' +track['id'] );
            },
            Locale.getString('www.error.trackNotFound')
        );
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
    
    function resolvePath( path, handler, errorMessage ) {
        $.ajax({
            type: 'POST',
            url: '/json/resolvePath',
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

    function addFileToPlaylist( path ) {
        resolvePath(
            path,
            function( responseText ) {
                eval( 'var track = ' +responseText );
                var item = new MusicItem( 'tr' + track.id, track.name );
                playlist.add( item );
            },
            Locale.getString('www.error.trackNotFound')
        );
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

    function isMediaFile( file ) {

        var exts = new Array( 'mp3', 'ogg', 'wma' );
        var ext = file.path.toLowerCase().substring( file.path.length - 3 );

        for ( var i=0; i<exts.length; i++ )
            if ( exts[i] == ext )
                return true;

        return false;

    };

    /**
     *  creates a folder node for the tree
     *
     *  @param folder folder object
     *
     */

    function getFolderItem( folder ) {
    
        var link = $( '<a></a>' )
                .attr({
                    href: 'javascript:;'
                })
                .click( toggleFolder )
                .html( folder.name );

        var play = getTrackAction( 'play', function() {
            getTracksForFolder( folder, playFolder );
        }, 'Play folder' );

        var download = null;
        if ( Properties.get('www.disableDownloads') != 'yes' )
            download = getTrackAction( 'download', function() {
                getTracksForFolder( folder, downloadFolder );
            }, 'Download folder' );

        var actions = $( '<span></span>' )
            .addClass( 'actions' )
            .append( play )
            .append( download );

        var links = $( '<span></span>' )
            .addClass( 'links' )
            .append( link )
            .append( actions );

        return $( '<li></li>' )
            .addClass( 'folder' )
            .append( links )
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

    function getTrackAction( icon, action, title ) {

        var skin = Properties.get( "www.skin", "original" );

        return $( '<a></a>' )
            .attr({
                href: 'javascript:;',
                title: title
            })
            .click( action )
            .append( $('<img />').attr('src','/file/skins/' +skin+ '/images/' + icon +'.png') )
            .append( '<span>&nbsp;</span>' );

    };

    /**
     *  tries to resolve a path to a tarck and then download it
     *
     *  @param path
     *
     */

    function downloadFile( path ) {
        resolvePath( path, function( responseText ) {
            eval( 'var track = ' +responseText );
            self.location.href = '/download/tr' +track['id'];
        });
    }

    /**
     *  creates a track node for a file (assumed to be a media file)
     *
     *  @param file
     *
     *  @return LI
     *
     */

    function getTrackItem( file ) {
        
        var play = getTrackAction( 'play', function() {
            playFile( file.path );
        }, file.name );

        var playlist = getTrackAction( 'add', function() {
            addFileToPlaylist( file.path );
        }, file.name );

        var download = null;
        if ( Properties.get('www.disableDownloads') != 'yes' )
            download = getTrackAction( 'download', function() {
                downloadFile( file.path );
            }, 'Download: ' +file.name );

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

    function getTracksForFolder( folder, handler ) {

        var url = '/json/tracksForPath?path=' +encodeURIComponent(folder.path);
        
        $.ajax({
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
     *  takes a play url to play
     *
     *  @param playUrl
     *
     */

    function playFolder( playUrl ) {
        player.play( playUrl );
    };

    /**
     *  takes a play url to download
     *
     *  @param playUrl
     *
     */

    function downloadFolder( playUrl ) {
        self.location.href = '/download/' +playUrl;
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

    function getTracks( results ) {
        var files = new Array();
        $.each( results, function(i,result){
            if ( !result.isFolder && isMediaFile(result) )
                files.push( result );
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

    function getFolders( results ) {
        var folders = new Array();
        $.each( results, function(i,result){
            if ( result.isFolder )
                folders.push( result );
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

    function handleLoadFolder( folder, responseText ) {
        
        eval( 'var results = ' + responseText );

        var children = $( $('ul',folder)[0] );
        var tracks = getTracks( results );
        var folders = getFolders( results );

        children.empty();

        // add files and folders if we have them
        if ( (tracks.length + folders.length) > 0 ) {

            // add sub folders, then files
            $.each( folders, function(i,subFolder) {
                children.append( getFolderItem(subFolder) );
            });
            $.each( tracks, function(i,track) {
                children.append( getTrackItem(track) );
            });

        }
        // otherwise mark as empty
        else children.append( $('<li></li>').addClass('empty').html('...') );


        folder.addClass( 'loaded folderOpen' );

    };

    /**
     *  loads a folder with it's contents
     *
     *  @param folder the element to load
     *
     */

    function loadFolder( folder ) {

        var skin = Properties.get( "www.skin", "original" );
        var path = getPath( folder );
        var collectionId = getCollectionId( folder );
        var url = '/json/folder' +
            path+ // already URI encoded
            '?collectionId=' +encodeURIComponent(collectionId);

        // show loading gif
        $( 'ul', folder ).append(
            $( '<img />' )
                .attr({ src: '/file/skins/' +skin+ '/images/loading.gif' })
        );

        // set a small timeout so the page can refresh with the loading
        // gif before we make the ajax request (which could take a lil bit)
        setTimeout( function() {
            $.ajax({
                url: url,
                success: function( responseText ) {
                    handleLoadFolder( folder, responseText )
                }
            });
        }, 100 );

    };

    /**
     *  toggles a folders expanded/collapses state, loading it's
     *  children if it needs it.
     *
     */

    function toggleFolder() {

        var elem = $( this );
        var folder = elem.parent();
        
        // if we're on a folder node we need to go
        // up another level in the DOM
        if ( folder.hasClass('links') )
            folder = folder.parent();
        
        if ( !folder.hasClass('loaded') )
            loadFolder( folder );

        else {
            var children = $( 'ul', folder )[ 0 ];
            folder.toggleClass( 'folderOpen' );
            $( children ).toggleClass( 'collapsed' );
        }

    };

    /**
     *  inits the root collection folders
     *
     */

    function initFolders() {
       $( '#folders li' ).each(function(i,folder) {

           var elem = $( folder );
           var link = $( '<a></a>' )
                        .attr({
                            href: 'javascript:;'
                        })
                        .click( toggleFolder )
                        .html( elem.html().replace(/\\/g,'/') );
           var children = $('<ul></ul>');
           var links = $( '<span></span>' )
                        .addClass( 'links' )
                        .append( link );

           elem.empty()
                .append( links )
                .append( children );

       }); 
    };

    initFolders();

});

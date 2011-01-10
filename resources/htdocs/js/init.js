
if ( !window.Properties ) {
    Properties = new sockso.Properties();
}

$(function() {

    /**
     *  does init for the "bold" skin
     *  
     */

    function initBoldSkin() {
        
        $( '#header' ).corner( 'top' );
        $( '#footer' ).corner( 'bottom' );
        $( '#sidebar' ).corner();
        $( '#login-info' ).corner( 'bottom' );
        $( '#sidebar h2' ).corner( 'top' );
        
    }

    var p = window.Properties;

    // add confirmation to logout link
    
    $( 'a#logoutLink' ).click(function() {
        return confirm( Locale.getString('www.text.confirmLogout') );
    });

    // add confirmation to scrobble log link
    
    $( 'a#scrobbleLogLink' ).click(function() {
        return confirm( Locale.getString('www.text.confirmScrobbleLog') );
    });

    // check skin to see if we need to do any init
    
    var skin = Properties.get( 'www.skin', 'original' );
    if ( skin == 'bold' ) {
        initBoldSkin();
    }

    var session = new sockso.Session();

    // create the search box

    var search = new sockso.SearchBox();
    search.init( '#nav' );

    // create the player selection control
    // needs to be accessed globally

    var player = new sockso.Player({
        session: session
    });
    player.init( '#nav' );

    // create playlist control

    var playlist = new sockso.Playlist({
        parentId: 'playlist',
        player: player,
        session: session,
        user: user
    });
    playlist.init();
    playlist.load();
    playlist.refresh();

    // @TODO - find a way to put stuff like this on individual pages
    // check if we need to init the uploads form

    var form = new sockso.UploadForm( 'uploadForm' );
    form.init();

    // Related/similair artists
    
    var related = new sockso.RelatedArtists({
        properties: p
    });
    related.init();

    // Sharing links

    $( '.share-music' ).each(function() {

        var popup = new sockso.Sharer( $(this) );
        popup.init();
        popup.addStandardLinks();

    });

    // Imageflow

    if ( Properties.get("www.imageflow.disable") != 'yes' && Properties.get("www.covers.disable") != 'yes' ) {
        var imageflow = new sockso.ImageFlow();
        imageflow.init();
    }

    // Folder browsing

    var folders = new sockso.FolderBrowsing( player, playlist );
    folders.init();

    // Admin console
    
    var console = new sockso.AdminConsole();
    console.init();

    // global objects
    
    window.player = player;
    window.playlist = playlist;

});

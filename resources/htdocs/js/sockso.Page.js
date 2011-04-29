
/**
 * Encapsulates different kind of page functionality
 *
 */
sockso.Page = function() {
};

/**
 * Initialise and wire up all the objects
 *
 */
sockso.Page.prototype.init = function() {

    this.session = new sockso.Session();

    // ajax loading pages

    this.ajaxer = new sockso.Ajaxer({
        page: this
    });

    this.search = new sockso.SearchBox({
        ajaxer: this.ajaxer
    });

    this.player = new sockso.Player({
        session: this.session
    });

    this.playlist = new sockso.Playlist({
        parentId: 'playlist',
        player: this.player,
        session: this.session,
        ajaxer: this.ajaxer,
        user: user
    });

    this.console = new sockso.AdminConsole();

    this.uploadForm = new sockso.UploadForm( 'uploadForm' );

    this.related = new sockso.RelatedArtists({
        properties: Properties,
        ajaxer: this.ajaxer
    });

    this.imageflow = new sockso.ImageFlow({
        ajaxer: this.ajaxer
    });

    this.folders = new sockso.FolderBrowsing( this.player, this.playlist );

};

/**
 * Initializes global sockso stuff in the page layout
 *
 */
sockso.Page.prototype.initLayout = function() {

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
    if ( skin === 'bold' ) {
        initBoldSkin();
    }

    this.ajaxer.init();

    // create the search box

    this.search.init( '#nav' );

    // create the player selection control
    // needs to be accessed globally

    this.player.init( '#nav' );

    // create playlist control

    this.playlist.init();
    this.playlist.load();
    this.playlist.refresh();

    // Admin console

    this.console.init();

    // global objects

    window.player = this.player;
    window.playlist = this.playlist;

};

/**
 * Initializes things in the #content area
 * 
 */
sockso.Page.prototype.initContent = function() {

    this.uploadForm.init();

    // Related/similair artists

    this.related.init();

    // Sharing links

    $( '.share-music' ).each(function() {
        var popup = new sockso.Sharer( $(this) );
        popup.init();
        popup.addStandardLinks();

    });

    // Imageflow

    if ( Properties.get("www.imageflow.disable") !== 'yes' && Properties.get("www.covers.disable") !== 'yes' ) {
        this.imageflow.init();
    }

    // Folder browsing

    this.folders.init();

};

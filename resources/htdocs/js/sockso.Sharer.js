
/**
 * Adds a popup menu to links for sharing music
 *
 * @param source jQuery
 */
sockso.Sharer = function( source ) {

    this.source = source;
    this.data = null;
    this.elem = null;
    this.timeout = null;

};

/**
 * Initialise the link to share some music
 *
 */
sockso.Sharer.prototype.init = function() {

    // check not needed as source needs to be provided?
    if ( this.source ) {
        this.source.click( this.show.bind(this) );
        this.setDataFromClasses( this.source.attr('class') );
    }

    this.elem = $( '<div></div>' )
        .addClass( 'popup' )
        .appendTo( 'body' )
        .hide();

};

/**
 * Tries to set the data property from a class on the source element
 *
 * @param classes String
 */
sockso.Sharer.prototype.setDataFromClasses = function( classes ) {

    var parts = classes.split( /\s+/ );
    
    for ( var i=0; i<parts.length; i++ ) {
        var part = parts[i];
        var matches = part.match( /share-music-(\w+\d+)/ );
        if ( matches ) {
            this.setData( matches[1] );
        }
    }

};

/**
 * Sets the share data (eg. tr123)
 *
 * @param data String
 */
sockso.Sharer.prototype.setData = function( data ) {
    
    this.data = data;
    
};

/**
 * Returns the current share data (eg. tr123)
 *
 * @return String
 */
sockso.Sharer.prototype.getData = function() {
    
    return this.data;
    
};

/**
 * Adds a link to the share popup
 *
 * @param text String
 * @param icon String
 * @param onClick Function
 */
sockso.Sharer.prototype.add = function( text, icon, onClick ) {

    var self = this;

    $( '<a></a>' )
        .attr( 'href', 'javascript:;' )
        .click(function() { self.hide(); onClick(); return false; })
        .mouseover( this.cancelHide.bind(this) )
        .mouseout( this.setHideDelay.bind(this) )
        .append( $('<img />').attr('src',Properties.getUrl('<skin>/images/' + icon)) )
        .append( text )
        .appendTo( this.elem );

};

/**
 * Adds standard links to this share popup
 *
 */
sockso.Sharer.prototype.addStandardLinks = function() {

    this.add( 'Show HTML code for player', 'embed.png', this.onShowHtml.bind(this) );

//    this.add( 'Add to Streamfinder', 'streamfinder.png', function() {
//        var url = '';
//        alert( 'URL: ' + url );
//    });

};

/**
 * Handler for showing HTML code to share music
 *
 */
sockso.Sharer.prototype.onShowHtml = function() {

    var w = window.open(
        Properties.getUrl('/share/' + this.getData()),
        'ShareWindow',
        'width=500,height=250,toolbars=no'
    );

    w.focus();

};

/**
 * Show the share popup from the source element
 *
 */
sockso.Sharer.prototype.show = function() {

    var pos = this.source.position();
    this.elem.css({
        top: (pos.top + 10) + 'px',
        left: pos.left + 'px'
    })
    .show();

    this.setHideDelay();

};

/**
 * Hide the share popup
 *
 */
sockso.Sharer.prototype.hide = function() {

    this.elem.fadeOut( 'slow' );

};

/**
 * Cancel any timeout that has been set to hide the element
 *
 */
sockso.Sharer.prototype.cancelHide = function() {

    if ( this.timeout != null ) {
        clearTimeout( this.timeout );
    }

};

/**
 * Set up a small timeout to hide the share popup
 *
 */
sockso.Sharer.prototype.setHideDelay = function( delay ) {

    this.cancelHide();

    this.timeout = setTimeout(
        this.hide.bind(this),
        1000
    );

};

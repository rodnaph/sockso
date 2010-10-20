
var sharePopup = null;

/**
 *  a popup object with methods to show/hide/etc...
 *
 */

function Popup() {

    var self = this;
    var elem = null;
    var timeout = null;
    var data = null;

    this.getData = function() {
        return self.data;
    };

    this.show = function( x, y, data ) {

        elem.css({
            top: y + 'px',
            left: x + 'px'
        })
        .show();

        self.data = data;
        self.setHideDelay();

    };

    this.add = function( text, icon, action ) {

        var skin = Properties.get( "www.skin", "original" );

        $( '<a></a>' )
            .attr( 'href', 'javascript:;' )
            .click( function() { self.hide(); action(); } )
            .mouseover( self.cancelHide )
            .mouseout( self.setHideDelay )
            .append( $('<img />').attr('src','/file/skins/' +skin+ '/images/' + icon) )
            .append( text )
            .appendTo( elem );
        
    };

    this.cancelHide = function() {
        if ( timeout != null )
            clearTimeout( timeout );
    };

    this.setHideDelay = function( delay ) {
        self.cancelHide();
        timeout = setTimeout( self.hide, 1000 );
    };

    this.hide = function() {
        elem.fadeOut( 'slow' );
    };

    // create popup
    elem = $( '<div></div>' )
        .addClass('popup')
        .appendTo('body')
        .hide();

}

/**
 *  show the popup to share the specifed url of music
 *
 *  @param eLink the anchor element clicked
 *  @param url the music url (eg. ar123/tr456)
 *
 */

function shareMusic( eLink, url ) {

    var pos = getPosition( eLink );

    sharePopup.show( pos.x, pos.y + 10, url );

}

//
//  set up the share popup menu.
//
$(document).ready( function() {

    sharePopup = new Popup();

    sharePopup.add( 'Show HTML code for player', 'embed.png', function() {
        var w = window.open(
            '/share/' + sharePopup.getData(),
            'ShareWindow',
            'width=500,height=250,toolbars=no'
        );
        w.focus();
    });
/*
    sharePopup.add( 'Add to Streamfinder', 'streamfinder.png', function() {
        
        var url = '';
        
        alert( 'URL: ' + url );
        
    });
*/
});

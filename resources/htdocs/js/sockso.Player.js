
/**
 *  Creates a control for selecting the type of player to use to play music,
 *  and a play() method for getting the selected player to activate and
 *  play the music.
 *
 *  var player = new sockso.Player();
 *  player.init( '#nav' );
 *  player.play( 'tr123' );
 *
 */

sockso.Player = function( options ) {

    options = options || {};

    var self = this;
    var session = options.session;
    var playType = null;
    var playOptions = null;
    var PLAY_COOKIE = 'play-type';

    this.PLAY_FLASH_POPUP = 'flash';
    this.PLAY_FLASH_EMBED = 'flash-embed';
    this.PLAY_FLEX = 'flash-flex';
    this.PLAY_M3U = 'm3u';
    this.PLAY_XSPF = 'xspf';
    this.PLAY_PLS = 'pls';
    this.PLAY_HTML5PLAYER = 'html5';

    /**
     *  creates a play option element
     *
     */

    function createPlayOption( playType, text ) {

        return $( '<option></option>' )
            .append( text )
            .attr({ value: playType });

    }

    /**
     *  sets the play type the user wants to use
     *
     */

    this.setPlayType = function( newPlayType ) {
        
        self.playType = ( newPlayType === null || newPlayType === undefined || newPlayType === 'null' )
            ? self.PLAY_FLASH_POPUP
            : newPlayType;

        $( 'option', playOptions )
            .attr({ selected: false })
            .filter( "option[value='" +self.playType+ "']" )
            .attr({ selected: true });

        session.set( PLAY_COOKIE, self.playType );

    };

    /**
     *  Use the embedded XSPF player
     *
     *  @param playUrl
     *  @param trackFilter
     *
     */
    
    this.playFlashEmbed = function( playUrl, trackFilter ) {

        var xspfUrl = Properties.getUrl('/file/flash/xspf_player.swf' +
            '?playlist_url=' + escape(Properties.getUrl('/xspf/' +playUrl + trackFilter))+
            '&autoplay=1');
        var toggle = $( '<a></a>' )
                            .html( '-' )
                            .click(function() {
                                var newText = '-';
                                var newHeight = 150;
                                if ( toggle.html() == '-' ) {
                                    newText = '+';
                                    newHeight = 15;
                                }
                                toggle.html( newText )
                                $( '#flash-player object' )
                                    .attr( 'height', newHeight );
                            });

        $( '#flash-player' )
            .empty()
            .append( toggle )
            .append($(
                '<object type="application/x-shockwave-flash" width="400" height="150" data="' + xspfUrl +'">' +
                    '<param name="movie" value="' +xspfUrl+ '" />' +
                '</object>'
            ))
            .fadeIn();

    };

    /**
     *  Plays using the HTML 5 Player
     *
     *  @param playUrl
     *
     */
    this.playHtml5Player = function( playUrl ) {

        var w = window.open( '', 'PlayerWin', 'width=590,height=310,toolbars=no' );
        
        // load window first time
        if ( !options.jspAllowReload || w.location.href == 'about:blank' ) {
            w.location.href = Properties.getUrl('/player/html5/' +playUrl);
        }
        // reload contents dynamically
        else {
            w.html5player_reload( playUrl );
        }
        
        w.focus();

    };

    /**
     *  Plays using the popup flash/flex player
     *
     *  @param playUrl
     *
     */
    
    this.playFlashPopup = function( playUrl ) {

        // default with and height for xspf player
        var width = 410;
        var height = 180;
        // adjust dimensions for different players...
        if ( self.playType == self.PLAY_FLEX ) {
            width = 610;
            height = 310;
            playUrl += '&player=flexPlayer';
        }
        // now we can open the window...
        var w = window.open( Properties.getUrl('/player/xspf/' + playUrl), 'PlayerWin', 'width=' +width+ ',height=' +height+ ',toolbars=no' );
        w.focus();

    };

    /**
     *  plays a track/artist/etc with the correct play type the user is using
     *
     */

    this.play = function( playUrl, extraArgs, options ) {

        if ( !options ) options = {};

        playUrl += "?" + ( extraArgs != undefined && extraArgs != null ? extraArgs : '' );

        var trackFilter = ( Properties.get('www.flashPlayer.dontFilterMp3s') == 'yes' )
                              ? '' : '&trackType=mp3';

        switch ( self.playType ) {

            case self.PLAY_PLS:
            case self.PLAY_M3U:
            case self.PLAY_XSPF:
                location.href = Properties.getUrl(self.playType+ '/' + playUrl);
                break;

            case self.PLAY_FLASH_EMBED:
                this.playFlashEmbed( playUrl, trackFilter );
                break;
                
            case self.PLAY_HTML5PLAYER:
            	this.playHtml5Player( playUrl );
            	break;

            case self.PLAY_FLEX:
            case self.PLAY_FLASH_POPUP:
            default:
                this.playFlashPopup( playUrl );
                break;

        }

    };

    /**
     *  initializes the player selection box
     *
     */

    this.init = function( parentId ) {

        playOptions = $( '<select></select>' )
                        .addClass( 'play-options' )
                        .append( createPlayOption(self.PLAY_FLASH_EMBED,'Embedded Flash Player') )
                        .append( createPlayOption(self.PLAY_FLASH_POPUP,'Popup Flash Player') )
                        .append( createPlayOption(self.PLAY_FLEX,'Flex Player') )
                        .append( createPlayOption(self.PLAY_M3U,'M3U (iTunes,WMP,etc...)') )
                        .append( createPlayOption(self.PLAY_PLS,'Pls (Winamp,Shoutcast,etc...)') )
                        .append( createPlayOption(self.PLAY_XSPF,'XSPF') )
                        .append( createPlayOption(self.PLAY_HTML5PLAYER,'HTML 5 Player') );

        $( parentId ).append(
            $( '<div></div>' )
                .attr( 'id', 'play-options' )
                .append( 'Play using: ' )
                .change(function() {
                    self.setPlayType( playOptions.val() );
                })
                .append( playOptions )
            );

        $( '<div></div>')
            .attr({ id: 'flash-player' })
            .appendTo( $('body') )
            .hide();

        session.get( PLAY_COOKIE, function(playType) {
            self.setPlayType( playType );
        });

    };

};


PlayerTest = TestCase( 'sockso.Player' );

PlayerTest.prototype.setUp = function() {
    this.session = new sockso.Session();
    this.player = new sockso.Player({
        session: this.session
    });
}

PlayerTest.prototype.testInit = function() {

    this.player.init( 'body' );

    assertTrue( $('#play-options select').length == 1 );
    assertTrue( $('#play-options select option').length == 8 );

    assertTrue( $('#flash-player').length == 1 );

};

PlayerTest.prototype.testSetPlayType = function() {
    this.player.init( 'body' );
    assertEquals( undefined, $('select:selected').val() );
    this.player.setPlayType( this.player.PLAY_M3U );
    assertEquals( 1, $('option:selected').length );
    assertEquals( this.player.PLAY_M3U, $('option:selected').val() );

};

PlayerTest.prototype.testPlayFlashPopup = function() {
    var windowOpened = false;
    var trackId = 'tr123';
    this.player.init( 'body' );
    this.player.setPlayType( this.player.PLAY_FLASH_POPUP );
    window.open = function( url, name, options ) {
        windowOpened = true;
        assertTrue( url.indexOf('xspf') > 0 );
        assertTrue( url.indexOf(trackId) > 0 );
        assertTrue( url.indexOf('flexPlayer') == -1 );
        return { focus: function() {} };
    };
    assertFalse( windowOpened );
    this.player.play( trackId );
    assertTrue( windowOpened );
};

PlayerTest.prototype.testPlayFlexPlayer = function() {

    var windowOpened = false;
    var trackId = 'tr123';
    this.player.init( 'body' );
    this.player.setPlayType( this.player.PLAY_FLEX );

    window.open = function( url, name, options ) {
        windowOpened = true;
        assertTrue( url.indexOf('xspf') > 0 );
        assertTrue( url.indexOf(trackId) > 0 );
        assertTrue( url.indexOf('flexPlayer') > 0 );
        return { focus: function() {} };
    };

    assertFalse( windowOpened );
    this.player.play( trackId );
    assertTrue( windowOpened );

};

PlayerTest.prototype.testPlayFlashEmbed = function() {

    var trackId = 'tr123';
    this.player.init( 'body' );
    this.player.setPlayType( this.player.PLAY_FLASH_EMBED );

    assertEquals( 0, $('#flash-player object').length );

    this.player.play( trackId );

    assertEquals( 1, $('#flash-player object').length );

};

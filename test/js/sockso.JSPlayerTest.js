
JSPlayer = TestCase( 'sockso.JSPlayer' );

JSPlayer.prototype.getJSPlayer = function() {

    var playerId = 'socksoJsPlayer';

    $( '<div></div>' )
        .attr({ id: playerId })
        .appendTo( 'body' );

    var player = new sockso.JSPlayer();

    player.init( playerId );

    return player;

};

JSPlayer.prototype.testInit = function() {

    var player = this.getJSPlayer();

    assertNotNull( player );
    assertEquals( 1, $('object, embed').length ); // IE uses object, others embed
    assertEquals( 1, $('.artwork').length );
    assertEquals( 1, $('.playlist').length );
    assertEquals( 1, $('.info').length );

};

JSPlayer.prototype.getJWPlayer = function() {

    return {
        sendEvent: function() {}
    };

};

JSPlayer.prototype.getTrack = function() {

    return {
        id: '123',
        name: 'some name',
        artist: {
            id: '456',
            name: 'some name'
        },
        album: {
            id: '789',
            name: 'another name'
        }
    };

}

JSPlayer.prototype.testPlayPrevNext = function() {

    var player = this.getJSPlayer();
    var track = this.getTrack();

    player.setPlayerObject( this.getJWPlayer() );
    player.addTrack( track );
    player.addTrack( track );
    player.addTrack( track );

    assertEquals( null, player.getCurrentItem() );

    player.playItem( 0 );
    assertEquals( 0, player.getCurrentItem() );

    player.playNextItem();
    assertEquals( 1, player.getCurrentItem() );

    player.playNextItem();
    player.playNextItem();
    player.playNextItem();
    assertEquals( 2, player.getCurrentItem() );

    player.playPrevItem();
    assertEquals( 1, player.getCurrentItem() );

    player.playPrevItem();
    player.playPrevItem();
    player.playPrevItem();
    assertEquals( 0, player.getCurrentItem() );

    player.playItem( 2 );
    player.playNextItem();
    assertEquals( 2, player.getCurrentItem() );

    player.playItem( 0 );
    player.playPrevItem();
    assertEquals( 0, player.getCurrentItem() );

};

JSPlayer.prototype.testStopPlaying = function() {

    var player = this.getJSPlayer();
    var track = this.getTrack();

    player.setPlayerObject( this.getJWPlayer() );
    player.addTrack( track );
    player.playItem( 0 );

    assertEquals( 0, player.getCurrentItem() );
    player.stopPlaying();
    assertEquals( -1, player.getCurrentItem() );

};

JSPlayer.prototype.testPlayItem = function() {

    var player = this.getJSPlayer();
    var track = this.getTrack();

    player.setPlayerObject( this.getJWPlayer() );
    player.addTrack( track );
    player.playItem( 0 );

    assertEquals(
        track.artist.name+ ' - ' +track.name,
        $( '.info' ).html()
    );

};

JSPlayer.prototype.testPlayerStateChanged = function() {

    var player = this.getJSPlayer();
    var track = this.getTrack();
    var playedItem = false;

    player.setPlayerObject( this.getJWPlayer() );
    player.addTrack( track );
    player.addTrack( track );
    player.playItem( 0 );

    assertEquals( 0, player.getCurrentItem() );

    player.playItem = function() {
        playedItem = true;
    };
    player.playerStateChanged({ newstate: 'STOPPED' });

    assertFalse( playedItem );

    player.playerStateChanged({ newstate: 'COMPLETED' });

    assertTrue( playedItem );

};

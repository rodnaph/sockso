
JSPlayer = TestCase( 'sockso.Html5Player' );

JSPlayer.prototype.getHtml5Player = function() {

    var playerId = 'socksoHtml5Player';

    $( '<div></div>' )
        .attr({ id: playerId })
        .appendTo( 'body' );

    var player = new sockso.Html5Player();

    player.init( playerId );

    return player;

};

JSPlayer.prototype.testInit = function() {

    var player = this.getHtml5Player();

    assertNotNull( player );
    assertEquals( 1, $('.artwork').length );
    assertEquals( 1, $('.playlist').length );
    assertEquals( 0, $('.playlist li').length);
    assertEquals( 1, $('.info').length );
    assertEquals( 1, $('.controls').length );
    assertEquals( 1, $('.audio').length );
    assertEquals( 1, $('audio').length );     

};

JSPlayer.prototype.getTrack = function(id) {
	
	var id = id || '123';

    return {
        id: id,
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

    var player = this.getHtml5Player();
    var track = this.getTrack();

    player.addTrack( this.getTrack('123') );
    player.addTrack( this.getTrack('456') );
    player.addTrack( this.getTrack('789') );
    
    player.start();
    assertEquals( 3, $('.playlist li').length);
    assertEquals( 1, $('.playlist li.current').length);
    
    assertEquals( null, player.getCurrentItem() );

    player.playItem( 0 );
    assertEquals( 0, player.getCurrentItem() );
    assertEquals( 1, $('.current').length);
    assertEquals( 1, $('#item123.current').length);

    player.playNextItem();
    assertEquals( 1, player.getCurrentItem() );
    assertEquals( 1, $('.current').length);
    assertEquals( 1, $('#item456.current').length);

    player.playNextItem();
    player.playNextItem();
    player.playNextItem();
    assertEquals( 2, player.getCurrentItem() );
    assertEquals( 1, $('.current').length);
    assertEquals( 1, $('#item789.current').length);

    player.playPrevItem();
    assertEquals( 1, player.getCurrentItem() );
    assertEquals( 1, $('.current').length);
    assertEquals( 1, $('#item456.current').length);

    player.playPrevItem();
    player.playPrevItem();
    player.playPrevItem();
    assertEquals( 0, player.getCurrentItem() );
    assertEquals( 1, $('.current').length);
    assertEquals( 1, $('#item123.current').length);


    player.playItem( 2 );
    player.playNextItem();
    assertEquals( 2, player.getCurrentItem() );
    assertEquals( 1, $('.current').length);
    assertEquals( 1, $('#item789.current').length);


    player.playItem( 0 );
    player.playPrevItem();
    assertEquals( 0, player.getCurrentItem() );
    assertEquals( 1, $('.current').length);
    assertEquals( 1, $('#item123.current').length);


};

JSPlayer.prototype.testStopPlaying = function() {

    var player = this.getHtml5Player();
    var track = this.getTrack();

    player.addTrack( track );
    player.playItem( 0 );

    assertEquals( 0, player.getCurrentItem() );    
    player.stopPlaying();
    assertEquals( -1, player.getCurrentItem() );

};

JSPlayer.prototype.testPlayItem = function() {

    var player = this.getHtml5Player();
    var track = this.getTrack();

    player.addTrack( track );
    player.playItem( 0 );

    assertEquals(
        track.artist.name+ ' - ' +track.name,
        $( '.info' ).html()
    );

};

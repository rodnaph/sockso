
Playlist = TestCase( 'sockso.Playlist' );

Playlist.prototype.setUp = function() {

    // @TODO this shouldn't be needed...
    Locale = new sockso.Locale();

    $( '#playlist' ).remove();
    $( '<div></div>' )
        .attr({ id: 'playlist' })
        .appendTo( 'body' );

};

Playlist.prototype.testConstructor = function() {

    expectAsserts( 1 );
    assertNotNull( new sockso.Playlist( {} ) );

};

Playlist.prototype.testInit = function() {

    expectAsserts( 2 );

    Properties.set( 'www.disableDownloads', '' );

    var playlist = new sockso.Playlist({ parentId: 'playlist', session: new sockso.mocks.Session() });
    playlist.init();

    assertEquals( 1, $('#playlist h2').length );
    assertEquals( 4, $('#playlist a').length );

};

Playlist.prototype.testInitNoDownloads = function() {

    expectAsserts( 1 );

    var playlist = new sockso.Playlist({ parentId: 'playlist', session: new sockso.mocks.Session() });

    Properties.set( 'www.disableDownloads', 'yes' );

    playlist.init();

    assertEquals( 3, $('#playlist a').length );

};

Playlist.prototype.testAddRemoveAndClearItems = function() {

    expectAsserts( 3 );

    var p = new sockso.Playlist({ parentId: 'playlist', session: new sockso.mocks.Session() });
    p.init();

    assertEquals( 0, p.getItems().length );
    p.add( new MusicItem('ar123','name') );
    assertEquals( 1, p.getItems().length );
    p.add( new MusicItem('tr123','Name') );
    p.add( new MusicItem('tr123','Name') );
    p.add( new MusicItem('tr123','Name') );
    p.clear();
    assertEquals( 0, p.getItems().length );

};

Playlist.prototype.testGetPlaylistAsUrl = function() {

    expectAsserts( 1 );

    var p = new sockso.Playlist({ parentId: 'playlist', session: new sockso.mocks.Session() });
    p.add( new sockso.MusicItem('tr123','track') );
    p.add( new sockso.MusicItem('al456','album') );
    p.add( new sockso.MusicItem('ar789','artist') );

    assertEquals( 'tr123/al456/ar789', p.getAsUrl() );

};

Playlist.prototype.testLoad = function() {

    var sess = new sockso.mocks.Session();
    var p = new sockso.Playlist({ parentId: 'playlist', session: sess });

    sess.set( 'playlist', '1:_::_:First:_::_:1_:__:_2:_::_:Second:_::_:2' );
    
    p.load(function() {
        assertEquals( 2, p.getItems().length );
    });

};

Playlist.prototype.testSave = function() {

    expectAsserts( 1 );

    var sess = new sockso.mocks.Session();
    var p = new sockso.Playlist({ parentId: 'playlist', session: sess });

    p.add( new sockso.MusicItem( 'ar123', 'First' ) );
    p.add( new sockso.MusicItem( 'al456', 'Second' ) );
    p.save();

    sess.get( 'playlist', function(data) {
        assertEquals(
            'ar123%3A_%3A%3A_%3AFirst%3A_%3A%3A_%3A0_:__:_al456%3A_%3A%3A_%3ASecond%3A_%3A%3A_%3A1',
            data
        );
    });

};

Playlist.prototype.testRefresh = function() {

    expectAsserts( 3 );

    var p = new sockso.Playlist({ parentId: 'playlist', session: new sockso.mocks.Session() });

    p.init();

    assertEquals( 1, $('#playlist .contents li').length );

    p.refresh();

    assertEquals( 1, $('#playlist .contents li').length );

    p.setItems([
        new sockso.MusicItem('tr123','Name'),
        new sockso.MusicItem('ar123','Name'),
        new sockso.MusicItem('al123','Name')
    ]);

    p.refresh();

    assertEquals( 3, $('#playlist .contents li').length );

};

Playlist.prototype.testGetMusicElement = function() {

    expectAsserts( 1 );

    var p = new sockso.Playlist({ parentId: 'playlist', session: new sockso.mocks.Session() });
    var item = p.getMusicElement( new sockso.MusicItem('tr123','Name',1) );

    assertEquals( 2, $('a',item).length );

};

Playlist.prototype.testRemoveItem = function() {

    expectAsserts( 9 );

    var p = new sockso.Playlist({ parentId: 'playlist', session: new sockso.mocks.Session() });
    var item1 = new MusicItem( 'tr123', 'Name' );
    var item2 = new MusicItem( 'tr123', 'Name' );
    var item3 = new MusicItem( 'tr123', 'Name' );

    assertEquals( undefined, item1.playlistId );
    assertEquals( 0, p.getItems().length );

    p.add( item1 );
    p.add( item2 );
    p.add( item3 );

    assertNotNull( item1.playlistId );
    assertNotNull( item2.playlistId );
    assertNotNull( item3.playlistId );
    assertEquals( 3, p.getItems().length );

    p.remove( item2.playlistId );

    assertEquals( 2, p.getItems().length );

    var item4 = new MusicItem( 'tr123', 'Name' );

    p.add( item4 );

    assertEquals( 3, p.getItems().length );

    p.remove( item4.playlistId );

    assertEquals( 2, p.getItems().length );

};

Playlist.prototype.testNoSaveLinkWhenUserNotLoggedIn = function() {

    var p = new sockso.Playlist({
        parentId: 'playlist',
        session: new sockso.mocks.Session()
    });

    p.init();

    assertEquals( 0, $('.user-controls').length );

};

Playlist.prototype.testAddSaveLinkWhenUserLoggedIn = function() {

    var p = new sockso.Playlist({
        parentId: 'playlist',
        session: new sockso.mocks.Session(),
        user: new sockso.User({ id: 123, name: 'asd' })
    });

    p.init();

    assertEquals( 1, $('.user-controls').length );

};

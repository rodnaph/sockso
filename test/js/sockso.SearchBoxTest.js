
SearchBoxTest = TestCase( 'sockso.SearchBox' );

SearchBoxTest.prototype.testInitOk = function() {

    var search = new sockso.SearchBox();
    var parentId = 'testInitOk';
    var parent = $( '<div></div>' )
        .attr({ id: parentId })
        .appendTo( 'body' );

    search.init( '#'+parentId );

    assertEquals( 1, $('#search-results').length );
    assertEquals( 1, $('input',parent).length );

};

SearchBoxTest.prototype.testMakeQuery = function() {

    var parentId = 'testMakeQuery';
    var parent = $( '<div></div>' )
        .attr({ id: parentId })
        .appendTo( 'body' );
    var search = new sockso.SearchBox();
    var results = [
        new MusicItem( 'ar123', 'artist name' ),
        new MusicItem( 'al456', 'another' )
    ];
    var getJSON = $.getJSON;

    $.getJSON = function( url, data, callback ) {
        assertEquals( '/json/search/myquery', url );
        callback( results );
    };

    search.init( '#'+parentId );
    search.makeQuery( 'myquery' );

    assertEquals( 1, $('ul.search-results').length );
    assertEquals( 2, $('ul.search-results li').length );

    $.getJSON = getJSON;

};

SearchBoxTest.prototype.testMakeQueryNoQuery = function() {

    var parentId = 'testMakeQueryNoQuery';
    var parent = $( '<div></div>' )
        .attr({ id: parentId })
        .appendTo( 'body' );
    var search = new sockso.SearchBox();

    search.init( '#'+parentId );
    search.makeQuery( '' );

    assertEquals( 1, $('ul.search-results').length );
    assertFalse( $('ul.search-results').is(':visible') );

};

SearchBoxTest.prototype.showNoResults = function() {

    var parentId = 'testInputNoResults';
    var parent = $( '<div></div>' )
        .attr({ id: parentId })
        .appendTo( 'body' );
    var search = new sockso.SearchBox();

    search.init( '#'+parentId );
    search.showResults( [] );

    assertEquals( 1, $('ul',parent).length );
    assertEquals( 1, $('li',parent).length );
    assertTrue( 1, $('li',parent).html().length > 0 );

};

SearchBoxTest.prototype.testInputKeyUp = function() {

    var parentId = 'testInputKeyUp';
    var parent = $( '<div></div>' )
        .attr({ id: parentId })
        .appendTo( 'body' );
    var search = new sockso.SearchBox();
    var queryMade = false;

    search.init( '#'+parentId );
    search.makeQuery = function( query ) {
        queryMade = true;
    };

    $( 'input', parent ).trigger( 'keyup' );

    // make sure timeout is used before making query
    assertFalse( queryMade );
    setTimeout(function() {
        assertTrue( queryMade );
    }, 2000 );

};

SearchBoxTest.prototype.testInputBlur = function() {

    var parentId = 'testInputBlur';
    var parent = $( '<div></div>' )
        .attr({ id: parentId })
        .appendTo( 'body' );
    var search = new sockso.SearchBox();

    search.init( '#'+parentId );
    search.showResults( [] );

    assertTrue( $('ul.search-results').is(':visible') );

    $( 'input', parent ).trigger( 'blur' );

    assertTrue( $('ul.search-results').is(':visible') );
    setTimeout(function() {
        assertFalse( $('ul.search-results').is(':visible') );
    }, 1000 )

};

SearchBoxTest.prototype.testInputFocusNoResults = function() {

    var parentId = 'testInputFocus';
    var parent = $( '<div></div>' )
        .attr({ id: parentId })
        .appendTo( 'body' );
    var search = new sockso.SearchBox();

    search.init( '#'+parentId );
    search.showResults( [] );

    assertTrue( $('ul.search-results').is(':visible') );

    $( 'ul.search-results' ).hide();

    assertFalse( $('ul.search-results').is(':visible') );

    $( 'input', parent ).trigger( 'focus' );

    assertTrue( $('ul.search-results').is(':visible') );

};

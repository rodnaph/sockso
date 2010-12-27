
TestCase( 'Sharer' ).prototype = {

    tearDown: function() {
        $( '.popup' ).remove();
    },

    testNothingAddedToThePageWhenCreatingTheSharerObject: function() {
        new sockso.Sharer();
        assertEquals( 0, $('.popup').length );
    },

    testInitialisingTheSharerAddsTheContainerToThePage: function() {
        new sockso.Sharer().init();
        assertEquals( 1, $('.popup').length );
    },

    testSharerHasNoItemsByDefault: function() {
        new sockso.Sharer().init();
        assertEquals( 0, $('.popup a').length );
    },

    testAddInsertsANewAnchorIntoTheSharerContainer: function() {
        var sharer = new sockso.Sharer();
        sharer.init();
        sharer.add( 'Text', 'image.png', function() {} );
        assertEquals( 1, $('.popup a').length );
    },

    testClickHandlerIsCalledWhenItemAddedIsClicked: function() {
        var sharer = new sockso.Sharer();
        var clicked = false;
        sharer.init();
        sharer.add( 'text', 'icon', function() { clicked=true; } );
        $( '.popup a' ).trigger( 'click' );
        assertTrue( clicked );
    },

    testDataIsInitiallyNull: function() {
        var sharer = new sockso.Sharer();
        assertNull( sharer.getData() );
    },

    testDataIsSetFromLinkClassIfItExistsByInit: function() {
        var source = $( '<a></a>' ).addClass( 'share-music share-music-tr123' );
        var sharer = new sockso.Sharer( source );
        sharer.init();
        assertEquals( 'tr123', sharer.getData() );
    },

    testDataCatBeSetWithSetdataMethod: function() {
        var sharer = new sockso.Sharer();
        sharer.setData( 'somedata' );
        assertEquals( 'somedata', sharer.getData() );
    },

    testAddingStandardLinksToSharerDoesSo: function() {
        var sharer = new sockso.Sharer();
        sharer.init();
        sharer.addStandardLinks();
        assertEquals( 1, $('.popup a').length );
    }

};

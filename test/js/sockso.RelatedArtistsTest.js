
RelatedArtistsTest = TestCase( 'sockso.RelatedArtists' );

RelatedArtistsTest.prototype = {

    setUp: function() {
        this.p = new sockso.Properties();
        this.r = new sockso.RelatedArtists({
            properties: this.p
        });
        $( '<div></div>' )
            .addClass( 'related artist-123' )
            .appendTo( 'body' );
        $( '<div></div>' )
            .addClass( 'related artist-456' )
            .appendTo( 'body' );
    },

    tearDown: function() {
        $( '.related' ).remove();
    },

    testConstructor: function() {
        assertNotNull( this.r );
    },

    testRenderingResponseData: function() {
        var related = [{ id:'tr789', name:'foo' }];
        this.r.updateArtist( 123, related );
        assertEquals( 1, $('.artist-123 .related-artist-789').length );
    },

    testResponseDataRenderedAsList: function() {
        var related = [{ id:'tr789', name:'foo' }];
        this.r.updateArtist( 123, related );
        assertEquals( 1, $('.artist-123 ul').length );
        assertEquals( 1, $('.artist-123 ul li').length );
    },

    testMultipleRelatedArtistsRendered: function() {
        var related = [{ id:'ar789', name:'foo' },{ id:'ar453', name:'boo' }];
        this.r.updateArtist( 123, related );
        assertEquals( 2, $('.artist-123 .related-artist').length );
    },

    testDisablePropertyIsRespected: function() {
        this.p.set( 'www.similarArtists.disable', 'yes' );
        assertFalse( this.r.isEnabled() );
        this.p.set( 'www.similarArtists.disable', 'no' );
        assertTrue( this.r.isEnabled() );
    },

    testMultipleArtistsOnSamePageFound: function() {
        var ids = this.r.getArtistIds();
        assertEquals( 2, ids.length );
    },

    testDuplicateArtistsIdsRemoved: function() {
        $( '<div></div>' )
            .addClass( 'related artist-456' )
            .appendTo( 'body' );
        var ids = this.r.getArtistIds();
        assertEquals( 2, ids.length );
    },

    testResponseDataTurnedInArray: function() {
        var gotResponse = false;
        this.r.ajax = function( options ) {
            options.success( '[{id:"ar123",name:"foo"}]' );
        };
        this.r.getRelatedArtists( 456, function(artists) {
            assertEquals( 1, artists.length );
            assertEquals( 'ar123', artists[0].id );
            gotResponse = true;
        });
        assertTrue( gotResponse );
    },

    testCorrectUrlQueried: function() {
        this.r.ajax = function( options ) {
            assertEquals( options.url, '/json/similarArtists/456' );
        };
        this.r.getRelatedArtists( 456, function(artists) {});
    }

};

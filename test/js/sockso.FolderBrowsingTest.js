
TestCase( 'sockso.FolderBrowsing' ).prototype = {

    setUp: function() {
        this.folders = new sockso.FolderBrowsing();
    },
    
    tearDown: function() {},

    testTrackIsPlayedAfterPathResolved: function() {},

    testGettingThePathReturnsAllParentNodesBackUpToTheCollectionRoot: function() {},

    testItemIsAddedToThePlaylistWhenItsPathHasBeenResolved: function() {},

    testMediaFileIsDetectedFromSupportedExtensions: function() {},

    testLoadingAFolderPopulatesItWithItsTracksAndSubfolders: function() {},

    testFilesWithDifferentExtensionsAreDetectedAsMediaFiles: function() {
        assertTrue( this.folders.isMediaFile({ path: 'somefile.mp3' }) );
        assertTrue( this.folders.isMediaFile({ path: 'somefile.flac' }) );
    },
    
    textNonMediaFilesAreNotPickedUpAsMediaFiles: function() {
        assertFalse( this.folders.isMediaFile({ path: 'somefile.doc' }) );
    }

};

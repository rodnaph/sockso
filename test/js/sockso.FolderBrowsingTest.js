
TestCase( 'sockso.FolderBrowsing' ).prototype = {

    setUp: function() {
        
    },
    
    tearDown: function() {
        
    },

    testTrackIsPlayedAfterPathResolved: function() {},

    testGettingThePathReturnsAllParentNodesBackUpToTheCollectionRoot: function() {},

    testItemIsAddedToThePlaylistWhenItsPathHasBeenResolved: function() {},

    testMediaFileIsDetectedFromSupportedExtensions: function() {},

    testLoadingAFolderPopulatesItWithItsTracksAndSubfolders: function() {
        var element = {};
        var folders = new sockso.FolderBrowsing();
        folders.ajax = function() {
            return '';
        };
        folders.onToggleClicked({ target:element });
    }

};

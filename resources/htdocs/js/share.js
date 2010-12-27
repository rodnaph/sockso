
$(function() {

    $( '.share-music' ).each(function() {

        var popup = new sockso.Sharer( $(this) );
        popup.init();
        popup.addStandardLinks();

    });

/*
    sharePopup.add( 'Add to Streamfinder', 'streamfinder.png', function() {
        var url = '';
        alert( 'URL: ' + url );        
    });
*/

});



if ( !window.sockso ) {
    sockso = {
        util: {}
    };
}

/**
 *  represents a music item
 *
 *  @param id the item id
 *  @param name the item name
 *  @param playlistId
 *
 */

function MusicItem( id, name, playlistId ) {

    this.id = id;
    this.name = name;
    this.playlistId = playlistId;

    var type = id.substring( 0, 2 );

    this.getTypeName = function() {
        switch ( type ) {
            case 'tr': return 'track';
            case 'ar': return 'artist';
            case 'al': return 'album';
            case 'pl': return 'playlist';
        }
    };

}

sockso.MusicItem = MusicItem;

/**
 *  returns an image wrapped in an achor, the image with the
 *  icon src, and the anchor with href set to the action
 *
 *  @param icon the image icon
 *  @param action the anchor action
 *  @param title the anchor title
 *
 *  @return jQuery
 *
 */

function getActionNode( icon, action, title ) {

    var skin = Properties.get( "www.skin", "original" );

    return $( '<a></a>' )
        .attr( 'href', action )
        .attr( 'title', title )
        .append( $('<img />').attr('src','/file/skins/' +skin+ '/images/' + icon +'.png') )
        .append( '<span>&nbsp;</span>' );

}

/**
 *  returns an LI element for a MusicItem
 *
 *  @param item a MusicItem object
 *  @param includePlaylistLink
 *
 *  @return jQuery
 *
 */

function getMusicElement( item, includePlaylistLink ) {

    var type = item.getTypeName();
    var doRemove = ( item.playlistId != null );
    var name = item.name.replace( /'/, '\\\'' );

    var remove = doRemove
        ? getActionNode('remove','javascript:playlist.remove('+item.playlistId+');','Remove')
        : null;

    var addToPlaylist = ( includePlaylistLink == true )
        ? getActionNode( 'add', 'javascript:playlist.add(new sockso.MusicItem(\'' +item.id+ '\',\'' +name+ '\'));', 'Add to playlist' )
        : null;

    var play = getActionNode('play','javascript:player.play(\''+item.id+'\')','Play \''+name+'\'');

    var link = ( type == 'track' )
        ? $( '<span>' + item.name + '</span>' )
        : $( '<a></a>' )
            .attr( 'href', '/browse/' + type + '/' + item.id.substring(2) )
            .append( item.name );

    var element = $( '<li></li>' )
        .attr( 'id', doRemove ? 'playlist-item-' + item.playlistId : '' )
        .addClass( type )
        .append( play )
        .append( addToPlaylist )
        .append( remove );

   if ( Properties.get('www.disableDownloads') != 'yes' )
        element.append( getActionNode('download','/download/'+item.id,'Download \''+name+'\'') );

   element.append( link );

   return element;

}

sockso.util.getMusicElement = getMusicElement;

/**
 *  represents an X,Y point
 *
 */

function Point( x, y ) {

    this.x = x;
    this.y = y;

}

/**
 *  returns the absolute position of a page element
 *
 */

function getPosition( obj ) {

    var curleft = curtop = 0;

    if ( obj.offsetParent ) {
        curleft = obj.offsetLeft;
        curtop = obj.offsetTop;
        while ( obj = obj.offsetParent ) {
            curleft += obj.offsetLeft;
            curtop += obj.offsetTop;
        }
    }

    return new Point( curleft, curtop );

}

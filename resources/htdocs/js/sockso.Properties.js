
/**
 *  Allows access to key/value based properties
 *
 *  example:
 *  var p = new sockso.Properties();
 *  p.set( 'foo', 'bar' );
 *  p.get( 'baz' );
 *  p.get( 'something', 'default value' );
 *
 */

sockso.Properties = sockso.Hashtable;

/**
 *  Fetches a url with base path and skin resolved
 *
 *  @param url
 *
 *  @return String
 *
 */

sockso.Properties.prototype.getUrl = function(url) {
    
    var basepath = this.get("server.basepath","/");

    if ( url.startsWith("http://") || url.startsWith("https://") ) {
        return url;
    }
    
    if ( url.startsWith("/") ) {
        url = url.substring(1);
    }

    if ( url.startsWith("<skin>/") ) {
        url = url.replace("<skin>", "file/skins/"+this.get("www.skin", "original" ));
    }
    
    if ( !basepath.endsWith("/") ) {
        basepath += "/";
    }

    if ( !basepath.startsWith("/") && !basepath.startsWith("http://") && !basepath.startsWith("https://") ) {
        basepath = "/" + basepath;
    }

    return basepath + url;

};

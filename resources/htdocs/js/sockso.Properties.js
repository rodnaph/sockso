
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

String.prototype.startsWith = function(prefix) {
    return this.indexOf(prefix) === 0;
};

String.prototype.endsWith = function(suffix) {
    return this.match(suffix + "$") == suffix;
};

sockso.Properties.prototype.getUrl = function(url) {
    
    if (url.startsWith("http://") || url.startsWith("http://")) {
        return url;
    }
    
    if (url.startsWith("/")) {
        url = url.substring(1);
    }

    if (url.startsWith("<skin>/")) {
        url = url.replace("<skin>", "file/skins/"+this.get("www.skin", "original" ));
    }
    
    var basepath = this.get("server.basepath","/");
    if (!basepath.endsWith("/")) {
        basepath += "/";
    }
    if (!basepath.startsWith("/") && !basepath.startsWith("http://") && !basepath.startsWith("https://")) {
        basepath = "/"+basepath;
    }

    return basepath+url;
};
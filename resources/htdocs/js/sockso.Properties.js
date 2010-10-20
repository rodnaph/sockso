
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

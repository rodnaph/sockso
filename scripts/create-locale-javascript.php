<?php

/**
 *  this script creates locale javascript files from the locale text files,
 *  this then allows easy access to all the locale text from javascript
 *  via the global "Locale" object.
 *
 *  usage: php create-locale-javascript.php
 *
 */

define( 'LOCALES_DIR', 'resources/locales' );
define( 'JAVASCRIPT_DIR', 'resources/htdocs/js' );

list( $IGNORE, $version ) = $argv;

// first delete any current locale js

$dh = opendir( JAVASCRIPT_DIR );

while ( $file = readdir($dh) ) {
    if ( preg_match('/^locale.*js$/',$file) ) {
        unlink( JAVASCRIPT_DIR . '/' . $file );
    }
}

closedir( $dh );

// then create the new locale js

$dh = opendir( LOCALES_DIR );

while ( $file = readdir($dh) ) {
    if ( preg_match('/^sockso.(\w+).txt$/',$file,$matches) ) {

        list( $all, $locale ) = $matches;

        $keys = '';
        $fh = fopen( LOCALES_DIR . "/sockso.$locale.txt", 'r' );

        // get key/value pair data from locale file
        while ( !feof($fh) ) {
            $line = chop( fgets($fh) );
            if ( preg_match('/^(www\.[A-Za-z\.]+)=(.*)$/',$line,$matches) ) {

                list( $all, $key, $value ) = $matches;

                $keys .= ",'$key':'" . addslashes($value) . "'";

            }
        }

        fclose( $fh );

        $keys = substr( $keys, 1 ); // trim leading commer
        $js = "

var Locale = new sockso.Locale();
Locale.setData({ $keys });

";

        // write locale javascript
        $fh = fopen( JAVASCRIPT_DIR . "/locale.$locale.$version.js", 'w' );
        fwrite( $fh, $js );
        fclose( $fh );

    }
}

closedir( $dh );

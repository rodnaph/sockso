<?php

/**
 *  this script reads the locales directory and creates a file in it called
 *  "index" which contains a list of the locale files (this is a work-around
 *  cause it's hard to get this info from the jar file via the class loader...
 *
 */

define( 'LOCALES_DIR', 'resources/locales' );

$dh = opendir( LOCALES_DIR );
$fh = fopen( LOCALES_DIR . '/index', 'w' );

while ( $file = readdir($dh) )
    if ( preg_match('/^sockso\..*\.txt$/',$file,$matches) )
        fwrite( $fh, $file . "\n" );

closedir( $dh );
fclose( $fh );

<?php

/**
 *  this script packs up all the javascript files in the js directory, it
 *  makes sure that the jquery library is put first in the file
 *
 */

include 'lib/php/JavaScriptPacker.class.php';

list( $IGNORE, $version ) = $argv;

$jsDir = 'resources/htdocs/js';
$packFile = "$jsDir/packed-$version.js";
$unpackFile = "$jsDir/unpacked-$version.js";
$licenses = '';
$allScript = '';

$preloadFiles = array( 'jquery.js', 'sockso.js' );
$postloadFiles = array( 'init.js' );

foreach ( $preloadFiles as $preloadFile ) {
    $allScript .= file_get_contents( "$jsDir/$preloadFile" );
    $licenses .= extractLicenses( $allScript );
}

$d = opendir( $jsDir );
$dirlist = Array();

// then read in all javascript files and pack them up
while ( $file = readdir($d) ) {
    $dirlist[] = $file;
}
sort($dirlist);
foreach ($dirlist as $key => $file) {
    $path = "$jsDir/$file";

    // remove old pack files
    if ( preg_match('/packed-/',$file) ) {
        unlink( $path );
    }

    elseif ( !in_array($file,array_merge($preloadFiles,$postloadFiles)) && substr($file,-2) == 'js' ) {

        $script = file_get_contents( $path );

        $licenses .= extractLicenses( $script );
        $allScript .= $script;

    }

}

foreach ( $postloadFiles as $file ) {
    $allScript .= file_get_contents( "$jsDir/$file" );
    $licenses .= extractLicenses( $allScript );
}

// write unpacked javascript
$f = fopen( $unpackFile, 'w' );
fwrite( $f, $allScript );
fclose( $f );

// write packed javascript
$packer = new JavaScriptPacker( $allScript, 'Normal', true, false );
$f = fopen( $packFile, 'w' );
fwrite( $f, $licenses . $packer->pack() );
//fwrite( $f, $allScript ); // not packed
fclose( $f );

/**
 *  extracts license info marked with /***'s
 *
 *  @param $script javascript to search in
 *  @return license info string
 *
 */

function extractLicenses( $script ) {

    $pattern = '#/\*\*\*.*?\*/#s';
    $licenses = '';

    preg_match_all( $pattern, $script, $matches );

    if ( $matches )
           foreach ( $matches[0] as $match )
                   $licenses .= $match . "\n";

    return $licenses;

}

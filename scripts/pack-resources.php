<?php

define( 'PACK_NONE', 1 );
define( 'PACK_PACKER', 2 );
define( 'PACK_YUI', 3 );

define( 'PACK_TYPE', PACK_YUI );

if ( count($argv) != 2 ) {
    throw new Exception( 'Usage: php pack-resources.php VERSION' );
}

list( $IGNORE, $version ) = $argv;

packJavascript( $version );

// @todo pack css

function packJavascript( $version ) {

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

        elseif ( !in_array($file,array_merge($preloadFiles,$postloadFiles)) && substr($file,-2) == 'js' && !preg_match('/locale.\w+/',$file) ) {

            echo "Include: $path\n";

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
    $f = fopen( $packFile, 'w' );

    echo "Unpacked JS: {$unpackFile}\n";

    switch ( PACK_TYPE ) {

        case PACK_PACKER:
            include 'lib/php/JavaScriptPacker.class.php';
            $packer = new JavaScriptPacker( $allScript, 'Normal', true, false );
            fwrite( $f, $licenses . $packer->pack() );
            break;

        case PACK_YUI:
            system(sprintf(
                'java -jar lib/dev/yuicompressor-2.4.2.jar --type js -o "%s" "%s"',
                $packFile,
                $unpackFile
            ));
            break;

        case PACK_NONE:
            break;

    }

    fclose( $f );

}

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

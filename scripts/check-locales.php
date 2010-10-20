<?php

/**
 *  checks the locale files to see if there's any text missing
 *
 *  usage: php check-locales.php
 *
 */

define( 'LOCALES_DIR', 'resources/locales' );
define( 'BASE_LOCALE', 'en' );

// load the base locale info we'll compare against
$base = getLocale( BASE_LOCALE );

// then look through the files looking for missing text
$dh = opendir( LOCALES_DIR );
while ( $file = readdir($dh) )
	if ( preg_match('/^sockso\.(.+?)\.txt$/',$file,$matches) ) {
		$locale = $matches[ 1 ];
		if ( $locale != BASE_LOCALE ) {
			echo "\nsockso.$locale.txt\n\n";
			$data = getLocale( $locale );
			foreach ( $base as $key => $value )
				if ( !isset($data[$key]) )
					echo "$key=$value\n";
			echo "\n";
		}
	}

/**
 *  reads through a locale file and returns an assoc array
 *  of the keys and values it contains
 *
 *  @param $locale the locale of the file to load
 *
 */

function getLocale( $locale ) {

	$path = LOCALES_DIR . "/sockso.$locale.txt";
	$data = array();
	$f = fopen( $path, 'r' );

	while ( !feof($f) ) {
		$line = fgets( $f );
		if ( preg_match('/^(.*?)=(.*)$/',$line,$matches) )
			$data[ $matches[1] ] = $matches[ 2 ];
	}

	return $data;

}

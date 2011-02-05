
load( "scripts/fulljslint.js" );

function showErrors() {
    
    for ( var i=0; i<JSLINT.errors.length; i++ ) {
        
        var error = JSLINT.errors[ i ];

        if ( error ) {
            print();
            print( "Error: " +error.reason );
            print( "\t" + error.evidence );
            print();
        }
        
    }
    
}

function lintFile( jsFile ) {
    
    if ( jsFile.match(/packed|locale|jquery|persist|swfobject/) ) {
        print( "Ignore: " +jsFile );
    }

    else {
        print( "Check: " +jsFile );
        if ( !JSLINT(readFile(jsFile)) ) {
            showErrors();
        }
    }

}

function lintDir( jsDir ) {

    var jsFiles = new java.io.File( jsDir ).list();

    for ( var i=0; i<jsFiles.length; i++ ) {
        var jsFile = jsFiles[ i ];
        lintFile( jsDir + jsFile );
    }


}

lintDir( "resources/htdocs/js/" );


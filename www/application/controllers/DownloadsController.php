<?

class DownloadsController extends Smutty_Controller {

    function indexAction() {

        $dldir = 'application/public/downloads';
        $dh = opendir( $dldir );
        $files = array();

        while ( $file = readdir($dh) )
            if ( !is_dir("$dldir/$file") )
                array_push( $files, $file );

        rsort( $files );

        $this->set( 'files', $files );
        $this->view();

    }

}

?>

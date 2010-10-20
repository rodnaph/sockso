<?

class NatController extends Smutty_Controller {

    function ipAction( $data ) {

        $server = $data->getServerData();
        $ip = $server->string( 'REMOTE_ADDR' );

	header( 'Content-type: text/plain' );

        echo "$ip\n";

    }

    function testAction( $data ) {

	header( 'Content-type: text/plain' );

        $port = $data->string( 'port' );
        $server = $data->getServerData();
        $ip = $server->string( 'REMOTE_ADDR' );
        $url = "http://$ip:$port/nat/test";
        $data = '';

        $f = fopen( $url, 'r' );
		while ( !feof($f) )
			$data .= fgets( $f, 4096 );
        fclose( $f );

		$data .= "\nURL: $url\n";

        echo $data;

    }

    function error() {
        // ignore errors (bad i know...)
        die( 'ERROR' );
    }

}

?>

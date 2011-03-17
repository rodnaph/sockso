<?php

class Community_Controller extends Default_Controller {

    /**
     * Show the index page listing active servers
     *
     */
    public function doIndex() {
        
        $this->render( 'index', array(
            'servers' => $this->getModel( 'CommunityActive' )
                              ->findActive()
        ));
        
    }

    /**
     * Handle POST call to ping active server details
     *
     */
    public function doPing() {

        $json = file_get_contents( 'php://input' );
        $pingData = json_decode( $json );

        $ip = $this->getServer()->REMOTE_ADDR;

        $this->errorCheck( $pingData );
        $this->recordPing( $ip, $pingData, $this->getServerData($ip,$pingData) );

    }

    /**
     * Tries to fetch the data from the servering we're being pinged by
     *
     * @param string $ip
     * @param object $pingData
     *
     * @return object
     */
    private function getServerData( $ip, $pingData ) {
        
        $url = sprintf(
            'http://%s:%s%s/json/serverinfo',
            $ip,
            $pingData->port,
            $pingData->basepath ? $pingData->basepath : ''
        );
        
        $json = file_get_contents( $url );

        return json_decode( $json );

    }

    /**
     * Records a ping as long as the server is reachable
     *
     * @param object $ip
     * @param object $pingData
     * @param object $serverData
     */
    private function recordPing( $ip, $pingData, $serverData ) {

        $active = $this->getModel( 'CommunityActive' )
                       ->findByKey( $pingData->skey );

        if ( !$active ) {
            $active = $this->getModel( 'CommunityActive' );
            $active->skey = $pingData->skey;
        }

        $active->ip = $ip;
        $active->title = $serverData->title;
        $active->tagline = $serverData->tagline;
        $active->port = $pingData->port;
        $active->version = $serverData->version;
        $active->requiresLogin = $serverData->requiresLogin;
        $active->basepath = $pingData->basepath;
        $active->dateUpdated = date( 'Y-m-d H:i:s' );
        $active->save();

        $this->show( 'ok' );

    }

    /**
     *  Checks data validity, returning error if found
     *
     *  @param object $pingData
     *
     */
    private function errorCheck( $pingData ) {

        if ( !$pingData->skey || !$pingData->port || strlen($pingData->skey) != 32 ) {
            $this->show( "Invalid data", 400 );
        }

    }

    /**
     * Signals a message to the user
     *
     * @param string $message
     * @param int $code
     *
     */
    private function show( $message, $code=200 ) {

        $text = array(
            200 => 'Ok',
            400 => 'Bad Request'
        );

        header( "HTTP/1.1 $code {$text[$code]}" );

        die( $message );

    }

}

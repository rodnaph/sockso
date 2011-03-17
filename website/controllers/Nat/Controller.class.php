<?php

class Nat_Controller extends Default_Controller {

    /**
     * Echo back the clients public IP address
     *
     */
    public function doIp() {

        $oServer = $this->getServer();

        $this->render( 'index', array(
            'ip' => $oServer->REMOTE_ADDR
        ));

    }

}

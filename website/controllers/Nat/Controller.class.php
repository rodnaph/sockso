<?php

class Nat_Controller extends Default_Controller {

    public function doIndex() {

        $oServer = $this->getServer();

        $this->render( 'index', array(
            'ip' => $oServer->REMOTE_ADDR
        ));

    }

}

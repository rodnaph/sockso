<?php

class Version_Controller extends Default_Controller {

    /**
     * Shows the latest version number (written by ant)
     * 
     */
    public function doLatest() {

        $this->render( 'latest' );

    }

}

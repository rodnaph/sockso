<?php

class Home_Controller extends Default_Controller {

    /**
     * Show the home page
     * 
     */
    public function doIndex() {

        $this->render( 'index', array(
            'siteDir' => $this->getSite()->getDir()
        ));
        
    }

}

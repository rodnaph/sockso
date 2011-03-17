<?php

class Manual_Controller extends Default_Controller {
    
    public function doIndex() {
        
        $oReq = $this->getRequest();

        $this->render( $oReq->getParam('page','index') );

    }

}

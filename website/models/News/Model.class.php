<?php

class News_Model extends Default_Model {

    /**
     *  Returns the news to go in the sidebar
     *
     *  @return array
     *
     */

    public function getSidebarNews() {
        
        return $this->createQuery()
                    ->orderBy( 'date_created', 'desc' )
                    ->limit( News_Controller::PER_PAGE )
                    ->find();
        
    }

}

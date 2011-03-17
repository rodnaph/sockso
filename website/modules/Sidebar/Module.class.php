<?php

class Sidebar_Module extends Default_Module {

    /**
     *  Extract the news to display in the sidebar
     *
     *  @return array
     *
     */

    public function process() {

        $aoNews = $this->getModel( 'News' )
                       ->getSidebarNews();

        return array(
            'aoNews' => $aoNews
        );

    }

}

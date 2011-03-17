<?php

require_once '../../tests/bootstrap.php';

class News_ModelTest extends Smut_Tests_ModelTestCase {

    public function testGettingTheSidebarNews() {
        $this->fixture( 'model-news' );
        $aoNews = $this->getModel( 'News' )
                       ->getSidebarNews();
        $this->assertEquals( 'foo', $aoNews[0]->title );
    }

}

<?php

class TodoController extends Smutty_Controller {

    function indexAction() {

        $cmd = '~/apps/bzr/bzr cat ~/public_html/bzr/sockso/TODO';

        exec( $cmd, $output );

        $todo = join( '<br />', $output );

        $this->set( 'todo', $todo );
        $this->view();

    }

}
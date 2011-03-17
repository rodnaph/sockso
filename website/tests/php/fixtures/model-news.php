<?php

$oNews = $this->getModel( 'News' );
$oNews->title = 'foo';
$oNews->body = 'bar';
$oNews->date_created = time();
$oNews->save();

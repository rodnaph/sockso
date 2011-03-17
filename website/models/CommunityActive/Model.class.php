<?php

class CommunityActive_Model extends Default_Model {

    /**
     * Finds active servers for use on the main listing
     *
     */
    public function findActive( $since='-1 day' ) {
        
        $sinceDate = date( 'Y-m-d H:i:s', strtotime($since) );
        
        return $this->createQuery()
                    ->where( 'dateUpdated', '>', $sinceDate )
                    ->orderBy( 'dateUpdated', 'desc' )
                    ->find();
        
    }

    /**
     * Find an active server by key
     *
     * @param string $skey
     *
     * @return CommunityActive_Model
     */
    public function findByKey( $skey ) {
        
        return $this->createQuery()
                    ->where( 'skey', '=', $skey )
                    ->findOne();
        
    }

}

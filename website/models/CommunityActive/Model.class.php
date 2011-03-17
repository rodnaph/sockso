<?php

class CommunityActive_Model extends Default_Model {

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

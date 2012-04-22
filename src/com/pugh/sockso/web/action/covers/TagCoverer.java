
package com.pugh.sockso.web.action.covers;

public class TagCoverer extends BaseCoverer {

    /**
     *  Currently unimplemented
     * 
     *  @param itemName
     * 
     *  @return 
     * 
     */

    public boolean serveCover( final String itemName ) {

/*
        String imgExt = p.get(Constants.DEFAULT_ARTWORK_TYPE, "jpg");
        // TODO: in order to extract cover from tag, need to find which tag to get from request:
        // album or artist is meta-info of one or more files!
        File musicFile = ;
        try {
            final Tag tag = AudioTag.getTag( musicFile );
            BufferedImage coverArt = tag.getCoverArt();
            // now the cover should be in the cache
            if( coverArt != null){
                serveCover(coverArt, imgExt, true);
                return;
            }
        } catch (InvalidTagException e) {
            log.error("Invalid tag for file: " + musicFile.toString(), e);
        }
*/

        return false;

    }

}


package com.pugh.sockso.web.action;

import com.pugh.sockso.web.BadRequestException;

/**
 *  Parent class for all web actions for admin users
 *
 */

public abstract class AdminAction extends BaseAction {

    /**
     *  Checks request is ok for an admin action (ie. user is an admin)
     *
     *  @throws BadRequestException
     *
     */

    public void handleRequest() throws Exception {
        
        if ( !getUser().isAdmin() ) {
            throw new BadRequestException(
                getLocale().getString("www.error.notAnAdmin")
            );
        }

        handleAdminRequest();

    }

    /**
     *  Method to implement to handle requests for this action
     *
     *  @throws Exception
     *
     */
    
    public abstract void handleAdminRequest() throws Exception;

}

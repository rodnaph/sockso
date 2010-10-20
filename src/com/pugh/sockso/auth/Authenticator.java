
package com.pugh.sockso.auth;

/**
 * This interface defines an authenticator for Sockso to validate a user with
 * a password.
 *
 */
public interface Authenticator {

    /**
     * Authenticates a user by name and password.
     *
     * @param user
     * @param pass
     * @return
     */
    public boolean authenticate( final String name, final String pass ) throws Exception;

}

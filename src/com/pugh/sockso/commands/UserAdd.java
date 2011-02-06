
package com.pugh.sockso.commands;

import com.pugh.sockso.Validater;
import com.pugh.sockso.ValidationException;
import com.pugh.sockso.db.Database;
import com.pugh.sockso.resources.Locale;
import com.pugh.sockso.web.User;

import java.sql.SQLException;

public class UserAdd extends BaseCommand {

    private final Database db;
    
    private final Locale locale;
    
    public UserAdd( final Database db, final Locale locale ) {
        
        this.db = db;
        this.locale = locale;
        
    }

    public String getName() {
        
        return "useradd";
        
    }
    
    public String getDescription() {
        
        return "Adds a new user";
        
    }

    /**
     *  Tries to add a new user
     *
     *  @param args
     *
     *  @return
     *
     *  @throws SQLException
     *
     */

    public String execute( final String[] args ) throws SQLException {

        try {

            final Validater v = new Validater( db );
            final String name = args[ 1 ];
            final String pass = args[ 2 ];
            final String email = args[ 3 ];
            final String isAdmin = args[ 4 ];

            if ( v.usernameExists(name) ) {
                throw new ValidationException( locale.getString("con.err.usernameExists") );
            }

            if ( v.emailExists(email) ) {
                throw new ValidationException( locale.getString("con.err.emailExists") );
            }

            final User newUser = new User(
                name,
                pass,
                email,
                isAdmin.equals("1") ? true : false
            );

            newUser.save( db );

            return newUser.getId() != -1
                ? locale.getString( "con.msg.userCreated")
                : locale.getString( "con.err.errorCreatingUser");

        }

        catch ( final ValidationException e ) {
            return e.getMessage();
        }

    }

    @Override
    public int getNumArgs() {

        return 4;
        
    }


}


package com.pugh.sockso.mail;

import com.pugh.sockso.Constants;
import com.pugh.sockso.Properties;

import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.InternetAddress;

import java.util.Date;

/**
 *
 */

public class Mailer {

    private final Properties p;

    /**
     *  constructor
     *
     */

    public Mailer( final Properties p ) {

        this.p = p;

    }

    /**
     *  sends the specified email
     *
     *  @param email
     *
     */

    public void send( final String to, final String subject, final String message ) throws MessagingException {

        if ( p.get(Constants.MAIL_ENABLED).equals(Properties.YES) ) {

            final java.util.Properties props = System.getProperties();

            if ( p.get(Constants.MAIL_SMTP_AUTH).equals(Properties.YES) ) {
                props.put( "mail.smtps.auth", "true" );
            }

            final Session session = Session.getDefaultInstance( props, null );
            final Message msg = new MimeMessage( session );

            msg.setFrom();
            msg.setRecipients(
                Message.RecipientType.TO,
                InternetAddress.parse(to,false)
            );
            msg.setSubject( subject );
            msg.setText( message );
            msg.setSentDate( new Date() );

            final Transport t = session.getTransport( p.get(Constants.MAIL_TYPE,"smtp") );

            t.connect(
                p.get(Constants.MAIL_HOST),
                p.get(Constants.MAIL_USER),
                p.get(Constants.MAIL_PASS)
            );

            t.sendMessage(
                msg,
                msg.getAllRecipients()
            );

        }

    }

}


package com.pugh.sockso.gui;

import com.pugh.sockso.Validater;
import com.pugh.sockso.ValidationException;
import com.pugh.sockso.db.Database;
import com.pugh.sockso.resources.Resources;
import com.pugh.sockso.resources.Locale;
import com.pugh.sockso.web.User;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.text.JTextComponent;
import javax.swing.JCheckBox;

import java.sql.SQLException;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.builder.DefaultFormBuilder;

import org.apache.log4j.Logger;

import com.google.inject.Inject;

public class CreateUserDialog extends JDialog {

    private static Logger log = Logger.getLogger( CreateUserDialog.class );
    
    private final Database db;
    private final Resources r;
    private final UsersPanel usersPanel;
    private final Locale locale;
    
    private JTextField txtName, txtEmail;
    private JPasswordField txtPass1, txtPass2;
    private JCheckBox isAdmin;
    
    @Inject
    public CreateUserDialog( final JFrame parent, final Database db, final Resources r,
                             final UsersPanel usersPanel, final Locale locale ) {
        
        super( parent, locale.getString("gui.title.creatingUser") );
        
        this.db = db;
        this.r = r;
        this.usersPanel = usersPanel;
        this.locale = locale;
        
        createComponents();
        
        setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE );
        setBounds( 30,30, 400,300 );
        setLocationRelativeTo( null );
        setResizable( false );
        
        // setup layout
        setLayout( new BorderLayout() );
        add( getMainPane(), BorderLayout.CENTER );
        add( getButtonPane(), BorderLayout.SOUTH );
        pack();
        
        // show!
        setVisible( true );

    }

    /**
     *  creates the components that are used on the form
     * 
     */
    
    private void createComponents() {
        
        txtName = new JTextField();
        txtEmail = new JTextField();
        txtPass1 = new JPasswordField();
        txtPass2 = new JPasswordField();
        isAdmin = new JCheckBox();
        
    }
    
    /**
     *  returns the main pane with all the input fields
     * 
     *  @return input fields panel
     * 
     */
    
    private JPanel getMainPane() {
        
        FormLayout layout = new FormLayout(
            " right:max(40dlu;pref), 3dlu, 150dlu, 7dlu "
        );
        DefaultFormBuilder builder = new DefaultFormBuilder(layout);
        builder.setDefaultDialogBorder();

        builder.append( locale.getString("gui.label.username"), txtName );
        builder.nextLine();
        builder.append( locale.getString("gui.label.password"), txtPass1 );
        builder.nextLine();
        builder.append( locale.getString("gui.label.passwordRepeat"), txtPass2 );
        builder.nextLine();
        builder.append( locale.getString("gui.label.email"), txtEmail );
        builder.nextLine();
        builder.append( locale.getString("gui.label.isAdmin"), isAdmin );
        builder.nextLine();

        return builder.getPanel();
        
    }
    
    /**
     *  returns the pane at the bottom of the dialog with the buttons on
     * 
     *  @return panel with buttons
     * 
     */
    
    private JPanel getButtonPane() {
        
        JButton create = new JButton( locale.getString("gui.label.createUser"), new ImageIcon(r.getImage("icons/22x22/ok.png")) );
        create.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent evt ) {
                createUser();
            }
        });

        JButton cancel = new JButton( locale.getString("gui.label.cancel"), new ImageIcon(r.getImage("icons/22x22/cancel.png")) );
        cancel.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent evt ) {
                close();
            }
        });        

        JPanel p = new JPanel();
        p.setLayout( new FlowLayout(FlowLayout.RIGHT) );
        p.add( cancel );
        p.add( create );
        
        return p;
        
    }
    
    /**
     *  closes the dialog
     * 
     */
    
    private void close() {
        
        setVisible( false );
        dispose();
        
    }
    
    /**
     *  checks if the data the user has input is valid for
     *  creating a new user.  if something bad is found then
     *  a validation error is thrown.
     * 
     */
    
    private void validateInputFields() throws ValidationException {
    
        final Validater v = new Validater( db );
        
        if ( !v.checkRequiredFields( new JTextComponent[] { txtName, txtPass1, txtEmail }) )
            throw new ValidationException( locale.getString("gui.error.missingField") );
        
        if ( !v.isValidEmail(txtEmail.getText()) )
            throw new ValidationException( locale.getString("gui.error.invalidEmail") );

        String pass1 = new String( txtPass1.getPassword() );
        String pass2 = new String( txtPass2.getPassword() );
        if ( !pass1.equals(pass2) )
            throw new ValidationException( locale.getString("gui.error.passwordsDontMatch") );

        if ( v.usernameExists(txtName.getText()) )
            throw new ValidationException( locale.getString("gui.error.duplicateUsername") );

        if ( v.emailExists(txtEmail.getText()) )
            throw new ValidationException( locale.getString("gui.error.duplicateEmail") );

    }
    
    /**
     *  tries to create a new user with the values from the
     *  fields on the form
     * 
     */
    
    private void createUser() {
        
        try {

            validateInputFields();
            
            final User newUser = new User(
                txtName.getText(),
                new String(txtPass1.getPassword()),
                txtEmail.getText(),
                isAdmin.isSelected()
            );

            newUser.save( db );
            
            usersPanel.refreshUsers();
            close();

        }
        
        catch ( final SQLException e ) {
            JOptionPane.showMessageDialog( this, e.getMessage() );
        }

        catch ( ValidationException e ) {
            JOptionPane.showMessageDialog( this, e.getMessage() );
        }
            
    }
    
}


package com.pugh.sockso.gui;

import com.pugh.sockso.Properties;
import com.pugh.sockso.Utils;
import com.pugh.sockso.db.Database;
import com.pugh.sockso.gui.controls.BooleanOptionField;
import com.pugh.sockso.resources.Resources;
import com.pugh.sockso.web.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.pugh.sockso.Constants;
import javax.swing.JOptionPane;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.apache.log4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

@Singleton
public class UsersPanel extends JPanel implements TableModelListener {

    private static Logger log = Logger.getLogger( UsersPanel.class );

    private final Injector injector;
    private final Properties p;
    private final Database db;
    private final Resources r;
    private final JFrame parent;
    
    private MyTableModel model;
    private JTable table;
    private boolean isRefreshing;

    @Inject
    public UsersPanel( final Injector injector, final AppFrame parent, final Database db,
                       final Properties p, final Resources r ) {

        this.injector = injector;
        this.parent = parent;
        this.db = db;
        this.p = p;
        this.r = r;
        
        isRefreshing = false;

    }

    /**
     *  Initialise the users panel
     *
     */
    
    public void init() {

        setLayout( new BorderLayout() );
        add( getOptionsPane(), BorderLayout.NORTH );
        add( getAccountsPane(), BorderLayout.CENTER );
        
        model.addTableModelListener( this );

        startAutoRefresh();
        
    }
    
    /**
     *  Start the thread to auto-refresh the table data as long as the user
     *  isn't editing the table
     * 
     */
    
    protected void startAutoRefresh() {

        final long refreshTimeout = 1000 * 30;
        
        new Thread() {
            @Override
            public void run() {
                while ( true ) {
                    try {
                        if ( !table.isEditing() ) {
                            refreshUsers();
                        }
                        sleep( refreshTimeout );
                    }
                    catch ( final InterruptedException e ) {}
                }
            }
        }.start();
        
    }
    
    /**
     *  returns the options pane at the top of the panel
     * 
     *  @return panel with options on
     * 
     */
    
    private JPanel getOptionsPane() {
        
        FormLayout layout = new FormLayout(
            " right:max(60dlu;pref), 10dlu, 150dlu, 7dlu "
        );
        DefaultFormBuilder builder = new DefaultFormBuilder(layout);
        builder.setDefaultDialogBorder();

        builder.appendSeparator( "Options" );
        builder.append( "Require login:", new BooleanOptionField(p,Constants.WWW_USERS_REQUIRE_LOGIN) );
        builder.nextLine();
        builder.append( "Disable registering:", new BooleanOptionField(p,Constants.WWW_USERS_DISABLE_REGISTRATION) );
        builder.nextLine();
        builder.append( "Require activation:", new BooleanOptionField(p,Constants.WWW_USERS_REQUIRE_ACTIVATION) );
        builder.nextLine();
        
        return builder.getPanel();
        
    }
    
    /**
     *  the main pane with the user accounts controls
     * 
     */
    
    private JPanel getAccountsPane() {
        
        JButton delete = new JButton( "Delete", new ImageIcon(r.getImage("icons/16x16/delete.png")) );
        delete.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent evt ) {
                deleteSelectedUser();
            }
        });

        JButton create = new JButton( "Create User", new ImageIcon(r.getImage("icons/16x16/add.png")) );
        create.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent evt ) {
                injector.getInstance( CreateUserDialog.class );
            }
        });

        JPanel accBtns = new JPanel( new FlowLayout(FlowLayout.LEFT) );
        accBtns.add( create );
        accBtns.add( delete );
        
        // create users table
        model = new MyTableModel();
        table = new JTable( model );
        TableColumnModel columns = table.getColumnModel();
        table.getSelectionModel().setSelectionMode( ListSelectionModel.SINGLE_SELECTION );

        // set table column widths
        int[] widths = { 10, 150, 200, 90, 10, 10 };
        for ( int i=0; i<widths.length; i++ )
            columns.getColumn(i).setPreferredWidth( widths[i] );

        JPanel pn = new JPanel();
        pn.setLayout( new BorderLayout() );
        pn.add( new JScrollPane(table), BorderLayout.CENTER );
        pn.add( accBtns, BorderLayout.SOUTH );
        return pn;

    }

    /**
     *  Save the users changes when the table is changed
     *
     *  @param evt
     *
     */

    public void tableChanged( final TableModelEvent evt ) {

        if ( !isRefreshing ) {
            saveChanges();
        }

    }

    /**
     *  Saves any changes made to the users data table
     *
     */

    protected void saveChanges() {

        for ( int i=0; i<table.getRowCount(); i++ ) {
            
            User user = new User(
                Integer.parseInt( model.getValueAt(i,0).toString() ),
                (String) model.getValueAt( i, 1 ),
                (String) model.getValueAt( i, 2 ),
                model.getValueAt( i, 4 ).equals( "true")
            );

            user.setActive( model.getValueAt( i, 5 ).equals("1") );

            try { user.update(db); }

            catch ( final SQLException e ) {
                log.error( e.getMessage() );
                JOptionPane.showMessageDialog(
                    parent,
                    e.getMessage(),
                    "Sockso",
                    JOptionPane.ERROR_MESSAGE
                );
            }

        }

    }

    /**
     *  if there is a user selected in the table, tries to
     *  delete it
     * 
     */
    
    protected void deleteSelectedUser() {
       
        int row = table.getSelectedRow();
        
        if ( row != -1 ) {
            
            try {
                
                int id = Integer.parseInt( (String) model.getValueAt(row,0) );

                User.delete( db, id );
                
                refreshUsers();

            }

            catch ( SQLException e ) {
                log.error( e );
            }
                        
        }
        
    }
    
    /**
     *  loads users from the database to the users table
     * 
     */
    
    protected void refreshUsers() {
        
        isRefreshing = true;

        for ( int i=model.getRowCount(); i>0; i-- )
            model.removeRow( i - 1 );
  
        PreparedStatement st = null;
        ResultSet rs = null;
        
        try {
            
            final String sql = " select u.id, u.name, u.email, u.date_created, u.is_admin, u.is_active " +
                               " from users u " +
                               " order by u.name asc ";
            
            st = db.prepare( sql );
            rs = st.executeQuery();
            
            while ( rs.next() ) {
                
                int row = model.getRowCount() + 1;
            
                model.setRowCount( row );
                model.setValueAt( rs.getString("id"), row - 1, 0 );
                model.setValueAt( rs.getString("name"), row - 1, 1 );
                model.setValueAt( rs.getString("email"), row - 1, 2 );
                model.setValueAt( rs.getString("date_created"), row - 1, 3 );
                model.setValueAt( rs.getString("is_admin"), row - 1, 4 );
                model.setValueAt( rs.getString("is_active"), row - 1, 5 );

            }
            
        }
        
        catch ( SQLException e ) {
            log.error( e );
        }
        
        finally {
            Utils.close( rs );
            Utils.close( st );
            isRefreshing = false;
        }
        
    }

}

class MyTableModel extends DefaultTableModel {
    
    private String[] columns = { "ID", "Name", "Email", "Date Created", "Admin", "Active" };
    
    @Override
    public int getColumnCount() { return columns.length; }
    
    @Override
    public String getColumnName( int i ) { return columns[i]; }

}

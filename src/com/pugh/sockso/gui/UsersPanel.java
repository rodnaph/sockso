/*
 * UsersPanel.java
 * 
 * Created on Aug 7, 2007, 7:40:54 AM
 * 
 * A panel for controlling user accounts and access
 * 
 */

package com.pugh.sockso.gui;

import com.pugh.sockso.Properties;
import com.pugh.sockso.Utils;
import com.pugh.sockso.db.Database;
import com.pugh.sockso.gui.controls.BooleanOptionField;
import com.pugh.sockso.resources.Resources;

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

import org.apache.log4j.Logger;

public class UsersPanel extends JPanel {

    private static Logger log = Logger.getLogger( UsersPanel.class );
    
    private Properties p;
    private Database db;
    private Resources r;
    private JFrame parent;
    
    private MyTableModel model;
    private JTable table;
    
    public UsersPanel( JFrame parent, Database db, Properties p, Resources r ) {

        this.parent = parent;
        this.db = db;
        this.p = p;
        this.r = r;
        
        setLayout( new BorderLayout() );
        add( getOptionsPane(), BorderLayout.NORTH );
        add( getAccountsPane(), BorderLayout.CENTER );
        
        // start thread to keep users up to date
        final int sixtySeconds = 1000 * 60;
        new Thread() {
            @Override
            public void run() {
                while ( true ) {
                    refreshUsers();
                    try { Thread.sleep(sixtySeconds); }
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
        builder.append( "Require login:", new BooleanOptionField(p,"users.requireLogin") );
        builder.nextLine();
        builder.append( "Disable registering:", new BooleanOptionField(p,"users.disableRegistration") );
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
                new CreateUserDialog( parent, db, r, UsersPanel.this );
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
        int[] widths = { 20, 150, 200, 100 };
        for ( int i=0; i<widths.length; i++ )
            columns.getColumn(i).setPreferredWidth( widths[i] );

        JPanel pn = new JPanel();
        pn.setLayout( new BorderLayout() );
        pn.add( new JScrollPane(table), BorderLayout.CENTER );
        pn.add( accBtns, BorderLayout.SOUTH );
        return pn;

    }

    /**
     *  if there is a user selected in the table, tries to
     *  delete it
     * 
     */
    
    private void deleteSelectedUser() {
       
        int row = table.getSelectedRow();
        
        if ( row != -1 ) {
            
            PreparedStatement st = null;
            
            try {
                
                int id = Integer.parseInt( (String) model.getValueAt(row,0) );
                
                String sql = " delete from playlist_tracks " +
                             " where playlist_id in ( " +
                                " select id " +
                                " from playlists " +
                                " where user_id = ? " +
                             " ) ";
                st = db.prepare( sql );
                st.setInt( 1, id );
                st.execute();
                st.close();
                
                sql = " delete from playlists " +
                      " where user_id = ? ";
                st = db.prepare( sql );
                st.setInt( 1, id );
                st.execute();
                st.close();
                
                sql = " delete from users " +
                             " where id = ? ";
                st = db.prepare( sql );
                st.setInt( 1, id );
                st.execute();
                st.close();
                
                refreshUsers();

            }

            catch ( SQLException e ) {
                log.error( e );
            }
            
            finally {
                Utils.close( st );
            }
            
        }
        
    }
    
    /**
     *  loads users from the database to the users table
     * 
     */
    
    protected void refreshUsers() {
        
        for ( int i=model.getRowCount(); i>0; i-- )
            model.removeRow( i - 1 );
  
        PreparedStatement st = null;
        ResultSet rs = null;
        
        try {
            
            final String sql = " select u.id, u.name, u.email, u.date_created " +
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

            }
            
        }
        
        catch ( SQLException e ) {
            log.error( e );
        }
        
        finally {
            Utils.close( rs );
            Utils.close( st );
        }
        
    }

}

class MyTableModel extends DefaultTableModel {
    
    private String[] columns = { "ID", "Name", "Email", "Date Created" };
    
    @Override
    public int getColumnCount() { return columns.length; }
    
    @Override
    public String getColumnName( int i ) { return columns[i]; }

}

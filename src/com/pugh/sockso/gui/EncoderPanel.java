/*
 * Controls encoding options for a particular file type
 * 
 * When selecting the encoding type there are the following options:
 * 
 *      1. Do nothing, stream unaltered
 *      2. Use a built in encoding option
 *      3. Specify your own encoding options
 * 
 */

package com.pugh.sockso.gui;

import com.pugh.sockso.Properties;
import com.pugh.sockso.resources.Resources;
import com.pugh.sockso.resources.Locale;
import com.pugh.sockso.music.encoders.Encoders;
import com.pugh.sockso.music.encoders.Encoders.Builtin;
import com.pugh.sockso.music.encoders.BuiltinEncoder;
import com.pugh.sockso.gui.controls.TextOptionField;
import com.pugh.sockso.gui.controls.ComboOptionField;

import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JPanel;
import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;
import javax.swing.JLabel;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.builder.DefaultFormBuilder;

import com.google.inject.Inject;

public class EncoderPanel extends JPanel {

    private JRadioButton typeNone, typeBuiltin, typeCustom;
    private ComboOptionField builtinOptions;
    private TextOptionField customCommand, builtinBitrate;
    private JLabel noneLabel;
    private ButtonGroup typeGroup;
    private JPanel builtinPanel, customPanel;
    
    private final Properties p;
    private final Resources r;
    private final Locale locale;
    
    private String fileType;
    
    @Inject
    public EncoderPanel( final Properties p, final Resources r, final Locale locale ) {
        
        this.p = p;
        this.r = r;
        this.locale = locale;
        
    }
    
    public void init( final String fileType ) {
        
        this.fileType = fileType;
        
        createComponents();
        layoutComponents();
        
        // set correct options for this encoders setup
        String typeProp = p.get( "encoders." +this.fileType );

        if ( Encoders.Type.BUILTIN.name().equals(typeProp) )
            showOption( Encoders.Type.BUILTIN );
        
        else if ( Encoders.Type.CUSTOM.name().equals(typeProp) )
            showOption( Encoders.Type.CUSTOM );
        
        else
            showOption( Encoders.Type.NONE );

    }

    /**
     *  creates the components that will be part of this panels gui
     * 
     */
    
    private void createComponents() {
        
        typeGroup = new ButtonGroup();
        
        typeNone = new JRadioButton( locale.getString("gui.label.encTypeNone") );
        typeNone.addActionListener(new ActionListener() {
            public void actionPerformed( ActionEvent evt ) {
                showOption( Encoders.Type.NONE );
                saveSelectedOption( Encoders.Type.NONE );
            }
        });
        
        typeBuiltin = new JRadioButton( locale.getString("gui.label.encTypeBuiltin") );
        typeBuiltin.addActionListener(new ActionListener() {
            public void actionPerformed( ActionEvent evt ) {
                showOption( Encoders.Type.BUILTIN );
                saveSelectedOption( Encoders.Type.BUILTIN );
            }
        });
        
        typeCustom = new JRadioButton( locale.getString("gui.label.encTypeCustom") );
        typeCustom.addActionListener(new ActionListener() {
            public void actionPerformed( ActionEvent evt ) {
                showOption( Encoders.Type.CUSTOM );
                saveSelectedOption( Encoders.Type.CUSTOM );
            }
        });
        
        typeGroup.add( typeNone );
        typeGroup.add( typeBuiltin );
        typeGroup.add( typeCustom );

        builtinOptions = new BuiltinOptionField(
            p, "encoders." +fileType+ ".name",
            getEncoderOptions(fileType)
        );
        builtinBitrate = new TextOptionField( p, "encoders." +fileType+ ".bitrate" );
        
        customCommand = new TextOptionField(p, "encoders." +fileType+ ".command" );
        customCommand.setMinimumSize( new Dimension(200,20) );
        
        noneLabel = new JLabel( locale.getString("gui.label.encNoneSelected") );

    }
    
    /**
     *  returns an array of encoder options to fill the combo, with a blank
     *  item first so nothing can be selected
     * 
     *  @param fileType
     *  @return
     * 
     */
    
    private Builtin[] getEncoderOptions( String fileType ) {
        
        Builtin[] encoders = Encoders.getBuiltinEncoders( fileType );
        Builtin[] withBlank = new Builtin[ encoders.length + 1 ];

        withBlank[ 0 ] = null;
        
        for ( int i=0; i<encoders.length; i++ )
            withBlank[ i + 1 ] = encoders[ i ];
        
        return withBlank;
        
    }
    
    /**
     *  saves the currently selected options.  all the fields will handle their
     *  own saving, so we just need to do the radio options (until i create a
     *  properties control for them to)
     * 
     *  @param type
     * 
     */
    
    protected void saveSelectedOption( Encoders.Type type ) {

        p.set( "encoders." +fileType, type.name() );
        p.save();

    }
    
    /**
     *  updates the UI to show the controls for the specified option
     * 
     *  @param type Encoders.TYPE_*
     * 
     */
    
    protected void showOption( Encoders.Type type ) {
        
        builtinPanel.setVisible( false );
        customPanel.setVisible( false );
        noneLabel.setVisible( false );

        switch ( type ) {
            
            case NONE:
                typeNone.setSelected( true );
                noneLabel.setVisible( true );
                break;
                
            case BUILTIN:
                typeBuiltin.setSelected( true );
                builtinPanel.setVisible( true );
                break;
                
            case CUSTOM:
                typeCustom.setSelected( true );
                customPanel.setVisible( true );
                break;
                
        }
        
    }
    
    /**
     *  takes the components that will be on this panel and creates the layout
     * 
     */
    
    private void layoutComponents() {

        FormLayout layout = new FormLayout(
            " right:max(40dlu;pref), 3dlu, 150dlu, 7dlu "
        );

        DefaultFormBuilder builtinBuilder = new DefaultFormBuilder(layout);
        builtinBuilder.setDefaultDialogBorder();
        builtinBuilder.append( locale.getString("gui.label.encoder"), builtinOptions );
        builtinBuilder.nextLine();
        builtinBuilder.append( locale.getString("gui.label.bitrate"), builtinBitrate );
        builtinBuilder.nextLine();
        builtinPanel = builtinBuilder.getPanel();

        DefaultFormBuilder customBuilder = new DefaultFormBuilder(layout);
        customBuilder.setDefaultDialogBorder();
        customBuilder.append( locale.getString("gui.label.command"), customCommand );
        customBuilder.nextLine();
        customPanel = customBuilder.getPanel();

        JPanel buttonPanel = new JPanel();
        buttonPanel.add( typeNone );
        buttonPanel.add( typeBuiltin );
        buttonPanel.add( typeCustom );

        JPanel mainPanel = new JPanel();
        mainPanel.add( builtinPanel );
        mainPanel.add( customPanel );
        mainPanel.add( noneLabel );
        
        setLayout( new BorderLayout() );
        add( buttonPanel, BorderLayout.NORTH  );
        add( mainPanel, BorderLayout.CENTER );

    }

}

/**
 *  changes the name used when saving an item
 * 
 *  @author rod
 * 
 */

class BuiltinOptionField extends ComboOptionField {

    public BuiltinOptionField( Properties p, String name, Object items[] ) {
        super( p, name, items );
    }
    
    @Override
    public String getItemSaveName( Object item ) {
        
        if ( item == null )
            return "";
        
        return ( (Builtin) item ).name();
        
    }
    
}

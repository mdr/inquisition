/*
 * LAFMenu.java
 * 
 * Created on 02 October 2006, 17:39
 * 
 */

package uk.co.bytemark.vm.enigma.inquisition.gui.misc;

import java.awt.event.ActionEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * A menu that lets the user switch Look & Feel. Buggy, but fun.
 */
public class LookAndFeelMenu extends JMenu {
    private static final Logger LOGGER = Logger.getLogger( LookAndFeelMenu.class.getName() );

    private final JFrame frame;

    /** Creates a new instance of lafMenu */
    public LookAndFeelMenu( JFrame frame ) {
        this.frame = frame;

        for ( UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels() )
            addLookAndFeelToMenu( info.getName(), info.getClassName() );
        addLookAndFeelToMenu( "Squareness", "net.beeger.squareness.SquarenessLookAndFeel" );
        // addLookAndFeelToMenu("Substance", "org.jvnet.substance.SubstanceLookAndFeel");
        // addLafToMenu("Napkin", "net.sourceforge.napkinlaf.NapkinLookAndFeel");
        // addLafToMenu("Plastic", "com.jgoodies.looks.plastic.PlasticXPLookAndFeel");

    }

    private void addLookAndFeelToMenu( String menuText, final String lookAndFeelName ) {
        JMenuItem menuItem = new JMenuItem( menuText );
        menuItem.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                switchLookAndFeel( lookAndFeelName );
            }
        } );
        add( menuItem );
    }

    private void switchLookAndFeel( String lookAndFeelName ) {
        try {
            UIManager.setLookAndFeel( lookAndFeelName );

            //            if (lookAndFeelName.equals("org.jvnet.substance.SubstanceLookAndFeel")) {
            //                // Amongst other things, disable the read-only lock on fields
            //                UIManager.put(SubstanceLookAndFeel.NO_EXTRA_ELEMENTS, Boolean.TRUE);
            //
            //                System.setProperty("sun.awt.noerasebackground", "true");
            //                // org.jvnet.substance.SubstanceLookAndFeel
            //                // .setCurrentTheme("org.jvnet.substance.theme.SubstanceSunGlareTheme");
            //                // org.jvnet.substance.SubstanceLookAndFeel
            //                // .setCurrentWatermark("org.jvnet.substance.watermark.SubstanceMetalWallWatermark");
            //            }
        } catch ( Exception e ) {
            LOGGER.log( Level.WARNING, "Problem switching L&F", e );
            JOptionPane.showMessageDialog( this, "Look and feel unavailable", "Look and feel unavailable", JOptionPane.ERROR_MESSAGE );
        }
        SwingUtilities.updateComponentTreeUI( frame );
    }

}

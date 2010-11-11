package uk.co.bytemark.vm.enigma.inquisition.gui.quizchooser;

import java.util.Collection;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.UIManager;

import uk.co.bytemark.vm.enigma.inquisition.misc.Constants;
import uk.co.bytemark.vm.enigma.inquisition.questions.QuestionSet;
import uk.co.bytemark.vm.enigma.inquisition.questions.QuestionSetManager;

public class InquisitionMain {
    private static final Logger LOGGER = Logger.getLogger( InquisitionMain.class.getName() );

    private final static Level LOGGING_LEVEL = Level.WARNING; // Level.INFO;

    public static void main( String args[] ) {
        runInquisition();
    }

    public static void runInquisition() {
        setFontAntialiasing();
        initialiseLogging();
        LOGGER.info( "Inquisition v" + Constants.VERSION + " by Matthew D. Russell" );
        setupLookAndFeel();
        Collection<QuestionSet> bundledQuestionSets = QuestionSetManager.loadBundledQuestionSets();
        launchAppFrame( bundledQuestionSets );
    }

    private static void setFontAntialiasing() {
        System.setProperty( "swing.aatext", "true" );
        if ( isLinux() )
            System.setProperty( "awt.useSystemAAFontSettings", "on" );
    }

    private static void initialiseLogging() {
        Logger logger = Logger.getLogger( "" );
        logger.setLevel( LOGGING_LEVEL );
        Handler[] handlers = Logger.getLogger( "" ).getHandlers();
        for ( Handler handler : handlers )
            handler.setLevel( LOGGING_LEVEL );
    }

    private static void launchAppFrame( final Collection<QuestionSet> bundledQuestionSets ) {
        java.awt.EventQueue.invokeLater( new Runnable() {
            public void run() {
                QuestionSetSelectorFrame frame = new QuestionSetSelectorFrame( bundledQuestionSets );
                putFrameAtCentreOfScreen( frame );
                frame.setVisible( true );
            }

            private void putFrameAtCentreOfScreen( QuestionSetSelectorFrame frame ) {
                frame.setLocationRelativeTo( null );
            }
        } );
    }

    public static void setupLookAndFeel() {
        try {
            if ( isLinux() ) {
                hideNumbersFromSlidersUnderGtk();
                //                useLookAndFeelForWindowDecorators();
                //                UIManager.setLookAndFeel("net.beeger.squareness.SquarenessLookAndFeel");
                UIManager.setLookAndFeel( "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel" );
            } else {
                UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
            }

        } catch ( Exception e ) {
            LOGGER.log( Level.WARNING, "Cannot set look and feel: " + UIManager.getSystemLookAndFeelClassName(), e );
        }
    }

    // private static void useLookAndFeelForWindowDecorators() {
    // JFrame.setDefaultLookAndFeelDecorated(true);
    // JDialog.setDefaultLookAndFeelDecorated(true);
    // }

    private static boolean isLinux() {
        return System.getProperty( "os.name" ).equals( "Linux" );
    }

    private static void hideNumbersFromSlidersUnderGtk() {
        UIManager.put( "Slider.paintValue", Boolean.FALSE );
    }

}

package uk.co.bytemark.vm.enigma.inquisition.gui.quiz;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.ImageIcon;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import uk.co.bytemark.vm.enigma.inquisition.gui.images.Icons;
import uk.co.bytemark.vm.enigma.inquisition.gui.misc.QuizConfig;
import uk.co.bytemark.vm.enigma.inquisition.gui.quizchooser.InquisitionMain;
import uk.co.bytemark.vm.enigma.inquisition.gui.screens.AbstractResultDialog;
import uk.co.bytemark.vm.enigma.inquisition.questions.QuestionSet;
import uk.co.bytemark.vm.enigma.inquisition.questions.QuestionSetManager;
import uk.co.bytemark.vm.enigma.inquisition.quiz.QuizState;

public class ResultsDialog extends AbstractResultDialog implements PropertyChangeListener {

    private final QuizState quizState;

    private final QuestionTableModel questionTableModel;

    private boolean updating;

    public ResultsDialog( Frame parent, QuizState quizState ) {
        super( parent, false );
        this.quizState = quizState;
        questionTableModel = new QuestionTableModel( quizState );
        getTable().setModel( questionTableModel );
        getTable().getSelectionModel().setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
        addEventHandlers();

        quizState.addPropertyChangeListener( this );
        updateFromModel();
    }

    private void addEventHandlers() {
        getCloseButton().addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                setVisible( false );
                dispose();
            }
        } );
        getTable().getSelectionModel().addListSelectionListener( new ListSelectionListener() {
            public void valueChanged( ListSelectionEvent e ) {
                boolean selected = ( getTable().getSelectedRowCount() == 1 );
                if ( selected && !updating ) {
                    int selectedRow = getTable().getSelectedRow();
                    quizState.goToQuestion( selectedRow + 1 );
                }
            }
        } );
    }

    public void propertyChange( PropertyChangeEvent evt ) {
        updateFromModel();
    }

    private void updateFromModel() {
        updating = true;
        try {
            questionTableModel.fireTableDataChanged();
            int questionNumber = quizState.getQuestionNumber();
            getTable().setRowSelectionInterval( questionNumber - 1, questionNumber - 1 );
            updateScoreBar();
        } finally {
            updating = false;
        }
    }

    private void updateScoreBar() {
        int correctCount = quizState.getNumberOfCorrectQuestions();
        int numberOfQuestions = quizState.getNumberOfQuestions();
        String percentageString = String.format( " (%.0f%%)", 100.0 * correctCount / numberOfQuestions );
        getScoreBar().setString( "" + correctCount + " / " + numberOfQuestions + percentageString );
        getScoreBar().setValue( (int) ( correctCount * 100.0 / numberOfQuestions ) );
    }

    public static class QuestionTableModel extends AbstractTableModel {

        private String[] columnHeadings = new String[] { "Question number", "Type", "Answered", "Marked for review", "Correct" };

        private Class<?>[] columnClasses = new Class[] { Integer.class, String.class, Boolean.class, Boolean.class, ImageIcon.class }; // Boolean.class};

        private QuizState quizState;

        @Override
        public String getColumnName( int column ) {
            return columnHeadings[column];
        }

        @Override
        public Class<?> getColumnClass( int column ) {
            return columnClasses[column];
        }

        /** Creates a new instance of QuestionTableModel */
        public QuestionTableModel( QuizState quizState ) {
            this.quizState = quizState;
        }

        public int getRowCount() {
            return quizState.getNumberOfQuestions();
        }

        public int getColumnCount() {
            return 5;
        }

        public Object getValueAt( int rowIndex, int columnIndex ) {
            switch ( columnIndex ) {
                case 0 :
                    return rowIndex + 1;
                case 1 :
                    return quizState.getQuestionType( rowIndex + 1 );
                case 2 :
                    return quizState.isAnswered( rowIndex + 1 );
                case 3 :
                    return quizState.isMarked( rowIndex + 1 );
                case 4 :
                    boolean correct = quizState.isCorrect( rowIndex + 1 );
                    if ( correct ) {
                        if ( Icons.SMALL_TICK.isAvailable() )
                            return Icons.SMALL_TICK.getIcon();
                    } else {
                        if ( Icons.SMALL_CROSS.isAvailable() )
                            return Icons.SMALL_CROSS.getIcon();
                    }
                    return null;
                default:
                    throw new AssertionError( "Should not reach here" );
            }
        }

        @Override
        public boolean isCellEditable( int rowIndex, int columnIndex ) {
            return columnIndex == 3;
        }

        @Override
        public void setValueAt( Object aValue, int rowIndex, int columnIndex ) {
            if ( columnIndex == 3 ) {
                boolean marked = ( (Boolean) aValue );
                quizState.markQuestionForReview( rowIndex + 1, marked );
            }
        }
    }

    public static void main( String[] args ) {
        InquisitionMain.setupLookAndFeel();
        QuizConfig quizConfig = QuizConfig.createWithTimer( false, true, 30 );
        Collection<QuestionSet> bundledQuestionSets = QuestionSetManager.loadBundledQuestionSets();
        Iterator<QuestionSet> iterator = bundledQuestionSets.iterator();
        iterator.next();
        QuestionSet questionSet = iterator.next();
        QuizState quizState_ = new QuizState( questionSet, quizConfig );
        final ResultsDialog resultsDialog = new ResultsDialog( null, quizState_ );
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                resultsDialog.setDefaultCloseOperation( DISPOSE_ON_CLOSE );
                resultsDialog.setVisible( true );
            }
        } );
    }

}

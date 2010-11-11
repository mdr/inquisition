package uk.co.bytemark.vm.enigma.inquisition.gui.editor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import uk.co.bytemark.vm.enigma.inquisition.gui.screens.editor.AbstractDragAndDropQuestionEditorPanel;
import uk.co.bytemark.vm.enigma.inquisition.questions.DragAndDropQuestion;
import uk.co.bytemark.vm.enigma.inquisition.questions.DragAndDropRenderingHelper;

public class DragAndDropQuestionEditorPanel extends AbstractDragAndDropQuestionEditorPanel implements IQuestionEditorPanel {

    private final DefaultTableModel tableModel;
    private final ListSelectionModel selectionModel;

    public DragAndDropQuestionEditorPanel( DragAndDropQuestion question ) {
        questionTextArea.setText( question.getQuestionText() );
        questionTextArea.setCaretPosition( 0 );
        explanationTextArea.setText( question.getExplanationText() );
        explanationTextArea.setCaretPosition( 0 );
        reuseFragmentsCheckBox.setSelected( question.canReuseFragments() );
        tableModel = makeExtraFragmentsTableModel( question );
        extraFragmentsTable.setModel( tableModel );
        extraFragmentsTable.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
        selectionModel = extraFragmentsTable.getSelectionModel();
        previewQuestionButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                String questionTextHtml = DragAndDropRenderingHelper.getQuestionText( getQuestion(), false );
                launchPreviewDialog( questionTextHtml );
            }
        } );
        previewExplanationButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                String explanationTextHtml = DragAndDropRenderingHelper.getExplanationText( getQuestion() );
                launchPreviewDialog( explanationTextHtml );
            }
        } );
        deleteButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                int deleteIndex = selectionModel.getMinSelectionIndex();
                tableModel.removeRow( deleteIndex );
                selectNearestQuestion( deleteIndex );
            }
        } );

        addButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                tableModel.addRow( new Object[] { "Extra fragment", false } );

            }
        } );
        moveUpButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                moveSelectedItem( -1 );
            }

        } );
        moveDownButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                moveSelectedItem( 1 );
            }
        } );
        selectionModel.addListSelectionListener( new ListSelectionListener() {
            public void valueChanged( ListSelectionEvent e ) {
                int index = selectionModel.getMinSelectionIndex();
                if ( index >= 0 ) {
                    deleteButton.setEnabled( true );
                    moveUpButton.setEnabled( index != 0 );
                    moveDownButton.setEnabled( index != extraFragmentsTable.getRowCount() - 1 );
                } else {
                    deleteButton.setEnabled( false );
                    moveUpButton.setEnabled( false );
                    moveDownButton.setEnabled( false );
                }
            }
        } );
    }

    private void selectNearestQuestion( int index ) {
        if ( tableModel.getRowCount() > 0 ) {
            int newIndex = Math.max( 0, index - 1 );
            selectionModel.setSelectionInterval( newIndex, newIndex );
        }
    }

    private void moveSelectedItem( int offset ) {
        int index = selectionModel.getMinSelectionIndex();
        tableModel.moveRow( index, index, index + offset );
        selectionModel.setSelectionInterval( index + offset, index + offset );
    }

    private void launchPreviewDialog( String html ) {
        PreviewHtmlDialog dialog = new PreviewHtmlDialog( getParentDialog(), html );
        dialog.setSize( PreviewHtmlDialog.PREVIEW_HTML_DIALOG_DEFAULT_DIMENSION );
        dialog.setLocationRelativeTo( getParentDialog() );
        dialog.setVisible( true );
    }

    private JDialog getParentDialog() { // TODO: Panel shouldn't really know where it's placed
        return (JDialog) SwingUtilities.getAncestorOfClass( JDialog.class, this );
    }

    private DefaultTableModel makeExtraFragmentsTableModel( DragAndDropQuestion question ) {
        List<String> extraFragments = question.getExtraFragments();
        Object[][] tableRows = new Object[extraFragments.size()][];
        for ( int i = 0; i < extraFragments.size(); i++ )
            tableRows[i] = new Object[] { extraFragments.get( i ) };
        DefaultTableModel model = new DefaultTableModel( tableRows, new String[] { "Extra fragments" } ) {
            @Override
            public Class<?> getColumnClass( int columnIndex ) {
                return java.lang.String.class;
            }
        };
        return model;
    }

    public DragAndDropQuestion getQuestion() {
        List<String> extraFragments = getExtraFragments();
        return new DragAndDropQuestion( questionTextArea.getText(), explanationTextArea.getText(), extraFragments, reuseFragmentsCheckBox.isSelected() );
    }

    private List<String> getExtraFragments() {
        List<String> fragments = new ArrayList<String>();
        for ( int row = 0; row < tableModel.getRowCount(); row++ )
            fragments.add( (String) tableModel.getValueAt( row, 0 ) );
        return fragments;
    }

    public void setQuestionValidityListener( QuestionValidityListener listener ) {
        // Always valid
    }

}

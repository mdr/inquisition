/*
 * DragAndDropPanel.java
 * 
 * Created on 10 September 2006, 21:15
 */

package uk.co.bytemark.vm.enigma.inquisition.gui.quiz;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceAdapter;
import java.awt.dnd.DragSourceContext;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.dnd.InvalidDnDOperationException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.text.AttributeSet;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.StyleConstants;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.FormView;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import uk.co.bytemark.vm.enigma.inquisition.questions.Answer;
import uk.co.bytemark.vm.enigma.inquisition.questions.DragAndDropAnswer;
import uk.co.bytemark.vm.enigma.inquisition.questions.DragAndDropQuestion;
import uk.co.bytemark.vm.enigma.inquisition.questions.DragAndDropRenderingHelper;

/**
 * 
 * A {@link QuestionPanel} for handling {@link DragAndDropQuestion}s. A <tt>DragAndDropPanel</tt> allows a user to
 * drag fragments of text around on a document containing slots.
 */
public class DragAndDropPanel extends QuestionPanel {

    private static final Logger LOGGER = Logger.getLogger(DragAndDropPanel.class.getName());

    public static final Color CORRECT_COLOUR = new Color(0.9f, 1.0f, 0.9f);

    public static final Color INCORRECT_COLOUR = new Color(1.0f, 0.9f, 0.9f);

    public static final Color NORMAL_COLOUR = new Color(1.0f, 1.0f, 0.8f);

    private final DragAndDropQuestion question;

    private final QuizFrame quizFrame;

    private final TransparentPane transparentPane;

    private final DragAndDropTextFieldManager textFieldManager = new DragAndDropTextFieldManager();

    private final DragAndDropFragmentBinPanel fragmentPanel;

    private boolean dragsDisabled = false;

    private boolean inQuestionMode = true;

    private final AnswerChangedObserver answerChangedObserver;

    /** Creates new form DragAndDropPanel */
    public DragAndDropPanel(DragAndDropQuestion question, QuizFrame quizFrame,
            AnswerChangedObserver answerChangedObserver, TransparentPane transparentPane) {

        this.question = question;
        this.quizFrame = quizFrame;
        this.transparentPane = transparentPane;
        this.answerChangedObserver = answerChangedObserver;

        initComponents();

        textFieldManager.blankAllTextFields();

        // TODO: Fix cast
        this.fragmentPanel = (DragAndDropFragmentBinPanel) optionsPanel;

        // Make sure the question is scrolled to the top if necessary
        questionTextPane.setCaretPosition(0);

        // Generic drag-over listeners so that a dragged image moves over these
        // components
        new DropTarget(questionTextPane, new DragOnMeDropListener(questionTextPane));
        new DropTarget(fragmentPanel, new DragOnMeDropListener(fragmentPanel));

        // Recognize drags starting from the fragments bin
        DragSource dragSource = new DragSource();
        dragSource.createDefaultDragGestureRecognizer(fragmentPanel, DnDConstants.ACTION_COPY,
                new FragmentsBinDragGestureListener(dragSource));
    }

    /**
     * A listener to recognize drags originating from the fragments bin.
     */
    class FragmentsBinDragGestureListener implements DragGestureListener {
        private DragSource dragSource;

        FragmentsBinDragGestureListener(DragSource dragSource) {
            this.dragSource = dragSource;
        }

        public void dragGestureRecognized(DragGestureEvent event) {
            if (dragsDisabled)
                return;

            // Get a fragment, if there's one there
            String dragText = fragmentPanel.getFragmentAtPoint(event.getDragOrigin());
            if (dragText == null)
                return;

            // Put the fragment text as the transferable
            StringSelection transferable = new StringSelection(dragText);

            // Hide the fragment
            if (!question.canReuseFragments())
                fragmentPanel.hideFragment(dragText);

            // Set up the fragment as the dragged image
            BufferedImage image = fragmentPanel.getFragmentImage(dragText);
            if (image == null)
                return;

            // Activate the transparent pane and set it up with the appropriate dragged image
            Point point = (Point) event.getDragOrigin().clone();
            SwingUtilities.convertPointToScreen(point, fragmentPanel);
            SwingUtilities.convertPointFromScreen(point, transparentPane);
            transparentPane.activate(point, image);
            transparentPane.setVisible(true);

            // Start the drag and set up source responses to dragging
            DragSourceListener dsl = new FragmentBinDragSourceListener(dragText);
            dragSource.startDrag(event, DragSource.DefaultCopyDrop, transferable, dsl);
        }
    }

    /**
     * A drag source listener to handle in-progress drag events that have started from the fragment bin
     */
    class FragmentBinDragSourceListener extends DragSourceAdapter {

        private String fragment;

        FragmentBinDragSourceListener(String fragment) {
            this.fragment = fragment;
        }

        @Override
        public void dragDropEnd(DragSourceDropEvent dsde) {
            // The drag has ended, so we close down the transparentPane

            answerChangedObserver.answerChanged(getAnswer());
            if (dsde.getDropSuccess()) {
                transparentPane.setVisible(false);
            } else {
                // If was unsuccessful, then we
                // need to put the fragment back into the bin
                transparentPane.sendBackToOrigin(new TransparentPane.Callback() {
                    public void returnedToOrigin() {
                        fragmentPanel.showFragment(fragment);
                    }
                });
            }
        }

        // This avoids cursor flicker
        @Override
        public void dragOver(DragSourceDragEvent dsde) {
            DragSourceContext context = dsde.getDragSourceContext();
            context.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
    }

    /**
     * A drag listener that listens for dragOver events and redraws the dragged image as the user moves over the
     * component.
     */
    class DragOnMeDropListener extends DropTargetAdapter {
        private JComponent component;

        DragOnMeDropListener(JComponent component) {
            this.component = component;
        }

        public void drop(DropTargetDropEvent event) {
        // We don't care about drop events here (TODO: why not?)
        }

        @Override
        public void dragOver(DropTargetDragEvent event) {
            Point point = (Point) event.getLocation().clone();
            SwingUtilities.convertPointToScreen(point, component);
            SwingUtilities.convertPointFromScreen(point, transparentPane);
            transparentPane.setPosition(point);
            transparentPane.repaint();
        }
    }

    boolean dragsAreDisabled() {
        return dragsDisabled;
    }

    @Override
    public Answer getAnswer() {
        return textFieldManager.getAnswer();
    }

    @Override
    public void enterReviewMode() {
        if (!inQuestionMode)
            return;
        inQuestionMode = false;

        textFieldManager.colourTextFieldsAccordingToCorrectness();
        dragsDisabled = true;
        fragmentPanel.setActive(false);
    }

    @Override
    void enterQuestionMode() {
        if (inQuestionMode)
            return;
        inQuestionMode = true;

        textFieldManager.colourTextFieldsAccordingToWhetherTheyAreEmptyOrNot();
        dragsDisabled = false;
        fragmentPanel.setActive(true);
    }

    @Override
    public DragAndDropQuestion getQuestion() {
        return question;
    }

    @Override
    public String getExplanationText() {
        return DragAndDropRenderingHelper.getExplanationText(question);
    }

    /**
     * This method is called from within the constructor to initialise the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {

        optionsPanel = new DragAndDropFragmentBinPanel(question.getFragments());
        questionTextScrollPane = new javax.swing.JScrollPane();
        questionTextPane = new javax.swing.JTextPane();

        optionsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(DragAndDropRenderingHelper
                .optionsTitle(question)));

        org.jdesktop.layout.GroupLayout optionsPanelLayout = new org.jdesktop.layout.GroupLayout(optionsPanel);
        optionsPanel.setLayout(optionsPanelLayout);
        optionsPanelLayout.setHorizontalGroup(optionsPanelLayout.createParallelGroup(
                org.jdesktop.layout.GroupLayout.LEADING).add(0, 420, Short.MAX_VALUE));
        optionsPanelLayout.setVerticalGroup(optionsPanelLayout.createParallelGroup(
                org.jdesktop.layout.GroupLayout.LEADING).add(0, 271, Short.MAX_VALUE));

        questionTextPane.setContentType("text/html");
        questionTextPane.setEditable(false);
        questionTextPane.setEditorKit(new DragAndDropSlotHTMLEditorKit());

        questionTextPane.setText(DragAndDropRenderingHelper.getQuestionText(question, true));
        questionTextScrollPane.setViewportView(questionTextPane);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                questionTextScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE).add(
                optionsPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
        layout.setVerticalGroup(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                layout.createSequentialGroup().add(questionTextScrollPane,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 296, Short.MAX_VALUE).addPreferredGap(
                        org.jdesktop.layout.LayoutStyle.RELATED).add(optionsPanel,
                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)));
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel optionsPanel;

    private javax.swing.JTextPane questionTextPane;

    private javax.swing.JScrollPane questionTextScrollPane;

    // End of variables declaration//GEN-END:variables

    /**
     * An HTMLEditorKit that customises &lt;INPUT&gt; creation for a <tt>JTextField</tt> loaded with HTML.
     */
    public class DragAndDropSlotHTMLEditorKit extends HTMLEditorKit {

        private class MyHTMLFactory extends HTMLFactory {

            @Override
            public View create(Element element) {
                AttributeSet attributes = element.getAttributes();

                if (attributes.getAttribute(StyleConstants.NameAttribute) == HTML.Tag.INPUT) {
                    return new DragAndDropSlotFormView(element);
                } else {
                    return super.create(element);
                }
            }
        }

        @Override
        public Document createDefaultDocument() {
            return new HTMLDocument(getStyleSheet());
        }

        @Override
        public ViewFactory getViewFactory() {
            return new MyHTMLFactory();
        }

    }

    /**
     * A class that lets us intercept the creation of JTextfields created to render HTML INPUT tags, so that we can
     * adapt them as drag and drop slots; namely, make them non-editable, colour them, and make them respond to drag
     * events.
     */
    private class DragAndDropSlotFormView extends FormView {

        public DragAndDropSlotFormView(Element elem) {
            super(elem);
        }

        @Override
        protected Component createComponent() {
            Component component = super.createComponent();
            AttributeSet attributes = getElement().getAttributes();
            HTML.Tag htmlTag = (HTML.Tag) attributes.getAttribute(StyleConstants.NameAttribute);
            if (htmlTag == HTML.Tag.INPUT) {
                int id = Integer.parseInt((String) attributes.getAttribute(HTML.Attribute.ID));
                String slotText = (String) attributes.getAttribute(HTML.Attribute.VALUE);
                JTextField textField = (JTextField) component;

                textField.setBackground(NORMAL_COLOUR);
                textField.setEditable(false);
                // int oldSize = tf.getFont().getSize();
                // tf.setFont(new Font("Monospaced", Font.PLAIN, oldSize));
                textField.setFont(new Font("Monospaced", Font.PLAIN, 12));
                // System.out.println(elem.getStartOffset() + ", " + elemText);
                textFieldManager.addTextField(id, slotText, textField);

                // Set up incoming drags
                new DropTarget(textField, new TextFieldDropTargetListener(textField));

                // Recognise new drags that start from this textField
                DragSource dragSource = new DragSource();
                dragSource.createDefaultDragGestureRecognizer(textField, DnDConstants.ACTION_MOVE,
                        new TextFieldDragGestureRecognizer(textField, dragSource));
            }
            return component;

        }

    }

    /**
     * Handles drops and drag-overs/exits on a text field.
     */
    private class TextFieldDropTargetListener implements DropTargetListener {
        private JTextField textField;

        private String swappedOutText = "";

        TextFieldDropTargetListener(JTextField textField) {
            this.textField = textField;
        }

        /**
         * On entering, temporarily swaps out the current text and puts in the drag fragment to show how this fragment
         * would look.
         */
        public void dragEnter(DropTargetDragEvent event) {
            swappedOutText = textField.getText();
            String dragText = getStringFromTransferable(event.getTransferable());
            if (dragText != null) {
                textField.setText(dragText);
                textField.setBackground(DragAndDropFragmentBinPanel.FRAGMENT_FILL_COLOUR);
            }
        }

        /**
         * Restores previous text (or blankness).
         */
        public void dragExit(DropTargetEvent event) {
            textField.setText(swappedOutText);
            if (swappedOutText.equals("")) {
                textField.setBackground(NORMAL_COLOUR);
            }
            swappedOutText = "";
            quizFrame.setGlassPaneVisible(true);
        }

        /**
         * Puts the fragment in this slot, and bumps any fragment already there back to the bin.
         */
        public void drop(final DropTargetDropEvent event) {
            try {
                String dragText = getStringFromTransferable(event.getTransferable());
                if (dragText == null) {
                    event.rejectDrop();
                } else {
                    textField.setText(dragText);

                    if (!swappedOutText.equals("")) {
                        // put the swapped-out fragment back into the bin
                        Point originPoint = new Point(fragmentPanel.getWidth() / 2, fragmentPanel.getHeight() / 2);
                        SwingUtilities.convertPointToScreen(originPoint, fragmentPanel);
                        SwingUtilities.convertPointFromScreen(originPoint, transparentPane);
                        transparentPane.setVisible(true);
                        transparentPane.sendBackToOrigin(new TransparentPane.Callback() {
                            public void returnedToOrigin() {
                                fragmentPanel.showFragment(swappedOutText);
                            }
                        }, originPoint, fragmentPanel.getFragmentImage(swappedOutText));

                    }
                    event.dropComplete(true);
                    answerChangedObserver.answerChanged(getAnswer());
                }

            } catch (InvalidDnDOperationException e) {
                LOGGER.log(Level.WARNING, "Drag and drop problem", e);
                event.rejectDrop();
            }
        }

        /**
         * When dragging over this textField, disable the transparent drag pane, because we are rendering the fragment
         * as "snapped" into the textfield.
         */
        public void dragOver(DropTargetDragEvent dtde) {
            quizFrame.setGlassPaneVisible(false);
        }

        public void dropActionChanged(DropTargetDragEvent dtde) {
        // TODO: What's this, then?
        }

        private String getStringFromTransferable(Transferable tr) {
            DataFlavor[] flavors = tr.getTransferDataFlavors();
            if (flavors.length >= 1 && flavors[0].isFlavorTextType()) {
                try {
                    return (String) tr.getTransferData(flavors[0]);
                } catch (IOException e) {
                    LOGGER.log(Level.WARNING, "IO problem", e);
                    return null;
                } catch (UnsupportedFlavorException e) {
                    LOGGER.log(Level.WARNING, "Weird drag flavour problem", e);
                    return null;
                }
            } else {
                return null;
            }
        }

    }

    /**
     * A recognizer for drags initiating from a text field.
     */
    private class TextFieldDragGestureRecognizer implements DragGestureListener {
        JTextField textField;

        DragSource dragSource;

        TextFieldDragGestureRecognizer(JTextField textField, DragSource dragSource) {
            this.textField = textField;
            this.dragSource = dragSource;
        }

        public void dragGestureRecognized(DragGestureEvent event) {
            if (dragsDisabled)
                return;

            final String dragText = textField.getText();
            // Don't initiate any drag if there's nothing in the slot
            if (dragText.equals(""))
                return;

            // If there's a large mouse move, then it seems that DropTargetListener
            // dragExit() never gets fired on the textField, so we do what gets done in dragExit here just in
            // case. However, if there's a small mouse move after this, a "dragEnter" gets
            // fired on the textField, restoring the text and setting the background. The visual effect
            // is of a momentary flicker. Not sure how to get around this.
            textField.setText("");
            textField.setBackground(NORMAL_COLOUR);
            // testQuestionFrame.answerChanged();

            StringSelection transferable = new StringSelection(dragText);

            // Set up the fragment as the dragged ghost image
            BufferedImage image = fragmentPanel.getFragmentImage(dragText);
            assert (image != null);

            // Activate the transparent pane and set it up with the appropriate dragged
            // image
            Point point = (Point) event.getDragOrigin().clone();
            SwingUtilities.convertPointToScreen(point, textField);
            SwingUtilities.convertPointFromScreen(point, transparentPane);
            transparentPane.activate(point, image);

            // TODO: merge this with DragAndDropPanel.FragmentBinDragSourceListener
            dragSource.startDrag(event, DragSource.DefaultMoveDrop, transferable, new DragSourceAdapter() {
                // Stops cursor flicker
                @Override
                public void dragOver(DragSourceDragEvent dragSourceDragEvent) {
                    DragSourceContext context = dragSourceDragEvent.getDragSourceContext();
                    context.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }

                @Override
                public void dragDropEnd(DragSourceDropEvent dragSourceDropEvent) {
                    answerChangedObserver.answerChanged(getAnswer());
                    if (dragSourceDropEvent.getDropSuccess()) {
                        transparentPane.setVisible(false);
                    } else {
                        // If was unsuccessful, then we need to put the fragment back into the bin
                        Point originPoint = new Point(fragmentPanel.getWidth() / 2, fragmentPanel.getHeight() / 2);
                        SwingUtilities.convertPointToScreen(originPoint, fragmentPanel);
                        SwingUtilities.convertPointFromScreen(originPoint, transparentPane);
                        transparentPane.sendBackToOrigin(new TransparentPane.Callback() {
                            public void returnedToOrigin() {
                                fragmentPanel.showFragment(dragText);
                            }
                        }, originPoint);
                    }
                }
            });
        }
    }

    @Override
    public void setAnswer(Answer generalAnswer) {
        DragAndDropAnswer answer = (DragAndDropAnswer) generalAnswer;
        List<String> slotAnswers = answer.getSlotAnswers();
        if (!question.canReuseFragments()) {
            for (String fragment : question.getFragments()) {
                if (slotAnswers.contains(fragment)) {
                    fragmentPanel.hideFragment(fragment);
                } else {  
                    fragmentPanel.showFragment(fragment);
                }    
            }
        }
        textFieldManager.setFields(slotAnswers);
        if (inQuestionMode) {
            textFieldManager.colourTextFieldsAccordingToWhetherTheyAreEmptyOrNot();
        } else {
            textFieldManager.colourTextFieldsAccordingToCorrectness();

        }

    }

}

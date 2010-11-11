///*
// * TestQuestionFrame.java
// * 
// * Created on 27 August 2006, 12:12
// */
//package uk.co.bytemark.vm.enigma.inquisition.gui.quiz;
//
//import java.awt.CardLayout;
//import java.awt.Dimension;
//import java.awt.Rectangle;
//import java.awt.Toolkit;
//import java.awt.event.ActionEvent;
//import java.awt.event.ComponentAdapter;
//import java.awt.event.ComponentEvent;
//import java.awt.event.WindowEvent;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//import java.util.logging.Logger;
//
//import javax.swing.DefaultComboBoxModel;
//import javax.swing.JDialog;
//import javax.swing.JFrame;
//
//import uk.co.bytemark.vm.enigma.inquisition.gui.editor.AboutDialog;
//import uk.co.bytemark.vm.enigma.inquisition.gui.misc.QuizConfig;
//import uk.co.bytemark.vm.enigma.inquisition.gui.quizchooser.ReturnCallback;
//import uk.co.bytemark.vm.enigma.inquisition.images.Icons;
//import uk.co.bytemark.vm.enigma.inquisition.questions.DragAndDropQuestion;
//import uk.co.bytemark.vm.enigma.inquisition.questions.MultipleChoiceQuestion;
//import uk.co.bytemark.vm.enigma.inquisition.questions.Question;
//import uk.co.bytemark.vm.enigma.inquisition.questions.QuestionSet;
//
///**
// * A GUI window for presenting a series of questions to the user; a container for {@link QuestionPanel}s.
// * 
// * Each question is provided with its own <tt>QuestionPanel</tt> which presents appropriate GUI for the question type
// * (e.g., Multiple choice, or drag-and-drop), each a subclass of <tt>QuestionPanel<tt>.
// * The <tt>TestQuestionFrame</tt> provides the ability to navigate amongst the
// * questions, gives an indication of how many have been answered and an optional timer.
// *
// * Further, the <tt>TestQuestionFrame</tt> can be in either question mode or review
// * mode. In question mode, the user can provide and modify answers to questions. In review mode,
// * the user gets feedback on his answers, but is unable to modify it.
// * In review mode, an explanation panel is shown, with an explanation of the
// * reasoning behind the right answer and an indication of whether the user
// * gave the right answer. The <tt>QuestionPanel</tt> may also give special feedback
// * on the answer when in review mode.
// *
// */
//public class TestQuestionFrame extends JFrame implements AnswerChangedObserver {
//
//    private static final Logger LOGGER = Logger.getLogger(TestQuestionFrame.class.getName());
//
//    private QuizConfig config;
//
//    private List<Question> questions = new ArrayList<Question>();
//
//    private int numberOfQuestions;
//
//    private List<QuestionPanel> questionPanels;
//
//    private CardLayout questionPanelLayout;
//
//    private int questionNumber = -1;
//
//    TransparentPane glassPane;
//
//    private TestTimerThread testTimerThread;
//
//    private ResultsPanel resultsPanel;
//
//    private ReturnCallback returnCallback;
//
//    private static final String CONTINUE_TEXT = "Continue";
//
//    private static final String PAUSE_TEXT = "Pause";
//
//    /**
//     * Creates new TestQuestionFrame.
//     * 
//     * @param questionSet
//     *            the set of {@link Question}s to be tested.
//     * @param config
//     *            the questions and configuration options to be used.
//     * @param returnCallback
//     */
//    public TestQuestionFrame(QuestionSet questionSet, QuizConfig config, ReturnCallback returnCallback) {
//        this.config = config;
//        questions.addAll(questionSet.getQuestions()); // Copy in questions
//        this.returnCallback = returnCallback;
//
//        // Shuffle questions if specified in the config options
//        if (config.shouldShuffleQuestionOrder())
//            Collections.shuffle(questions);
//
//        // IDE generated GUI init
//        initComponents();
//
//        // Add a glass pane to support transparent D&D
//        glassPane = new TransparentPane();
//        setGlassPane(glassPane);
//
//        // Add a favicon thingy
//        if (Icons.FAVICON.isAvailable())
//            setIconImage(Icons.FAVICON.getImage());
//
//        // Respond dynamically to window resizes
//        Toolkit.getDefaultToolkit().setDynamicLayout(true);
//        numberOfQuestions = questions.size();
//        questionPanels = new ArrayList<QuestionPanel>();
//
//        // QuestionPanel panel;
//        for (int i = 0; i < numberOfQuestions; i++)
//            questionPanels.add(null); // Create these lazily
//        this.questionPanelLayout = (CardLayout) questionPanelHolderPanel.getLayout();
//        questionPanelHolderPanel.requestFocus();
//
//        // Set-up the combobox
//        String[] questionNumbers = new String[numberOfQuestions];
//        for (int i = 0; i < numberOfQuestions; i++) {
//            questionNumbers[i] = Integer.toString(i + 1); // User sees questions numbered starting
//            // from 1
//        }
//        navigateToQuestionComboBox.setModel(new DefaultComboBoxModel(questionNumbers));
//        // Go to the first question
//        changeToQuestion(0);
//        // Initial setup
//        answerChanged();
//        updateMarkedForReview();
//        hideExplanationPanel();
//        // Intercept resizes to enforce a minimum window size
//        this.addComponentListener(new ResizeListener());
//        // Start the timer
//        if (config.isQuizTimed()) {
//            testTimerThread = new TestTimerThread(timeRemainingTextField, pauseButton, config.getTimeAllowed());
//            Thread t = new Thread(testTimerThread);
//            t.start();
//        } else {
//            timeRemainingTextField.setVisible(false);
//            pauseButton.setVisible(false);
//            timeRemainingLabel.setVisible(false);
//        }
//        findNextIncorrectAnswerButton.setVisible(false);
//        pack();
//    }
//
//    /**
//     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
//     * content of this method is always regenerated by the Form Editor.
//     */
//    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
//    private void initComponents() {
//
//        progressAndAdvancedNavLayoutPanel = new javax.swing.JPanel();
//        advancedNavigationPanel = new javax.swing.JPanel();
//        jLabel5 = new javax.swing.JLabel();
//        navigateToQuestionComboBox = new javax.swing.JComboBox();
//        markedForReviewBox = new javax.swing.JCheckBox();
//        findNextMarkedButton = new javax.swing.JButton();
//        findNextUnansweredButton = new javax.swing.JButton();
//        progressPanel = new javax.swing.JPanel();
//        timeRemainingLabel = new javax.swing.JLabel();
//        timeRemainingTextField = new javax.swing.JTextField();
//        pauseButton = new javax.swing.JButton();
//        progressBar = new javax.swing.JProgressBar();
//        jLabel1 = new javax.swing.JLabel();
//        overviewButton = new javax.swing.JButton();
//        checkAnswerButton = new javax.swing.JButton();
//        splitPane = new javax.swing.JSplitPane();
//        questionPanelHolderPanel = new javax.swing.JPanel();
//        explanationPanel = new javax.swing.JPanel();
//        rightOrWrongLabel = new javax.swing.JLabel();
//        jScrollPane2 = new javax.swing.JScrollPane();
//        explanationTextPane = new javax.swing.JTextPane();
//        pinExplanationPanelBox = new javax.swing.JCheckBox();
//        findNextIncorrectAnswerButton = new javax.swing.JButton();
//        navigationButtonsPanel = new javax.swing.JPanel();
//        jPanel6 = new javax.swing.JPanel();
//        firstQuestionButton = new javax.swing.JButton();
//        previousQuestionButton = new javax.swing.JButton();
//        nextQuestionButton = new javax.swing.JButton();
//        lastQuestionButton = new javax.swing.JButton();
//        testMenuBar = new javax.swing.JMenuBar();
//        jMenu1 = new javax.swing.JMenu();
//        exitMenuItem = new javax.swing.JMenuItem();
//        jMenu2 = new javax.swing.JMenu();
//        aboutMenuItem = new javax.swing.JMenuItem();
//
//        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
//        setTitle("Java.Inquisition");
//        addWindowListener(new java.awt.event.WindowAdapter() {
//            @Override
//            public void windowClosing(java.awt.event.WindowEvent evt) {
//                windowClosingHandler(evt);
//            }
//        });
//
//        advancedNavigationPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Advanced navigation"));
//
//        jLabel5.setText("Question:");
//
//        navigateToQuestionComboBox.addActionListener(new java.awt.event.ActionListener() {
//            public void actionPerformed(java.awt.event.ActionEvent evt) {
//                navigateToQuestionComboBoxActionPerformed(evt);
//            }
//        });
//
//        markedForReviewBox.setText("Mark for review?");
//        markedForReviewBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
//        markedForReviewBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
//        markedForReviewBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
//        markedForReviewBox.addActionListener(new java.awt.event.ActionListener() {
//            public void actionPerformed(java.awt.event.ActionEvent evt) {
//                markedForReviewBoxActionPerformed(evt);
//            }
//        });
//
//        findNextMarkedButton.setText("Find next marked");
//        findNextMarkedButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
//        findNextMarkedButton.addActionListener(new java.awt.event.ActionListener() {
//            public void actionPerformed(java.awt.event.ActionEvent evt) {
//                findNextMarkedButtonActionPerformed(evt);
//            }
//        });
//
//        findNextUnansweredButton.setText("Find next unanswered");
//        findNextUnansweredButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
//        findNextUnansweredButton.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
//        findNextUnansweredButton.addActionListener(new java.awt.event.ActionListener() {
//            public void actionPerformed(java.awt.event.ActionEvent evt) {
//                findNextUnansweredButtonActionPerformed(evt);
//            }
//        });
//
//        org.jdesktop.layout.GroupLayout advancedNavigationPanelLayout = new org.jdesktop.layout.GroupLayout(
//                advancedNavigationPanel);
//        advancedNavigationPanel.setLayout(advancedNavigationPanelLayout);
//        advancedNavigationPanelLayout.setHorizontalGroup(advancedNavigationPanelLayout.createParallelGroup(
//                org.jdesktop.layout.GroupLayout.LEADING).add(
//                advancedNavigationPanelLayout.createSequentialGroup().addContainerGap().add(
//                        advancedNavigationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
//                                markedForReviewBox).add(
//                                advancedNavigationPanelLayout.createSequentialGroup().add(jLabel5).addPreferredGap(
//                                        org.jdesktop.layout.LayoutStyle.RELATED).add(navigateToQuestionComboBox,
//                                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 59,
//                                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))).addPreferredGap(
//                        org.jdesktop.layout.LayoutStyle.RELATED).add(
//                        advancedNavigationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING,
//                                false).add(findNextMarkedButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
//                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).add(
//                                findNextUnansweredButton)).addContainerGap(
//                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
//        advancedNavigationPanelLayout.setVerticalGroup(advancedNavigationPanelLayout.createParallelGroup(
//                org.jdesktop.layout.GroupLayout.LEADING).add(
//                advancedNavigationPanelLayout.createSequentialGroup().add(
//                        advancedNavigationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
//                                .add(jLabel5).add(navigateToQuestionComboBox,
//                                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
//                                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
//                                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).add(findNextUnansweredButton))
//                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).add(
//                                advancedNavigationPanelLayout.createParallelGroup(
//                                        org.jdesktop.layout.GroupLayout.BASELINE).add(markedForReviewBox).add(
//                                        findNextMarkedButton)).addContainerGap(
//                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
//
//        progressPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Progress"));
//
//        timeRemainingLabel.setText("Time remaining:");
//
//        timeRemainingTextField.setEditable(false);
//
//        pauseButton.setText(PAUSE_TEXT);
//        pauseButton.addActionListener(new java.awt.event.ActionListener() {
//            public void actionPerformed(java.awt.event.ActionEvent evt) {
//                pauseButtonActionPerformed(evt);
//            }
//        });
//
//        progressBar.setStringPainted(true);
//
//        jLabel1.setText("Answered:");
//
//        org.jdesktop.layout.GroupLayout progressPanelLayout = new org.jdesktop.layout.GroupLayout(progressPanel);
//        progressPanel.setLayout(progressPanelLayout);
//        progressPanelLayout.setHorizontalGroup(progressPanelLayout.createParallelGroup(
//                org.jdesktop.layout.GroupLayout.LEADING).add(
//                org.jdesktop.layout.GroupLayout.TRAILING,
//                progressPanelLayout.createSequentialGroup().addContainerGap().add(
//                        progressPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING).add(
//                                timeRemainingLabel).add(jLabel1)).addPreferredGap(
//                        org.jdesktop.layout.LayoutStyle.RELATED).add(
//                        progressPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
//                                progressPanelLayout.createSequentialGroup().add(timeRemainingTextField,
//                                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 76,
//                                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addPreferredGap(
//                                        org.jdesktop.layout.LayoutStyle.RELATED).add(pauseButton,
//                                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)).add(
//                                progressBar, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 202, Short.MAX_VALUE))
//                        .addContainerGap()));
//        progressPanelLayout.setVerticalGroup(progressPanelLayout.createParallelGroup(
//                org.jdesktop.layout.GroupLayout.LEADING).add(
//                progressPanelLayout.createSequentialGroup().add(
//                        progressPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE).add(
//                                timeRemainingLabel).add(timeRemainingTextField,
//                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
//                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
//                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).add(pauseButton)).addPreferredGap(
//                        org.jdesktop.layout.LayoutStyle.RELATED).add(
//                        progressPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE).add(jLabel1,
//                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14,
//                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).add(progressBar,
//                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
//                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
//                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)).addContainerGap(17, Short.MAX_VALUE)));
//
//        org.jdesktop.layout.GroupLayout progressAndAdvancedNavLayoutPanelLayout = new org.jdesktop.layout.GroupLayout(
//                progressAndAdvancedNavLayoutPanel);
//        progressAndAdvancedNavLayoutPanel.setLayout(progressAndAdvancedNavLayoutPanelLayout);
//        progressAndAdvancedNavLayoutPanelLayout.setHorizontalGroup(progressAndAdvancedNavLayoutPanelLayout
//                .createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
//                        org.jdesktop.layout.GroupLayout.TRAILING,
//                        progressAndAdvancedNavLayoutPanelLayout.createSequentialGroup().addContainerGap().add(
//                                advancedNavigationPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
//                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
//                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addPreferredGap(
//                                org.jdesktop.layout.LayoutStyle.RELATED, 279, Short.MAX_VALUE).add(progressPanel,
//                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
//                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
//                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addContainerGap()));
//        progressAndAdvancedNavLayoutPanelLayout.setVerticalGroup(progressAndAdvancedNavLayoutPanelLayout
//                .createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(progressPanel,
//                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
//                        Short.MAX_VALUE).add(advancedNavigationPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
//                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
//
//        overviewButton.setText("Score test");
//        overviewButton.addActionListener(new java.awt.event.ActionListener() {
//            public void actionPerformed(java.awt.event.ActionEvent evt) {
//                overviewButtonActionPerformed(evt);
//            }
//        });
//
//        checkAnswerButton.setText("Show answer and explanation");
//        checkAnswerButton.addActionListener(new java.awt.event.ActionListener() {
//            public void actionPerformed(java.awt.event.ActionEvent evt) {
//                checkAnswerButtonActionPerformed(evt);
//            }
//        });
//
//        splitPane.setDividerLocation(500);
//        splitPane.setDividerSize(0);
//        splitPane.setResizeWeight(0.5);
//        splitPane.setContinuousLayout(true);
//
//        questionPanelHolderPanel.addComponentListener(new java.awt.event.ComponentAdapter() {
//            @Override
//            public void componentResized(java.awt.event.ComponentEvent evt) {
//                componentResizedHandler(evt);
//            }
//        });
//        questionPanelHolderPanel.setLayout(new java.awt.CardLayout());
//        splitPane.setTopComponent(questionPanelHolderPanel);
//
//        explanationPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Answer and explanation"));
//
//        rightOrWrongLabel.setText("Correct");
//
//        explanationTextPane.setContentType("text/html");
//        explanationTextPane.setEditable(false);
//        explanationTextPane.setEditorKit(new ExplanationPanelHTMLEditorKit());
//        jScrollPane2.setViewportView(explanationTextPane);
//
//        pinExplanationPanelBox.setText(" Keep explanation open");
//        pinExplanationPanelBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
//        pinExplanationPanelBox.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
//        pinExplanationPanelBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
//        pinExplanationPanelBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
//        pinExplanationPanelBox.addActionListener(new java.awt.event.ActionListener() {
//            public void actionPerformed(java.awt.event.ActionEvent evt) {
//                pinExplanationPanelBoxActionPerformed(evt);
//            }
//        });
//
//        findNextIncorrectAnswerButton.setText("Find next incorrect answer");
//        findNextIncorrectAnswerButton.addActionListener(new java.awt.event.ActionListener() {
//            public void actionPerformed(java.awt.event.ActionEvent evt) {
//                findNextIncorrectAnswerButtonActionPerformed(evt);
//            }
//        });
//
//        org.jdesktop.layout.GroupLayout explanationPanelLayout = new org.jdesktop.layout.GroupLayout(explanationPanel);
//        explanationPanel.setLayout(explanationPanelLayout);
//        explanationPanelLayout.setHorizontalGroup(explanationPanelLayout.createParallelGroup(
//                org.jdesktop.layout.GroupLayout.LEADING).add(
//                explanationPanelLayout.createSequentialGroup().add(rightOrWrongLabel,
//                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 177,
//                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addContainerGap(283, Short.MAX_VALUE)).add(
//                jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 460, Short.MAX_VALUE).add(
//                explanationPanelLayout.createSequentialGroup().addContainerGap().add(pinExplanationPanelBox)
//                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 85, Short.MAX_VALUE).add(
//                                findNextIncorrectAnswerButton)));
//        explanationPanelLayout.setVerticalGroup(explanationPanelLayout.createParallelGroup(
//                org.jdesktop.layout.GroupLayout.LEADING).add(
//                explanationPanelLayout.createSequentialGroup().add(rightOrWrongLabel,
//                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 36,
//                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addPreferredGap(
//                        org.jdesktop.layout.LayoutStyle.RELATED).add(jScrollPane2,
//                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 412, Short.MAX_VALUE).addPreferredGap(
//                        org.jdesktop.layout.LayoutStyle.RELATED).add(
//                        explanationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE).add(
//                                pinExplanationPanelBox).add(findNextIncorrectAnswerButton))));
//
//        splitPane.setRightComponent(explanationPanel);
//
//        navigationButtonsPanel.setLayout(new java.awt.GridBagLayout());
//
//        if (Icons.FIRST.isAvailable())
//            firstQuestionButton.setIcon(Icons.FIRST.getIcon());
//        firstQuestionButton.setText("First");
//        firstQuestionButton.setFocusable(false);
//        firstQuestionButton.addActionListener(new java.awt.event.ActionListener() {
//            public void actionPerformed(java.awt.event.ActionEvent evt) {
//                firstQuestionButtonActionPerformed(evt);
//            }
//        });
//
//        if (Icons.BACK.isAvailable())
//            previousQuestionButton.setIcon(Icons.BACK.getIcon());
//        previousQuestionButton.setFocusable(false);
//        previousQuestionButton.setText("Previous");
//        previousQuestionButton.setFocusable(false);
//        previousQuestionButton.addActionListener(new java.awt.event.ActionListener() {
//            public void actionPerformed(java.awt.event.ActionEvent evt) {
//                previousQuestionButtonActionPerformed(evt);
//            }
//        });
//        if (Icons.FORWARD.isAvailable())
//            nextQuestionButton.setIcon(Icons.FORWARD.getIcon());
//        nextQuestionButton.setText("Next");
//        nextQuestionButton.setFocusable(false);
//        nextQuestionButton.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
//        nextQuestionButton.addActionListener(new java.awt.event.ActionListener() {
//            public void actionPerformed(java.awt.event.ActionEvent evt) {
//                nextQuestionButtonActionPerformed(evt);
//            }
//        });
//
//        if (Icons.LAST.isAvailable())
//            lastQuestionButton.setIcon(Icons.LAST.getIcon());
//        lastQuestionButton.setText("Last");
//        lastQuestionButton.setFocusable(false);
//        lastQuestionButton.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
//        lastQuestionButton.addActionListener(new java.awt.event.ActionListener() {
//            public void actionPerformed(java.awt.event.ActionEvent evt) {
//                lastQuestionButtonActionPerformed(evt);
//            }
//        });
//
//        org.jdesktop.layout.GroupLayout jPanel6Layout = new org.jdesktop.layout.GroupLayout(jPanel6);
//        jPanel6.setLayout(jPanel6Layout);
//        jPanel6Layout.setHorizontalGroup(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
//                .add(
//                        jPanel6Layout.createSequentialGroup().addContainerGap().add(firstQuestionButton,
//                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 118,
//                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addPreferredGap(
//                                org.jdesktop.layout.LayoutStyle.RELATED).add(previousQuestionButton,
//                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 98,
//                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addPreferredGap(
//                                org.jdesktop.layout.LayoutStyle.RELATED).add(nextQuestionButton,
//                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 98,
//                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addPreferredGap(
//                                org.jdesktop.layout.LayoutStyle.RELATED).add(lastQuestionButton,
//                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 110,
//                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addContainerGap()));
//
//        jPanel6Layout.linkSize(new java.awt.Component[] { firstQuestionButton, lastQuestionButton, nextQuestionButton,
//                previousQuestionButton }, org.jdesktop.layout.GroupLayout.HORIZONTAL);
//
//        jPanel6Layout.setVerticalGroup(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
//                jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE).add(nextQuestionButton,
//                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 23,
//                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).add(previousQuestionButton).add(
//                        lastQuestionButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 23,
//                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).add(firstQuestionButton)));
//
//        jPanel6Layout.linkSize(new java.awt.Component[] { firstQuestionButton, lastQuestionButton, nextQuestionButton,
//                previousQuestionButton }, org.jdesktop.layout.GroupLayout.VERTICAL);
//
//        navigationButtonsPanel.add(jPanel6, new java.awt.GridBagConstraints());
//
//        jMenu1.setText("Test");
//
//        exitMenuItem.setText("Quit this test");
//        exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
//            public void actionPerformed(java.awt.event.ActionEvent evt) {
//                exitMenuItemActionPerformed(evt);
//            }
//        });
//        jMenu1.add(exitMenuItem);
//
//        testMenuBar.add(jMenu1);
//
//        jMenu2.setText("Help");
//
//        aboutMenuItem.setText("About");
//        aboutMenuItem.addActionListener(new java.awt.event.ActionListener() {
//            public void actionPerformed(java.awt.event.ActionEvent evt) {
//                aboutMenuItemActionPerformed(evt);
//            }
//        });
//        jMenu2.add(aboutMenuItem);
//
//        testMenuBar.add(jMenu2);
//
//        setJMenuBar(testMenuBar);
//
//        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
//        getContentPane().setLayout(layout);
//        layout.setHorizontalGroup(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
//                layout.createSequentialGroup().addContainerGap().add(navigationButtonsPanel,
//                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 971, Short.MAX_VALUE).addContainerGap()).add(
//                progressAndAdvancedNavLayoutPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
//                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).add(
//                layout.createSequentialGroup().addContainerGap().add(
//                        layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
//                                org.jdesktop.layout.GroupLayout.TRAILING, splitPane,
//                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 971, Short.MAX_VALUE).add(
//                                layout.createSequentialGroup().add(overviewButton).addPreferredGap(
//                                        org.jdesktop.layout.LayoutStyle.RELATED, 655, Short.MAX_VALUE).add(
//                                        checkAnswerButton))).addContainerGap()));
//        layout.setVerticalGroup(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
//                layout.createSequentialGroup().add(progressAndAdvancedNavLayoutPanel,
//                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
//                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addPreferredGap(
//                        org.jdesktop.layout.LayoutStyle.RELATED).add(splitPane,
//                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 511, Short.MAX_VALUE).addPreferredGap(
//                        org.jdesktop.layout.LayoutStyle.RELATED).add(
//                        layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE).add(checkAnswerButton)
//                                .add(overviewButton)).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).add(
//                        navigationButtonsPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
//                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
//                        .addContainerGap()));
//
//        pack();
//    }// </editor-fold>//GEN-END:initComponents
//
//    private void componentResizedHandler(java.awt.event.ComponentEvent evt) {// GEN-FIRST:event_componentResizedHandler
//        fixLayout();
//    }// GEN-LAST:event_componentResizedHandler
//
//    private void findNextIncorrectAnswerButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_findNextIncorrectAnswerButtonActionPerformed
//        int number = (questionNumber + 1) % numberOfQuestions;
//        while (number != questionNumber) {
//            if (!questionPanels.get(number).getAnswer().isCorrect()) {
//                changeToQuestion(number);
//                return;
//            }
//            number = (number + 1) % numberOfQuestions;
//        }
//        // If we cannot find any, we will end up where we started.
//        // (We will never test the start position)
//    }// GEN-LAST:event_findNextIncorrectAnswerButtonActionPerformed
//
//    private void pinExplanationPanelBoxActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_pinExplanationPanelBoxActionPerformed
//        updateAppearanceOfNextIncorrectButton();
//    }// GEN-LAST:event_pinExplanationPanelBoxActionPerformed
//
//    // <editor-fold defaultstate="collapsed" desc="Callbacks managed by the GUI editor">
//    private void windowClosingHandler(WindowEvent evt) {// GEN-FIRST:event_windowClosingHandler
//        returnCallback.doReturn();
//    }// GEN-LAST:event_windowClosingHandler
//
//    private void overviewButtonActionPerformed(ActionEvent evt) {// GEN-FIRST:event_overviewButtonActionPerformed
//        // Create and set-up the results dialog
//        JDialog resultsDialog = new JDialog(this, "Results", true);
//        // Count the number of correct questions
//        int correctCount = 0;
//        for (QuestionPanel panel : questionPanels)
//            if (panel != null && panel.getAnswer().isCorrect())
//                correctCount++;
//        resultsPanel = new ResultsPanel(resultsDialog, this, new QuestionTableModel(questionPanels, questions));
//        resultsDialog.add(resultsPanel);
//        resultsPanel.update(questionNumber, correctCount, numberOfQuestions);
//        resultsDialog.pack();
//        resultsDialog.setLocationRelativeTo(this);
//        // Enter review mode if necessary
//        if (!explanationPanel.isVisible())
//            showExplanationPanel();
//        // Now that the user's seen the results, probably wants to review all the questions
//        pinExplanationPanelBox.setSelected(true);
//        resultsDialog.setVisible(true);
//    }// GEN-LAST:event_overviewButtonActionPerformed
//
//    /**
//     * Searches cyclically for the next marked question and changes to it.
//     */
//    private void findNextMarkedButtonActionPerformed(ActionEvent evt) {// GEN-FIRST:event_findNextMarkedButtonActionPerformed
//        int number = (questionNumber + 1) % numberOfQuestions;
//        do {
//            if (questionPanels.get(number) != null && questionPanels.get(number).isMarkedForReview()) {
//                changeToQuestion(number);
//                return;
//            }
//            number = (number + 1) % numberOfQuestions;
//        } while (number != questionNumber);
//        // If we cannot find any, we will end up where we started.
//        // (We will never test the start position)
//    }// GEN-LAST:event_findNextMarkedButtonActionPerformed
//
//    /**
//     * Searches cyclically for the next unanswered question and changes to it.
//     */
//    private void findNextUnansweredButtonActionPerformed(ActionEvent evt) {// GEN-FIRST:event_findNextUnansweredButtonActionPerformed
//        int number = (questionNumber + 1) % numberOfQuestions;
//        while (number != questionNumber) {
//            QuestionPanel questionPanel = questionPanels.get(number);
//            if (questionPanel == null || !questionPanel.getQuestion().isAnswered(questionPanel.getQuestion())) {
//                changeToQuestion(number);
//                return;
//            }
//            number = (number + 1) % numberOfQuestions;
//        }
//        // If we cannot find any, we will end up where we started.
//        // (We will never test the start position)
//    }// GEN-LAST:event_findNextUnansweredButtonActionPerformed
//
//    private void markedForReviewBoxActionPerformed(ActionEvent evt) {// GEN-FIRST:event_markedForReviewBoxActionPerformed
//        getCurrentQuestionPanel().setMarkedForReview(markedForReviewBox.isSelected());
//        updateMarkedForReview();
//    }// GEN-LAST:event_markedForReviewBoxActionPerformed
//
//    private void checkAnswerButtonActionPerformed(ActionEvent evt) {// GEN-FIRST:event_checkAnswerButtonActionPerformed
//        if (explanationPanel.isVisible())
//            hideExplanationPanel();
//        else
//            showExplanationPanel();
//    }// GEN-LAST:event_checkAnswerButtonActionPerformed
//
//    /**
//     * Pauses or restarts the timer.
//     * 
//     */
//    private void pauseButtonActionPerformed(ActionEvent evt) {// GEN-FIRST:event_pauseButtonActionPerformed
//        synchronized (pauseButton) {
//            String name = pauseButton.getText();
//            if (name.equals(PAUSE_TEXT)) {
//                pauseButton.setText(CONTINUE_TEXT);
//                timeRemainingLabel.setEnabled(false);
//                timeRemainingTextField.setEnabled(false);
//                testTimerThread.pause();
//            } else if (name.equals(CONTINUE_TEXT)) {
//                timeRemainingLabel.setEnabled(true);
//                timeRemainingTextField.setEnabled(true);
//                testTimerThread.resume();
//                pauseButton.setText(PAUSE_TEXT);
//            }
//        }
//    }// GEN-LAST:event_pauseButtonActionPerformed
//
//    private void navigateToQuestionComboBoxActionPerformed(ActionEvent evt) {// GEN-FIRST:event_navigateToQuestionComboBoxActionPerformed
//        changeToQuestion(navigateToQuestionComboBox.getSelectedIndex());
//    }// GEN-LAST:event_navigateToQuestionComboBoxActionPerformed
//
//    private void lastQuestionButtonActionPerformed(ActionEvent evt) {// GEN-FIRST:event_lastQuestionButtonActionPerformed
//        changeToQuestion(numberOfQuestions - 1);
//    }// GEN-LAST:event_lastQuestionButtonActionPerformed
//
//    private void previousQuestionButtonActionPerformed(ActionEvent evt) {// GEN-FIRST:event_previousQuestionButtonActionPerformed
//        changeToQuestion(questionNumber - 1);
//    }// GEN-LAST:event_previousQuestionButtonActionPerformed
//
//    private void nextQuestionButtonActionPerformed(ActionEvent evt) {// GEN-FIRST:event_nextQuestionButtonActionPerformed
//        changeToQuestion(questionNumber + 1);
//    }// GEN-LAST:event_nextQuestionButtonActionPerformed
//
//    private void firstQuestionButtonActionPerformed(ActionEvent evt) {// GEN-FIRST:event_firstQuestionButtonActionPerformed
//        changeToQuestion(0);
//    }// GEN-LAST:event_firstQuestionButtonActionPerformed
//
//    private void aboutMenuItemActionPerformed(ActionEvent evt) {// GEN-FIRST:event_aboutMenuItemActionPerformed
//        new AboutDialog(this).setVisible(true);
//    }// GEN-LAST:event_aboutMenuItemActionPerformed
//
//    private void exitMenuItemActionPerformed(ActionEvent evt) {// GEN-FIRST:event_exitMenuItemActionPerformed
//        setVisible(false);
//        dispose();
//        returnCallback.doReturn();
//    }// GEN-LAST:event_exitMenuItemActionPerformed
//
//    // </editor-fold>
//    private void updateAppearanceOfNextIncorrectButton() {
//        if (explanationPanel.isVisible() && pinExplanationPanelBox.isSelected()) {
//            findNextIncorrectAnswerButton.setVisible(true);
//        } else {
//            findNextIncorrectAnswerButton.setVisible(false);
//        }
//        // Any questions incorrect?
//        boolean allQuestionsCorrect = true;
//        for (QuestionPanel p : questionPanels)
//            if (p == null || !p.getAnswer().isCorrect())
//                allQuestionsCorrect = false;
//        if (allQuestionsCorrect) {
//            findNextIncorrectAnswerButton.setEnabled(false);
//        } else {
//            findNextIncorrectAnswerButton.setEnabled(true);
//        }
//    }
//
//    /**
//     * A callback to indicate that the answer has been changed in the current <tt>QuestionPanel</tt>
//     */
//    public void answerChanged() {
//        // Count questions answered
//        int questionsAnswered = 0;
//        for (QuestionPanel p : questionPanels)
//            if (p != null && p.getAnswer().isAnswered())
//                questionsAnswered++;
//        // Update the "Find next unanswered" button
//        if (questionsAnswered == numberOfQuestions)
//            findNextUnansweredButton.setEnabled(false);
//        else
//            findNextUnansweredButton.setEnabled(true);
//        // Update the progressBar
//        String percentageString = String.format(" (%.0f%%)", 100.0 * questionsAnswered / numberOfQuestions);
//        progressBar.setString("" + questionsAnswered + " / " + numberOfQuestions + percentageString);
//        progressBar.setValue((int) (questionsAnswered * 100.0 / numberOfQuestions));
//    }
//
//    private void createQuestionPanelIfNecessary(int i) {
//        QuestionPanel panel;
//        if (null == questionPanels.get(i)) {
//            if (questions.get(i) instanceof MultipleChoiceQuestion) {
//                panel = new MultipleChoicePanel((MultipleChoiceQuestion) questions.get(i), this, config
//                        .shouldStateNumberOfOptionsNeededForMultipleChoice());
//            } else if (questions.get(i) instanceof DragAndDropQuestion) {
//                panel = new DragAndDropPanel((DragAndDropQuestion) questions.get(i), this, this, glassPane);
//            } else {
//                LOGGER.warning("Unknown panel type" + questions.get(i));
//                return;
//            }
//            questionPanelHolderPanel.add(panel, Integer.toString(i));
//            questionPanels.set(i, panel);
//        }
//    }
//
//    /*
//     * Changes the current question to the indicated question number, updating the GUI as required.
//     */
//    void changeToQuestion(int newQuestionNumber) {
//        // Later in this method we change the navigation combo box.
//        // Doing this fires an actionPerformed event, and its event handler
//        // calls this method again. To avoid firing the event a second time,
//        // we ignore any updates that do not change the questionNumber
//        if (questionNumber == newQuestionNumber) {
//            return;
//        }
//        questionNumber = newQuestionNumber;
//        createQuestionPanelIfNecessary(newQuestionNumber);
//        questionPanelLayout.show(questionPanelHolderPanel, Integer.toString(newQuestionNumber));
//        // Set all the nav buttons enabled, and we'll disable them
//        // again if necessary
//        firstQuestionButton.setEnabled(true);
//        previousQuestionButton.setEnabled(true);
//        lastQuestionButton.setEnabled(true);
//        nextQuestionButton.setEnabled(true);
//        // Disable buttons if we're at the first or last question
//        if (questionNumber == 0) {
//            firstQuestionButton.setEnabled(false);
//            previousQuestionButton.setEnabled(false);
//        } else { // Pre-build next and previous
//            createQuestionPanelIfNecessary(questionNumber - 1);
//        }
//        if (questionNumber == numberOfQuestions - 1) {
//            lastQuestionButton.setEnabled(false);
//            nextQuestionButton.setEnabled(false);
//        } else { // Pre-build next and previous
//            createQuestionPanelIfNecessary(questionNumber + 1);
//        }
//        // Set the navigation ComboBox
//        navigateToQuestionComboBox.setSelectedIndex(questionNumber);
//        // Update "marked for review" box
//        QuestionPanel p = getCurrentQuestionPanel();
//        markedForReviewBox.setSelected(p.isMarkedForReview());
//        if (pinExplanationPanelBox.isSelected()) { // Maintain review mode
//            p.enterReviewMode();
//            // Update the explanation panel for the new question
//            explanationTextPane.setText(getCurrentQuestionPanel().getExplanationText());
//            explanationTextPane.setCaretPosition(0); // Scroll to beginning
//            setRightWrongLabels();
//        } else { // Leave review mode and enter question mode
//            hideExplanationPanel();
//            p.enterQuestionMode();
//        }
//        fixLayout();
//    }
//
//    private void hideExplanationPanel() {
//        explanationPanel.setVisible(false);
//        splitPane.setDividerLocation(1.0D);
//        splitPane.setDividerSize(0); // Hide the divider when in question mode
//        checkAnswerButton.setText("Show answer and explanation");
//        pinExplanationPanelBox.setSelected(false);
//        getCurrentQuestionPanel().enterQuestionMode();
//        fixLayout();
//        updateAppearanceOfNextIncorrectButton();
//    }
//
//    private void showExplanationPanel() {
//        setRightWrongLabels();
//        getCurrentQuestionPanel().enterReviewMode();
//        checkAnswerButton.setText("Hide answer and explanation");
//        explanationTextPane.setText(getCurrentQuestionPanel().getExplanationText());
//        explanationTextPane.setCaretPosition(0); // Scroll to beginning
//        explanationPanel.setVisible(true);
//        splitPane.setDividerLocation(-1);
//        splitPane.setDividerSize(5);
//        fixLayout();
//        updateAppearanceOfNextIncorrectButton();
//    }
//
//    /**
//     * Performs any layout update hacks that might be needed.
//     */
//    private void fixLayout() {
//        QuestionPanel panel = getCurrentQuestionPanel();
//        // Hack to help along the layout for MultipleChoicePanels
//        if (panel instanceof MultipleChoicePanel)
//            ((MultipleChoicePanel) panel).fixDividerLocation();
//    }
//
//    /**
//     * Provides visual feedback to the user on whether his answer is correct or incorrect
//     */
//    private void setRightWrongLabels() {
//        if (getCurrentQuestionPanel().getAnswer().isCorrect()) {
//            if (Icons.TICK.isAvailable())
//                rightOrWrongLabel.setIcon(Icons.TICK.getIcon());
//            rightOrWrongLabel.setText("Correct");
//        } else {
//            if (Icons.CROSS.isAvailable())
//                rightOrWrongLabel.setIcon(Icons.CROSS.getIcon());
//            rightOrWrongLabel.setText("Incorrect");
//        }
//    }
//
//    private QuestionPanel getCurrentQuestionPanel() {
//        return questionPanels.get(questionNumber);
//    }
//
//    private void updateMarkedForReview() {
//        // Search for any marked questions
//        boolean anyMarked = false;
//        for (QuestionPanel p : questionPanels) {
//            if (p != null && p.isMarkedForReview()) {
//                anyMarked = true;
//                break;
//            }
//        }
//        if (anyMarked)
//            findNextMarkedButton.setEnabled(true);
//        else
//            findNextMarkedButton.setEnabled(false);
//    }
//
//    // <editor-fold defaultstate="collapsed" desc="Generated GUI variable declarations ">
//    // Variables declaration - do not modify//GEN-BEGIN:variables
//    private javax.swing.JMenuItem aboutMenuItem;
//
//    private javax.swing.JPanel advancedNavigationPanel;
//
//    private javax.swing.JButton checkAnswerButton;
//
//    private javax.swing.JMenuItem exitMenuItem;
//
//    private javax.swing.JPanel explanationPanel;
//
//    private javax.swing.JTextPane explanationTextPane;
//
//    private javax.swing.JButton findNextIncorrectAnswerButton;
//
//    private javax.swing.JButton findNextMarkedButton;
//
//    private javax.swing.JButton findNextUnansweredButton;
//
//    private javax.swing.JButton firstQuestionButton;
//
//    private javax.swing.JLabel jLabel1;
//
//    private javax.swing.JLabel jLabel5;
//
//    private javax.swing.JMenu jMenu1;
//
//    private javax.swing.JMenu jMenu2;
//
//    private javax.swing.JPanel jPanel6;
//
//    private javax.swing.JScrollPane jScrollPane2;
//
//    private javax.swing.JButton lastQuestionButton;
//
//    private javax.swing.JCheckBox markedForReviewBox;
//
//    private javax.swing.JComboBox navigateToQuestionComboBox;
//
//    private javax.swing.JPanel navigationButtonsPanel;
//
//    private javax.swing.JButton nextQuestionButton;
//
//    private javax.swing.JButton overviewButton;
//
//    private javax.swing.JButton pauseButton;
//
//    private javax.swing.JCheckBox pinExplanationPanelBox;
//
//    private javax.swing.JButton previousQuestionButton;
//
//    private javax.swing.JPanel progressAndAdvancedNavLayoutPanel;
//
//    private javax.swing.JProgressBar progressBar;
//
//    private javax.swing.JPanel progressPanel;
//
//    private javax.swing.JPanel questionPanelHolderPanel;
//
//    private javax.swing.JLabel rightOrWrongLabel;
//
//    private javax.swing.JSplitPane splitPane;
//
//    private javax.swing.JMenuBar testMenuBar;
//
//    private javax.swing.JLabel timeRemainingLabel;
//
//    private javax.swing.JTextField timeRemainingTextField;
//
//    // End of variables declaration//GEN-END:variables
//    // </editor-fold>
//    /**
//     * An event listener for window resizes that enforces a minimum size for the window.
//     */
//    private class ResizeListener extends ComponentAdapter {
//        private Dimension minSize = getMinimumSize();
//
//        private Rectangle bounds = getBounds();
//
//        @Override
//        public void componentResized(ComponentEvent evt) {
//            int oldX = bounds.x;
//            int oldY = bounds.y;
//            int newX = getX();
//            int newY = getY();
//            int newWidth = getWidth();
//            int newHeight = getHeight();
//            int diffX = 0;
//            int diffY = 0;
//            // Check if any corrections are needed
//            if (newWidth < minSize.width)
//                diffX = minSize.width - newWidth;
//            if (newHeight < minSize.height)
//                diffY = (minSize.height + 25) - newHeight; // 25 is a correction to avoid an
//            // undersize bug
//            // If any corrections are needed, resize (and possibly undo previous move)
//            if (diffX > 0 || diffY > 0) {
//                setBounds(oldX, oldY, newWidth + diffX, newHeight + diffY);
//                bounds.setBounds(oldX, oldY, newWidth + diffX, newHeight + diffY);
//            } else {
//                bounds.setBounds(newX, newY, newWidth, newHeight);
//            }
//            fixLayout();
//        }
//
//        @Override
//        public void componentMoved(ComponentEvent evt) {
//            // Update only for non-resizing moves (resizing moves will be handled in
//            // ComponentResized
//            if (getWidth() == bounds.width && getHeight() == bounds.height) {
//                bounds.setLocation(getX(), getY());
//            }
//        }
//    }
//    // Won't work, because the time-intensive task is stuff being done
//    // by the Swing event-dispatch thread anyway
//    // private class GUIBuilderThread extends Thread {
//    // public void run() {
//    // this.setPriority(Thread.MIN_PRIORITY);
//    // for (int i = 0; i < questionPanels.size(); i++) {
//    // final int y = i;
//    // Runnable runnable = new Runnable() {
//    // public void run() {
//    // try {
//    // Thread.sleep(300);
//    // } catch (InterruptedException ex) {
//    // // We don't care, ignore.
//    // }
//    // createQuestionPanelIfNecessary(y);
//    // System.out.println("Built " + y);
//    // }
//    // };
//    // SwingUtilities.invokeLater(runnable);
//    // }
//    // }
//    // }
//}

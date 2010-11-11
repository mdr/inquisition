package uk.co.bytemark.vm.enigma.inquisition.gui.editor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import uk.co.bytemark.vm.enigma.inquisition.gui.images.Icons;
import uk.co.bytemark.vm.enigma.inquisition.gui.misc.QuizConfig;
import uk.co.bytemark.vm.enigma.inquisition.gui.quiz.QuizFrame;
import uk.co.bytemark.vm.enigma.inquisition.gui.quizchooser.ReturnCallback;
import uk.co.bytemark.vm.enigma.inquisition.gui.quizchooser.SuffixFileFilter;
import uk.co.bytemark.vm.enigma.inquisition.misc.Constants;
import uk.co.bytemark.vm.enigma.inquisition.questions.QuestionSet;
import uk.co.bytemark.vm.enigma.inquisition.questions.QuestionSetManager;
import uk.co.bytemark.vm.enigma.inquisition.questions.xml.XmlQuestionSetParser;
import uk.co.bytemark.vm.enigma.inquisition.quiz.QuizState;

public class QuestionEditor extends JFrame implements DirtyingActionListener {

    private static final String           NO_FILE_NAME    = null;
    private static final QuestionSet      NO_QUESTION_SET = null;

    private String                        fileName        = NO_FILE_NAME;
    private QuestionSet                   baseQuestionSet = NO_QUESTION_SET;

    private final QuestionEditorMainPanel questionEditorMainPanel;

    public QuestionEditor() {
        if (Icons.FAVICON.isAvailable())
            setIconImage(Icons.FAVICON.getImage());
        questionEditorMainPanel = new QuestionEditorMainPanel();
        questionEditorMainPanel.setDirtyingActionListener(this);
        add(questionEditorMainPanel);
        setUpMenu();
        newQuestionSet();
        setTitleBar();
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                performExitProtocol();
            }
        });
    }

    private void setTitleBar() {
        String title = "Inquisition Question Editor: ";
        if (fileName == NO_FILE_NAME)
            title += "(No filename)";
        else
            title += fileName;
        if (isDirty())
            title += " [Unsaved]";
        setTitle(title);
    }

    private boolean isDirty() {
        return !getQuestionSet().equals(baseQuestionSet);
    }

    private void setUpMenu() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem newMenuItem = new JMenuItem("New");
        newMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean proceed = performSaveProtocolIfNecessary();
                if (proceed) {
                    newQuestionSet();
                    setTitleBar();
                }
            }
        });
        fileMenu.add(newMenuItem);
        JMenuItem openMenuItem = new JMenuItem("Open...");
        openMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean proceed = performSaveProtocolIfNecessary();
                if (proceed) {
                    QuestionSet questionSet = openQuestionSet();
                    if (questionSet != NO_QUESTION_SET) {
                        questionEditorMainPanel.setQuestionSet(questionSet);
                    }
                }
                setTitleBar();
            }
        });
        fileMenu.add(openMenuItem);
        JMenuItem saveMenuItem = new JMenuItem("Save");
        saveMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (fileName == NO_FILE_NAME) {
                    doSaveAs();
                } else {
                    doSave(fileName);
                }
                setTitleBar();
            }
        });
        fileMenu.add(saveMenuItem);
        JMenuItem saveAsMenuItem = new JMenuItem("Save as...");
        saveAsMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doSaveAs();
                setTitleBar();
            }

        });
        fileMenu.add(saveAsMenuItem);
        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                performExitProtocol();
            }

        });
        fileMenu.add(exitMenuItem);
        menuBar.add(fileMenu);
        JMenu questionsMenu = new JMenu("Questions");
        JMenuItem runQuizItem = new JMenuItem("Start quiz on these questions");
        runQuizItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                runQuiz();
            }

        });
        questionsMenu.add(runQuizItem);
        menuBar.add(questionsMenu);
        setJMenuBar(menuBar);
    }

    private void runQuiz() {
        QuestionSet questionSet = getQuestionSet();
        QuizState quizState = new QuizState(questionSet, QuizConfig.createWithTimer(false, true, questionSet
                .getRecommendedTimeForAllQuestions()));
        QuizFrame quizFrame = new QuizFrame(quizState, ReturnCallback.NO_OP_RETURN_CALLBACK);
        quizFrame.setVisible(true);
    }

    private void performExitProtocol() {
        boolean proceed = performSaveProtocolIfNecessary();
        if (proceed) {
            dispose();
        }
    }

    private boolean performSaveProtocolIfNecessary() {
        boolean proceed;
        if (isDirty()) {
            int saveStatus = JOptionPane.showConfirmDialog(QuestionEditor.this,
                    "Do you want to save the current question set?", "Exit Question Editor",
                    JOptionPane.YES_NO_CANCEL_OPTION);
            if (saveStatus == JOptionPane.YES_OPTION) {
                boolean saved = doSaveAs();
                proceed = saved;
            } else if (saveStatus == JOptionPane.NO_OPTION) {
                proceed = true;
            } else { // JOptionPane.CANCEL_OPTION
                proceed = false;
            }
        } else {
            proceed = true;
        }
        return proceed;
    }

    private QuestionSet openQuestionSet() { // TODO: Duplicate code with QuizFrame
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Open Questions File");
        SuffixFileFilter.setSoleFileFilter(fileChooser, Constants.QUESTION_SET_FILE_FILTER);
        if (fileChooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION)
            return NO_QUESTION_SET;
        String fileNameToOpen = fileChooser.getSelectedFile().getAbsolutePath();
        InputStream stream = null;
        try {
            QuestionSet questionSet;
            stream = new FileInputStream(fileNameToOpen);
            questionSet = QuestionSetManager.getQuestionSetFromInputStream(stream);
            this.fileName = fileNameToOpen;
            this.baseQuestionSet = questionSet;
            return questionSet;
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Could not parse " + fileNameToOpen, "Could not parse file",
                    JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Could not open " + fileNameToOpen, "Could not open file",
                    JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (stream != null)
                    stream.close();
            } catch (IOException e) {
                // Not much we can do about it...
            }
        }
        return NO_QUESTION_SET;
    }

    private boolean doSaveAs() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save In-Progress Quiz");
        SuffixFileFilter.setSoleFileFilter(fileChooser, Constants.QUESTION_SET_FILE_FILTER);
        int resultStatus = fileChooser.showSaveDialog(this);
        if (resultStatus != JFileChooser.APPROVE_OPTION) {
            return false;
        }
        String questionSetFileName = fileChooser.getSelectedFile().getAbsolutePath();
        File questionSetFile = new File(questionSetFileName);
        if (!Constants.QUESTION_SET_FILE_FILTER.accept(questionSetFile)) {
            questionSetFileName = Constants.QUESTION_SET_FILE_FILTER.addSuffixTo(questionSetFileName);
            questionSetFile = new File(questionSetFileName);
        }
        if (questionSetFile.exists()) {
            int confirmStatus = JOptionPane.showConfirmDialog(this, "'" + questionSetFile.getAbsolutePath()
                    + "' exists. Do you want to overwrite it?", "Confirm Overwrite", JOptionPane.OK_CANCEL_OPTION);
            if (confirmStatus != JOptionPane.OK_OPTION) {
                return false;
            }
        }
        boolean saved = doSave(questionSetFileName);

        return saved;
    }

    private boolean doSave(String questionSetFileName) {
        QuestionSet questionSet = getQuestionSet();
        Document doc = new XmlQuestionSetParser().asXmlDocument(questionSet);
        doc.setDocType(new DocType("QuestionSet", "inquisitionQuestions.dtd"));
        XMLOutputter outputter = new XMLOutputter();
        Format format = Format.getPrettyFormat();
        outputter.setFormat(format);
        try {
            OutputStream outputStream = new FileOutputStream(new File(questionSetFileName));
            outputter.output(doc, outputStream);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Could not save questions '" + questionSetFileName + "' due to error '"
                    + e.getMessage() + "'", "Could not save questions", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        fileName = questionSetFileName;
        baseQuestionSet = questionSet;
        return true;
    }

    private QuestionSet getQuestionSet() {
        return questionEditorMainPanel.getQuestionSet();
    }

    private void newQuestionSet() {
        fileName = NO_FILE_NAME;
        questionEditorMainPanel.setUpNewQuestionSet();
        baseQuestionSet = getQuestionSet();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new QuestionEditor();
                frame.pack();
                frame.setVisible(true);
            }

        });
    }

    public void dirtyingActionHappened() {
        setTitleBar();
    }
}
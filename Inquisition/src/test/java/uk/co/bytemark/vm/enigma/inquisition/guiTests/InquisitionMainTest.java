//package uk.co.bytemark.vm.enigma.inquisition.guiTests;
//
//import org.fest.swing.core.Robot;
//import org.fest.swing.finder.WindowFinder;
//import org.fest.swing.fixture.FrameFixture;
//import org.fest.swing.fixture.JCheckBoxFixture;
//import org.junit.Before;
//import org.junit.Test;
//
//import uk.co.bytemark.vm.enigma.inquisition.gui.misc.SwingComponentNames;
//import uk.co.bytemark.vm.enigma.inquisition.gui.quiz.QuizFrame;
//import uk.co.bytemark.vm.enigma.inquisition.gui.quizchooser.InquisitionMain;
//import uk.co.bytemark.vm.enigma.inquisition.gui.quizchooser.QuestionSetSelectorFrame;
//
//public class InquisitionMainTest {
//    @Before
//    public void setUp() throws Exception {
//        InquisitionMain.runInquisition();
//
//    }
//
//    @Test
//    public void integrationTest()  {
//        Robot robot = Robot.robotWithCurrentAwtHierarchy();
//        FrameFixture selectorWindow = WindowFinder.findFrame(QuestionSetSelectorFrame.class).using(robot);
//        selectorWindow.button(SwingComponentNames.BEGIN_BUTTON).requireDisabled();
//        JCheckBoxFixture shuffleCheckBox = selectorWindow.checkBox(SwingComponentNames.SHUFFLE_CHECK_BOX);
//        shuffleCheckBox.requireDisabled();
//        selectorWindow.tree().selectRow(1);
//        shuffleCheckBox.requireEnabled();
//        shuffleCheckBox.requireSelected();
//        shuffleCheckBox.uncheck();
//        shuffleCheckBox.requireNotSelected();
//        selectorWindow.button(SwingComponentNames.BEGIN_BUTTON).requireEnabled().click();
//        FrameFixture quizWindow = WindowFinder.findFrame(QuizFrame.class).using(selectorWindow.robot);
//        quizWindow.button(SwingComponentNames.NEXT_BUTTON);
//        quizWindow.cleanUp();
//    }
////
////    @After
////    public void tearDown() {}
//}

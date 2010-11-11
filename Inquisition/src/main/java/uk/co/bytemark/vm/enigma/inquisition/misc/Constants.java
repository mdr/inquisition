package uk.co.bytemark.vm.enigma.inquisition.misc;

import uk.co.bytemark.vm.enigma.inquisition.gui.quizchooser.SuffixFileFilter;

public class Constants {

    private Constants() {
    // Cannot instantiate class
    }

    public static final String           QUIZ_STATE_FILE_SUFFIX   = "quiz";
    public static final SuffixFileFilter QUIZ_SUFFIX_FILE_FILTER  = new SuffixFileFilter(QUIZ_STATE_FILE_SUFFIX,
                                                                          "In-progress Quizzes");

    public static final String           QUESTION_SET_FILE_SUFFIX = "questions";
    public static final SuffixFileFilter QUESTION_SET_FILE_FILTER = new SuffixFileFilter(QUESTION_SET_FILE_SUFFIX,
                                                                          "Question Sets");

    public static final String           HTML_FILE_SUFFIX         = "html";
    public static final SuffixFileFilter HTML_FILE_FILTER         = new SuffixFileFilter(HTML_FILE_SUFFIX, "HTML file");

    public final static String           VERSION                  = "0.14";
    private final static String          ABOUT_TEXT               = "<html><body><h2>Inquisition v"
                                                                          + VERSION
                                                                          + "</h2>\n"
                                                                          + "Written by Matthew D. Russell, October 2006-June 2008. Inquisition is free software / open source, and "
                                                                          + "has been released into the public domain.\n"
                                                                          + "<p>\n"
                                                                          + "Home page: <a href='http://enigma.vm.bytemark.co.uk/inquisition'>http://enigma.vm.bytemark.co.uk/inquisition</a>\n"
                                                                          + "\n"
                                                                          + "<h3>Warranty</h3>\n"
                                                                          + "THE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR \n"
                                                                          + "IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, \n"
                                                                          + "FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE \n"
                                                                          + "AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER \n"
                                                                          + "LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, \n"
                                                                          + "OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.\n"
                                                                          + "\n"
                                                                          + "<h3>Libraries</h3>\n"
                                                                          + "This software makes use of a number of external libraries:\n"
                                                                          + "<ul>\n"
                                                                          + "<li>The Squareness look and feel by Robert F. Beeger  (Academic Free License 3.0) -- <a href='http://squareness.beeger.net/'>http://squareness.beeger.net/</a>\n"
                                                                          + "<li>JDOM -- <a href='http://www.jdom.org/'>http://www.jdom.org/</a>(available under an Apache-style open source license, with the acknowledgment clause removed)\n"
                                                                          + "<li>a modified version of jhighlight (CDDL 1.0 or LGPL) -- <a href='https://jhighlight.dev.java.net/'>https://jhighlight.dev.java.net/</a> by Geert Bevin, Omnicore Software, and Hans Kratz & Dennis Strein GbR\n"
                                                                          + "<li>UISpec4J -- <a href='http://www.uispec4j.org/'>http://www.uispec4j.org/</a>, available under the Common Public License Version 1.0</li>"
                                                                          + "<li>FEST-Swing -- <a href='http://fest.easytesting.org/swing/wiki/pmwiki.php'>http://fest.easytesting.org/swing/wiki/pmwiki.php</a>, available under the Apache License 2.0</li>"
                                                                          + "</ul>\n"
                                                                          + "\n"
                                                                          + "<h3>Other acknowledgements</h3>\n"
                                                                          + "<ul>\n"
                                                                          + "<li>Forward and back arrows by Marko J. Kolehmainen, part of the Blueberry iconset on Open Clipart: http://openclipart.org/clipart//computer/icons/bb_bback_.svg\n"
                                                                          + "<li>Tick and cross icons recoloured from http://commons.wikimedia.org/wiki/Image:Symbol_delete_vote.svg and http://commons.wikimedia.org/wiki/Image:Symbol_keep_vote.svg by \"Bastique\": http://commons.wikimedia.org/wiki/User:Bastique\n"
                                                                          + "</ul>\n" + "</body></html>";
    public static final String           REPOSITORY_URL           = "http://enigma.vm.bytemark.co.uk/exams/examlist-"
                                                                          + VERSION + ".html";

    public static String getAboutText() {
        return ABOUT_TEXT;
    }

}

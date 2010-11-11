package uk.co.bytemark.vm.enigma.inquisition.questions;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.uwyn.jhighlight.renderer.JavaXhtmlRenderer;

public class QuestionRenderingHelper {
    private static final Pattern JAVA_CODE_BLOCK_PATTERN = Pattern.compile("<java>(.*?)</java>", Pattern.DOTALL
                                                                 | Pattern.CASE_INSENSITIVE);

    private static final Logger  LOGGER                  = Logger.getLogger(QuestionRenderingHelper.class.getName());

    public static String syntaxHighlightForJava(String htmlText) {
        Matcher matcher = JAVA_CODE_BLOCK_PATTERN.matcher(htmlText);
        StringBuffer stringBuffer = new StringBuffer();
        String replacement;
        while (matcher.find()) {
            try {
                String javaCodeBlock = matcher.group(1);
                String rendered = QuestionRenderingHelper.renderJavaCodeBlock(javaCodeBlock);
                replacement = Matcher.quoteReplacement(rendered);
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Error in string replacement for: " + htmlText, e);
                return htmlText;
            }
            matcher.appendReplacement(stringBuffer, replacement);

        }
        matcher.appendTail(stringBuffer);
        return stringBuffer.toString();
    }

    private static String renderJavaCodeBlock(String javaCodeBlock) throws IOException {
        JavaXhtmlRenderer renderer = new JavaXhtmlRenderer(); // TODO: Figure out how this works
        String rendered = renderer.highlight("", javaCodeBlock, "utf8", true);
        rendered = rendered.trim(); // In particular, we don't want the trailing newline
        // replacement = Matcher.quoteReplacement("<table border=1 cellspacing=0><tr><td><pre>" + rendered +
        // "</pre></td></tr></table>");
        rendered = "<pre>" + rendered + "</pre>";
        return rendered;
    }

}

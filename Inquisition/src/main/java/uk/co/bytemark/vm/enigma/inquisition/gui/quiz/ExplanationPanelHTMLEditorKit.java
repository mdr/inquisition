/*
 * ExplanationPanelHTMLEditorKit.java
 *
 * Created on 25 September 2006, 19:41
 */

package uk.co.bytemark.vm.enigma.inquisition.gui.quiz;


import java.awt.Component;
import java.awt.Font;

import javax.swing.JTextField;
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
import javax.swing.text.html.StyleSheet;


public class ExplanationPanelHTMLEditorKit extends HTMLEditorKit {
    
    @Override
    public Document createDefaultDocument() {
        StyleSheet styles = getStyleSheet();
        HTMLDocument doc = new HTMLDocument(styles);
        
        return doc;
    }
    
    @Override
    public ViewFactory getViewFactory() { return new MyHTMLFactory(); }
    
    private static class MyHTMLFactory extends HTMLFactory {
        
        @Override
        public View create(Element e) {
            AttributeSet a = e.getAttributes();
            
            if (a.getAttribute(StyleConstants.NameAttribute) == HTML.Tag.INPUT) {
                return new NonEditableInputFormView(e);
            } else {
                return super.create(e);
            }
        }
    }
    
    private static class NonEditableInputFormView extends FormView {
        
        public NonEditableInputFormView(Element elem) {
            super(elem);
        }
        
        @Override
        protected Component createComponent() {
            Component c = super.createComponent();
            if (c instanceof JTextField) {
                JTextField tf = (JTextField) c;
                tf.setEditable(false);
                tf.setBackground(DragAndDropPanel.NORMAL_COLOUR);
                tf.setEditable(false);
                int oldSize = tf.getFont().getSize();
                tf.setFont(new Font("Monospaced", Font.PLAIN, oldSize));

            }
            return c;
        }
    }
}


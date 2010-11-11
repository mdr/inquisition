/*
 * TestHighlighter.java
 *
 * Created on 07 October 2006, 23:16
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.uwyn.jhighlight.renderer;

import com.uwyn.jhighlight.renderer.JavaXhtmlRenderer;
import java.io.IOException;

/**
 *
 * @author matthew
 */
public class TestHighlighter {
    
    /** Creates a new instance of TestHighlighter */
    public TestHighlighter() {
        JavaXhtmlRenderer r = new JavaXhtmlRenderer();
        try {
            System.out.println("<pre>\n" + 
                    r.highlight("namea",
                    "public class Wibble {}",
                    "utf8",
                    true)
                    + "</pre>");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
    }
    public static void main(String [] args) {
        new TestHighlighter();
    }
    
}

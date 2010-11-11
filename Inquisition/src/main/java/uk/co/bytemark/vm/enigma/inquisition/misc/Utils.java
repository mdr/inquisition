/*
 * Utils.java
 * 
 * Created on 03 October 2006, 22:40
 */
package uk.co.bytemark.vm.enigma.inquisition.misc;

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

public class Utils {
    /*
     * Escapes special HTML characters
     */
    public static String htmlEscape(String s) {
        List<Character> charList = new ArrayList<Character>(s.length());
        for (int pos = 0; pos < s.length(); pos++) {
            Character c = s.charAt(pos);
            if (c.equals('<')) {
                charList.add('&');
                charList.add('l');
                charList.add('t');
                charList.add(';');
            } else if (c.equals('>')) {
                charList.add('&');
                charList.add('g');
                charList.add('t');
                charList.add(';');
            } else if (c.equals('&')) {
                charList.add('&');
                charList.add('a');
                charList.add('m');
                charList.add('p');
                charList.add(';');
            } else {
                charList.add(c);
            }
        }
        StringBuffer sb = new StringBuffer(charList.size());
        for (int i = 0; i < charList.size(); i++)
            sb.append(charList.get(i));
        return sb.toString();
    }

    /**
     * Parse a string as a boolean. Needed because java.lang.Boolean.parseBoolean() will return false for strings such
     * as "wibble".
     */
    public static boolean parseBoolean(String s) throws ParseException {
        if (s.equalsIgnoreCase("true")) {
            return true;
        } else if (s.equalsIgnoreCase("false")) {
            return false;
        } else {
            throw new ParseException("expecting boolean value, got " + s, 0);
        }
    }

    // Used to ignore DTDs
    public static class NoOpEntityResolver implements EntityResolver {
        public InputSource resolveEntity(String publicId, String systemId) {
            return new InputSource();
        }
    }

    public static String join(Collection<?> s, String delimiter) {
        StringBuffer buffer = new StringBuffer();
        Iterator<?> iter = s.iterator();
        while (iter.hasNext()) {
            buffer.append(iter.next());
            if (iter.hasNext()) {
                buffer.append(delimiter);
            }
        }
        return buffer.toString();
    }

    public static Element makeXMLFragment(String xmlAsString) throws JDOMException, IOException {
        StringReader reader = new StringReader(xmlAsString);
        SAXBuilder builder = new SAXBuilder(false);
        builder.setEntityResolver(new NoOpEntityResolver()); // ignore DTDs
        Document document = builder.build(reader);
        return document.getRootElement();
    }

    public static <T> Set<T> makeSet(T... options) {
        List<T> asList = Arrays.asList(options);
        return new HashSet<T>(asList);
    }

    public static void checkArgumentNotNull(Object argument, String parameter) {
        if (argument == null)
            throw new IllegalArgumentException("Parameter '" + parameter + "' should not be null");
    }

    public static void checkPositive(int timeAllowed, String argName) {
        if (timeAllowed <= 0)
            throw new IllegalArgumentException("Parameter '" + argName + "' should be positive");
    }

    public static <K, V> Map<K, V> newHashMap() {
        return new HashMap<K, V>();
    }

}

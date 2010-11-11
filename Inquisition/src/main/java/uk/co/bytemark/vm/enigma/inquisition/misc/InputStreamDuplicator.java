/*
 * InputStreamDuplicator.java
 *
 * Created on 03 October 2006, 08:32
 *
 */

package uk.co.bytemark.vm.enigma.inquisition.misc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author Matt
 */
public class InputStreamDuplicator {
    
    private byte [] byteArray;
    public InputStreamDuplicator(InputStream inputStream) throws IOException {
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        int readByte;
        while ((readByte = inputStream.read()) >= 0)
            outputStream.write(readByte);

        byteArray = outputStream.toByteArray();
    }
    
    public InputStream freshCopy() {
        return new ByteArrayInputStream(byteArray);
    }
    
}

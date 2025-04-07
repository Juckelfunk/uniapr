// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.util;

import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;

public class FileUtil
{
    public static String readToString(final InputStream is) throws IOException {
        final StringBuilder fileData = new StringBuilder(1000);
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            char[] buf = new char[1024];
            int numRead = 0;
            while ((numRead = reader.read(buf)) != -1) {
                final String readData = String.valueOf(buf, 0, numRead);
                fileData.append(readData);
                buf = new char[1024];
            }
            return fileData.toString();
        }
    }
    
    public static String randomFilename() {
        return System.currentTimeMillis() + ("" + Math.random()).replaceAll("\\.", "");
    }
}

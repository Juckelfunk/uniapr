// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.util;

import java.io.ByteArrayInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedInputStream;
import java.io.StringReader;
import java.io.OutputStreamWriter;
import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.io.InputStreamReader;
import java.io.Writer;
import java.io.Reader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;

public final class IOUtil
{
    private static final int DEFAULT_BUFFER_SIZE = 4096;
    
    private IOUtil() {
    }
    
    public static void copy(final InputStream input, final OutputStream output) throws IOException {
        copy(input, output, 4096);
    }
    
    public static void copy(final InputStream input, final OutputStream output, final int bufferSize) throws IOException {
        final byte[] buffer = new byte[bufferSize];
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
        }
    }
    
    public static void copy(final Reader input, final Writer output) throws IOException {
        copy(input, output, 4096);
    }
    
    public static void copy(final Reader input, final Writer output, final int bufferSize) throws IOException {
        final char[] buffer = new char[bufferSize];
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
        }
        output.flush();
    }
    
    public static void copy(final InputStream input, final Writer output) throws IOException {
        copy(input, output, 4096);
    }
    
    public static void copy(final InputStream input, final Writer output, final int bufferSize) throws IOException {
        final InputStreamReader in = new InputStreamReader(input);
        copy(in, output, bufferSize);
    }
    
    public static void copy(final InputStream input, final Writer output, final String encoding) throws IOException {
        final InputStreamReader in = new InputStreamReader(input, encoding);
        copy(in, output);
    }
    
    public static void copy(final InputStream input, final Writer output, final String encoding, final int bufferSize) throws IOException {
        final InputStreamReader in = new InputStreamReader(input, encoding);
        copy(in, output, bufferSize);
    }
    
    public static String toString(final InputStream input) throws IOException {
        return toString(input, 4096);
    }
    
    public static String toString(final InputStream input, final int bufferSize) throws IOException {
        final StringWriter sw = new StringWriter();
        copy(input, sw, bufferSize);
        return sw.toString();
    }
    
    public static String toString(final InputStream input, final String encoding) throws IOException {
        return toString(input, encoding, 4096);
    }
    
    public static String toString(final InputStream input, final String encoding, final int bufferSize) throws IOException {
        final StringWriter sw = new StringWriter();
        copy(input, sw, encoding, bufferSize);
        return sw.toString();
    }
    
    public static byte[] toByteArray(final InputStream input) throws IOException {
        return toByteArray(input, 4096);
    }
    
    public static byte[] toByteArray(final InputStream input, final int bufferSize) throws IOException {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        copy(input, output, bufferSize);
        return output.toByteArray();
    }
    
    public static void copy(final Reader input, final OutputStream output) throws IOException {
        copy(input, output, 4096);
    }
    
    public static void copy(final Reader input, final OutputStream output, final int bufferSize) throws IOException {
        final OutputStreamWriter out = new OutputStreamWriter(output);
        copy(input, out, bufferSize);
        out.flush();
    }
    
    public static String toString(final Reader input) throws IOException {
        return toString(input, 4096);
    }
    
    public static String toString(final Reader input, final int bufferSize) throws IOException {
        final StringWriter sw = new StringWriter();
        copy(input, sw, bufferSize);
        return sw.toString();
    }
    
    public static byte[] toByteArray(final Reader input) throws IOException {
        return toByteArray(input, 4096);
    }
    
    public static byte[] toByteArray(final Reader input, final int bufferSize) throws IOException {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        copy(input, output, bufferSize);
        return output.toByteArray();
    }
    
    public static void copy(final String input, final OutputStream output) throws IOException {
        copy(input, output, 4096);
    }
    
    public static void copy(final String input, final OutputStream output, final int bufferSize) throws IOException {
        final StringReader in = new StringReader(input);
        final OutputStreamWriter out = new OutputStreamWriter(output);
        copy(in, out, bufferSize);
        out.flush();
    }
    
    public static void copy(final String input, final Writer output) throws IOException {
        output.write(input);
    }
    
    public static void bufferedCopy(final InputStream input, final OutputStream output) throws IOException {
        final BufferedInputStream in = new BufferedInputStream(input);
        final BufferedOutputStream out = new BufferedOutputStream(output);
        copy(in, out);
        out.flush();
    }
    
    public static byte[] toByteArray(final String input) throws IOException {
        return toByteArray(input, 4096);
    }
    
    public static byte[] toByteArray(final String input, final int bufferSize) throws IOException {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        copy(input, output, bufferSize);
        return output.toByteArray();
    }
    
    public static void copy(final byte[] input, final Writer output) throws IOException {
        copy(input, output, 4096);
    }
    
    public static void copy(final byte[] input, final Writer output, final int bufferSize) throws IOException {
        final ByteArrayInputStream in = new ByteArrayInputStream(input);
        copy(in, output, bufferSize);
    }
    
    public static void copy(final byte[] input, final Writer output, final String encoding) throws IOException {
        final ByteArrayInputStream in = new ByteArrayInputStream(input);
        copy(in, output, encoding);
    }
    
    public static void copy(final byte[] input, final Writer output, final String encoding, final int bufferSize) throws IOException {
        final ByteArrayInputStream in = new ByteArrayInputStream(input);
        copy(in, output, encoding, bufferSize);
    }
    
    public static String toString(final byte[] input) throws IOException {
        return toString(input, 4096);
    }
    
    public static String toString(final byte[] input, final int bufferSize) throws IOException {
        final StringWriter sw = new StringWriter();
        copy(input, sw, bufferSize);
        return sw.toString();
    }
    
    public static String toString(final byte[] input, final String encoding) throws IOException {
        return toString(input, encoding, 4096);
    }
    
    public static String toString(final byte[] input, final String encoding, final int bufferSize) throws IOException {
        final StringWriter sw = new StringWriter();
        copy(input, sw, encoding, bufferSize);
        return sw.toString();
    }
    
    public static void copy(final byte[] input, final OutputStream output) throws IOException {
        copy(input, output, 4096);
    }
    
    public static void copy(final byte[] input, final OutputStream output, final int bufferSize) throws IOException {
        output.write(input);
    }
    
    public static boolean contentEquals(final InputStream input1, final InputStream input2) throws IOException {
        final InputStream bufferedInput1 = new BufferedInputStream(input1);
        final InputStream bufferedInput2 = new BufferedInputStream(input2);
        for (int ch = bufferedInput1.read(); -1 != ch; ch = bufferedInput1.read()) {
            final int ch2 = bufferedInput2.read();
            if (ch != ch2) {
                return false;
            }
        }
        final int ch2 = bufferedInput2.read();
        return -1 == ch2;
    }
    
    public static void close(final InputStream inputStream) {
        if (inputStream == null) {
            return;
        }
        try {
            inputStream.close();
        }
        catch (IOException ex) {}
    }
    
    public static void close(final OutputStream outputStream) {
        if (outputStream == null) {
            return;
        }
        try {
            outputStream.close();
        }
        catch (IOException ex) {}
    }
    
    public static void close(final Reader reader) {
        if (reader == null) {
            return;
        }
        try {
            reader.close();
        }
        catch (IOException ex) {}
    }
    
    public static void close(final Writer writer) {
        if (writer == null) {
            return;
        }
        try {
            writer.close();
        }
        catch (IOException ex) {}
    }
}

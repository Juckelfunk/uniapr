// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.util;

import java.nio.channels.WritableByteChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public abstract class StreamUtil
{
    public static byte[] streamToByteArray(final InputStream in) throws IOException {
        try (final ByteArrayOutputStream result = new ByteArrayOutputStream()) {
            copy(in, result);
            return result.toByteArray();
        }
    }
    
    public static InputStream copyStream(final InputStream in) throws IOException {
        final byte[] bs = streamToByteArray(in);
        return new ByteArrayInputStream(bs);
    }
    
    private static void copy(final InputStream input, final OutputStream output) throws IOException {
        final ReadableByteChannel src = Channels.newChannel(input);
        final WritableByteChannel dest = Channels.newChannel(output);
        final ByteBuffer buffer = ByteBuffer.allocateDirect(16384);
        while (src.read(buffer) != -1) {
            buffer.flip();
            dest.write(buffer);
            buffer.compact();
        }
        buffer.flip();
        while (buffer.hasRemaining()) {
            dest.write(buffer);
        }
    }
}

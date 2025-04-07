// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.util;

import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.io.IOException;
import java.io.InputStream;
import java.io.DataInputStream;

public class SafeDataInputStream
{
    private final DataInputStream dis;
    
    public SafeDataInputStream(final InputStream is) {
        this.dis = new DataInputStream(is);
    }
    
    public int readInt() {
        try {
            return this.dis.readInt();
        }
        catch (IOException e) {
            throw Unchecked.translateCheckedException(e);
        }
    }
    
    public String readString() {
        try {
            return new String(this.readBytes(), "UTF-8");
        }
        catch (IOException e) {
            throw Unchecked.translateCheckedException(e);
        }
    }
    
    public byte[] readBytes() {
        try {
            final int length = this.dis.readInt();
            final byte[] data = new byte[length];
            this.dis.readFully(data);
            return data;
        }
        catch (IOException e) {
            throw Unchecked.translateCheckedException(e);
        }
    }
    
    public <T extends Serializable> T read(final Class<T> type) {
        try {
            return (T)this.deserialize(this.readBytes());
        }
        catch (IOException e) {
            throw Unchecked.translateCheckedException(e);
        }
    }
    
    public void close() {
        try {
            this.dis.close();
        }
        catch (IOException e) {
            throw Unchecked.translateCheckedException(e);
        }
    }
    
    public byte readByte() {
        try {
            return this.dis.readByte();
        }
        catch (IOException e) {
            throw Unchecked.translateCheckedException(e);
        }
    }
    
    public boolean readBoolean() {
        try {
            return this.dis.readBoolean();
        }
        catch (IOException e) {
            throw Unchecked.translateCheckedException(e);
        }
    }
    
    public long readLong() {
        try {
            return this.dis.readLong();
        }
        catch (IOException e) {
            throw Unchecked.translateCheckedException(e);
        }
    }
    
    private Object deserialize(final byte[] bytes) throws IOException {
        final ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInput in = null;
        try {
            in = new ObjectInputStream(bis);
            return in.readObject();
        }
        catch (ClassNotFoundException e) {
            throw Unchecked.translateCheckedException(e);
        }
        finally {
            if (in != null) {
                in.close();
            }
        }
    }
}

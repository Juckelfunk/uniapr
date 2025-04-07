// 
// Decompiled by Procyon v0.5.36
// 

package edu.utdallas.prf.reloc.apache.commons.io;

import java.nio.ByteOrder;

public final class ByteOrderParser
{
    private ByteOrderParser() {
    }
    
    public static ByteOrder parseByteOrder(final String value) {
        if (ByteOrder.BIG_ENDIAN.toString().equals(value)) {
            return ByteOrder.BIG_ENDIAN;
        }
        if (ByteOrder.LITTLE_ENDIAN.toString().equals(value)) {
            return ByteOrder.LITTLE_ENDIAN;
        }
        throw new IllegalArgumentException("Unsupported byte order setting: " + value + ", expeced one of " + ByteOrder.LITTLE_ENDIAN + ", " + ByteOrder.BIG_ENDIAN);
    }
}

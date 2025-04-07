// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.reloc.asm.commons;

import org.pitest.reloc.asm.ByteVector;
import org.pitest.reloc.asm.ClassWriter;
import org.pitest.reloc.asm.Label;
import org.pitest.reloc.asm.ClassReader;
import org.pitest.reloc.asm.Attribute;

public final class ModuleResolutionAttribute extends Attribute
{
    public static final int RESOLUTION_DO_NOT_RESOLVE_BY_DEFAULT = 1;
    public static final int RESOLUTION_WARN_DEPRECATED = 2;
    public static final int RESOLUTION_WARN_DEPRECATED_FOR_REMOVAL = 4;
    public static final int RESOLUTION_WARN_INCUBATING = 8;
    public int resolution;
    
    public ModuleResolutionAttribute(final int resolution) {
        super("ModuleResolution");
        this.resolution = resolution;
    }
    
    public ModuleResolutionAttribute() {
        this(0);
    }
    
    @Override
    protected Attribute read(final ClassReader cr, final int off, final int len, final char[] buf, final int codeOff, final Label[] labels) {
        final int resolution = cr.readUnsignedShort(off);
        return new ModuleResolutionAttribute(resolution);
    }
    
    @Override
    protected ByteVector write(final ClassWriter cw, final byte[] code, final int len, final int maxStack, final int maxLocals) {
        final ByteVector v = new ByteVector();
        v.putShort(this.resolution);
        return v;
    }
}

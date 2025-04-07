// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.reloc.asm.commons;

import org.pitest.reloc.asm.ByteVector;
import org.pitest.reloc.asm.ClassWriter;
import org.pitest.reloc.asm.Label;
import org.pitest.reloc.asm.ClassReader;
import org.pitest.reloc.asm.Attribute;

public final class ModuleTargetAttribute extends Attribute
{
    public String platform;
    
    public ModuleTargetAttribute(final String platform) {
        super("ModuleTarget");
        this.platform = platform;
    }
    
    public ModuleTargetAttribute() {
        this(null);
    }
    
    @Override
    protected Attribute read(final ClassReader cr, final int off, final int len, final char[] buf, final int codeOff, final Label[] labels) {
        final String platform = cr.readUTF8(off, buf);
        return new ModuleTargetAttribute(platform);
    }
    
    @Override
    protected ByteVector write(final ClassWriter cw, final byte[] code, final int len, final int maxStack, final int maxLocals) {
        final ByteVector v = new ByteVector();
        final int index = (this.platform == null) ? 0 : cw.newUTF8(this.platform);
        v.putShort(index);
        return v;
    }
}

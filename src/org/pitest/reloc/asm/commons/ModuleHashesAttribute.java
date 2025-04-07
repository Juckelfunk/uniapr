// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.reloc.asm.commons;

import org.pitest.reloc.asm.ByteVector;
import org.pitest.reloc.asm.ClassWriter;
import java.util.ArrayList;
import org.pitest.reloc.asm.Label;
import org.pitest.reloc.asm.ClassReader;
import java.util.List;
import org.pitest.reloc.asm.Attribute;

public final class ModuleHashesAttribute extends Attribute
{
    public String algorithm;
    public List<String> modules;
    public List<byte[]> hashes;
    
    public ModuleHashesAttribute(final String algorithm, final List<String> modules, final List<byte[]> hashes) {
        super("ModuleHashes");
        this.algorithm = algorithm;
        this.modules = modules;
        this.hashes = hashes;
    }
    
    public ModuleHashesAttribute() {
        this(null, null, null);
    }
    
    @Override
    protected Attribute read(final ClassReader cr, int off, final int len, final char[] buf, final int codeOff, final Label[] labels) {
        final String hashAlgorithm = cr.readUTF8(off, buf);
        final int count = cr.readUnsignedShort(off + 2);
        final ArrayList<String> modules = new ArrayList<String>(count);
        final ArrayList<byte[]> hashes = new ArrayList<byte[]>(count);
        off += 4;
        for (int i = 0; i < count; ++i) {
            final String module = cr.readModule(off, buf);
            final int hashLength = cr.readUnsignedShort(off + 2);
            off += 4;
            final byte[] hash = new byte[hashLength];
            for (int j = 0; j < hashLength; ++j) {
                hash[j] = (byte)(cr.readByte(off + j) & 0xFF);
            }
            off += hashLength;
            modules.add(module);
            hashes.add(hash);
        }
        return new ModuleHashesAttribute(hashAlgorithm, modules, hashes);
    }
    
    @Override
    protected ByteVector write(final ClassWriter cw, final byte[] code, final int len, final int maxStack, final int maxLocals) {
        final ByteVector v = new ByteVector();
        final int index = cw.newUTF8(this.algorithm);
        v.putShort(index);
        final int count = (this.modules == null) ? 0 : this.modules.size();
        v.putShort(count);
        for (int i = 0; i < count; ++i) {
            final String module = this.modules.get(i);
            v.putShort(cw.newModule(module));
            final byte[] hash = this.hashes.get(i);
            v.putShort(hash.length);
            for (final byte b : hash) {
                v.putByte(b);
            }
        }
        return v;
    }
}

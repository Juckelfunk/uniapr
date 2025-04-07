// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.reloc.asm.commons;

import java.security.MessageDigest;
import java.io.IOException;
import java.io.DataOutput;
import java.util.Arrays;
import java.io.OutputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayOutputStream;
import org.pitest.reloc.asm.FieldVisitor;
import org.pitest.reloc.asm.MethodVisitor;
import java.util.ArrayList;
import java.util.Collection;
import org.pitest.reloc.asm.ClassVisitor;

public class SerialVersionUIDAdder extends ClassVisitor
{
    private boolean computeSVUID;
    private boolean hasSVUID;
    private int access;
    private String name;
    private String[] interfaces;
    private Collection<Item> svuidFields;
    private boolean hasStaticInitializer;
    private Collection<Item> svuidConstructors;
    private Collection<Item> svuidMethods;
    
    public SerialVersionUIDAdder(final ClassVisitor cv) {
        this(393216, cv);
        if (this.getClass() != SerialVersionUIDAdder.class) {
            throw new IllegalStateException();
        }
    }
    
    protected SerialVersionUIDAdder(final int api, final ClassVisitor cv) {
        super(api, cv);
        this.svuidFields = new ArrayList<Item>();
        this.svuidConstructors = new ArrayList<Item>();
        this.svuidMethods = new ArrayList<Item>();
    }
    
    @Override
    public void visit(final int version, final int access, final String name, final String signature, final String superName, final String[] interfaces) {
        this.computeSVUID = ((access & 0x4000) == 0x0);
        if (this.computeSVUID) {
            this.name = name;
            this.access = access;
            System.arraycopy(interfaces, 0, this.interfaces = new String[interfaces.length], 0, interfaces.length);
        }
        super.visit(version, access, name, signature, superName, interfaces);
    }
    
    @Override
    public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
        if (this.computeSVUID) {
            if ("<clinit>".equals(name)) {
                this.hasStaticInitializer = true;
            }
            final int mods = access & 0xD3F;
            if ((access & 0x2) == 0x0) {
                if ("<init>".equals(name)) {
                    this.svuidConstructors.add(new Item(name, mods, desc));
                }
                else if (!"<clinit>".equals(name)) {
                    this.svuidMethods.add(new Item(name, mods, desc));
                }
            }
        }
        return super.visitMethod(access, name, desc, signature, exceptions);
    }
    
    @Override
    public FieldVisitor visitField(final int access, final String name, final String desc, final String signature, final Object value) {
        if (this.computeSVUID) {
            if ("serialVersionUID".equals(name)) {
                this.computeSVUID = false;
                this.hasSVUID = true;
            }
            if ((access & 0x2) == 0x0 || (access & 0x88) == 0x0) {
                final int mods = access & 0xDF;
                this.svuidFields.add(new Item(name, mods, desc));
            }
        }
        return super.visitField(access, name, desc, signature, value);
    }
    
    @Override
    public void visitInnerClass(final String aname, final String outerName, final String innerName, final int attr_access) {
        if (this.name != null && this.name.equals(aname)) {
            this.access = attr_access;
        }
        super.visitInnerClass(aname, outerName, innerName, attr_access);
    }
    
    @Override
    public void visitEnd() {
        if (this.computeSVUID && !this.hasSVUID) {
            try {
                this.addSVUID(this.computeSVUID());
            }
            catch (Throwable e) {
                throw new RuntimeException("Error while computing SVUID for " + this.name, e);
            }
        }
        super.visitEnd();
    }
    
    public boolean hasSVUID() {
        return this.hasSVUID;
    }
    
    protected void addSVUID(final long svuid) {
        final FieldVisitor fv = super.visitField(24, "serialVersionUID", "J", null, svuid);
        if (fv != null) {
            fv.visitEnd();
        }
    }
    
    protected long computeSVUID() throws IOException {
        DataOutputStream dos = null;
        long svuid = 0L;
        try {
            final ByteArrayOutputStream bos = new ByteArrayOutputStream();
            dos = new DataOutputStream(bos);
            dos.writeUTF(this.name.replace('/', '.'));
            int access = this.access;
            if ((access & 0x200) != 0x0) {
                access = ((this.svuidMethods.size() > 0) ? (access | 0x400) : (access & 0xFFFFFBFF));
            }
            dos.writeInt(access & 0x611);
            Arrays.sort(this.interfaces);
            for (int i = 0; i < this.interfaces.length; ++i) {
                dos.writeUTF(this.interfaces[i].replace('/', '.'));
            }
            writeItems(this.svuidFields, dos, false);
            if (this.hasStaticInitializer) {
                dos.writeUTF("<clinit>");
                dos.writeInt(8);
                dos.writeUTF("()V");
            }
            writeItems(this.svuidConstructors, dos, true);
            writeItems(this.svuidMethods, dos, true);
            dos.flush();
            final byte[] hashBytes = this.computeSHAdigest(bos.toByteArray());
            for (int j = Math.min(hashBytes.length, 8) - 1; j >= 0; --j) {
                svuid = (svuid << 8 | (long)(hashBytes[j] & 0xFF));
            }
        }
        finally {
            if (dos != null) {
                dos.close();
            }
        }
        return svuid;
    }
    
    protected byte[] computeSHAdigest(final byte[] value) {
        try {
            return MessageDigest.getInstance("SHA").digest(value);
        }
        catch (Exception e) {
            throw new UnsupportedOperationException(e.toString());
        }
    }
    
    private static void writeItems(final Collection<Item> itemCollection, final DataOutput dos, final boolean dotted) throws IOException {
        final int size = itemCollection.size();
        final Item[] items = itemCollection.toArray(new Item[size]);
        Arrays.sort(items);
        for (int i = 0; i < size; ++i) {
            dos.writeUTF(items[i].name);
            dos.writeInt(items[i].access);
            dos.writeUTF(dotted ? items[i].desc.replace('/', '.') : items[i].desc);
        }
    }
    
    private static class Item implements Comparable<Item>
    {
        final String name;
        final int access;
        final String desc;
        
        Item(final String name, final int access, final String desc) {
            this.name = name;
            this.access = access;
            this.desc = desc;
        }
        
        public int compareTo(final Item other) {
            int retVal = this.name.compareTo(other.name);
            if (retVal == 0) {
                retVal = this.desc.compareTo(other.desc);
            }
            return retVal;
        }
        
        @Override
        public boolean equals(final Object o) {
            return o instanceof Item && this.compareTo((Item)o) == 0;
        }
        
        @Override
        public int hashCode() {
            return (this.name + this.desc).hashCode();
        }
    }
}

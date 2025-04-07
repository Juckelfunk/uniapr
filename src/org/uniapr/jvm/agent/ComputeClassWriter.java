// 
// Decompiled by Procyon v0.5.36
// 

package org.uniapr.jvm.agent;

import java.io.InputStream;
import org.objectweb.asm.ClassReader;
import java.io.IOException;
import org.objectweb.asm.ClassWriter;

public class ComputeClassWriter extends ClassWriter
{
    private ClassLoader l;
    
    public ComputeClassWriter(final int flags) {
        super(flags);
        this.l = this.getClass().getClassLoader();
    }
    
    @Override
    protected String getCommonSuperClass(final String type1, final String type2) {
        try {
            ClassReader info1;
            try {
                info1 = this.typeInfo(type1);
            }
            catch (NullPointerException e) {
                throw new RuntimeException("Class not found: " + type1 + ": " + e.toString(), e);
            }
            ClassReader info2;
            try {
                info2 = this.typeInfo(type2);
            }
            catch (NullPointerException e) {
                throw new RuntimeException("Class not found: " + type2 + ": " + e.toString(), e);
            }
            if ((info1.getAccess() & 0x200) != 0x0) {
                if (this.typeImplements(type2, info2, type1)) {
                    return type1;
                }
                if ((info2.getAccess() & 0x200) != 0x0 && this.typeImplements(type1, info1, type2)) {
                    return type2;
                }
                return "java/lang/Object";
            }
            else if ((info2.getAccess() & 0x200) != 0x0) {
                if (this.typeImplements(type1, info1, type2)) {
                    return type2;
                }
                return "java/lang/Object";
            }
            else {
                final StringBuilder b1 = this.typeAncestors(type1, info1);
                final StringBuilder b2 = this.typeAncestors(type2, info2);
                String result = "java/lang/Object";
                int end1 = b1.length();
                int end2 = b2.length();
                while (true) {
                    final int start1 = b1.lastIndexOf(";", end1 - 1);
                    final int start2 = b2.lastIndexOf(";", end2 - 1);
                    if (start1 == -1 || start2 == -1 || end1 - start1 != end2 - start2) {
                        return result;
                    }
                    final String p1 = b1.substring(start1 + 1, end1);
                    final String p2 = b2.substring(start2 + 1, end2);
                    if (!p1.equals(p2)) {
                        return result;
                    }
                    result = p1;
                    end1 = start1;
                    end2 = start2;
                }
            }
        }
        catch (IOException e2) {
            throw new RuntimeException(e2.toString());
        }
        catch (NullPointerException e3) {
            throw new RuntimeException(e3.toString());
        }
    }
    
    private StringBuilder typeAncestors(String type, ClassReader info) throws IOException {
        final StringBuilder b = new StringBuilder();
        while (!"java/lang/Object".equals(type)) {
            b.append(';').append(type);
            type = info.getSuperName();
            info = this.typeInfo(type);
        }
        return b;
    }
    
    private boolean typeImplements(String type, ClassReader info, final String itf) throws IOException {
        while (!"java/lang/Object".equals(type)) {
            final String[] itfs = info.getInterfaces();
            for (int i = 0; i < itfs.length; ++i) {
                if (itfs[i].equals(itf)) {
                    return true;
                }
            }
            for (int i = 0; i < itfs.length; ++i) {
                if (this.typeImplements(itfs[i], this.typeInfo(itfs[i]), itf)) {
                    return true;
                }
            }
            type = info.getSuperName();
            info = this.typeInfo(type);
        }
        return false;
    }
    
    private ClassReader typeInfo(final String type) throws IOException, NullPointerException {
        final InputStream is = this.l.getResourceAsStream(type + ".class");
        try {
            if (is == null) {
                throw new NullPointerException("Class not found " + type);
            }
            return new ClassReader(is);
        }
        finally {
            if (is != null) {
                is.close();
            }
        }
    }
}

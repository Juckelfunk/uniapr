// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.classinfo;

import org.pitest.functional.Option;
import org.pitest.util.PitError;
import org.pitest.reloc.asm.ClassReader;
import java.util.Map;
import org.pitest.reloc.asm.ClassWriter;

public class ComputeClassWriter extends ClassWriter
{
    private final ClassByteArraySource bytes;
    private final Map<String, String> cache;
    
    public ComputeClassWriter(final ClassByteArraySource bytes, final Map<String, String> cache, final int flags) {
        super(flags);
        this.bytes = bytes;
        this.cache = cache;
    }
    
    @Override
    protected String getCommonSuperClass(final String type1, final String type2) {
        final String key = type1 + "!_!" + type2;
        final String previous = this.cache.get(key);
        if (previous != null) {
            return previous;
        }
        final ClassReader info1 = this.typeInfo(type1);
        final ClassReader info2 = this.typeInfo(type2);
        final String result = this.getCommonSuperClass(type1, info1, type2, info2);
        this.cache.put(key, result);
        return result;
    }
    
    private String getCommonSuperClass(final String type1, final ClassReader info1, final String type2, final ClassReader info2) {
        if (isInterface(info1)) {
            if (this.typeImplements(type2, info2, type1)) {
                return type1;
            }
            if (isInterface(info2)) {
                if (this.typeImplements(type1, info1, type2)) {
                    return type2;
                }
                return "java/lang/Object";
            }
        }
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
    
    private static boolean isInterface(final ClassReader info1) {
        return (info1.getAccess() & 0x200) != 0x0;
    }
    
    private StringBuilder typeAncestors(String type, ClassReader info) {
        final StringBuilder b = new StringBuilder();
        while (!"java/lang/Object".equals(type)) {
            b.append(';').append(type);
            type = info.getSuperName();
            info = this.typeInfo(type);
        }
        return b;
    }
    
    private boolean typeImplements(String type, ClassReader info, final String itf) {
        final String cleanItf = itf.replace(".", "/");
        while (!"java/lang/Object".equals(type)) {
            final String[] interfaces;
            final String[] itfs = interfaces = info.getInterfaces();
            for (final String itf2 : interfaces) {
                if (itf2.equals(cleanItf)) {
                    return true;
                }
            }
            for (final String itf2 : itfs) {
                if (this.typeImplements(itf2, this.typeInfo(itf2), cleanItf)) {
                    return true;
                }
            }
            type = info.getSuperName();
            info = this.typeInfo(type);
        }
        return false;
    }
    
    private ClassReader typeInfo(final String type) {
        final Option<byte[]> maybeBytes = this.bytes.getBytes(type);
        if (maybeBytes.hasNone()) {
            throw new PitError("Could not find class defintiion for " + type);
        }
        return new ClassReader(maybeBytes.value());
    }
}

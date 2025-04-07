// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.coverage;

import org.pitest.classinfo.ClassName;

public final class ClassLine
{
    private final ClassName clazz;
    private final int lineNumber;
    
    public ClassLine(final String clazz, final int lineNumber) {
        this(ClassName.fromString(clazz), lineNumber);
    }
    
    public ClassLine(final ClassName clazz, final int lineNumber) {
        this.clazz = clazz;
        this.lineNumber = lineNumber;
    }
    
    public ClassName getClassName() {
        return this.clazz;
    }
    
    public int getLineNumber() {
        return this.lineNumber;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + ((this.clazz == null) ? 0 : this.clazz.hashCode());
        result = 31 * result + this.lineNumber;
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final ClassLine other = (ClassLine)obj;
        if (this.clazz == null) {
            if (other.clazz != null) {
                return false;
            }
        }
        else if (!this.clazz.equals(other.clazz)) {
            return false;
        }
        return this.lineNumber == other.lineNumber;
    }
    
    @Override
    public String toString() {
        return "ClassLine [" + this.clazz + ":" + this.lineNumber + "]";
    }
}

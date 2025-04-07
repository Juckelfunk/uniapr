// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.engine;

import org.pitest.classinfo.ClassName;
import java.io.Serializable;

public final class Location implements Comparable<Location>, Serializable
{
    private static final long serialVersionUID = 1L;
    private final ClassName clazz;
    private final MethodName method;
    private final String methodDesc;
    
    public Location(final ClassName clazz, final MethodName method, final String methodDesc) {
        this.clazz = clazz;
        this.method = method;
        this.methodDesc = methodDesc;
    }
    
    public static Location location(final ClassName clazz, final MethodName method, final String methodDesc) {
        return new Location(clazz, method, methodDesc);
    }
    
    public ClassName getClassName() {
        return this.clazz;
    }
    
    public MethodName getMethodName() {
        return this.method;
    }
    
    public String getMethodDesc() {
        return this.methodDesc;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + ((this.clazz == null) ? 0 : this.clazz.hashCode());
        result = 31 * result + ((this.method == null) ? 0 : this.method.hashCode());
        result = 31 * result + ((this.methodDesc == null) ? 0 : this.methodDesc.hashCode());
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
        final Location other = (Location)obj;
        if (this.clazz == null) {
            if (other.clazz != null) {
                return false;
            }
        }
        else if (!this.clazz.equals(other.clazz)) {
            return false;
        }
        if (this.method == null) {
            if (other.method != null) {
                return false;
            }
        }
        else if (!this.method.equals(other.method)) {
            return false;
        }
        if (this.methodDesc == null) {
            if (other.methodDesc != null) {
                return false;
            }
        }
        else if (!this.methodDesc.equals(other.methodDesc)) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
        return "Location [clazz=" + this.clazz + ", method=" + this.method + ", methodDesc=" + this.methodDesc + "]";
    }
    
    public String describe() {
        return this.method.name();
    }
    
    @Override
    public int compareTo(final Location o) {
        int comp = this.clazz.compareTo(o.getClassName());
        if (comp != 0) {
            return comp;
        }
        comp = this.method.name().compareTo(o.getMethodName().name());
        if (comp != 0) {
            return comp;
        }
        return this.methodDesc.compareTo(o.getMethodDesc());
    }
}

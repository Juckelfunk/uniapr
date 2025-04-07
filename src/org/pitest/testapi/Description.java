// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.testapi;

import java.io.Serializable;

public final class Description implements Serializable
{
    private static final long serialVersionUID = 1L;
    private final String testClass;
    private final String name;
    
    public Description(final String name) {
        this(name, (String)null);
    }
    
    public Description(final String name, final Class<?> testClass) {
        this(name, testClass.getName());
    }
    
    public Description(final String name, final String testClass) {
        this.testClass = this.internIfNotNull(testClass);
        this.name = name;
    }
    
    private String internIfNotNull(final String string) {
        if (string == null) {
            return null;
        }
        return string.intern();
    }
    
    public String getFirstTestClass() {
        return this.testClass;
    }
    
    public String getQualifiedName() {
        if (this.testClass != null) {
            return this.getFirstTestClass() + "." + this.getName();
        }
        return this.getName();
    }
    
    public String getName() {
        return this.name;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + ((this.name == null) ? 0 : this.name.hashCode());
        result = 31 * result + ((this.testClass == null) ? 0 : this.testClass.hashCode());
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
        final Description other = (Description)obj;
        if (this.name == null) {
            if (other.name != null) {
                return false;
            }
        }
        else if (!this.name.equals(other.name)) {
            return false;
        }
        if (this.testClass == null) {
            if (other.testClass != null) {
                return false;
            }
        }
        else if (!this.testClass.equals(other.testClass)) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
        return "Description [testClass=" + this.testClass + ", name=" + this.name + "]";
    }
}

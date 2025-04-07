// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.model;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

public class PatternSet implements Serializable
{
    private List<String> includes;
    private List<String> excludes;
    
    public void addExclude(final String string) {
        if (!(string instanceof String)) {
            throw new ClassCastException("PatternSet.addExcludes(string) parameter must be instanceof " + String.class.getName());
        }
        this.getExcludes().add(string);
    }
    
    public void addInclude(final String string) {
        if (!(string instanceof String)) {
            throw new ClassCastException("PatternSet.addIncludes(string) parameter must be instanceof " + String.class.getName());
        }
        this.getIncludes().add(string);
    }
    
    public List<String> getExcludes() {
        if (this.excludes == null) {
            this.excludes = new ArrayList<String>();
        }
        return this.excludes;
    }
    
    public List<String> getIncludes() {
        if (this.includes == null) {
            this.includes = new ArrayList<String>();
        }
        return this.includes;
    }
    
    public void removeExclude(final String string) {
        if (!(string instanceof String)) {
            throw new ClassCastException("PatternSet.removeExcludes(string) parameter must be instanceof " + String.class.getName());
        }
        this.getExcludes().remove(string);
    }
    
    public void removeInclude(final String string) {
        if (!(string instanceof String)) {
            throw new ClassCastException("PatternSet.removeIncludes(string) parameter must be instanceof " + String.class.getName());
        }
        this.getIncludes().remove(string);
    }
    
    public void setExcludes(final List<String> excludes) {
        this.excludes = excludes;
    }
    
    public void setIncludes(final List<String> includes) {
        this.includes = includes;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("PatternSet [includes: {");
        for (final String str : this.getIncludes()) {
            sb.append(str).append(", ");
        }
        if (sb.substring(sb.length() - 2).equals(", ")) {
            sb.delete(sb.length() - 2, sb.length());
        }
        sb.append("}, excludes: {");
        for (final String str : this.getExcludes()) {
            sb.append(str).append(", ");
        }
        if (sb.substring(sb.length() - 2).equals(", ")) {
            sb.delete(sb.length() - 2, sb.length());
        }
        sb.append("}]");
        return sb.toString();
    }
}

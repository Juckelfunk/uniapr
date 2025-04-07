// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest;

import org.pitest.classinfo.ClassName;
import java.util.ArrayList;
import java.util.Collection;

public class ClassMutationResults
{
    private final Collection<MutationResult> mutations;
    
    public ClassMutationResults(final Collection<MutationResult> mutations) {
        (this.mutations = new ArrayList<MutationResult>()).addAll(mutations);
    }
    
    public String getFileName() {
        return this.mutations.iterator().next().getDetails().getFilename();
    }
    
    public Collection<MutationResult> getMutations() {
        return this.mutations;
    }
    
    public ClassName getMutatedClass() {
        return this.mutations.iterator().next().getDetails().getClassName();
    }
    
    public String getPackageName() {
        final ClassName name = this.getMutatedClass();
        final int lastDot = name.asJavaName().lastIndexOf(46);
        return (lastDot > 0) ? name.asJavaName().substring(0, lastDot) : "default";
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + ((this.mutations == null) ? 0 : this.mutations.hashCode());
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
        final ClassMutationResults other = (ClassMutationResults)obj;
        if (this.mutations == null) {
            if (other.mutations != null) {
                return false;
            }
        }
        else if (!this.mutations.equals(other.mutations)) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
        return "ClassMutationResults [mutations=" + this.mutations + "]";
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest;

import java.util.Iterator;
import org.pitest.classinfo.ClassName;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;
import java.util.Collection;
import java.util.List;

public final class MutationMetaData
{
    private final List<MutationResult> mutations;
    
    public MutationMetaData(final List<MutationResult> mutations) {
        this.mutations = mutations;
    }
    
    public Collection<MutationResult> getMutations() {
        return this.mutations;
    }
    
    public Collection<ClassMutationResults> toClassResults() {
        Collections.sort(this.mutations, comparator());
        final List<ClassMutationResults> cmrs = new ArrayList<ClassMutationResults>();
        final List<MutationResult> buffer = new ArrayList<MutationResult>();
        ClassName cn = null;
        for (final MutationResult each : this.mutations) {
            if (cn != null && !each.getDetails().getClassName().equals(cn)) {
                cmrs.add(new ClassMutationResults(buffer));
                buffer.clear();
            }
            cn = each.getDetails().getClassName();
            buffer.add(each);
        }
        if (!buffer.isEmpty()) {
            cmrs.add(new ClassMutationResults(buffer));
        }
        return cmrs;
    }
    
    private static Comparator<MutationResult> comparator() {
        return new Comparator<MutationResult>() {
            @Override
            public int compare(final MutationResult arg0, final MutationResult arg1) {
                return arg0.getDetails().getId().compareTo(arg1.getDetails().getId());
            }
        };
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
        final MutationMetaData other = (MutationMetaData)obj;
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
}

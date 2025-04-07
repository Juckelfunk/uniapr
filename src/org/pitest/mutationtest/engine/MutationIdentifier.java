// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.engine;

import org.pitest.classinfo.ClassName;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.io.Serializable;

public final class MutationIdentifier implements Comparable<MutationIdentifier>, Serializable
{
    private static final long serialVersionUID = 1L;
    private final Location location;
    private final List<Integer> indexes;
    private final String mutator;
    
    public MutationIdentifier(final Location location, final int index, final String mutatorUniqueId) {
        this(location, Collections.singleton(index), mutatorUniqueId);
    }
    
    public MutationIdentifier(final Location location, final Collection<Integer> indexes, final String mutatorUniqueId) {
        this.location = location;
        this.indexes = new ArrayList<Integer>(indexes);
        this.mutator = mutatorUniqueId;
    }
    
    public Location getLocation() {
        return this.location;
    }
    
    public String getMutator() {
        return this.mutator;
    }
    
    public int getFirstIndex() {
        return this.indexes.iterator().next();
    }
    
    @Override
    public String toString() {
        return "MutationIdentifier [location=" + this.location + ", indexes=" + this.indexes + ", mutator=" + this.mutator + "]";
    }
    
    public boolean matches(final MutationIdentifier id) {
        return this.location.equals(id.location) && this.mutator.equals(id.mutator) && this.indexes.contains(id.getFirstIndex());
    }
    
    public ClassName getClassName() {
        return this.location.getClassName();
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + ((this.indexes == null) ? 0 : this.indexes.hashCode());
        result = 31 * result + ((this.location == null) ? 0 : this.location.hashCode());
        result = 31 * result + ((this.mutator == null) ? 0 : this.mutator.hashCode());
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
        final MutationIdentifier other = (MutationIdentifier)obj;
        if (this.indexes == null) {
            if (other.indexes != null) {
                return false;
            }
        }
        else if (!this.indexes.equals(other.indexes)) {
            return false;
        }
        if (this.location == null) {
            if (other.location != null) {
                return false;
            }
        }
        else if (!this.location.equals(other.location)) {
            return false;
        }
        if (this.mutator == null) {
            if (other.mutator != null) {
                return false;
            }
        }
        else if (!this.mutator.equals(other.mutator)) {
            return false;
        }
        return true;
    }
    
    @Override
    public int compareTo(final MutationIdentifier other) {
        int comp = this.location.compareTo(other.getLocation());
        if (comp != 0) {
            return comp;
        }
        comp = this.mutator.compareTo(other.getMutator());
        if (comp != 0) {
            return comp;
        }
        return this.indexes.get(0).compareTo(other.indexes.get(0));
    }
}

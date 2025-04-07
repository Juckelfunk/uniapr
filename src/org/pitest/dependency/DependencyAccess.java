// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.dependency;

final class DependencyAccess
{
    private final Member source;
    private final Member dest;
    
    protected DependencyAccess(final Member source, final Member dest) {
        this.source = source;
        this.dest = dest;
    }
    
    public Member getSource() {
        return this.source;
    }
    
    public Member getDest() {
        return this.dest;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + ((this.dest == null) ? 0 : this.dest.hashCode());
        result = 31 * result + ((this.source == null) ? 0 : this.source.hashCode());
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
        final DependencyAccess other = (DependencyAccess)obj;
        if (this.dest == null) {
            if (other.dest != null) {
                return false;
            }
        }
        else if (!this.dest.equals(other.dest)) {
            return false;
        }
        if (this.source == null) {
            if (other.source != null) {
                return false;
            }
        }
        else if (!this.source.equals(other.source)) {
            return false;
        }
        return true;
    }
    
    static class Member implements Comparable<Member>
    {
        private final String owner;
        private final String name;
        
        protected Member(final String owner, final String name) {
            this.owner = owner;
            this.name = name;
        }
        
        public String getOwner() {
            return this.owner;
        }
        
        public String getName() {
            return this.name;
        }
        
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = 31 * result + ((this.name == null) ? 0 : this.name.hashCode());
            result = 31 * result + ((this.owner == null) ? 0 : this.owner.hashCode());
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
            final Member other = (Member)obj;
            if (this.name == null) {
                if (other.name != null) {
                    return false;
                }
            }
            else if (!this.name.equals(other.name)) {
                return false;
            }
            if (this.owner == null) {
                if (other.owner != null) {
                    return false;
                }
            }
            else if (!this.owner.equals(other.owner)) {
                return false;
            }
            return true;
        }
        
        @Override
        public int compareTo(final Member other) {
            return other.name.compareTo(this.name) * 100 + other.owner.compareTo(this.owner) * 1000;
        }
    }
}

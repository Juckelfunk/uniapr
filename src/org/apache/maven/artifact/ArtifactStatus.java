// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.artifact;

import java.util.HashMap;
import java.util.Map;

public final class ArtifactStatus implements Comparable
{
    public static final ArtifactStatus NONE;
    public static final ArtifactStatus GENERATED;
    public static final ArtifactStatus CONVERTED;
    public static final ArtifactStatus PARTNER;
    public static final ArtifactStatus DEPLOYED;
    public static final ArtifactStatus VERIFIED;
    private final int rank;
    private final String key;
    private static Map map;
    
    private ArtifactStatus(final String key, final int rank) {
        this.rank = rank;
        this.key = key;
        if (ArtifactStatus.map == null) {
            ArtifactStatus.map = new HashMap();
        }
        ArtifactStatus.map.put(key, this);
    }
    
    public static ArtifactStatus valueOf(final String status) {
        ArtifactStatus retVal = null;
        if (status != null) {
            retVal = ArtifactStatus.map.get(status);
        }
        return (retVal != null) ? retVal : ArtifactStatus.NONE;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final ArtifactStatus that = (ArtifactStatus)o;
        return this.rank == that.rank;
    }
    
    @Override
    public int hashCode() {
        return this.rank;
    }
    
    @Override
    public String toString() {
        return this.key;
    }
    
    public int compareTo(final Object o) {
        final ArtifactStatus s = (ArtifactStatus)o;
        return this.rank - s.rank;
    }
    
    static {
        NONE = new ArtifactStatus("none", 0);
        GENERATED = new ArtifactStatus("generated", 1);
        CONVERTED = new ArtifactStatus("converted", 2);
        PARTNER = new ArtifactStatus("partner", 3);
        DEPLOYED = new ArtifactStatus("deployed", 4);
        VERIFIED = new ArtifactStatus("verified", 5);
    }
}

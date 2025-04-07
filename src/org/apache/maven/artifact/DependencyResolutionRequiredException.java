// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.artifact;

public class DependencyResolutionRequiredException extends Exception
{
    public DependencyResolutionRequiredException(final Artifact artifact) {
        super("Attempted to access the artifact " + artifact + "; which has not yet been resolved");
    }
}

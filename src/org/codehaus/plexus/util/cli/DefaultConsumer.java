// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.util.cli;

public class DefaultConsumer implements StreamConsumer
{
    public void consumeLine(final String line) {
        System.out.println(line);
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.wagon;

public interface CommandExecutor extends Wagon
{
    public static final String ROLE = ((CommandExecutor$1.class$org$apache$maven$wagon$CommandExecutor == null) ? (CommandExecutor$1.class$org$apache$maven$wagon$CommandExecutor = CommandExecutor$1.class$("org.apache.maven.wagon.CommandExecutor")) : CommandExecutor$1.class$org$apache$maven$wagon$CommandExecutor).getName();
    
    void executeCommand(final String p0) throws CommandExecutionException;
    
    Streams executeCommand(final String p0, final boolean p1) throws CommandExecutionException;
}

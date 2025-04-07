// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.wagon.observers;

import org.apache.maven.wagon.events.TransferEvent;
import org.apache.maven.wagon.events.SessionEvent;
import java.io.PrintStream;
import org.apache.maven.wagon.events.TransferListener;
import org.apache.maven.wagon.events.SessionListener;

public class Debug implements SessionListener, TransferListener
{
    private PrintStream out;
    long timestamp;
    long transfer;
    
    public Debug() {
        this(System.out);
    }
    
    public Debug(final PrintStream out) {
        this.out = out;
    }
    
    public void sessionOpening(final SessionEvent sessionEvent) {
    }
    
    public void sessionOpened(final SessionEvent sessionEvent) {
        this.out.println(sessionEvent.getWagon().getRepository().getUrl() + " - Session: Opened  ");
    }
    
    public void sessionDisconnecting(final SessionEvent sessionEvent) {
        this.out.println(sessionEvent.getWagon().getRepository().getUrl() + " - Session: Disconnecting  ");
    }
    
    public void sessionDisconnected(final SessionEvent sessionEvent) {
        this.out.println(sessionEvent.getWagon().getRepository().getUrl() + " - Session: Disconnected");
    }
    
    public void sessionConnectionRefused(final SessionEvent sessionEvent) {
        this.out.println(sessionEvent.getWagon().getRepository().getUrl() + " - Session: Connection refused");
    }
    
    public void sessionLoggedIn(final SessionEvent sessionEvent) {
        this.out.println(sessionEvent.getWagon().getRepository().getUrl() + " - Session: Logged in");
    }
    
    public void sessionLoggedOff(final SessionEvent sessionEvent) {
        this.out.println(sessionEvent.getWagon().getRepository().getUrl() + " - Session: Logged off");
    }
    
    public void debug(final String message) {
        this.out.println(message);
    }
    
    public void transferInitiated(final TransferEvent transferEvent) {
    }
    
    public void transferStarted(final TransferEvent transferEvent) {
        this.timestamp = transferEvent.getTimestamp();
        this.transfer = 0L;
        if (transferEvent.getRequestType() == 5) {
            final String message = "Downloading: " + transferEvent.getResource().getName() + " from " + transferEvent.getWagon().getRepository().getUrl();
            this.out.println(message);
            this.out.println("");
        }
        else {
            final String message = "Uploading: " + transferEvent.getResource().getName() + " to " + transferEvent.getWagon().getRepository().getUrl();
            this.out.println(message);
            this.out.println("");
        }
    }
    
    public void transferProgress(final TransferEvent transferEvent, final byte[] buffer, final int length) {
        this.out.print("#");
        this.transfer += length;
    }
    
    public void transferCompleted(final TransferEvent transferEvent) {
        final double duration = (transferEvent.getTimestamp() - this.timestamp) / 1000.0;
        this.out.println();
        final String message = "Transfer finished. " + this.transfer + " bytes copied in " + duration + " seconds";
        this.out.println(message);
    }
    
    public void transferError(final TransferEvent transferEvent) {
        this.out.println(" Transfer error: " + transferEvent.getException());
    }
    
    public void sessionError(final SessionEvent sessionEvent) {
        this.out.println(" Session error: " + sessionEvent.getException());
    }
    
    public PrintStream getOut() {
        return this.out;
    }
}

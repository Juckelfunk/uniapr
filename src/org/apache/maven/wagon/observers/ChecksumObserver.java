// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.wagon.observers;

import org.apache.maven.wagon.events.TransferEvent;
import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;
import org.apache.maven.wagon.events.TransferListener;

public class ChecksumObserver implements TransferListener
{
    private MessageDigest digester;
    private String actualChecksum;
    
    public ChecksumObserver() throws NoSuchAlgorithmException {
        this("MD5");
    }
    
    public ChecksumObserver(final String algorithm) throws NoSuchAlgorithmException {
        this.digester = null;
        this.digester = MessageDigest.getInstance(algorithm);
    }
    
    public void transferInitiated(final TransferEvent transferEvent) {
    }
    
    public void transferStarted(final TransferEvent transferEvent) {
        this.actualChecksum = null;
        this.digester.reset();
    }
    
    public void transferProgress(final TransferEvent transferEvent, final byte[] buffer, final int length) {
        this.digester.update(buffer, 0, length);
    }
    
    public void transferCompleted(final TransferEvent transferEvent) {
        this.actualChecksum = this.encode(this.digester.digest());
    }
    
    public void transferError(final TransferEvent transferEvent) {
        this.digester.reset();
        this.actualChecksum = null;
    }
    
    public void debug(final String message) {
    }
    
    public String getActualChecksum() {
        return this.actualChecksum;
    }
    
    protected String encode(final byte[] binaryData) {
        if (binaryData.length != 16 && binaryData.length != 20) {
            final int bitLength = binaryData.length * 8;
            throw new IllegalArgumentException("Unrecognised length for binary data: " + bitLength + " bits");
        }
        String retValue = "";
        for (int i = 0; i < binaryData.length; ++i) {
            final String t = Integer.toHexString(binaryData[i] & 0xFF);
            if (t.length() == 1) {
                retValue = retValue + "0" + t;
            }
            else {
                retValue += t;
            }
        }
        return retValue.trim();
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.wagon.events;

import org.apache.maven.wagon.Wagon;
import java.util.EventObject;

public class WagonEvent extends EventObject
{
    protected long timestamp;
    
    public WagonEvent(final Wagon source) {
        super(source);
    }
    
    public Wagon getWagon() {
        return (Wagon)this.getSource();
    }
    
    public long getTimestamp() {
        return this.timestamp;
    }
    
    public void setTimestamp(final long timestamp) {
        this.timestamp = timestamp;
    }
}

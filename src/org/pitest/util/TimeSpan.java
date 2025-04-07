// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.util;

public class TimeSpan
{
    private long start;
    private long end;
    
    public TimeSpan(final long start, final long end) {
        this.start = start;
        this.end = end;
    }
    
    public long duration() {
        return this.end - this.start;
    }
    
    public long getStart() {
        return this.start;
    }
    
    public long getEnd() {
        return this.end;
    }
    
    public void setStart(final long start) {
        this.start = start;
    }
    
    public void setEnd(final long end) {
        this.end = end;
    }
    
    @Override
    public String toString() {
        final long millis = this.duration();
        final int seconds = (int)(millis / 1000L) % 60;
        final int minutes = (int)(millis / 60000L % 60L);
        final int hours = (int)(millis / 3600000L % 24L);
        if (hours != 0) {
            return "" + hours + " hours, " + minutes + " minutes and " + seconds + " seconds";
        }
        if (minutes != 0) {
            return "" + minutes + " minutes and " + seconds + " seconds";
        }
        if (seconds != 0) {
            return "" + seconds + " seconds";
        }
        return "< 1 second";
    }
}

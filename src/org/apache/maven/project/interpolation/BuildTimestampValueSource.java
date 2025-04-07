// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.project.interpolation;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.codehaus.plexus.interpolation.AbstractValueSource;

public class BuildTimestampValueSource extends AbstractValueSource
{
    private final Date startTime;
    private final String format;
    private String formattedDate;
    
    public BuildTimestampValueSource(final Date startTime, final String format) {
        super(false);
        this.startTime = startTime;
        this.format = format;
    }
    
    public Object getValue(final String expression) {
        if ("build.timestamp".equals(expression) || "maven.build.timestamp".equals(expression)) {
            if (this.formattedDate == null && this.startTime != null) {
                this.formattedDate = new SimpleDateFormat(this.format).format(this.startTime);
            }
            return this.formattedDate;
        }
        return null;
    }
}

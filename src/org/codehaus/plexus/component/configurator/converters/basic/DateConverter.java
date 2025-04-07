// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.component.configurator.converters.basic;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.ParseException;
import java.text.DateFormat;

public class DateConverter extends AbstractBasicConverter
{
    private static final DateFormat[] formats;
    
    public boolean canConvert(final Class type) {
        return type.equals(Date.class);
    }
    
    public Object fromString(final String str) {
        int i = 0;
        while (i < DateConverter.formats.length) {
            try {
                return DateConverter.formats[i].parse(str);
            }
            catch (ParseException e) {
                ++i;
                continue;
            }
            break;
        }
        return null;
    }
    
    public String toString(final Object obj) {
        final Date date = (Date)obj;
        return DateConverter.formats[0].format(date);
    }
    
    static {
        formats = new DateFormat[] { new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S a"), new SimpleDateFormat("yyyy-MM-dd HH:mm:ssa") };
    }
}

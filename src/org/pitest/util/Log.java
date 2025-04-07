// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.util;

import java.io.Writer;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.logging.LogRecord;
import java.text.DateFormat;
import java.util.logging.Formatter;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Handler;
import java.util.logging.Logger;

public class Log
{
    private static final Logger LOGGER;
    
    public static Logger getLogger() {
        return Log.LOGGER;
    }
    
    private static void addOrSetHandler(final Handler handler) {
        if (Log.LOGGER.getHandlers().length == 0) {
            Log.LOGGER.addHandler(handler);
        }
        else {
            Log.LOGGER.getHandlers()[0] = handler;
        }
    }
    
    public static void setVerbose(final boolean on) {
        if (on) {
            setLevel(Level.FINEST);
        }
        else {
            setLevel(Level.INFO);
        }
    }
    
    private static void setLevel(final Level level) {
        Log.LOGGER.setLevel(level);
        for (final Handler each : Log.LOGGER.getHandlers()) {
            each.setLevel(level);
        }
    }
    
    public static boolean isVerbose() {
        return Level.FINEST.equals(Log.LOGGER.getLevel());
    }
    
    static {
        LOGGER = Logger.getLogger("PIT");
        if (System.getProperty("java.util.logging.config.file") == null && System.getProperty("java.util.logging.config.class") == null) {
            Log.LOGGER.setUseParentHandlers(false);
            final Handler handler = new ConsoleHandler();
            handler.setFormatter(new PlainFormatter());
            addOrSetHandler(handler);
            Log.LOGGER.setLevel(Level.INFO);
            handler.setLevel(Level.ALL);
        }
    }
    
    static class PlainFormatter extends Formatter
    {
        private static final String LINE_SEPARATOR;
        private final DateFormat dateFormat;
        
        PlainFormatter() {
            this.dateFormat = DateFormat.getTimeInstance();
        }
        
        @Override
        public String format(final LogRecord record) {
            final StringBuilder buf = new StringBuilder(180);
            buf.append(this.dateFormat.format(new Date(record.getMillis())));
            buf.append(" PIT >> ");
            buf.append(record.getLevel());
            buf.append(" : ");
            buf.append(this.formatMessage(record));
            buf.append(PlainFormatter.LINE_SEPARATOR);
            final Throwable throwable = record.getThrown();
            if (throwable != null) {
                final StringWriter sink = new StringWriter();
                throwable.printStackTrace(new PrintWriter(sink, true));
                buf.append(sink.toString());
            }
            return buf.toString();
        }
        
        static {
            LINE_SEPARATOR = System.getProperty("line.separator");
        }
    }
}

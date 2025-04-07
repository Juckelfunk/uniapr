// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.util;

import java.util.LinkedList;
import java.util.StringTokenizer;
import java.io.Writer;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;

public class ExceptionUtils
{
    static final String WRAPPED_MARKER = " [wrapped] ";
    protected static String[] CAUSE_METHOD_NAMES;
    
    protected ExceptionUtils() {
    }
    
    public static void addCauseMethodName(final String methodName) {
        if (methodName != null && methodName.length() > 0) {
            final List list = new ArrayList(Arrays.asList(ExceptionUtils.CAUSE_METHOD_NAMES));
            list.add(methodName);
            ExceptionUtils.CAUSE_METHOD_NAMES = list.toArray(new String[list.size()]);
        }
    }
    
    public static Throwable getCause(final Throwable throwable) {
        return getCause(throwable, ExceptionUtils.CAUSE_METHOD_NAMES);
    }
    
    public static Throwable getCause(final Throwable throwable, final String[] methodNames) {
        Throwable cause = getCauseUsingWellKnownTypes(throwable);
        if (cause == null) {
            for (int i = 0; i < methodNames.length; ++i) {
                cause = getCauseUsingMethodName(throwable, methodNames[i]);
                if (cause != null) {
                    break;
                }
            }
            if (cause == null) {
                cause = getCauseUsingFieldName(throwable, "detail");
            }
        }
        return cause;
    }
    
    public static Throwable getRootCause(Throwable throwable) {
        Throwable cause = getCause(throwable);
        if (cause != null) {
            throwable = cause;
            while ((throwable = getCause(throwable)) != null) {
                cause = throwable;
            }
        }
        return cause;
    }
    
    private static Throwable getCauseUsingWellKnownTypes(final Throwable throwable) {
        if (throwable instanceof SQLException) {
            return ((SQLException)throwable).getNextException();
        }
        if (throwable instanceof InvocationTargetException) {
            return ((InvocationTargetException)throwable).getTargetException();
        }
        return null;
    }
    
    private static Throwable getCauseUsingMethodName(final Throwable throwable, final String methodName) {
        Method method = null;
        try {
            method = throwable.getClass().getMethod(methodName, (Class<?>[])null);
        }
        catch (NoSuchMethodException ignored) {}
        catch (SecurityException ex) {}
        if (method != null && Throwable.class.isAssignableFrom(method.getReturnType())) {
            try {
                return (Throwable)method.invoke(throwable, new Object[0]);
            }
            catch (IllegalAccessException ignored2) {}
            catch (IllegalArgumentException ignored3) {}
            catch (InvocationTargetException ex2) {}
        }
        return null;
    }
    
    private static Throwable getCauseUsingFieldName(final Throwable throwable, final String fieldName) {
        Field field = null;
        try {
            field = throwable.getClass().getField(fieldName);
        }
        catch (NoSuchFieldException ignored) {}
        catch (SecurityException ex) {}
        if (field != null && Throwable.class.isAssignableFrom(field.getType())) {
            try {
                return (Throwable)field.get(throwable);
            }
            catch (IllegalAccessException ignored2) {}
            catch (IllegalArgumentException ex2) {}
        }
        return null;
    }
    
    public static int getThrowableCount(Throwable throwable) {
        int count = 0;
        while (throwable != null) {
            ++count;
            throwable = getCause(throwable);
        }
        return count;
    }
    
    public static Throwable[] getThrowables(Throwable throwable) {
        final List list = new ArrayList();
        while (throwable != null) {
            list.add(throwable);
            throwable = getCause(throwable);
        }
        return list.toArray(new Throwable[list.size()]);
    }
    
    public static int indexOfThrowable(final Throwable throwable, final Class type) {
        return indexOfThrowable(throwable, type, 0);
    }
    
    public static int indexOfThrowable(final Throwable throwable, final Class type, final int fromIndex) {
        if (fromIndex < 0) {
            throw new IndexOutOfBoundsException("Throwable index out of range: " + fromIndex);
        }
        final Throwable[] throwables = getThrowables(throwable);
        if (fromIndex >= throwables.length) {
            throw new IndexOutOfBoundsException("Throwable index out of range: " + fromIndex);
        }
        for (int i = fromIndex; i < throwables.length; ++i) {
            if (throwables[i].getClass().equals(type)) {
                return i;
            }
        }
        return -1;
    }
    
    public static void printRootCauseStackTrace(final Throwable t, final PrintStream stream) {
        final String[] trace = getRootCauseStackTrace(t);
        for (int i = 0; i < trace.length; ++i) {
            stream.println(trace[i]);
        }
        stream.flush();
    }
    
    public static void printRootCauseStackTrace(final Throwable t) {
        printRootCauseStackTrace(t, System.err);
    }
    
    public static void printRootCauseStackTrace(final Throwable t, final PrintWriter writer) {
        final String[] trace = getRootCauseStackTrace(t);
        for (int i = 0; i < trace.length; ++i) {
            writer.println(trace[i]);
        }
        writer.flush();
    }
    
    public static String[] getRootCauseStackTrace(final Throwable t) {
        final Throwable[] throwables = getThrowables(t);
        final int count = throwables.length;
        final ArrayList frames = new ArrayList();
        List nextTrace = getStackFrameList(throwables[count - 1]);
        int i = count;
        while (--i >= 0) {
            final List trace = nextTrace;
            if (i != 0) {
                nextTrace = getStackFrameList(throwables[i - 1]);
                removeCommonFrames(trace, nextTrace);
            }
            if (i == count - 1) {
                frames.add(throwables[i].toString());
            }
            else {
                frames.add(" [wrapped] " + throwables[i].toString());
            }
            for (int j = 0; j < trace.size(); ++j) {
                frames.add(trace.get(j));
            }
        }
        return frames.toArray(new String[0]);
    }
    
    private static void removeCommonFrames(final List causeFrames, final List wrapperFrames) {
        for (int causeFrameIndex = causeFrames.size() - 1, wrapperFrameIndex = wrapperFrames.size() - 1; causeFrameIndex >= 0 && wrapperFrameIndex >= 0; --causeFrameIndex, --wrapperFrameIndex) {
            final String causeFrame = causeFrames.get(causeFrameIndex);
            final String wrapperFrame = wrapperFrames.get(wrapperFrameIndex);
            if (causeFrame.equals(wrapperFrame)) {
                causeFrames.remove(causeFrameIndex);
            }
        }
    }
    
    public static String getStackTrace(final Throwable t) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw, true);
        t.printStackTrace(pw);
        return sw.getBuffer().toString();
    }
    
    public static String getFullStackTrace(final Throwable t) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw, true);
        final Throwable[] ts = getThrowables(t);
        for (int i = 0; i < ts.length; ++i) {
            ts[i].printStackTrace(pw);
            if (isNestedThrowable(ts[i])) {
                break;
            }
        }
        return sw.getBuffer().toString();
    }
    
    public static boolean isNestedThrowable(final Throwable throwable) {
        if (throwable == null) {
            return false;
        }
        if (throwable instanceof SQLException) {
            return true;
        }
        if (throwable instanceof InvocationTargetException) {
            return true;
        }
        for (int sz = ExceptionUtils.CAUSE_METHOD_NAMES.length, i = 0; i < sz; ++i) {
            try {
                final Method method = throwable.getClass().getMethod(ExceptionUtils.CAUSE_METHOD_NAMES[i], (Class<?>[])null);
                if (method != null) {
                    return true;
                }
            }
            catch (NoSuchMethodException ignored) {}
            catch (SecurityException ex) {}
        }
        try {
            final Field field = throwable.getClass().getField("detail");
            if (field != null) {
                return true;
            }
        }
        catch (NoSuchFieldException ignored2) {}
        catch (SecurityException ex2) {}
        return false;
    }
    
    public static String[] getStackFrames(final Throwable t) {
        return getStackFrames(getStackTrace(t));
    }
    
    static String[] getStackFrames(final String stackTrace) {
        final String linebreak = System.getProperty("line.separator");
        final StringTokenizer frames = new StringTokenizer(stackTrace, linebreak);
        final List list = new LinkedList();
        while (frames.hasMoreTokens()) {
            list.add(frames.nextToken());
        }
        return list.toArray(new String[0]);
    }
    
    static List getStackFrameList(final Throwable t) {
        final String stackTrace = getStackTrace(t);
        final String linebreak = System.getProperty("line.separator");
        final StringTokenizer frames = new StringTokenizer(stackTrace, linebreak);
        final List list = new LinkedList();
        boolean traceStarted = false;
        while (frames.hasMoreTokens()) {
            final String token = frames.nextToken();
            final int at = token.indexOf("at");
            if (at != -1 && token.substring(0, at).trim().length() == 0) {
                traceStarted = true;
                list.add(token);
            }
            else {
                if (traceStarted) {
                    break;
                }
                continue;
            }
        }
        return list;
    }
    
    static {
        ExceptionUtils.CAUSE_METHOD_NAMES = new String[] { "getCause", "getNextException", "getTargetException", "getException", "getSourceException", "getRootCause", "getCausedByException", "getNested" };
    }
}

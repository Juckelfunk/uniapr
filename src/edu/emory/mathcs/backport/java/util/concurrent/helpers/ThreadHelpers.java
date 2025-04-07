// 
// Decompiled by Procyon v0.5.36
// 

package edu.emory.mathcs.backport.java.util.concurrent.helpers;

public class ThreadHelpers
{
    private ThreadHelpers() {
    }
    
    public static Runnable assignExceptionHandler(final Runnable runnable, final UncaughtExceptionHandler handler) {
        if (runnable == null || handler == null) {
            throw new NullPointerException();
        }
        return new Runnable() {
            public void run() {
                try {
                    runnable.run();
                }
                catch (Throwable error) {
                    try {
                        handler.uncaughtException(Thread.currentThread(), error);
                    }
                    catch (Throwable t) {}
                }
            }
        };
    }
    
    public interface UncaughtExceptionHandler
    {
        void uncaughtException(final Thread p0, final Throwable p1);
    }
}

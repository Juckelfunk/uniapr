// 
// Decompiled by Procyon v0.5.36
// 

package org.uniapr.jvm.core;

import java.io.Writer;
import java.io.PrintWriter;
import java.io.FileWriter;

public class Utils
{
    public static void log(final String s) {
        try {
            final FileWriter fileWriter = new FileWriter("log.log", true);
            final PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.println(s);
            printWriter.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}

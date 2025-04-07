// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.execute;

public abstract class CommandLineMessage
{
    public static void report(final String message) {
        final StringBuilder sb = new StringBuilder();
        sb.append("---------------------------------------------------------------------------------\n");
        sb.append(message + "\n");
        sb.append("---------------------------------------------------------------------------------\n");
        System.out.println(sb);
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.wagon.proxy;

import java.util.StringTokenizer;

public final class ProxyUtils
{
    private ProxyUtils() {
    }
    
    public static boolean validateNonProxyHosts(final ProxyInfo proxy, String targetHost) {
        if (targetHost == null) {
            targetHost = new String();
        }
        if (proxy == null) {
            return false;
        }
        final String nonProxyHosts = proxy.getNonProxyHosts();
        if (nonProxyHosts == null) {
            return false;
        }
        final StringTokenizer tokenizer = new StringTokenizer(nonProxyHosts, "|");
        while (tokenizer.hasMoreTokens()) {
            String pattern = tokenizer.nextToken();
            pattern = pattern.replaceAll("\\.", "\\\\.").replaceAll("\\*", ".*");
            if (targetHost.matches(pattern)) {
                return true;
            }
        }
        return false;
    }
}

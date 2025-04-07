// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.interpolation.util;

import java.util.Iterator;
import java.util.Collection;

public final class ValueSourceUtils
{
    private ValueSourceUtils() {
    }
    
    public static String trimPrefix(final String expression, final Collection possiblePrefixes, final boolean allowUnprefixedExpressions) {
        if (expression == null) {
            return null;
        }
        String realExpr = null;
        for (final String prefix : possiblePrefixes) {
            if (expression.startsWith(prefix)) {
                realExpr = expression.substring(prefix.length());
                if (realExpr.startsWith(".")) {
                    realExpr = realExpr.substring(1);
                    break;
                }
                break;
            }
        }
        if (realExpr == null && allowUnprefixedExpressions) {
            realExpr = expression;
        }
        return realExpr;
    }
}

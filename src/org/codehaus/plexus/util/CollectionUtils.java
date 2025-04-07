// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.util;

import java.util.NoSuchElementException;
import java.util.List;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CollectionUtils
{
    public static Map mergeMaps(final Map dominantMap, final Map recessiveMap) {
        if (dominantMap == null && recessiveMap == null) {
            return null;
        }
        if (dominantMap != null && recessiveMap == null) {
            return dominantMap;
        }
        if (dominantMap == null && recessiveMap != null) {
            return recessiveMap;
        }
        final Map result = new HashMap();
        final Set dominantMapKeys = dominantMap.keySet();
        final Set recessiveMapKeys = recessiveMap.keySet();
        final Collection contributingRecessiveKeys = subtract(recessiveMapKeys, intersection(dominantMapKeys, recessiveMapKeys));
        result.putAll(dominantMap);
        for (final Object key : contributingRecessiveKeys) {
            result.put(key, recessiveMap.get(key));
        }
        return result;
    }
    
    public static Map mergeMaps(final Map[] maps) {
        Map result = null;
        if (maps.length == 0) {
            result = null;
        }
        else if (maps.length == 1) {
            result = maps[0];
        }
        else {
            result = mergeMaps(maps[0], maps[1]);
            for (int i = 2; i < maps.length; ++i) {
                result = mergeMaps(result, maps[i]);
            }
        }
        return result;
    }
    
    public static Collection intersection(final Collection a, final Collection b) {
        final ArrayList list = new ArrayList();
        final Map mapa = getCardinalityMap(a);
        final Map mapb = getCardinalityMap(b);
        final Set elts = new HashSet(a);
        elts.addAll(b);
        for (final Object obj : elts) {
            for (int i = 0, m = Math.min(getFreq(obj, mapa), getFreq(obj, mapb)); i < m; ++i) {
                list.add(obj);
            }
        }
        return list;
    }
    
    public static Collection subtract(final Collection a, final Collection b) {
        final ArrayList list = new ArrayList(a);
        final Iterator it = b.iterator();
        while (it.hasNext()) {
            list.remove(it.next());
        }
        return list;
    }
    
    public static Map getCardinalityMap(final Collection col) {
        final HashMap count = new HashMap();
        for (final Object obj : col) {
            final Integer c = count.get(obj);
            if (null == c) {
                count.put(obj, new Integer(1));
            }
            else {
                count.put(obj, new Integer(c + 1));
            }
        }
        return count;
    }
    
    public static List iteratorToList(final Iterator it) {
        if (it == null) {
            throw new NullPointerException("it cannot be null.");
        }
        final List list = new ArrayList();
        while (it.hasNext()) {
            list.add(it.next());
        }
        return list;
    }
    
    private static final int getFreq(final Object obj, final Map freqMap) {
        try {
            final Object o = freqMap.get(obj);
            if (o != null) {
                return (int)o;
            }
        }
        catch (NullPointerException e) {}
        catch (NoSuchElementException ex) {}
        return 0;
    }
}

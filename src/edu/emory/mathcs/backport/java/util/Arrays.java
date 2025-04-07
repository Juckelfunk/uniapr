// 
// Decompiled by Procyon v0.5.36
// 

package edu.emory.mathcs.backport.java.util;

import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.Array;
import java.util.Comparator;

public class Arrays
{
    private Arrays() {
    }
    
    public static void sort(final long[] a) {
        java.util.Arrays.sort(a);
    }
    
    public static void sort(final long[] a, final int fromIndex, final int toIndex) {
        java.util.Arrays.sort(a, fromIndex, toIndex);
    }
    
    public static void sort(final int[] a) {
        java.util.Arrays.sort(a);
    }
    
    public static void sort(final int[] a, final int fromIndex, final int toIndex) {
        java.util.Arrays.sort(a, fromIndex, toIndex);
    }
    
    public static void sort(final short[] a) {
        java.util.Arrays.sort(a);
    }
    
    public static void sort(final short[] a, final int fromIndex, final int toIndex) {
        java.util.Arrays.sort(a, fromIndex, toIndex);
    }
    
    public static void sort(final char[] a) {
        java.util.Arrays.sort(a);
    }
    
    public static void sort(final char[] a, final int fromIndex, final int toIndex) {
        java.util.Arrays.sort(a, fromIndex, toIndex);
    }
    
    public static void sort(final byte[] a) {
        java.util.Arrays.sort(a);
    }
    
    public static void sort(final byte[] a, final int fromIndex, final int toIndex) {
        java.util.Arrays.sort(a, fromIndex, toIndex);
    }
    
    public static void sort(final double[] a) {
        java.util.Arrays.sort(a);
    }
    
    public static void sort(final double[] a, final int fromIndex, final int toIndex) {
        java.util.Arrays.sort(a, fromIndex, toIndex);
    }
    
    public static void sort(final float[] a) {
        java.util.Arrays.sort(a);
    }
    
    public static void sort(final float[] a, final int fromIndex, final int toIndex) {
        java.util.Arrays.sort(a, fromIndex, toIndex);
    }
    
    public static void sort(final Object[] a) {
        java.util.Arrays.sort(a);
    }
    
    public static void sort(final Object[] a, final int fromIndex, final int toIndex) {
        java.util.Arrays.sort(a, fromIndex, toIndex);
    }
    
    public static void sort(final Object[] a, final Comparator c) {
        java.util.Arrays.sort(a, c);
    }
    
    public static void sort(final Object[] a, final int fromIndex, final int toIndex, final Comparator c) {
        java.util.Arrays.sort(a, fromIndex, toIndex, c);
    }
    
    public static int binarySearch(final long[] a, final long key) {
        return java.util.Arrays.binarySearch(a, key);
    }
    
    public static int binarySearch(final int[] a, final int key) {
        return java.util.Arrays.binarySearch(a, key);
    }
    
    public static int binarySearch(final short[] a, final short key) {
        return java.util.Arrays.binarySearch(a, key);
    }
    
    public static int binarySearch(final char[] a, final char key) {
        return java.util.Arrays.binarySearch(a, key);
    }
    
    public static int binarySearch(final byte[] a, final byte key) {
        return java.util.Arrays.binarySearch(a, key);
    }
    
    public static int binarySearch(final double[] a, final double key) {
        return java.util.Arrays.binarySearch(a, key);
    }
    
    public static int binarySearch(final float[] a, final float key) {
        return java.util.Arrays.binarySearch(a, key);
    }
    
    public static int binarySearch(final Object[] a, final Object key) {
        return java.util.Arrays.binarySearch(a, key);
    }
    
    public static int binarySearch(final Object[] a, final Object key, final Comparator c) {
        return java.util.Arrays.binarySearch(a, key, c);
    }
    
    public static boolean equals(final long[] a, final long[] a2) {
        return java.util.Arrays.equals(a, a2);
    }
    
    public static boolean equals(final int[] a, final int[] a2) {
        return java.util.Arrays.equals(a, a2);
    }
    
    public static boolean equals(final short[] a, final short[] a2) {
        return java.util.Arrays.equals(a, a2);
    }
    
    public static boolean equals(final char[] a, final char[] a2) {
        return java.util.Arrays.equals(a, a2);
    }
    
    public static boolean equals(final byte[] a, final byte[] a2) {
        return java.util.Arrays.equals(a, a2);
    }
    
    public static boolean equals(final boolean[] a, final boolean[] a2) {
        return java.util.Arrays.equals(a, a2);
    }
    
    public static boolean equals(final double[] a, final double[] a2) {
        return java.util.Arrays.equals(a, a2);
    }
    
    public static boolean equals(final float[] a, final float[] a2) {
        return java.util.Arrays.equals(a, a2);
    }
    
    public static boolean equals(final Object[] a, final Object[] a2) {
        return java.util.Arrays.equals(a, a2);
    }
    
    public static void fill(final long[] a, final long val) {
        java.util.Arrays.fill(a, val);
    }
    
    public static void fill(final long[] a, final int fromIndex, final int toIndex, final long val) {
        java.util.Arrays.fill(a, fromIndex, toIndex, val);
    }
    
    public static void fill(final int[] a, final int val) {
        java.util.Arrays.fill(a, val);
    }
    
    public static void fill(final int[] a, final int fromIndex, final int toIndex, final int val) {
        java.util.Arrays.fill(a, fromIndex, toIndex, val);
    }
    
    public static void fill(final short[] a, final short val) {
        java.util.Arrays.fill(a, val);
    }
    
    public static void fill(final short[] a, final int fromIndex, final int toIndex, final short val) {
        java.util.Arrays.fill(a, fromIndex, toIndex, val);
    }
    
    public static void fill(final char[] a, final char val) {
        java.util.Arrays.fill(a, val);
    }
    
    public static void fill(final char[] a, final int fromIndex, final int toIndex, final char val) {
        java.util.Arrays.fill(a, fromIndex, toIndex, val);
    }
    
    public static void fill(final byte[] a, final byte val) {
        java.util.Arrays.fill(a, val);
    }
    
    public static void fill(final byte[] a, final int fromIndex, final int toIndex, final byte val) {
        java.util.Arrays.fill(a, fromIndex, toIndex, val);
    }
    
    public static void fill(final boolean[] a, final boolean val) {
        java.util.Arrays.fill(a, val);
    }
    
    public static void fill(final boolean[] a, final int fromIndex, final int toIndex, final boolean val) {
        java.util.Arrays.fill(a, fromIndex, toIndex, val);
    }
    
    public static void fill(final double[] a, final double val) {
        java.util.Arrays.fill(a, val);
    }
    
    public static void fill(final double[] a, final int fromIndex, final int toIndex, final double val) {
        java.util.Arrays.fill(a, fromIndex, toIndex, val);
    }
    
    public static void fill(final float[] a, final float val) {
        java.util.Arrays.fill(a, val);
    }
    
    public static void fill(final float[] a, final int fromIndex, final int toIndex, final float val) {
        java.util.Arrays.fill(a, fromIndex, toIndex, val);
    }
    
    public static void fill(final Object[] a, final Object val) {
        java.util.Arrays.fill(a, val);
    }
    
    public static void fill(final Object[] a, final int fromIndex, final int toIndex, final Object val) {
        java.util.Arrays.fill(a, fromIndex, toIndex, val);
    }
    
    public static Object[] copyOf(final Object[] original, final int newLength) {
        return copyOf(original, newLength, original.getClass());
    }
    
    public static Object[] copyOf(final Object[] original, final int newLength, final Class newType) {
        final Object[] arr = (Object[])((newType == Object[].class) ? new Object[newLength] : Array.newInstance(newType.getComponentType(), newLength));
        final int len = (original.length < newLength) ? original.length : newLength;
        System.arraycopy(original, 0, arr, 0, len);
        return arr;
    }
    
    public static byte[] copyOf(final byte[] original, final int newLength) {
        final byte[] arr = new byte[newLength];
        final int len = (original.length < newLength) ? original.length : newLength;
        System.arraycopy(original, 0, arr, 0, len);
        return arr;
    }
    
    public static short[] copyOf(final short[] original, final int newLength) {
        final short[] arr = new short[newLength];
        final int len = (original.length < newLength) ? original.length : newLength;
        System.arraycopy(original, 0, arr, 0, len);
        return arr;
    }
    
    public static int[] copyOf(final int[] original, final int newLength) {
        final int[] arr = new int[newLength];
        final int len = (original.length < newLength) ? original.length : newLength;
        System.arraycopy(original, 0, arr, 0, len);
        return arr;
    }
    
    public static long[] copyOf(final long[] original, final int newLength) {
        final long[] arr = new long[newLength];
        final int len = (original.length < newLength) ? original.length : newLength;
        System.arraycopy(original, 0, arr, 0, len);
        return arr;
    }
    
    public static char[] copyOf(final char[] original, final int newLength) {
        final char[] arr = new char[newLength];
        final int len = (original.length < newLength) ? original.length : newLength;
        System.arraycopy(original, 0, arr, 0, len);
        return arr;
    }
    
    public static float[] copyOf(final float[] original, final int newLength) {
        final float[] arr = new float[newLength];
        final int len = (original.length < newLength) ? original.length : newLength;
        System.arraycopy(original, 0, arr, 0, len);
        return arr;
    }
    
    public static double[] copyOf(final double[] original, final int newLength) {
        final double[] arr = new double[newLength];
        final int len = (original.length < newLength) ? original.length : newLength;
        System.arraycopy(original, 0, arr, 0, len);
        return arr;
    }
    
    public static boolean[] copyOf(final boolean[] original, final int newLength) {
        final boolean[] arr = new boolean[newLength];
        final int len = (original.length < newLength) ? original.length : newLength;
        System.arraycopy(original, 0, arr, 0, len);
        return arr;
    }
    
    public static Object[] copyOfRange(final Object[] original, final int from, final int to) {
        return copyOfRange(original, from, to, original.getClass());
    }
    
    public static Object[] copyOfRange(final Object[] original, final int from, final int to, final Class newType) {
        final int newLength = to - from;
        if (newLength < 0) {
            throw new IllegalArgumentException(from + " > " + to);
        }
        final Object[] arr = (Object[])((newType == Object[].class) ? new Object[newLength] : Array.newInstance(newType.getComponentType(), newLength));
        final int ceil = original.length - from;
        final int len = (ceil < newLength) ? ceil : newLength;
        System.arraycopy(original, from, arr, 0, len);
        return arr;
    }
    
    public static byte[] copyOfRange(final byte[] original, final int from, final int to) {
        final int newLength = to - from;
        if (newLength < 0) {
            throw new IllegalArgumentException(from + " > " + to);
        }
        final byte[] arr = new byte[newLength];
        final int ceil = original.length - from;
        final int len = (ceil < newLength) ? ceil : newLength;
        System.arraycopy(original, from, arr, 0, len);
        return arr;
    }
    
    public static short[] copyOfRange(final short[] original, final int from, final int to) {
        final int newLength = to - from;
        if (newLength < 0) {
            throw new IllegalArgumentException(from + " > " + to);
        }
        final short[] arr = new short[newLength];
        final int ceil = original.length - from;
        final int len = (ceil < newLength) ? ceil : newLength;
        System.arraycopy(original, from, arr, 0, len);
        return arr;
    }
    
    public static int[] copyOfRange(final int[] original, final int from, final int to) {
        final int newLength = to - from;
        if (newLength < 0) {
            throw new IllegalArgumentException(from + " > " + to);
        }
        final int[] arr = new int[newLength];
        final int ceil = original.length - from;
        final int len = (ceil < newLength) ? ceil : newLength;
        System.arraycopy(original, from, arr, 0, len);
        return arr;
    }
    
    public static long[] copyOfRange(final long[] original, final int from, final int to) {
        final int newLength = to - from;
        if (newLength < 0) {
            throw new IllegalArgumentException(from + " > " + to);
        }
        final long[] arr = new long[newLength];
        final int ceil = original.length - from;
        final int len = (ceil < newLength) ? ceil : newLength;
        System.arraycopy(original, from, arr, 0, len);
        return arr;
    }
    
    public static char[] copyOfRange(final char[] original, final int from, final int to) {
        final int newLength = to - from;
        if (newLength < 0) {
            throw new IllegalArgumentException(from + " > " + to);
        }
        final char[] arr = new char[newLength];
        final int ceil = original.length - from;
        final int len = (ceil < newLength) ? ceil : newLength;
        System.arraycopy(original, from, arr, 0, len);
        return arr;
    }
    
    public static float[] copyOfRange(final float[] original, final int from, final int to) {
        final int newLength = to - from;
        if (newLength < 0) {
            throw new IllegalArgumentException(from + " > " + to);
        }
        final float[] arr = new float[newLength];
        final int ceil = original.length - from;
        final int len = (ceil < newLength) ? ceil : newLength;
        System.arraycopy(original, from, arr, 0, len);
        return arr;
    }
    
    public static double[] copyOfRange(final double[] original, final int from, final int to) {
        final int newLength = to - from;
        if (newLength < 0) {
            throw new IllegalArgumentException(from + " > " + to);
        }
        final double[] arr = new double[newLength];
        final int ceil = original.length - from;
        final int len = (ceil < newLength) ? ceil : newLength;
        System.arraycopy(original, from, arr, 0, len);
        return arr;
    }
    
    public static boolean[] copyOfRange(final boolean[] original, final int from, final int to) {
        final int newLength = to - from;
        if (newLength < 0) {
            throw new IllegalArgumentException(from + " > " + to);
        }
        final boolean[] arr = new boolean[newLength];
        final int ceil = original.length - from;
        final int len = (ceil < newLength) ? ceil : newLength;
        System.arraycopy(original, from, arr, 0, len);
        return arr;
    }
    
    public static List asList(final Object[] a) {
        return java.util.Arrays.asList(a);
    }
    
    public static int hashCode(final long[] a) {
        if (a == null) {
            return 0;
        }
        int hash = 1;
        for (int i = 0; i < a.length; ++i) {
            final long e = a[i];
            hash = 31 * hash + (int)(e ^ e >>> 32);
        }
        return hash;
    }
    
    public static int hashCode(final int[] a) {
        if (a == null) {
            return 0;
        }
        int hash = 1;
        for (int i = 0; i < a.length; ++i) {
            hash = 31 * hash + a[i];
        }
        return hash;
    }
    
    public static int hashCode(final short[] a) {
        if (a == null) {
            return 0;
        }
        int hash = 1;
        for (int i = 0; i < a.length; ++i) {
            hash = 31 * hash + a[i];
        }
        return hash;
    }
    
    public static int hashCode(final char[] a) {
        if (a == null) {
            return 0;
        }
        int hash = 1;
        for (int i = 0; i < a.length; ++i) {
            hash = 31 * hash + a[i];
        }
        return hash;
    }
    
    public static int hashCode(final byte[] a) {
        if (a == null) {
            return 0;
        }
        int hash = 1;
        for (int i = 0; i < a.length; ++i) {
            hash = 31 * hash + a[i];
        }
        return hash;
    }
    
    public static int hashCode(final boolean[] a) {
        if (a == null) {
            return 0;
        }
        int hash = 1;
        for (int i = 0; i < a.length; ++i) {
            hash = 31 * hash + (a[i] ? 1231 : 1237);
        }
        return hash;
    }
    
    public static int hashCode(final float[] a) {
        if (a == null) {
            return 0;
        }
        int hash = 1;
        for (int i = 0; i < a.length; ++i) {
            hash = 31 * hash + Float.floatToIntBits(a[i]);
        }
        return hash;
    }
    
    public static int hashCode(final double[] a) {
        if (a == null) {
            return 0;
        }
        int hash = 1;
        for (int i = 0; i < a.length; ++i) {
            final long e = Double.doubleToLongBits(a[i]);
            hash = 31 * hash + (int)(e ^ e >>> 32);
        }
        return hash;
    }
    
    public static int hashCode(final Object[] a) {
        if (a == null) {
            return 0;
        }
        int hash = 1;
        for (int i = 0; i < a.length; ++i) {
            final Object e = a[i];
            hash = 31 * hash + ((e == null) ? 0 : e.hashCode());
        }
        return hash;
    }
    
    public static int deepHashCode(final Object[] a) {
        if (a == null) {
            return 0;
        }
        int hash = 1;
        for (int i = 0; i < a.length; ++i) {
            final Object e = a[i];
            hash = 31 * hash + ((e instanceof Object[]) ? deepHashCode((Object[])e) : ((e instanceof byte[]) ? hashCode((byte[])e) : ((e instanceof short[]) ? hashCode((short[])e) : ((e instanceof int[]) ? hashCode((int[])e) : ((e instanceof long[]) ? hashCode((long[])e) : ((e instanceof char[]) ? hashCode((char[])e) : ((e instanceof boolean[]) ? hashCode((boolean[])e) : ((e instanceof float[]) ? hashCode((float[])e) : ((e instanceof double[]) ? hashCode((double[])e) : ((e != null) ? e.hashCode() : 0))))))))));
        }
        return hash;
    }
    
    public static boolean deepEquals(final Object[] a1, final Object[] a2) {
        if (a1 == a2) {
            return true;
        }
        if (a1 == null || a2 == null) {
            return false;
        }
        final int len = a1.length;
        if (len != a2.length) {
            return false;
        }
        for (int i = 0; i < len; ++i) {
            final Object e1 = a1[i];
            final Object e2 = a2[i];
            if (e1 != e2) {
                if (e1 == null) {
                    return false;
                }
                final boolean eq = (e1.getClass() != e2.getClass() || e1.getClass().isArray()) ? e1.equals(e2) : ((e1 instanceof Object[] && e2 instanceof Object[]) ? deepEquals((Object[])e1, (Object[])e2) : ((e1 instanceof byte[] && e2 instanceof byte[]) ? equals((byte[])e1, (byte[])e2) : ((e1 instanceof short[] && e2 instanceof short[]) ? equals((short[])e1, (short[])e2) : ((e1 instanceof int[] && e2 instanceof int[]) ? equals((int[])e1, (int[])e2) : ((e1 instanceof long[] && e2 instanceof long[]) ? equals((long[])e1, (long[])e2) : ((e1 instanceof char[] && e2 instanceof char[]) ? equals((char[])e1, (char[])e2) : ((e1 instanceof boolean[] && e2 instanceof boolean[]) ? equals((boolean[])e1, (boolean[])e2) : ((e1 instanceof float[] && e2 instanceof float[]) ? equals((float[])e1, (float[])e2) : ((e1 instanceof double[] && e2 instanceof double[]) ? equals((double[])e1, (double[])e2) : e1.equals(e2))))))))));
                if (!eq) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public static String toString(final long[] a) {
        if (a == null) {
            return "null";
        }
        if (a.length == 0) {
            return "[]";
        }
        final StringBuffer buf = new StringBuffer();
        buf.append('[').append(a[0]);
        for (int i = 1; i < a.length; ++i) {
            buf.append(", ").append(a[i]);
        }
        buf.append(']');
        return buf.toString();
    }
    
    public static String toString(final int[] a) {
        if (a == null) {
            return "null";
        }
        if (a.length == 0) {
            return "[]";
        }
        final StringBuffer buf = new StringBuffer();
        buf.append('[').append(a[0]);
        for (int i = 1; i < a.length; ++i) {
            buf.append(", ").append(a[i]);
        }
        buf.append(']');
        return buf.toString();
    }
    
    public static String toString(final short[] a) {
        if (a == null) {
            return "null";
        }
        if (a.length == 0) {
            return "[]";
        }
        final StringBuffer buf = new StringBuffer();
        buf.append('[').append(a[0]);
        for (int i = 1; i < a.length; ++i) {
            buf.append(", ").append(a[i]);
        }
        buf.append(']');
        return buf.toString();
    }
    
    public static String toString(final char[] a) {
        if (a == null) {
            return "null";
        }
        if (a.length == 0) {
            return "[]";
        }
        final StringBuffer buf = new StringBuffer();
        buf.append('[').append(a[0]);
        for (int i = 1; i < a.length; ++i) {
            buf.append(", ").append(a[i]);
        }
        buf.append(']');
        return buf.toString();
    }
    
    public static String toString(final byte[] a) {
        if (a == null) {
            return "null";
        }
        if (a.length == 0) {
            return "[]";
        }
        final StringBuffer buf = new StringBuffer();
        buf.append('[').append(a[0]);
        for (int i = 1; i < a.length; ++i) {
            buf.append(", ").append(a[i]);
        }
        buf.append(']');
        return buf.toString();
    }
    
    public static String toString(final boolean[] a) {
        if (a == null) {
            return "null";
        }
        if (a.length == 0) {
            return "[]";
        }
        final StringBuffer buf = new StringBuffer();
        buf.append('[').append(a[0]);
        for (int i = 1; i < a.length; ++i) {
            buf.append(", ").append(a[i]);
        }
        buf.append(']');
        return buf.toString();
    }
    
    public static String toString(final float[] a) {
        if (a == null) {
            return "null";
        }
        if (a.length == 0) {
            return "[]";
        }
        final StringBuffer buf = new StringBuffer();
        buf.append('[').append(a[0]);
        for (int i = 1; i < a.length; ++i) {
            buf.append(", ").append(a[i]);
        }
        buf.append(']');
        return buf.toString();
    }
    
    public static String toString(final double[] a) {
        if (a == null) {
            return "null";
        }
        if (a.length == 0) {
            return "[]";
        }
        final StringBuffer buf = new StringBuffer();
        buf.append('[').append(a[0]);
        for (int i = 1; i < a.length; ++i) {
            buf.append(", ").append(a[i]);
        }
        buf.append(']');
        return buf.toString();
    }
    
    public static String toString(final Object[] a) {
        if (a == null) {
            return "null";
        }
        if (a.length == 0) {
            return "[]";
        }
        final StringBuffer buf = new StringBuffer();
        buf.append('[').append(a[0]);
        for (int i = 1; i < a.length; ++i) {
            buf.append(", ").append(a[i]);
        }
        buf.append(']');
        return buf.toString();
    }
    
    public static String deepToString(final Object[] a) {
        if (a == null) {
            return "null";
        }
        final StringBuffer buf = new StringBuffer();
        deepToString(a, buf, new ArrayList());
        return buf.toString();
    }
    
    private static void deepToString(final Object[] a, final StringBuffer buf, final List seen) {
        seen.add(a);
        buf.append('[');
        for (int i = 0; i < a.length; ++i) {
            if (i > 0) {
                buf.append(", ");
            }
            final Object e = a[i];
            if (e == null) {
                buf.append("null");
            }
            else if (!e.getClass().isArray()) {
                buf.append(e.toString());
            }
            else if (e instanceof Object[]) {
                if (seen.contains(e)) {
                    buf.append("[...]");
                }
                else {
                    deepToString((Object[])e, buf, seen);
                }
            }
            else {
                buf.append((e instanceof byte[]) ? toString((byte[])e) : ((e instanceof short[]) ? toString((short[])e) : ((e instanceof int[]) ? toString((int[])e) : ((e instanceof long[]) ? toString((long[])e) : ((e instanceof char[]) ? toString((char[])e) : ((e instanceof boolean[]) ? toString((boolean[])e) : ((e instanceof float[]) ? toString((float[])e) : ((e instanceof double[]) ? toString((double[])e) : ""))))))));
            }
        }
        buf.append(']');
        seen.remove(seen.size() - 1);
    }
}

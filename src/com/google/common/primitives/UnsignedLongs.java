// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.primitives;

import java.math.BigInteger;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Comparator;
import com.google.common.base.Preconditions;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.Beta;

@Beta
@GwtCompatible
public final class UnsignedLongs
{
    public static final long MAX_VALUE = -1L;
    private static final long[] maxValueDivs;
    private static final int[] maxValueMods;
    private static final int[] maxSafeDigits;
    
    private UnsignedLongs() {
    }
    
    private static long flip(final long a) {
        return a ^ Long.MIN_VALUE;
    }
    
    public static int compare(final long a, final long b) {
        return Longs.compare(flip(a), flip(b));
    }
    
    public static long min(final long... array) {
        Preconditions.checkArgument(array.length > 0);
        long min = flip(array[0]);
        for (int i = 1; i < array.length; ++i) {
            final long next = flip(array[i]);
            if (next < min) {
                min = next;
            }
        }
        return flip(min);
    }
    
    public static long max(final long... array) {
        Preconditions.checkArgument(array.length > 0);
        long max = flip(array[0]);
        for (int i = 1; i < array.length; ++i) {
            final long next = flip(array[i]);
            if (next > max) {
                max = next;
            }
        }
        return flip(max);
    }
    
    public static String join(final String separator, final long... array) {
        Preconditions.checkNotNull(separator);
        if (array.length == 0) {
            return "";
        }
        final StringBuilder builder = new StringBuilder(array.length * 5);
        builder.append(toString(array[0]));
        for (int i = 1; i < array.length; ++i) {
            builder.append(separator).append(toString(array[i]));
        }
        return builder.toString();
    }
    
    public static Comparator<long[]> lexicographicalComparator() {
        return LexicographicalComparator.INSTANCE;
    }
    
    public static long divide(final long dividend, final long divisor) {
        if (divisor < 0L) {
            if (compare(dividend, divisor) < 0) {
                return 0L;
            }
            return 1L;
        }
        else {
            if (dividend >= 0L) {
                return dividend / divisor;
            }
            final long quotient = (dividend >>> 1) / divisor << 1;
            final long rem = dividend - quotient * divisor;
            return quotient + ((compare(rem, divisor) >= 0) ? 1 : 0);
        }
    }
    
    public static long remainder(final long dividend, final long divisor) {
        if (divisor < 0L) {
            if (compare(dividend, divisor) < 0) {
                return dividend;
            }
            return dividend - divisor;
        }
        else {
            if (dividend >= 0L) {
                return dividend % divisor;
            }
            final long quotient = (dividend >>> 1) / divisor << 1;
            final long rem = dividend - quotient * divisor;
            return rem - ((compare(rem, divisor) >= 0) ? divisor : 0L);
        }
    }
    
    @CanIgnoreReturnValue
    public static long parseUnsignedLong(final String string) {
        return parseUnsignedLong(string, 10);
    }
    
    @CanIgnoreReturnValue
    public static long decode(final String stringValue) {
        final ParseRequest request = ParseRequest.fromString(stringValue);
        try {
            return parseUnsignedLong(request.rawValue, request.radix);
        }
        catch (NumberFormatException e) {
            final NumberFormatException decodeException = new NumberFormatException("Error parsing value: " + stringValue);
            decodeException.initCause(e);
            throw decodeException;
        }
    }
    
    @CanIgnoreReturnValue
    public static long parseUnsignedLong(final String string, final int radix) {
        Preconditions.checkNotNull(string);
        if (string.length() == 0) {
            throw new NumberFormatException("empty string");
        }
        if (radix < 2 || radix > 36) {
            throw new NumberFormatException("illegal radix: " + radix);
        }
        final int maxSafePos = UnsignedLongs.maxSafeDigits[radix] - 1;
        long value = 0L;
        for (int pos = 0; pos < string.length(); ++pos) {
            final int digit = Character.digit(string.charAt(pos), radix);
            if (digit == -1) {
                throw new NumberFormatException(string);
            }
            if (pos > maxSafePos && overflowInParse(value, digit, radix)) {
                throw new NumberFormatException("Too large for unsigned long: " + string);
            }
            value = value * radix + digit;
        }
        return value;
    }
    
    private static boolean overflowInParse(final long current, final int digit, final int radix) {
        return current < 0L || (current >= UnsignedLongs.maxValueDivs[radix] && (current > UnsignedLongs.maxValueDivs[radix] || digit > UnsignedLongs.maxValueMods[radix]));
    }
    
    public static String toString(final long x) {
        return toString(x, 10);
    }
    
    public static String toString(long x, final int radix) {
        Preconditions.checkArgument(radix >= 2 && radix <= 36, "radix (%s) must be between Character.MIN_RADIX and Character.MAX_RADIX", radix);
        if (x == 0L) {
            return "0";
        }
        if (x > 0L) {
            return Long.toString(x, radix);
        }
        final char[] buf = new char[64];
        int i = buf.length;
        if ((radix & radix - 1) == 0x0) {
            final int shift = Integer.numberOfTrailingZeros(radix);
            final int mask = radix - 1;
            do {
                buf[--i] = Character.forDigit((int)x & mask, radix);
                x >>>= shift;
            } while (x != 0L);
        }
        else {
            long quotient;
            if ((radix & 0x1) == 0x0) {
                quotient = (x >>> 1) / (radix >>> 1);
            }
            else {
                quotient = divide(x, radix);
            }
            final long rem = x - quotient * radix;
            buf[--i] = Character.forDigit((int)rem, radix);
            for (x = quotient; x > 0L; x /= radix) {
                buf[--i] = Character.forDigit((int)(x % radix), radix);
            }
        }
        return new String(buf, i, buf.length - i);
    }
    
    static {
        maxValueDivs = new long[37];
        maxValueMods = new int[37];
        maxSafeDigits = new int[37];
        final BigInteger overflow = new BigInteger("10000000000000000", 16);
        for (int i = 2; i <= 36; ++i) {
            UnsignedLongs.maxValueDivs[i] = divide(-1L, i);
            UnsignedLongs.maxValueMods[i] = (int)remainder(-1L, i);
            UnsignedLongs.maxSafeDigits[i] = overflow.toString(i).length() - 1;
        }
    }
    
    enum LexicographicalComparator implements Comparator<long[]>
    {
        INSTANCE;
        
        @Override
        public int compare(final long[] left, final long[] right) {
            for (int minLength = Math.min(left.length, right.length), i = 0; i < minLength; ++i) {
                if (left[i] != right[i]) {
                    return UnsignedLongs.compare(left[i], right[i]);
                }
            }
            return left.length - right.length;
        }
        
        @Override
        public String toString() {
            return "UnsignedLongs.lexicographicalComparator()";
        }
    }
}

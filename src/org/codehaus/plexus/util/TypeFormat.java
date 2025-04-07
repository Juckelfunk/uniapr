// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.util;

public final class TypeFormat
{
    private static final char[] DIGITS;
    private static final int[] INT_POW_10;
    private static final long[] LONG_POW_10;
    private static final double LOG_10;
    private static final float FLOAT_RELATIVE_ERROR;
    private static final double DOUBLE_RELATIVE_ERROR;
    private static String[] LEADING_ZEROS;
    private static final double[] DOUBLE_POW_10;
    
    private TypeFormat() {
    }
    
    public static int indexOf(final CharSequence pattern, final CharSequence chars, int fromIndex) {
        final int patternLength = pattern.length();
        fromIndex = Math.max(0, fromIndex);
        if (patternLength != 0) {
            final char firstChar = pattern.charAt(0);
            for (int last = chars.length() - patternLength, i = fromIndex; i <= last; ++i) {
                if (chars.charAt(i) == firstChar) {
                    boolean match = true;
                    for (int j = 1; j < patternLength; ++j) {
                        if (chars.charAt(i + j) != pattern.charAt(j)) {
                            match = false;
                            break;
                        }
                    }
                    if (match) {
                        return i;
                    }
                }
            }
            return -1;
        }
        return Math.min(0, fromIndex);
    }
    
    public static boolean parseBoolean(final CharSequence chars) {
        return chars.length() == 4 && (chars.charAt(0) == 't' || chars.charAt(0) == 'T') && (chars.charAt(1) == 'r' || chars.charAt(1) == 'R') && (chars.charAt(2) == 'u' || chars.charAt(2) == 'U') && (chars.charAt(3) == 'e' || chars.charAt(3) == 'E');
    }
    
    public static short parseShort(final CharSequence chars) {
        return parseShort(chars, 10);
    }
    
    public static short parseShort(final CharSequence chars, final int radix) {
        try {
            final boolean isNegative = chars.charAt(0) == '-';
            int result = 0;
            final int limit = isNegative ? -32768 : -32767;
            final int multmin = limit / radix;
            final int length = chars.length();
            int i = (isNegative || chars.charAt(0) == '+') ? 1 : 0;
            while (true) {
                final int digit = Character.digit(chars.charAt(i), radix);
                final int tmp = result * radix;
                if (digit < 0 || result < multmin || tmp < limit + digit) {
                    throw new NumberFormatException("For input characters: \"" + chars.toString() + "\"");
                }
                result = tmp - digit;
                if (++i >= length) {
                    return (short)(isNegative ? result : (-result));
                }
            }
        }
        catch (IndexOutOfBoundsException e) {
            throw new NumberFormatException("For input characters: \"" + chars.toString() + "\"");
        }
    }
    
    public static int parseInt(final CharSequence chars) {
        return parseInt(chars, 10);
    }
    
    public static int parseInt(final CharSequence chars, final int radix) {
        try {
            final boolean isNegative = chars.charAt(0) == '-';
            int result = 0;
            final int limit = isNegative ? Integer.MIN_VALUE : -2147483647;
            final int multmin = limit / radix;
            final int length = chars.length();
            int i = (isNegative || chars.charAt(0) == '+') ? 1 : 0;
            while (true) {
                final int digit = Character.digit(chars.charAt(i), radix);
                final int tmp = result * radix;
                if (digit < 0 || result < multmin || tmp < limit + digit) {
                    throw new NumberFormatException("For input characters: \"" + chars.toString() + "\"");
                }
                result = tmp - digit;
                if (++i >= length) {
                    return isNegative ? result : (-result);
                }
            }
        }
        catch (IndexOutOfBoundsException e) {
            throw new NumberFormatException("For input characters: \"" + chars.toString() + "\"");
        }
    }
    
    public static long parseLong(final CharSequence chars) {
        return parseLong(chars, 10);
    }
    
    public static long parseLong(final CharSequence chars, final int radix) {
        try {
            final boolean isNegative = chars.charAt(0) == '-';
            long result = 0L;
            final long limit = isNegative ? Long.MIN_VALUE : -9223372036854775807L;
            final long multmin = limit / radix;
            final int length = chars.length();
            int i = (isNegative || chars.charAt(0) == '+') ? 1 : 0;
            while (true) {
                final int digit = Character.digit(chars.charAt(i), radix);
                final long tmp = result * radix;
                if (digit < 0 || result < multmin || tmp < limit + digit) {
                    throw new NumberFormatException("For input characters: \"" + chars.toString() + "\"");
                }
                result = tmp - digit;
                if (++i >= length) {
                    return isNegative ? result : (-result);
                }
            }
        }
        catch (IndexOutOfBoundsException e) {
            throw new NumberFormatException("For input characters: \"" + chars.toString() + "\"");
        }
    }
    
    public static float parseFloat(final CharSequence chars) {
        final double d = parseDouble(chars);
        if (d >= 1.401298464324817E-45 && d <= 3.4028234663852886E38) {
            return (float)d;
        }
        throw new NumberFormatException("Float overflow for input characters: \"" + chars.toString() + "\"");
    }
    
    public static double parseDouble(final CharSequence chars) throws NumberFormatException {
        try {
            final int length = chars.length();
            double result = 0.0;
            int exp = 0;
            final boolean isNegative = chars.charAt(0) == '-';
            int i = (isNegative || chars.charAt(0) == '+') ? 1 : 0;
            if (chars.charAt(i) == 'N' || chars.charAt(i) == 'I') {
                if (chars.toString().equals("NaN")) {
                    return Double.NaN;
                }
                if (chars.subSequence(i, length).toString().equals("Infinity")) {
                    return isNegative ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
                }
            }
            boolean fraction = false;
            do {
                final char c = chars.charAt(i);
                if (c == '.' && !fraction) {
                    fraction = true;
                }
                else {
                    if (c == 'e') {
                        break;
                    }
                    if (c == 'E') {
                        break;
                    }
                    if (c < '0' || c > '9') {
                        throw new NumberFormatException("For input characters: \"" + chars.toString() + "\"");
                    }
                    result = result * 10.0 + (c - '0');
                    if (fraction) {
                        --exp;
                    }
                }
            } while (++i < length);
            result = (isNegative ? (-result) : result);
            if (i < length) {
                ++i;
                final boolean negE = chars.charAt(i) == '-';
                i = ((negE || chars.charAt(i) == '+') ? (i + 1) : i);
                int valE = 0;
                while (true) {
                    final char c2 = chars.charAt(i);
                    if (c2 < '0' || c2 > '9') {
                        throw new NumberFormatException("For input characters: \"" + chars.toString() + "\"");
                    }
                    valE = valE * 10 + (c2 - '0');
                    if (valE > 10000000) {
                        valE = 10000000;
                    }
                    if (++i >= length) {
                        exp += (negE ? (-valE) : valE);
                        break;
                    }
                }
            }
            return multE(result, exp);
        }
        catch (IndexOutOfBoundsException e) {
            throw new NumberFormatException("For input characters: \"" + chars.toString() + "\"");
        }
    }
    
    public static StringBuffer format(final boolean b, final StringBuffer sb) {
        return b ? sb.append("true") : sb.append("false");
    }
    
    public static StringBuffer format(final short s, final StringBuffer sb) {
        return format((int)s, sb);
    }
    
    public static StringBuffer format(final short s, final int radix, final StringBuffer sb) {
        return format((int)s, radix, sb);
    }
    
    public static StringBuffer format(int i, final StringBuffer sb) {
        if (i <= 0) {
            if (i == Integer.MIN_VALUE) {
                return sb.append("-2147483648");
            }
            if (i == 0) {
                return sb.append('0');
            }
            i = -i;
            sb.append('-');
        }
        int j;
        for (j = 1; j < 10 && i >= TypeFormat.INT_POW_10[j]; ++j) {}
        --j;
        while (j >= 0) {
            final int pow10 = TypeFormat.INT_POW_10[j];
            final int digit = i / pow10;
            i -= digit * pow10;
            sb.append(TypeFormat.DIGITS[digit]);
            --j;
        }
        return sb;
    }
    
    public static StringBuffer format(int i, final int radix, final StringBuffer sb) {
        if (radix == 10) {
            return format(i, sb);
        }
        if (radix < 2 || radix > 36) {
            throw new IllegalArgumentException("radix: " + radix);
        }
        if (i < 0) {
            sb.append('-');
        }
        else {
            i = -i;
        }
        format2(i, radix, sb);
        return sb;
    }
    
    private static void format2(final int i, final int radix, final StringBuffer sb) {
        if (i <= -radix) {
            format2(i / radix, radix, sb);
            sb.append(TypeFormat.DIGITS[-(i % radix)]);
        }
        else {
            sb.append(TypeFormat.DIGITS[-i]);
        }
    }
    
    public static StringBuffer format(long l, final StringBuffer sb) {
        if (l <= 0L) {
            if (l == Long.MIN_VALUE) {
                return sb.append("-9223372036854775808");
            }
            if (l == 0L) {
                return sb.append('0');
            }
            l = -l;
            sb.append('-');
        }
        int j;
        for (j = 1; j < 19 && l >= TypeFormat.LONG_POW_10[j]; ++j) {}
        --j;
        while (j >= 0) {
            final long pow10 = TypeFormat.LONG_POW_10[j];
            final int digit = (int)(l / pow10);
            l -= digit * pow10;
            sb.append(TypeFormat.DIGITS[digit]);
            --j;
        }
        return sb;
    }
    
    public static StringBuffer format(long l, final int radix, final StringBuffer sb) {
        if (radix == 10) {
            return format(l, sb);
        }
        if (radix < 2 || radix > 36) {
            throw new IllegalArgumentException("radix: " + radix);
        }
        if (l < 0L) {
            sb.append('-');
        }
        else {
            l = -l;
        }
        format2(l, radix, sb);
        return sb;
    }
    
    private static void format2(final long l, final int radix, final StringBuffer sb) {
        if (l <= -radix) {
            format2(l / radix, radix, sb);
            sb.append(TypeFormat.DIGITS[(int)(-(l % radix))]);
        }
        else {
            sb.append(TypeFormat.DIGITS[(int)(-l)]);
        }
    }
    
    public static StringBuffer format(final float f, final StringBuffer sb) {
        return format(f, 0.0f, sb);
    }
    
    public static StringBuffer format(final float f, float precision, final StringBuffer sb) {
        boolean precisionOnLastDigit;
        if (precision > 0.0f) {
            precisionOnLastDigit = true;
        }
        else {
            if (precision != 0.0f) {
                throw new IllegalArgumentException("precision: Negative values not allowed");
            }
            if (f == 0.0f) {
                return sb.append("0.0");
            }
            precisionOnLastDigit = false;
            precision = Math.max(Math.abs(f * TypeFormat.FLOAT_RELATIVE_ERROR), Float.MIN_VALUE);
        }
        return format(f, precision, precisionOnLastDigit, sb);
    }
    
    public static StringBuffer format(final double d, final StringBuffer sb) {
        return format(d, 0.0, sb);
    }
    
    public static StringBuffer format(final double d, final int digits, final StringBuffer sb) {
        if (digits >= 1 && digits <= 19) {
            final double precision = Math.abs(d / TypeFormat.DOUBLE_POW_10[digits - 1]);
            return format(d, precision, sb);
        }
        throw new IllegalArgumentException("digits: " + digits + " is not in range [1 .. 19]");
    }
    
    public static StringBuffer format(final double d, double precision, final StringBuffer sb) {
        boolean precisionOnLastDigit = false;
        if (precision > 0.0) {
            precisionOnLastDigit = true;
        }
        else if (precision == 0.0) {
            if (d == 0.0) {
                return sb.append("0.0");
            }
            precision = Math.max(Math.abs(d * TypeFormat.DOUBLE_RELATIVE_ERROR), Double.MIN_VALUE);
        }
        else if (precision < 0.0) {
            throw new IllegalArgumentException("precision: Negative values not allowed");
        }
        return format(d, precision, precisionOnLastDigit, sb);
    }
    
    private static StringBuffer format(double d, final double precision, final boolean precisionOnLastDigit, final StringBuffer sb) {
        if (Double.isNaN(d)) {
            return sb.append("NaN");
        }
        if (Double.isInfinite(d)) {
            return (d >= 0.0) ? sb.append("Infinity") : sb.append("-Infinity");
        }
        if (d < 0.0) {
            d = -d;
            sb.append('-');
        }
        final int rank = (int)Math.floor(Math.log(precision) / TypeFormat.LOG_10);
        final double digitValue = multE(d, -rank);
        if (digitValue >= 9.223372036854776E18) {
            throw new IllegalArgumentException("Specified precision would result in too many digits");
        }
        final int digitStart = sb.length();
        format(Math.round(digitValue), sb);
        final int digitLength = sb.length() - digitStart;
        final int dotPos = digitLength + rank;
        boolean useScientificNotation = false;
        if (dotPos <= -TypeFormat.LEADING_ZEROS.length || dotPos > digitLength) {
            sb.insert(digitStart + 1, '.');
            useScientificNotation = true;
        }
        else if (dotPos > 0) {
            sb.insert(digitStart + dotPos, '.');
        }
        else {
            sb.insert(digitStart, TypeFormat.LEADING_ZEROS[-dotPos]);
        }
        if (!precisionOnLastDigit) {
            int newLength = sb.length();
            do {
                --newLength;
            } while (sb.charAt(newLength) == '0');
            sb.setLength(newLength + 1);
        }
        if (sb.charAt(sb.length() - 1) == '.') {
            if (precisionOnLastDigit) {
                sb.setLength(sb.length() - 1);
            }
            else {
                sb.append('0');
            }
        }
        if (useScientificNotation) {
            sb.append('E');
            format(dotPos - 1, sb);
        }
        return sb;
    }
    
    private static final double multE(double value, int E) {
        if (E >= 0) {
            if (E <= 308) {
                return value * TypeFormat.DOUBLE_POW_10[E];
            }
            value *= 1.0E21;
            E = Math.min(308, E - 21);
            return value * TypeFormat.DOUBLE_POW_10[E];
        }
        else {
            if (E >= -308) {
                return value / TypeFormat.DOUBLE_POW_10[-E];
            }
            value /= 1.0E21;
            E = Math.max(-308, E + 21);
            return value / TypeFormat.DOUBLE_POW_10[-E];
        }
    }
    
    static {
        DIGITS = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };
        INT_POW_10 = new int[10];
        int pow = 1;
        for (int i = 0; i < 10; ++i) {
            TypeFormat.INT_POW_10[i] = pow;
            pow *= 10;
        }
        LONG_POW_10 = new long[19];
        long pow2 = 1L;
        for (int j = 0; j < 19; ++j) {
            TypeFormat.LONG_POW_10[j] = pow2;
            pow2 *= 10L;
        }
        LOG_10 = Math.log(10.0);
        FLOAT_RELATIVE_ERROR = (float)Math.pow(2.0, -24.0);
        DOUBLE_RELATIVE_ERROR = Math.pow(2.0, -53.0);
        TypeFormat.LEADING_ZEROS = new String[] { "0.", "0.0", "0.00" };
        DOUBLE_POW_10 = new double[] { 1.0, 10.0, 100.0, 1000.0, 10000.0, 100000.0, 1000000.0, 1.0E7, 1.0E8, 1.0E9, 1.0E10, 1.0E11, 1.0E12, 1.0E13, 1.0E14, 1.0E15, 1.0E16, 1.0E17, 1.0E18, 1.0E19, 1.0E20, 1.0E21, 1.0E22, 9.999999999999999E22, 1.0E24, 1.0E25, 1.0E26, 1.0E27, 1.0E28, 1.0E29, 1.0E30, 1.0E31, 1.0E32, 1.0E33, 1.0E34, 1.0E35, 1.0E36, 1.0E37, 1.0E38, 1.0E39, 1.0E40, 1.0E41, 1.0E42, 1.0E43, 1.0E44, 1.0E45, 1.0E46, 1.0E47, 1.0E48, 1.0E49, 1.0E50, 1.0E51, 1.0E52, 1.0E53, 1.0E54, 1.0E55, 1.0E56, 1.0E57, 1.0E58, 1.0E59, 1.0E60, 1.0E61, 1.0E62, 1.0E63, 1.0E64, 1.0E65, 1.0E66, 1.0E67, 1.0E68, 1.0E69, 1.0E70, 1.0E71, 1.0E72, 1.0E73, 1.0E74, 1.0E75, 1.0E76, 1.0E77, 1.0E78, 1.0E79, 1.0E80, 1.0E81, 1.0E82, 1.0E83, 1.0E84, 1.0E85, 1.0E86, 1.0E87, 1.0E88, 1.0E89, 1.0E90, 1.0E91, 1.0E92, 1.0E93, 1.0E94, 1.0E95, 1.0E96, 1.0E97, 1.0E98, 1.0E99, 1.0E100, 1.0E101, 1.0E102, 1.0E103, 1.0E104, 1.0E105, 1.0E106, 1.0E107, 1.0E108, 1.0E109, 1.0E110, 1.0E111, 1.0E112, 1.0E113, 1.0E114, 1.0E115, 1.0E116, 1.0E117, 1.0E118, 1.0E119, 1.0E120, 1.0E121, 1.0E122, 1.0E123, 1.0E124, 1.0E125, 1.0E126, 1.0E127, 1.0E128, 1.0E129, 1.0E130, 1.0E131, 1.0E132, 1.0E133, 1.0E134, 1.0E135, 1.0E136, 1.0E137, 1.0E138, 1.0E139, 1.0E140, 1.0E141, 1.0E142, 1.0E143, 1.0E144, 1.0E145, 1.0E146, 1.0E147, 1.0E148, 1.0E149, 1.0E150, 1.0E151, 1.0E152, 1.0E153, 1.0E154, 1.0E155, 1.0E156, 1.0E157, 1.0E158, 1.0E159, 1.0E160, 1.0E161, 1.0E162, 1.0E163, 1.0E164, 1.0E165, 1.0E166, 1.0E167, 1.0E168, 1.0E169, 1.0E170, 1.0E171, 1.0E172, 1.0E173, 1.0E174, 1.0E175, 1.0E176, 1.0E177, 1.0E178, 1.0E179, 1.0E180, 1.0E181, 1.0E182, 1.0E183, 1.0E184, 1.0E185, 1.0E186, 1.0E187, 1.0E188, 1.0E189, 1.0E190, 1.0E191, 1.0E192, 1.0E193, 1.0E194, 1.0E195, 1.0E196, 1.0E197, 1.0E198, 1.0E199, 1.0E200, 1.0E201, 1.0E202, 1.0E203, 1.0E204, 1.0E205, 1.0E206, 1.0E207, 1.0E208, 1.0E209, 1.0E210, 1.0E211, 1.0E212, 1.0E213, 1.0E214, 1.0E215, 1.0E216, 1.0E217, 1.0E218, 1.0E219, 1.0E220, 1.0E221, 1.0E222, 1.0E223, 1.0E224, 1.0E225, 1.0E226, 1.0E227, 1.0E228, 1.0E229, 1.0E230, 1.0E231, 1.0E232, 1.0E233, 1.0E234, 1.0E235, 1.0E236, 1.0E237, 1.0E238, 1.0E239, 1.0E240, 1.0E241, 1.0E242, 1.0E243, 1.0E244, 1.0E245, 1.0E246, 1.0E247, 1.0E248, 1.0E249, 1.0E250, 1.0E251, 1.0E252, 1.0E253, 1.0E254, 1.0E255, 1.0E256, 1.0E257, 1.0E258, 1.0E259, 1.0E260, 1.0E261, 1.0E262, 1.0E263, 1.0E264, 1.0E265, 1.0E266, 1.0E267, 1.0E268, 1.0E269, 1.0E270, 1.0E271, 1.0E272, 1.0E273, 1.0E274, 1.0E275, 1.0E276, 1.0E277, 1.0E278, 1.0E279, 1.0E280, 1.0E281, 1.0E282, 1.0E283, 1.0E284, 1.0E285, 1.0E286, 1.0E287, 1.0E288, 1.0E289, 1.0E290, 1.0E291, 1.0E292, 1.0E293, 1.0E294, 1.0E295, 1.0E296, 1.0E297, 1.0E298, 1.0E299, 1.0E300, 1.0E301, 1.0E302, 1.0E303, 1.0E304, 1.0E305, 1.0E306, 1.0E307, 1.0E308 };
    }
}

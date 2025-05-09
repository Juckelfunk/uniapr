// 
// Decompiled by Procyon v0.5.36
// 

package edu.utdallas.prf.reloc.apache.commons.codec.digest;

import edu.utdallas.prf.reloc.apache.commons.codec.binary.Hex;
import edu.utdallas.prf.reloc.apache.commons.codec.binary.StringUtils;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Key;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.Mac;

public final class HmacUtils
{
    private static final int STREAM_BUFFER_LENGTH = 1024;
    
    public static Mac getHmacMd5(final byte[] key) {
        return getInitializedMac(HmacAlgorithms.HMAC_MD5, key);
    }
    
    public static Mac getHmacSha1(final byte[] key) {
        return getInitializedMac(HmacAlgorithms.HMAC_SHA_1, key);
    }
    
    public static Mac getHmacSha256(final byte[] key) {
        return getInitializedMac(HmacAlgorithms.HMAC_SHA_256, key);
    }
    
    public static Mac getHmacSha384(final byte[] key) {
        return getInitializedMac(HmacAlgorithms.HMAC_SHA_384, key);
    }
    
    public static Mac getHmacSha512(final byte[] key) {
        return getInitializedMac(HmacAlgorithms.HMAC_SHA_512, key);
    }
    
    public static Mac getInitializedMac(final HmacAlgorithms algorithm, final byte[] key) {
        return getInitializedMac(algorithm.toString(), key);
    }
    
    public static Mac getInitializedMac(final String algorithm, final byte[] key) {
        if (key == null) {
            throw new IllegalArgumentException("Null key");
        }
        try {
            final SecretKeySpec keySpec = new SecretKeySpec(key, algorithm);
            final Mac mac = Mac.getInstance(algorithm);
            mac.init(keySpec);
            return mac;
        }
        catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }
        catch (InvalidKeyException e2) {
            throw new IllegalArgumentException(e2);
        }
    }
    
    public static byte[] hmacMd5(final byte[] key, final byte[] valueToDigest) {
        try {
            return getHmacMd5(key).doFinal(valueToDigest);
        }
        catch (IllegalStateException e) {
            throw new IllegalArgumentException(e);
        }
    }
    
    public static byte[] hmacMd5(final byte[] key, final InputStream valueToDigest) throws IOException {
        return updateHmac(getHmacMd5(key), valueToDigest).doFinal();
    }
    
    public static byte[] hmacMd5(final String key, final String valueToDigest) {
        return hmacMd5(StringUtils.getBytesUtf8(key), StringUtils.getBytesUtf8(valueToDigest));
    }
    
    public static String hmacMd5Hex(final byte[] key, final byte[] valueToDigest) {
        return Hex.encodeHexString(hmacMd5(key, valueToDigest));
    }
    
    public static String hmacMd5Hex(final byte[] key, final InputStream valueToDigest) throws IOException {
        return Hex.encodeHexString(hmacMd5(key, valueToDigest));
    }
    
    public static String hmacMd5Hex(final String key, final String valueToDigest) {
        return Hex.encodeHexString(hmacMd5(key, valueToDigest));
    }
    
    public static byte[] hmacSha1(final byte[] key, final byte[] valueToDigest) {
        try {
            return getHmacSha1(key).doFinal(valueToDigest);
        }
        catch (IllegalStateException e) {
            throw new IllegalArgumentException(e);
        }
    }
    
    public static byte[] hmacSha1(final byte[] key, final InputStream valueToDigest) throws IOException {
        return updateHmac(getHmacSha1(key), valueToDigest).doFinal();
    }
    
    public static byte[] hmacSha1(final String key, final String valueToDigest) {
        return hmacSha1(StringUtils.getBytesUtf8(key), StringUtils.getBytesUtf8(valueToDigest));
    }
    
    public static String hmacSha1Hex(final byte[] key, final byte[] valueToDigest) {
        return Hex.encodeHexString(hmacSha1(key, valueToDigest));
    }
    
    public static String hmacSha1Hex(final byte[] key, final InputStream valueToDigest) throws IOException {
        return Hex.encodeHexString(hmacSha1(key, valueToDigest));
    }
    
    public static String hmacSha1Hex(final String key, final String valueToDigest) {
        return Hex.encodeHexString(hmacSha1(key, valueToDigest));
    }
    
    public static byte[] hmacSha256(final byte[] key, final byte[] valueToDigest) {
        try {
            return getHmacSha256(key).doFinal(valueToDigest);
        }
        catch (IllegalStateException e) {
            throw new IllegalArgumentException(e);
        }
    }
    
    public static byte[] hmacSha256(final byte[] key, final InputStream valueToDigest) throws IOException {
        return updateHmac(getHmacSha256(key), valueToDigest).doFinal();
    }
    
    public static byte[] hmacSha256(final String key, final String valueToDigest) {
        return hmacSha256(StringUtils.getBytesUtf8(key), StringUtils.getBytesUtf8(valueToDigest));
    }
    
    public static String hmacSha256Hex(final byte[] key, final byte[] valueToDigest) {
        return Hex.encodeHexString(hmacSha256(key, valueToDigest));
    }
    
    public static String hmacSha256Hex(final byte[] key, final InputStream valueToDigest) throws IOException {
        return Hex.encodeHexString(hmacSha256(key, valueToDigest));
    }
    
    public static String hmacSha256Hex(final String key, final String valueToDigest) {
        return Hex.encodeHexString(hmacSha256(key, valueToDigest));
    }
    
    public static byte[] hmacSha384(final byte[] key, final byte[] valueToDigest) {
        try {
            return getHmacSha384(key).doFinal(valueToDigest);
        }
        catch (IllegalStateException e) {
            throw new IllegalArgumentException(e);
        }
    }
    
    public static byte[] hmacSha384(final byte[] key, final InputStream valueToDigest) throws IOException {
        return updateHmac(getHmacSha384(key), valueToDigest).doFinal();
    }
    
    public static byte[] hmacSha384(final String key, final String valueToDigest) {
        return hmacSha384(StringUtils.getBytesUtf8(key), StringUtils.getBytesUtf8(valueToDigest));
    }
    
    public static String hmacSha384Hex(final byte[] key, final byte[] valueToDigest) {
        return Hex.encodeHexString(hmacSha384(key, valueToDigest));
    }
    
    public static String hmacSha384Hex(final byte[] key, final InputStream valueToDigest) throws IOException {
        return Hex.encodeHexString(hmacSha384(key, valueToDigest));
    }
    
    public static String hmacSha384Hex(final String key, final String valueToDigest) {
        return Hex.encodeHexString(hmacSha384(key, valueToDigest));
    }
    
    public static byte[] hmacSha512(final byte[] key, final byte[] valueToDigest) {
        try {
            return getHmacSha512(key).doFinal(valueToDigest);
        }
        catch (IllegalStateException e) {
            throw new IllegalArgumentException(e);
        }
    }
    
    public static byte[] hmacSha512(final byte[] key, final InputStream valueToDigest) throws IOException {
        return updateHmac(getHmacSha512(key), valueToDigest).doFinal();
    }
    
    public static byte[] hmacSha512(final String key, final String valueToDigest) {
        return hmacSha512(StringUtils.getBytesUtf8(key), StringUtils.getBytesUtf8(valueToDigest));
    }
    
    public static String hmacSha512Hex(final byte[] key, final byte[] valueToDigest) {
        return Hex.encodeHexString(hmacSha512(key, valueToDigest));
    }
    
    public static String hmacSha512Hex(final byte[] key, final InputStream valueToDigest) throws IOException {
        return Hex.encodeHexString(hmacSha512(key, valueToDigest));
    }
    
    public static String hmacSha512Hex(final String key, final String valueToDigest) {
        return Hex.encodeHexString(hmacSha512(key, valueToDigest));
    }
    
    public static Mac updateHmac(final Mac mac, final byte[] valueToDigest) {
        mac.reset();
        mac.update(valueToDigest);
        return mac;
    }
    
    public static Mac updateHmac(final Mac mac, final InputStream valueToDigest) throws IOException {
        mac.reset();
        final byte[] buffer = new byte[1024];
        for (int read = valueToDigest.read(buffer, 0, 1024); read > -1; read = valueToDigest.read(buffer, 0, 1024)) {
            mac.update(buffer, 0, read);
        }
        return mac;
    }
    
    public static Mac updateHmac(final Mac mac, final String valueToDigest) {
        mac.reset();
        mac.update(StringUtils.getBytesUtf8(valueToDigest));
        return mac;
    }
}

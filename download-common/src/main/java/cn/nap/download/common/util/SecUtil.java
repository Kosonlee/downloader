package cn.nap.download.common.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SecUtil {
    public static MessageDigest createMd5Digest() throws NoSuchAlgorithmException {
        return createDigest("MD5");
    }

    public static MessageDigest createMd5SaltDigest(String salt) throws NoSuchAlgorithmException {
        MessageDigest digest = createDigest("MD5");
        updateDigest(digest, salt);
        return digest;
    }

    public static MessageDigest createDigest(String algorithm) throws NoSuchAlgorithmException {
        return MessageDigest.getInstance(algorithm);
    }

    public static String md5(String salt, File file) {
        StringBuilder md5Code = new StringBuilder();
        try (FileInputStream fileInputStream = new FileInputStream(file);
             BufferedInputStream inputStream = new BufferedInputStream(fileInputStream)) {
            MessageDigest digest = createMd5SaltDigest(salt);
            updateDigest(digest, inputStream);
            md5Code.append(new BigInteger(1, digest.digest()).toString(16));
            // 不足32位补0
            CommonUtil.leftPad(md5Code, 32, '0');
        } catch (Exception e) {
            return null;
        }
        return md5Code.toString();
    }



    public static void updateDigest(MessageDigest digest, String salt) {
        updateDigest(digest, salt.getBytes(StandardCharsets.UTF_8));
    }

    public static void updateDigest(MessageDigest digest, byte[] salt) {
        digest.update(salt);
    }

    public static void updateDigest(MessageDigest digest, InputStream inputStream) throws IOException {
        updateDigest(digest, inputStream, 8192);
    }

    public static void updateDigest(MessageDigest digest, InputStream inputStream, int byteLen) throws IOException {
        if (byteLen <= 0) {
            byteLen = 8192;
        }

        byte[] buffer = new byte[byteLen];

        int read;
        while ((read = inputStream.read(buffer, 0, byteLen)) > -1) {
            digest.update(buffer, 0, read);
        }
    }
}

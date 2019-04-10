package com.zom.sample.core.util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;


public class Md5Util {
    public static String getMD5(String string) {

        byte[] source = string.getBytes();

        String s = null;

        // 用来将字节转换成 16 进制表示的字符
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F'};
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(source);
            byte tmp[] = md.digest(); // MD5 的计算结果是一个 128 位的长整数，
            // 用字节表示就是 16 个字节
            char str[] = new char[16 * 2]; // 每个字节用 16 进制表示的话，使用两个字符，
            // 所以表示成 16 进制需要 32 个字符
            int k = 0; // 表示转换结果中对应的字符位置
            for (int i = 0; i < 16; i++) { // 从第一个字节开始，对 MD5 的每一个字节
                // 转换成 16 进制字符的转换
                byte byte0 = tmp[i]; // 取第 i 个字节
                str[k++] = hexDigits[byte0 >>> 4 & 0xf]; // 取字节中高 4 位的数字转换,
                // >>> 为逻辑右移，将符号位一起右移
                str[k++] = hexDigits[byte0 & 0xf]; // 取字节中低 4 位的数字转换
            }
            s = new String(str); // 换后的结果转换为字符串

        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

    public static String getFileMD5(String fileName, long size)
            throws Exception {
        if (size > 100 * 1024 * 1024) {
            return fastGetHash(fileName, "MD5", size);
        } else {
            return getHash(fileName, "MD5");
        }

    }

    public static String getFtpFileMD5(InputStream in, long size)
            throws Exception {
        if (size > 100 * 1024 * 1024) {
            return fastGetHash(in, "MD5", size);
        } else {
            return getHash(in, "MD5");
        }

    }

    public static String getHash(InputStream in, String hashType) throws Exception {
        // 声明16进制字母
        char[] hexChar = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F'};
        BufferedInputStream bis = new BufferedInputStream(in);
        byte buffer[] = new byte[1024 * 500];
        MessageDigest md5 = MessageDigest.getInstance(hashType);
        for (int numRead = 0; (numRead = bis.read(buffer)) > 0; ) {
            md5.update(buffer, 0, numRead);
        }

        in.close();

        byte[] tmp = md5.digest();

        char[] str = new char[32];
        byte b = 0;
        for (int i = 0; i < 16; i++) {
            b = tmp[i];
            str[2 * i] = hexChar[b >>> 4 & 0xf];
            str[2 * i + 1] = hexChar[b & 0xf];
        }

        return new String(str);
    }

    public static String fastGetHash(InputStream in, String hashType, long fileSize) throws Exception {
        // 声明16进制字母
        char[] hexChar = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F'};
        BufferedInputStream bis = new BufferedInputStream(in);
        byte buffer[] = new byte[1024 * 100];
        MessageDigest md5 = MessageDigest.getInstance(hashType);

        long amount = fileSize / 1024; // 跳过一定的量，计算1024次的hash值
        for (int numRead = 0; (numRead = bis.read(buffer)) > 0; bis
                .skip(amount)) {
            md5.update(buffer, 0, numRead);
        }

        in.close();
        // bis.close();

        byte[] tmp = md5.digest();

        char[] str = new char[32];
        byte b = 0;
        for (int i = 0; i < 16; i++) {
            b = tmp[i];
            str[2 * i] = hexChar[b >>> 4 & 0xf];
            str[2 * i + 1] = hexChar[b & 0xf];
        }

        return new String(str);
    }

    public static String getFileMD5(String fileName) throws Exception {
        return getHash(fileName, "MD5");
    }

    private static String getHash(String fileName, String hashType)
            throws Exception {
        // 声明16进制字母
        char[] hexChar = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F'};
        InputStream fis = new FileInputStream(fileName);
        BufferedInputStream bis = new BufferedInputStream(fis);
        byte buffer[] = new byte[1024 * 500];
        MessageDigest md5 = MessageDigest.getInstance(hashType);
        for (int numRead = 0; (numRead = bis.read(buffer)) > 0; ) {
            md5.update(buffer, 0, numRead);
        }

        fis.close();

        byte[] tmp = md5.digest();

        char[] str = new char[32];
        byte b = 0;
        for (int i = 0; i < 16; i++) {
            b = tmp[i];
            str[2 * i] = hexChar[b >>> 4 & 0xf];
            str[2 * i + 1] = hexChar[b & 0xf];
        }

        return new String(str);
    }

    private static String fastGetHash(String fileName, String hashType,
                                      long fileSize) throws Exception {
        // 声明16进制字母
        char[] hexChar = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F'};
        InputStream fis = new FileInputStream(fileName);
        BufferedInputStream bis = new BufferedInputStream(fis);
        byte buffer[] = new byte[1024 * 100];
        MessageDigest md5 = MessageDigest.getInstance(hashType);

        long amount = fileSize / 1024; // 跳过一定的量，计算1024次的hash值
        for (int numRead = 0; (numRead = bis.read(buffer)) > 0; bis
                .skip(amount)) {
            md5.update(buffer, 0, numRead);
        }

        fis.close();
        // bis.close();

        byte[] tmp = md5.digest();

        char[] str = new char[32];
        byte b = 0;
        for (int i = 0; i < 16; i++) {
            b = tmp[i];
            str[2 * i] = hexChar[b >>> 4 & 0xf];
            str[2 * i + 1] = hexChar[b & 0xf];
        }

        return new String(str);
    }

    public static String getMD5(String string, boolean isCase) {
        return getMD5(string.getBytes(StandardCharsets.UTF_8), isCase);
    }

    public static String getMD5(byte[] source, boolean isCase) {

        String s = null;

        // 用来将字节转换成 16 进制表示的字符
        char[] hexDigits = isCase ? new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F'} : new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f'};

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(source);
            // MD5 的计算结果是一个 128 位的长整数，
            byte[] tmp = md.digest();
            // 用字节表示就是 16 个字节
            // 每个字节用 16 进制表示的话，使用两个字符，
            char[] str = new char[16 * 2];
            // 所以表示成 16 进制需要 32 个字符
            // 表示转换结果中对应的字符位置
            int k = 0;
            for (int i = 0; i < 16; i++) {
                // 转换成 16 进制字符的转换
                byte byte0 = tmp[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                // >>> 为逻辑右移，将符号位一起右移
                str[k++] = hexDigits[byte0 & 0xf];
            }
            s = new String(str);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }
}

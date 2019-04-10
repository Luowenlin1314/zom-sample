package com.zom.sample.core.util;

import com.zom.sample.spring.security.ApiAuthenticationFilter;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.security.Security;
import java.util.Arrays;
import java.util.Base64;

/**
 * AES 是一种可逆加密算法，对用户的敏感信息加密处理 对原始数据进行AES加密后，在进行Base64编码转化；
 */
public class AesUtil {

    private static AesUtil instance = null;
    /**
     * 加密用的Key 可以用26个字母和数字组成 此处使用AES-128-CBC加密模式，key需要为16位。
     */
    private Integer keyLen = 16;
    private String sKey = "zomaa15915786108";
    private String ivParameter = "0392039203920300";

    private AesUtil() {
    }

    public static AesUtil getInstance() {
        if (instance == null) {
            instance = new AesUtil();
        }
        return instance;
    }

    public String encrypt(String src) {
        return encrypt(src, sKey, ivParameter);
    }

    public String encrypt(String src, String key, String ivs) {

        try {
            if (checkParams(src, key, ivs)) {
                return null;
            }

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            byte[] raw = key.getBytes();
            SecretKeySpec sKeySpec = new SecretKeySpec(raw, "AES");
            // 使用CBC模式，需要一个向量iv，可增加加密算法的强度
            IvParameterSpec iv = new IvParameterSpec(ivs.getBytes());
            cipher.init(Cipher.ENCRYPT_MODE, sKeySpec, iv);
            byte[] encrypted = cipher.doFinal(src.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String encodeBytes(byte[] bytes) {
        StringBuilder strBuf = new StringBuilder();

        for (byte aByte : bytes) {
            strBuf.append((char) (((aByte >> 4) & 0xF) + ((int) 'a')));
            strBuf.append((char) (((aByte) & 0xF) + ((int) 'a')));
        }

        return strBuf.toString();
    }

    public String decrypt(String src) {
        return decrypt(src, sKey, ivParameter);
    }

    public String decrypt(String src, String key, String ivs) {
        try {
            if (checkParams(src, key, ivs)) {
                return null;
            }

            byte[] raw = key.getBytes(StandardCharsets.US_ASCII);
            SecretKeySpec sKeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec(ivs.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, sKeySpec, iv);
            byte[] encrypted1 = Base64.getDecoder().decode(src);
            byte[] original = cipher.doFinal(encrypted1);
            return new String(original, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            return null;
        }
    }

    private boolean checkParams(String src, String key, String ivs) {
        return StringUtils.isBlank(src) || StringUtils.isBlank(key) || StringUtils.isBlank(ivs)
                || key.length() != keyLen || ivs.length() != keyLen;
    }


    public String getRandomString(int len) {
        String seed = "~`!@#$%^&*()_+-=[]{}|;':<>/,.?0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        return RandomStringUtils.random(len == 0 ? 32 : len, seed.toCharArray());
    }

    public String getClientSecret(String key, String vi) {

        int aesKeyLen = 16;
        if (StringUtils.isBlank(key) || StringUtils.isBlank(vi) || key.length() != aesKeyLen || vi.length() != aesKeyLen) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < aesKeyLen; i++) {
            sb.append(key.charAt(i)).append(vi.charAt(i));
        }

        return sb.toString();
    }

    /**
     * 将密文密码解密成明文密码
     *
     * @param request   请求
     * @param src       密文密码
     * @param isEncrypt 是否加密否则解密
     * @return 明文密码
     */
    public String cryptoPassword(HttpServletRequest request, String src, boolean isEncrypt) {
        String clientId = request.getParameter("clientId");
        String clientSecret = ApiAuthenticationFilter.KEY_MAP.get(clientId);

        String aesKey = clientSecret.substring(0, 16);
        String aesIv = clientSecret.substring(16);

        return isEncrypt ? encrypt(src, aesKey, aesIv) : decrypt(src, aesKey, aesIv);
    }

}

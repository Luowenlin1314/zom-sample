package com.zom.sample.core.util;

import org.springframework.core.io.FileSystemResource;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

/**
 * 加密规则：
 * sign=MD5(uri+ASC(queryMap.putAll(bodyMap))+clientSecret)
 * <p>
 * MD5 = 32位小写md5计算函数
 * uri = 域名端口以后的uri
 * ASC = 将参数map正向排序函数，map值仅支持字符或数字或数组，当map值为数组时，将数组值以逗号拼接
 * queryMap = 查询参数map，必定包含clientId(客户端id)、time(10位时间戳)，包含中文请进行urlEncode
 * bodyMap = body参数map
 * clientSecret = 客户端密钥
 *
 */
public class CryptoUtil {

    @Deprecated
    public static String encrypt(String uri, TreeMap<String, Object> query, TreeMap<String, Object> body, String secret) {

        TreeMap<String, Object> sortedMap = new TreeMap<>();
        if (query != null) {
            sortedMap.putAll(query);
        }
        if (body != null) {
            sortedMap.putAll(body);
        }

        return Md5Util.getMD5(uri + sortedParamsAsc(sortedMap) + secret, false);
    }

    public static String encrypt(String uri, TreeMap<String, Object> query, String secret) {

        TreeMap<String, Object> sortedMap = new TreeMap<>();
        if (query != null) {
            sortedMap.putAll(query);
        }

        return Md5Util.getMD5(uri + sortedParamsAsc(sortedMap) + secret, false);
    }

    @Deprecated
    public static boolean matches(String sign, String uri, TreeMap<String, Object> query, TreeMap<String, Object> body, String secret) {
        return sign.equalsIgnoreCase(encrypt(uri, query, body, secret));
    }

    public static boolean matches(String sign, String uri, TreeMap<String, Object> query, String secret) {
        String eData = encrypt(uri, query, secret);
        return sign.equalsIgnoreCase(eData);
    }

    /**
     * 将参数排序
     *
     * @param params 待排序的参数及值，参数值仅支持字符或数字或数组，当数组为byte[]时，将转base64
     * @return 获得排序后的参数字符串
     */
    public static String sortedParamsAsc(Map<String, Object> params) {

        if (params == null) {
            return null;
        }

        Map<String, Object> map;
        if (params instanceof TreeMap) {
            map = params;
        } else {
            map = new TreeMap<>(params);
        }

        List<String> result = new ArrayList<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getValue() instanceof Iterable) {
                result.add(entry.getKey() + "=" + String.join(",", (Iterable<? extends CharSequence>) entry.getValue()));
            } else if (entry.getValue() instanceof CharSequence[]) {
                result.add(entry.getKey() + "=" + String.join(",", (CharSequence[]) entry.getValue()));
            } else if (entry.getValue() instanceof byte[]) {
                result.add(entry.getKey() + "=" + base64Encode((byte[]) entry.getValue()));
            } else {
                result.add(entry.getKey() + "=" + entry.getValue());
            }
        }

        return String.join("&", result);
    }

    /**
     * 将参数排序
     *
     * @param params 待排序的参数及值，参数值仅支持字符或数字或数组，当数组为byte[]时，将转base64
     * @return 获得排序后的参数字符串
     */
    public static String sortedParamsAscCompact(Map<String, Object> params) {

        if (params == null) {
            return null;
        }

        Map<String, Object> map;
        if (params instanceof TreeMap) {
            map = params;
        } else {
            map = new TreeMap<>(params);
        }

        List<String> result = new ArrayList<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getValue() instanceof Iterable) {
                result.add(entry.getKey() + "" + String.join("", (Iterable<? extends CharSequence>) entry.getValue()));
            } else if (entry.getValue() instanceof CharSequence[]) {
                result.add(entry.getKey() + "" + String.join("", (CharSequence[]) entry.getValue()));
            } else if (entry.getValue() instanceof byte[]) {
                result.add(entry.getKey() + "" + base64Encode((byte[]) entry.getValue()));
            } else {
                result.add(entry.getKey() + "" + entry.getValue());
            }
        }
        return String.join("", result);
    }


    public static String base64Encode(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * 对map编码
     *
     * @param map map对象
     * @return 编码后的map
     */
    public static Map<String, Object> mapEncode(Map<String, Object> map) {

        Map<String, Object> result = new HashMap<>(1);

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getValue() instanceof Iterable) {
                Iterable<String> el = (Iterable<String>) entry.getValue();
                List<String> list = new ArrayList<>();
                el.forEach(str -> list.add(strEncode(str)));
                result.put(entry.getKey(), list);
            } else if (entry.getValue() instanceof CharSequence[]) {
                String[] strArr = (String[]) entry.getValue();
                List<String> list = new ArrayList<>();
                for (String str : strArr) {
                    list.add(strEncode(str));
                }
                result.put(entry.getKey(), list);
            } else if (entry.getValue() instanceof CharSequence) {
                String str = (String) entry.getValue();
                result.put(entry.getKey(), strEncode(str));
            } else if (entry.getValue() instanceof FileSystemResource) {
                //do nothing
            } else {
                result.put(entry.getKey(), entry.getValue());
            }
        }

        return result;
    }

    /**
     * 对数组编码
     *
     * @param arr 数组对象
     * @return 编码后的数组
     */
    public static Object arrayEncode(Object arr) {
        if (arr instanceof Iterable) {
            Iterable<String> el = (Iterable<String>) arr;
            List<String> list = new ArrayList<>();
            el.forEach(str -> list.add(strEncode(str)));
            return list;
        } else if (arr instanceof CharSequence[]) {
            String[] strArr = (String[]) arr;
            List<String> list = new ArrayList<>();
            for (String str : strArr) {
                list.add(strEncode(str));
            }
            return list;
        } else {
            return null;
        }
    }

    /**
     * 对单独字段编码
     *
     * @param str 字符
     * @return 编码后的字符
     */
    public static String strEncode(String str) {
        try {
            return URLEncoder.encode(str, "UTF-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {

        //登录示例
        String uri = "/ylin/api/v1/community/auth/login";
        TreeMap<String, Object> queryParams = new TreeMap<>();
        TreeMap<String, Object> bodyParams = new TreeMap<>();
        String clientSecret = "testSecret";
        String sign;
        queryParams.put("clientId", "62862db07be303be7362024c55c90ed0");
        queryParams.put("time", 1536840637);
        bodyParams.put("identity", "fdgfdg345");
        bodyParams.put("type", 1);
        sign = encrypt(uri, queryParams, clientSecret);
        //sign=e9e23d91aea5ac1ecedd6b15d208b480
        System.out.println("sign=" + sign);

        //数组参数排序
        List<String> arr = new ArrayList<>();
        arr.add("234");
        arr.add("中文");
        Map<String, Object> map = new TreeMap<>();
        map.put("test", 213);
        map.put("bytes", "hhhhh".getBytes());
        map.put("arrayList", arr);
        map.put("stringList", new String[]{"aa", "中文"});
        //arrayList=234%E4%B8%AD%E6%96%87&bytes=aGhoaGg=&stringList=aa%E4%B8%AD%E6%96%87&test=213
        System.out.println(sortedParamsAsc(mapEncode(map)));

        System.out.println(String.join(",",new String[]{"775757,556"}));
    }
}

package com.zom.sample.core;

/**
 * 系统常量
 */
public class Const {

    /**
     * jwt密钥
     */
    public static final String JWT_SECRET = "zom@ssw02d";

    /**
     * 存放token的header 的 key值
     */
    public static final String TOKEN_HEADER = "Access-Token";

    /**
     * 访问令牌
     */
    public static final String RKNS_AT = "account:at:";
    /**
     * 刷新令牌
     */
    public static final String RKNS_RT = "account:rt:";

    public static final String RKNS_USER_INFO = "account:info:";

    /**
     * 访问令牌有效期为一小时
     */
    public static final Long AT_TIMEOUT = 3600L;
    /**
     * 刷新令牌有效期为一个月
     */
    public static final Long RT_TIMEOUT = 2592000L;


}

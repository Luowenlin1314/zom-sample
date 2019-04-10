package com.zom.sample.core;


public enum ErrorCode {

    /**
     * 默认值
     */
    OK(1, "成功"),
    SYSTEM_ERROR(10001, "系统错误"),
    SERVICE_UNAVAILABLE(10002, "非法请求"),
    PARAMETER_REQUIRED(10003, "缺少参数"),
    PARAMETER_ILLEGAL(10004, "非法参数"),
    ILLEGAL_REQUEST(10012, "非法请求"),
    REQUEST_TIMEOUT(10013, "请求超时"),
    SESSION_TIMEOUT(10014, "会话超时"),
    REQUEST_UN_SUPPORT(10015, "请求方式错误"),
    REQUEST_CONTENT_TYPE_UN_SUPPORT(10016, "请求媒体类型错误"),
    REQUEST_DUPLICATE(10017, "重复提交"),
    CODING(10018, "此功能正在开发中..."),
    INCOMPLETE_INFORMATION(10019, "输入信息不完整"),
    IP_LIMIT(10030, "此 IP 被禁止"),
    ERROR_BASE64(10031, "base64文件处理失败"),
    FILE_TRANSFER_ERROR(10032, "文件传输异常"),
    INCORRECT_PWD_RULES(10033, "密码规则不符"),

    /**
     * 21XXX: OAuth 系统
     */
    AUTH_FAILURE(21001, "认证失败"),
    AUTH_PARAMETER_ABSENT(21003, "缺少参数"),
    AUTH_TYPE_UN_SUPPORT(21004, "认证类型不支持"),
    AUTH_CLIENT_ILLEGAL(21010, "非法客户端"),
    AUTH_CLIENT_FORBIDDEN(21011, "客户端被禁"),
    AUTH_CLIENT_AUTH_FAILURE(21012, "接入端认证失败"),
    AUTH_REQUEST_EXPIRED(21013, "URL请求过期"),
    AUTH_CODE_EXPIRED(21020, "code 已经失效"),
    AUTH_TOKEN_EXPIRED(21021, "access_token 已经失效"),
    AUTH_TOKEN_ILLEGAL(21022, "非法 token"),
    AUTH_PWD_ERROR(21023, "密码错误"),
    AUTH_PWD_TIME_OUT(21024, "密码过期"),
    AUTH_UNKNOWN_USER(21025, "账号不存在"),
    AUTH_USER_INFO_MISMATCH(21026, "用户信息不一致"),
    AUTH_REFRESH_TOKEN_EXPIRED(21027, "refresh_token 已经失效"),

    /**
     * 31XXX:user 系统
     */
    USER_EXIST(31001, "账号已存在"),
    USER_NOT_PERMISSION(31002, "账号无权限"),
    USER_FORBIDDEN(31003, "账号已冻结"),

    ;

    private Integer code;
    private String msg;

    ErrorCode(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}

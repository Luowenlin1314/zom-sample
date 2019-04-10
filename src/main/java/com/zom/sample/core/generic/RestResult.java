package com.zom.sample.core.generic;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.zom.sample.core.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;


@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestResult<T> {

    private Integer code;
    private String msg;
    private T data;

    public static <T> RestResult<T> ok() {
        return new RestResult<>(ErrorCode.OK.getCode(), ErrorCode.OK.getMsg(), null);
    }

    public static <T> RestResult<T> ok(T data) {
        return new RestResult<>(ErrorCode.OK.getCode(), ErrorCode.OK.getMsg(), data);
    }

    public static <T> RestResult<T> error() {
        return new RestResult<>(ErrorCode.SYSTEM_ERROR.getCode(), ErrorCode.SYSTEM_ERROR.getMsg(), null);
    }

    public static <T> RestResult<T> error(T data) {
        return new RestResult<>(ErrorCode.SYSTEM_ERROR.getCode(), ErrorCode.SYSTEM_ERROR.getMsg(), data);
    }

    public static <T> RestResult<T> gen(Integer code, String msg, T data) {
        return new RestResult<>(code, msg, data);
    }

    public static <T> RestResult<T> gen(ErrorCode code) {
        return new RestResult<>(code.getCode(), code.getMsg(), null);
    }

    public static <T> RestResult<T> gen(ErrorCode code, String msg) {
        return new RestResult<>(code.getCode(), StringUtils.isBlank(msg) ? code.getMsg() : msg, null);
    }

    public static <T> RestResult<T> gen(ErrorCode code, T data) {
        if (data instanceof String) {
            return gen(code, (String) data);
        }
        return new RestResult<>(code.getCode(), code.getMsg(), data);
    }

    @JsonIgnore
    public boolean isOk() {
        return ErrorCode.OK.getCode().equals(getCode());
    }

    @JsonIgnore
    public boolean isNotOk() {
        return !ErrorCode.OK.getCode().equals(getCode());
    }
}

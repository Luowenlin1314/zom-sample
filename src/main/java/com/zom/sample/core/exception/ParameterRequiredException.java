package com.zom.sample.core.exception;


import com.zom.sample.core.ErrorCode;
import com.zom.sample.core.generic.RestResult;

/**
 * 参数为空异常
 *
 * @author HeJunLin
 * @version 1.0
 * @since 2018/12/04 10:01
 */
public class ParameterRequiredException extends AbstractErrorCodeException {

    public ParameterRequiredException() {
    }

    public ParameterRequiredException(String message) {
        super(message);
    }

    @Override
    public RestResult response() {
        return RestResult.gen(ErrorCode.PARAMETER_REQUIRED, this.getMessage());
    }
}
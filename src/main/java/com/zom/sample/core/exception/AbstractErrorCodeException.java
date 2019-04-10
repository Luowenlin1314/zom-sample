package com.zom.sample.core.exception;


import com.zom.sample.core.generic.RestResult;

/**
 * 自定义抽象异常，所有继承该类的异常都可以向前台抛出自定义错误码，只生效于抛出到controller的异常
 */
public abstract class AbstractErrorCodeException extends Exception {

//    private String message;

    public AbstractErrorCodeException() {
    }

    public AbstractErrorCodeException(String message) {
        super(message);
    }

    /**
     * 返回错误json
     *
     * @return code、msg
     */
    public abstract RestResult response();
}

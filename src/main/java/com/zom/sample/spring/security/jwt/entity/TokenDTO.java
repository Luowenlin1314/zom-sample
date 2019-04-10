package com.zom.sample.spring.security.jwt.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @description: token 模型
 * @author: zoom
 * @create: 2019-04-10 10:29
 */
@Data
@AllArgsConstructor
public class TokenDTO {

    /**
     * 访问令牌
     */
    private String accessToken;
    /**
     * at失效时间
     */
    private Long expire;
    /**
     * 刷新令牌
     */
    private String refreshToken;

}

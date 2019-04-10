package com.zom.sample.spring.security.jwt.entity;

import lombok.Data;

/**
 * @description:
 * @author: zoom
 * @create: 2019-04-10 10:30
 */
@Data
public class LoginInfoDTO {

    private String userId;
    private String loginToken;

}

package com.zom.sample.spring.security.jwt.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Date;

/**
 * @description: 加密
 * @author: zoom
 * @create: 2019-04-10 10:31
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClaimsDTO {

    /**
     * 令牌类型
     */
    private String type;
    /**
     * 用户id
     */
    private String id;
    /**
     * 失效日期
     */
    private Date date;

}

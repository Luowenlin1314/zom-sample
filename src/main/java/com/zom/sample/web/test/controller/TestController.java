package com.zom.sample.web.test.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description:
 * @author: zoom
 * @create: 2019-04-10 09:10
 */
@RestController
@RequestMapping("/v1/api/test")
public class TestController {

    @GetMapping
    public String test(){
        return "test";
    }

}

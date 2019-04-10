package com.zom.sample.spring.data;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Data
@Component
@ConfigurationProperties(prefix = "spring.redis")
public class RedisProps {

    private String host;
    private int port;
    private String password;
    private int timeout;
    private int database;
    private Pool pool;

    @Data
    public static class Pool {
        int maxTotal;
        int maxIdle;
        long maxWaitMillis;
        int numTestsPerEvictionRun;
        long timeBetweenEvictionRunsMillis;
        long minEvictableIdleTimeMillis;
        long softMinEvictableIdleTimeMillis;
        boolean testOnBorrow;
        boolean testOnReturn;
        boolean testWhileIdle;
        boolean blockWhenExhausted;
    }
}

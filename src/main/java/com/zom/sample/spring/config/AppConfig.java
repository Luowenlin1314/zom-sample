package com.zom.sample.spring.config;

import com.zom.sample.core.util.SnowflakeIdWorker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;


@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class AppConfig {

    @Bean
    public SnowflakeIdWorker snowflakeIdWorker(){
        return new SnowflakeIdWorker();
    }

    /**
     * 用于spring redis，防止每次创建一个线程
     *
     */
    @Bean("redisExecutor")
    public ThreadPoolTaskExecutor redisExecutor() {
        ThreadPoolTaskExecutor redisTaskExecutor = new ThreadPoolTaskExecutor();
        redisTaskExecutor.setCorePoolSize(8);
        redisTaskExecutor.setMaxPoolSize(16);
        redisTaskExecutor.setKeepAliveSeconds(30);
        redisTaskExecutor.setQueueCapacity(50);
        redisTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return redisTaskExecutor;
    }
}
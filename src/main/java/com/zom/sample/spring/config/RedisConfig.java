package com.zom.sample.spring.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zom.sample.spring.data.RedisProps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @description: redis相关配置
 * @author: zoom
 * @create: 2019-04-09 17:30
 */
@Slf4j
@Configuration
public class RedisConfig {

    @Autowired
    private RedisProps redisProps;
    @Autowired
    @Qualifier("redisExecutor")
    private ThreadPoolTaskExecutor redisExecutor;

    /**
     * redis连接工厂
     */
    @Bean
    public JedisConnectionFactory redisConnectionFactory() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxIdle(redisProps.getPool().getMaxIdle());
        config.setMaxTotal(redisProps.getPool().getMaxTotal());
        config.setMaxWaitMillis(redisProps.getPool().getMaxWaitMillis());
        config.setTestOnBorrow(redisProps.getPool().isTestOnBorrow());
        config.setTestOnReturn(redisProps.getPool().isTestOnReturn());
        config.setNumTestsPerEvictionRun(redisProps.getPool().getNumTestsPerEvictionRun());
        config.setTimeBetweenEvictionRunsMillis(redisProps.getPool().getTimeBetweenEvictionRunsMillis());
        config.setMinEvictableIdleTimeMillis(redisProps.getPool().getMinEvictableIdleTimeMillis());
        config.setSoftMinEvictableIdleTimeMillis(redisProps.getPool().getSoftMinEvictableIdleTimeMillis());
        config.setTestWhileIdle(redisProps.getPool().isTestWhileIdle());
        config.setBlockWhenExhausted(redisProps.getPool().isBlockWhenExhausted());

        JedisConnectionFactory factory = new JedisConnectionFactory(config);
        factory.setHostName(redisProps.getHost());
        factory.setPort(redisProps.getPort());
        factory.setPassword(redisProps.getPassword());
        factory.setTimeout(redisProps.getTimeout());
        factory.setDatabase(redisProps.getDatabase());
        return factory;
    }

    /**
     * redis模板，用于获取redis操作对象
     */
    @Bean
    public StringRedisTemplate redisTemplate() {

        StringRedisTemplate template = new StringRedisTemplate();

        template.setConnectionFactory(redisConnectionFactory());

        //Jackson序列化节省redis内存
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);

        // 设置默认序列化工具
        template.setValueSerializer(jackson2JsonRedisSerializer);
        // 设置支持事务
//        template.setEnableTransactionSupport(true);
        template.afterPropertiesSet();

        return template;
    }

}

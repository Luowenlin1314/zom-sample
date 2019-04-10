package com.zom.sample.spring.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * @description: 数据库相关配置
 * @author: zoom
 * @create: 2019-04-09 17:05
 */
@Configuration
public class DruidDataSourceConfig {

    @Value("${datasource.type}")
    private Class<? extends DataSource> dataSourceType;

    /**
     * 配置写数据源 jpa注入
     */
    @Bean(name = "mainDataSource")
    @Primary
    @ConfigurationProperties(prefix = "datasource")
    public DruidDataSource mainDataSource() throws Exception {
        return (DruidDataSource) DataSourceBuilder.create().type(dataSourceType).build();
    }

}

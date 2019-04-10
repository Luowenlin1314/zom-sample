package com.zom.sample.spring.config;

import com.zom.sample.spring.security.ApiAuthenticationFilter;
import com.zom.sample.spring.security.jwt.JwtAuthenticationTokenFilter;
import com.zom.sample.spring.security.RestAuthenticationEntryPoint;
import com.zom.sample.spring.security.jwt.TokenService;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @description:
 * @author: zoom
 * @create: 2019-04-10 09:18
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public JwtAuthenticationTokenFilter authenticationTokenFilterBean() throws Exception {
        return new JwtAuthenticationTokenFilter();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 由于使用的是JWT，我们这里不需要csrf
        // 基于token，所以不需要session
        // 所以请求都需要认证
        // 没有验证的统一返回
        // 增加token过滤
        http.csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeRequests().anyRequest().authenticated().and()
                .exceptionHandling().authenticationEntryPoint(new RestAuthenticationEntryPoint()).and()
                .addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class);
        //禁用缓存
        http.headers().cacheControl();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/**");
    }

    // 装载BCrypt密码编码器
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    /**
     * 注册第三方api过滤器
     *
     * @param tokenService
     * @return
     */
    @Bean
    public FilterRegistrationBean apiAuthenticationFilterRegistration(TokenService tokenService) {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new ApiAuthenticationFilter(tokenService));
        registration.addUrlPatterns("/v1/api/*");
        registration.setName("ApiAuthenticationFilter");
        registration.setOrder(1);
        return registration;
    }
}

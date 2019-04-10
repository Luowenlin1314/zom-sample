package com.zom.sample.spring.security.jwt;

import com.zom.sample.spring.security.jwt.TokenService;
import com.zom.sample.spring.security.jwt.entity.LoginInfoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @description: token校验
 * @author: zoom
 * @create: 2019-04-10 10:13
 */
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private TokenService tokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "DELETE,POST,GET,PUT");
        response.setHeader("Access-Control-Allow-Headers", "Access-Token,Refresh-Token,Content-Type");
        if (!request.getMethod().toUpperCase().equals(HttpMethod.OPTIONS.name())) {
            String token = request.getHeader("Access-Token");
            LoginInfoDTO loginInfoDTO = tokenService.getLoginInfoByToken(token);
            if (loginInfoDTO != null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(loginInfoDTO.getUserId());
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                logger.info("authenticated user " + userDetails.getUsername() + ", setting security context");
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            filterChain.doFilter(request, response);
        }
    }
}

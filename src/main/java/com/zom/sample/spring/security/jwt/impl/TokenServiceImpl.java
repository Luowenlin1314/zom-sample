package com.zom.sample.spring.security.jwt.impl;


import com.fasterxml.jackson.core.type.TypeReference;
import com.zom.sample.core.Const;
import com.zom.sample.core.redis.RedisService;
import com.zom.sample.core.util.JacksonUtil;
import com.zom.sample.spring.security.jwt.TokenService;
import com.zom.sample.spring.security.jwt.entity.ClaimsDTO;
import com.zom.sample.spring.security.jwt.entity.LoginInfoDTO;
import com.zom.sample.spring.security.jwt.entity.TokenDTO;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Claims;

import java.util.Date;
import java.util.Map;

/**
 * jwt token管理实现类
 *
 * @author HeJunLin
 * @version 1.0
 * @since 2018/9/5 19:20
 */
@Service("tokenService")
public class TokenServiceImpl implements TokenService {

    private RedisService redisService;

    @Override
    public TokenDTO getTokens(String id, boolean isTest) {
        String atKey = Const.RKNS_AT + id;
        if (redisService.exists(atKey)) {
            String cachedAt = redisService.get(atKey);
            String cachedRt = redisService.get(Const.RKNS_RT + id);
            return new TokenDTO(cachedAt, Const.AT_TIMEOUT, cachedRt);
        } else {
            return new TokenDTO(newAt(id), Const.AT_TIMEOUT, isTest ? null : newRt(id));
        }
    }

    @Override
    public boolean checkAccessToken(String accessToken) {
        // 1.校验token
        Claims claims = getTokenClaims(accessToken);
        if (claims == null) {
            return false;
        }
        // 2.查找redis中是否存在该access_token
        String id = getId(claims);
        String cachedAt = redisService.get(Const.RKNS_AT + id);
        // 3.校验access_token是否一致
        return accessToken.equals(cachedAt);
    }

    @Override
    public String getId(String accessToken) {
        Claims claims = getTokenClaims(accessToken);
        if (claims == null) {
            return null;
        }
        return getId(claims);
    }

    @Override
    public LoginInfoDTO getLoginInfoById(String id) {
        String cacheKey = Const.RKNS_USER_INFO + id;

        if (redisService.exists(cacheKey)) {
            String json = redisService.get(cacheKey);
            LoginInfoDTO loginInfoDTO;
            try {
                loginInfoDTO = JacksonUtil.json2pojo(json, new TypeReference<LoginInfoDTO>() {
                });
                return loginInfoDTO;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public LoginInfoDTO getLoginInfoByToken(String token) {
        // 1.校验token
        Claims claims = getTokenClaims(token);
        if (claims == null) {
            return null;
        }

        // 2.查找redis中是否存在该access_token
        String id = getId(claims);

        return getLoginInfoById(id);
    }

    @Override
    public void saveLoginInfo(LoginInfoDTO dto) throws Exception {
        String cacheKey = Const.RKNS_USER_INFO + dto.getUserId();
        redisService.set(cacheKey, JacksonUtil.obj2json(dto), Const.AT_TIMEOUT);
    }

    @Override
    public String getAccessToken(String refreshToken) {
        // 1.校验token
        Claims claims = getTokenClaims(refreshToken);
        if (claims == null) {
            return null;
        }
        // 2.查找redis中是否存在该refresh_token
        String id = getId(claims);
        String cachedRt = redisService.get(Const.RKNS_RT + id);
        // 3.校验refresh_token是否一致
        if (!refreshToken.equals(cachedRt)) {
            return null;
        }
        // 4.校验通过，生成新access_token
        return newAt(id);
    }

    @Override
    public void revokeTokens(String id) {
        redisService.del(Const.RKNS_AT + id);
        redisService.del(Const.RKNS_RT + id);
        redisService.del(Const.RKNS_USER_INFO + id);
    }

    private String newAt(String id) {
        ClaimsDTO atClaims = newClaims(id, "AT");
        String accessToken = newToken(atClaims, generateExpirationDate(Const.AT_TIMEOUT), false);
        redisService.set(Const.RKNS_AT + id, accessToken, Const.AT_TIMEOUT);
        return accessToken;
    }

    private String newRt(String id) {
        ClaimsDTO rtClaims = newClaims(id, "RT");
        String refreshToken = newToken(rtClaims, generateExpirationDate(Const.RT_TIMEOUT), true);
        redisService.set(Const.RKNS_RT + id, refreshToken, Const.RT_TIMEOUT);
        return refreshToken;
    }

    private String newToken(ClaimsDTO dto, Date dateExpiration, boolean isEnhance) {
        return Jwts.builder()
                .setClaims(JacksonUtil.pojo2map(dto, Map.class))
                .setExpiration(dateExpiration)
                .signWith(isEnhance ? SignatureAlgorithm.HS512 : SignatureAlgorithm.HS256, Const.JWT_SECRET)
                .compact();
    }

    private ClaimsDTO newClaims(String id, String type) {
        ClaimsDTO dto = new ClaimsDTO();
        dto.setType(type);
        dto.setId(id);
        dto.setDate(new Date());
        return dto;
    }

    private Claims getTokenClaims(String token) {
        Claims claims;
        try {
            claims = Jwts.parser()
                    .setSigningKey(Const.JWT_SECRET)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            return null;
        }
        return claims;
    }

    private String getId(Claims claims) {
        ClaimsDTO dto = JacksonUtil.map2pojo(claims, ClaimsDTO.class);
        return dto.getId();
    }

    private Date generateExpirationDate(Long time) {
        return new Date(System.currentTimeMillis() + time * 1000);
    }

    @Autowired
    public void setRedisService(RedisService redisService) {
        this.redisService = redisService;
    }
}

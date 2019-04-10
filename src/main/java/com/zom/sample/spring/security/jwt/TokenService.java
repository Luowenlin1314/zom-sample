package com.zom.sample.spring.security.jwt;

import com.zom.sample.spring.security.jwt.entity.LoginInfoDTO;
import com.zom.sample.spring.security.jwt.entity.TokenDTO;

/**
 * jwt token管理
 *
 * @author HeJunLin
 * @version 1.0
 * @since 2018/9/5 16:02
 */
public interface TokenService {
    /**
     * 获取令牌
     *
     * @param id     用户id或其他id
     * @param isTest 测试接口不生成rt，不占用内存
     * @return at、rt
     */
    TokenDTO getTokens(String id, boolean isTest);

    /**
     * 校验访问令牌
     *
     * @param accessToken 访问令牌
     * @return 校验结果
     */
    boolean checkAccessToken(String accessToken);

    /**
     * 解析token信息
     *
     * @param token
     * @return
     */
    LoginInfoDTO getLoginInfoByToken(String token);

    /**
     * 获取登录id
     *
     * @param accessToken 访问令牌
     * @return 用户id
     */
    String getId(String accessToken);

    /**
     * 获取登录信息
     *
     * @param id id
     * @return 登录信息
     */
    LoginInfoDTO getLoginInfoById(String id);

    /**
     * 保存登录信息
     *
     * @param dto 登录信息
     */
    void saveLoginInfo(LoginInfoDTO dto) throws Exception;

    /**
     * 获取新的访问令牌
     *
     * @param refreshToken 刷新令牌
     * @return 访问令牌
     */
    String getAccessToken(String refreshToken);

    /**
     * 立即吊销全部令牌
     */
    void revokeTokens(String id);
}

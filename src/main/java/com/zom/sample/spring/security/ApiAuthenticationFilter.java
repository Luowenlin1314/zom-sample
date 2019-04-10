package com.zom.sample.spring.security;



import com.zom.sample.core.Const;
import com.zom.sample.core.ErrorCode;
import com.zom.sample.core.generic.RestResult;
import com.zom.sample.core.util.CryptoUtil;
import com.zom.sample.core.util.JacksonUtil;
import com.zom.sample.spring.security.jwt.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * api鉴权过滤器
 */
@Slf4j
public class ApiAuthenticationFilter implements Filter {

    public static final Map<String, String> KEY_MAP = new ConcurrentHashMap<>();
    private static final Pattern PATTERN = Pattern.compile("/v1/api/users/login.*");
    private static final String PARAM_CLIENT_ID = "clientId";
    private static final String PARAM_TIME = "time";
    private static final String PARAM_SIGN = "sign";
    private static final Integer URL_TIMEOUT = 60000;

    private TokenService tokenService;

    public ApiAuthenticationFilter(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 开发
        KEY_MAP.put("c7fbc23983056715286424974f0c7567", "%@tosG1>r^lR$<pSIUB+-Nbth3|%FYN6");
        // 测试
        KEY_MAP.put("2b78727586eb91c5af5c61b0c9f4b512", "U.mN45zik>#F~hM3Y4,i@~`h*F>F`DBD");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        long start = System.currentTimeMillis();

        // 备份request
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        ServletRequestWrapper requestWrapper;
        // 跨域支持
        response.setHeader("Access-control-Allow-Origin", request.getHeader("Origin"));
        response.setHeader("Access-Control-Allow-Methods", "*");
        response.setHeader("Access-Control-Allow-Headers", request.getHeader("Access-Control-Request-Headers"));
        response.setHeader("Access-Control-Allow-Credentials", "true");
        // 跨域时会首先发送一个option请求，这里我们给option请求直接返回正常状态
        if (request.getMethod().equals(RequestMethod.OPTIONS.name())) {
            response.setStatus(HttpStatus.OK.value());
            filterChain.doFilter(request, response);
            return;
        }

        // 解析uri参数
        String uri = getUri(request);
        log.debug("uri参数({})：{}", request.getMethod(), uri);

        // 解析query参数
        TreeMap<String, Object> queryParams = getQueryParams(request);
        log.debug("query参数：{}", queryParams);

        // 解析body参数
        requestWrapper = getWrapper(request);
        TreeMap<String, Object> bodyParams = null;
        if (requestWrapper != null) {
            bodyParams = getBodyParams(requestWrapper);
        }
        log.debug("body参数：{}", bodyParams);

        try {
            // 验签
            if (!checkSign(uri, queryParams, bodyParams, response)) {
                return;
            }

            // accessToken校验
            if (!PATTERN.matcher(uri).find()) {
                String token = request.getHeader(Const.TOKEN_HEADER);
                if (token == null) {
                    responseErrorCode(response, RestResult.gen(ErrorCode.AUTH_TOKEN_ILLEGAL));
                    return;
                }
                if (!tokenService.checkAccessToken(token)) {
                    responseErrorCode(response, RestResult.gen(ErrorCode.AUTH_TOKEN_EXPIRED));
                    return;
                }
            }

            log.debug("解密总耗时：{}ms", System.currentTimeMillis() - start);

            if (requestWrapper != null) {
                filterChain.doFilter(requestWrapper, servletResponse);
            } else {
                filterChain.doFilter(request, servletResponse);
            }

        } finally {
            log.debug("请求总耗时：{}ms", System.currentTimeMillis() - start);
        }
    }

    @Override
    public void destroy() {
    }

    /**
     * 获取uri
     *
     * @param request req
     * @return uri
     */
    private String getUri(HttpServletRequest request) {
        return request.getRequestURI();
    }

    /**
     * 获取查询参数
     *
     * @param request req
     * @return sorted map
     */
    private TreeMap<String, Object> getQueryParams(HttpServletRequest request) {
        TreeMap<String, Object> queryParams = new TreeMap<>();
        Enumeration paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = (String) paramNames.nextElement();
            String[] paramValues = request.getParameterValues(paramName);
            if (paramValues.length == 1) {
                String paramValue = paramValues[0];
                if (paramValue.length() != 0) {
                    queryParams.put(paramName, paramValue);
                }else{
                    queryParams.put(paramName, "");
                }
            }
        }
        return queryParams;
    }

    /**
     * 从post 或 put请求中获取请求封装类，以便@RequestBody还能获取inputStream
     * TODO 考虑文件上传处理
     *
     * @param request req
     * @return requestWrapper
     * @throws IOException e
     */
    private ServletRequestWrapper getWrapper(HttpServletRequest request)
            throws IOException {
        ServletRequestWrapper requestWrapper = null;
        if (HttpMethod.POST.matches(request.getMethod()) || HttpMethod.PUT.matches(request.getMethod())) {
            requestWrapper = new ServletRequestWrapper(request);
        }
        return requestWrapper;
    }

    /**
     * 从post 或 put请求中获取参数
     *
     * @param requestWrapper req
     * @return sorted map
     */
    private TreeMap<String, Object> getBodyParams(ServletRequestWrapper requestWrapper) {
        TreeMap<String, Object> bodyParams = new TreeMap<>();
        String json = requestWrapper.getBody();
        try {
            if (StringUtils.isBlank(json)) {
                return null;
            }
            Map<String, Object> map = JacksonUtil.json2map(json);
            bodyParams.putAll(map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bodyParams;
    }

    /**
     * 验签
     *
     * @param uri         uri
     * @param queryParams 查询参数
     * @param bodyParams  body参数
     * @param response    resp
     * @return flag
     */
    private boolean checkSign(String uri, TreeMap<String, Object> queryParams,
                              TreeMap<String, Object> bodyParams, HttpServletResponse response) {
        if (!queryParams.containsKey(PARAM_CLIENT_ID)
                || !queryParams.containsKey(PARAM_TIME)
                || !queryParams.containsKey(PARAM_SIGN)) {
            responseErrorCode(response, RestResult.gen(ErrorCode.AUTH_PARAMETER_ABSENT));
            return false;
        }
        String clientId = String.valueOf(queryParams.get(PARAM_CLIENT_ID));
        String clientSecret = getSecret(clientId);
        if (StringUtils.isEmpty(clientSecret)) {
            responseErrorCode(response, RestResult.gen(ErrorCode.AUTH_CLIENT_ILLEGAL));
            return false;
        }
        long time = Long.valueOf(String.valueOf(queryParams.get(PARAM_TIME)));
        if (System.currentTimeMillis() / 1000 - time > URL_TIMEOUT) {
            responseErrorCode(response, RestResult.gen(ErrorCode.AUTH_REQUEST_EXPIRED));
            return false;
        }
        String sign = String.valueOf(queryParams.remove(PARAM_SIGN));
        if (!CryptoUtil.matches(sign, uri, queryParams, clientSecret)) {
            responseErrorCode(response, RestResult.gen(ErrorCode.AUTH_FAILURE));
            return false;
        }

        return true;
    }

    /**
     * 获取客户端密码
     *
     * @param clientId 客户端id
     * @return 密码
     */
    private String getSecret(String clientId) {
        return KEY_MAP.get(clientId);
    }

    /**
     * filter执行优先于controller，@ControllerAdvice无法适配全局异常处理，此处单独处理
     *
     * @param response  http响应
     * @param errorCode 错误码
     */
    private void responseErrorCode(HttpServletResponse response, RestResult errorCode) {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setContentType(MediaType.APPLICATION_JSON_UTF8.toString());
        ServletOutputStream out;
        try {
            out = response.getOutputStream();
            String json = JacksonUtil.obj2json(errorCode);
            out.write(json.getBytes(StandardCharsets.UTF_8));
            out.flush();
            log.debug("返回错误码：{}", json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

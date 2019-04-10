package com.zom.sample.spring.aspect;


import com.zom.sample.core.annotation.Parameter;
import com.zom.sample.core.base.aspect.BaseAspect;
import com.zom.sample.core.exception.ParameterRequiredException;
import com.zom.sample.core.util.JacksonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 统一处理参数为空的请求
 */
@Slf4j
@Component
@Aspect
public class ParamAspect extends BaseAspect {

    @Pointcut("@annotation(com.zom.sample.core.annotation.Parameter)")
    public void parameter() {
    }

    @Before(value = "parameter()")
    public void doBefore(JoinPoint joinPoint) throws Throwable {
        // 获取自定义注解
        Parameter param = getAnnotation(joinPoint, Parameter.class);
        String[] requiredFields = param.value();
        //获取HttpServletRequest
        HttpServletRequest request = getHttpServletRequest();

        Map<String, Object> paramMap = null;

        if (request.getMethod().equals(HttpMethod.POST.name()) || request.getMethod().equals(HttpMethod.PUT.name())) {
            String body = getBodyString(request);
            paramMap = JacksonUtil.json2map(body);

        } else if (request.getMethod().equals(HttpMethod.DELETE.name()) || request.getMethod().equals(HttpMethod.GET.name())) {
            paramMap = new HashMap<>();
            Enumeration<String> enumeration = request.getParameterNames();
            while (enumeration.hasMoreElements()) {
                String parameter = enumeration.nextElement();
                paramMap.put(parameter, request.getParameter(parameter));
            }
        }
        checkParam(requiredFields, paramMap);
    }

    /**
     * 检查参数是否为空
     *
     * @param requiredFields
     * @param paramMap
     * @throws ParameterRequiredException
     */
    private void checkParam(String[] requiredFields, Map<String, Object> paramMap) throws ParameterRequiredException {
        if (requiredFields != null && requiredFields.length > 0) {
            if (paramMap == null || paramMap.size() <= 0) {
                throw new ParameterRequiredException();
            } else {
                for (String requiredField : requiredFields) {
                    if (!paramMap.containsKey(requiredField) || (paramMap.containsKey(requiredField) && StringUtils.isEmpty(paramMap.get(requiredField).toString()))) {
                        throw new ParameterRequiredException();
                    }
                }
            }
        }
    }

    /**
     * 获取请求Body
     *
     * @param request
     * @return
     */
    private String getBodyString(ServletRequest request) {
        StringBuilder sb = new StringBuilder();
        InputStream inputStream = null;
        BufferedReader reader = null;
        try {
            inputStream = request.getInputStream();
            reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
            String line = "";
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }

}

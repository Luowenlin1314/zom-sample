package com.zom.sample.spring.security;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * 获取request body
 */
public class ServletRequestWrapper extends HttpServletRequestWrapper {
    private byte[] buffer = "".getBytes();

    private String body;

    public ServletRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        InputStream is = request.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }
        if (builder.length() != 0) {
            body = builder.toString();
            this.buffer = body.getBytes(StandardCharsets.UTF_8);
        }
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return new BufferedServletInputStream(this.buffer);
    }

    public String getBody() {
        return body;
    }
}

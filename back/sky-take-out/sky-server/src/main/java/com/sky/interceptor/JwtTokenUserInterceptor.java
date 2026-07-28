package com.sky.interceptor;

import com.sky.constant.JwtClaimsConstant;
import com.sky.context.BaseContext;
import com.sky.properties.JwtProperties;
import com.sky.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 用户端 jwt令牌校验的拦截器
 */
@Component
@Slf4j
public class JwtTokenUserInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 校验jwt
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 判断当前拦截到的是Controller的方法还是其他资源
        if (!(handler instanceof HandlerMethod)) {
            // 当前拦截到的不是动态方法，直接放行
            return true;
        }

        // 1、从请求头中获取令牌（用户端使用 userTokenName）
        String token = request.getHeader(jwtProperties.getUserTokenName());
        log.info("用户端jwt校验，token: {}", token);

        // 2、校验令牌
        try {
            // 使用用户端的密钥解析token
            Claims claims = JwtUtil.parseJWT(jwtProperties.getUserSecretKey(), token);

            // 获取token令牌里包含的用户id信息（注意：用户端使用的是 USER_ID）
            Long userId = Long.valueOf(claims.get(JwtClaimsConstant.USER_ID).toString());

            // 存为线程局部变量
            BaseContext.setCurrentId(userId);
            log.info("当前用户id：{}", userId);

            // 3、通过，放行
            return true;
        } catch (Exception ex) {
            log.error("用户端jwt校验失败：{}", ex.getMessage());
            // 4、不通过，响应401状态码
            response.setStatus(401);
            return false;
        }
    }
}
package com.by.tsgl.config;

import com.by.tsgl.interceptor.TokenInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author leibaoyu
 * @version 1.0
 * @date 2021/11/12 19:13
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    //跨域访问请求
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("POST", "GET", "PUT", "OPTIONS", "DELETE")
                .maxAge(3600)
                .allowCredentials(true);
    }

    //添加拦截器 并放行静态资源
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
//        List<String> excludePath = new ArrayList<>();
//        //排除拦截，除了注册登录(此时还没token)，其他都拦截
//        excludePath.add("/register");  //登录
//        excludePath.add("/login");     //注册
//        excludePath.add("/alipay/**");
//
//        registry.addInterceptor(new TokenInterceptor())
//                .addPathPatterns("/**")
//                .excludePathPatterns(excludePath);
//        WebMvcConfigurer.super.addInterceptors(registry);

    }
}

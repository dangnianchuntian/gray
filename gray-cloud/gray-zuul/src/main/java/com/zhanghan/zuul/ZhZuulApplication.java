/*
 * Copyright (c) 2019. zhanghan_java@163.com All Rights Reserved.
 * 项目名称：灰度实战
 * 类名称：ZhZuulApplication.java
 * 创建人：张晗
 * 联系方式：zhanghan_java@163.com
 * 开源地址: https://github.com/dangnianchuntian/springboot
 * 博客地址: https://blog.csdn.net/zhanghan18333611647
 */

package com.zhanghan.zuul;

import com.zhanghan.zuul.filter.GrayRedirectZuulFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableEurekaClient
@EnableZuulProxy
public class ZhZuulApplication {

    public static void main(String[] args) {

        SpringApplication.run(ZhZuulApplication.class, args);
    }


    /**
     * 重定向方式的Filter---性能低
     */
    @Bean
    public GrayRedirectZuulFilter grayRedirectZuulFilter() {
        return new GrayRedirectZuulFilter();
    }

    /**
     * 直接跳转方式的Filter---性能高建议采用
     *
     * @return
     */
//    @Bean
//    public GrayDirectZuulFilter grayDirectZuulFilter() {
//        return new GrayDirectZuulFilter();
//    }


}

/*
 * Copyright (c) 2019. zhanghan_java@163.com All Rights Reserved.
 * 项目名称：灰度实战
 * 类名称：SmsController.java
 * 创建人：张晗
 * 联系方式：zhanghan_java@163.com
 * 开源地址: https://github.com/dangnianchuntian/springboot
 * 博客地址: https://blog.csdn.net/zhanghan18333611647
 */

package com.zhanghan.service.sms.controller;


import com.zhanghan.service.sms.controller.request.SmsRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class SmsController {

    /**
     * 在不同的服务器部署时通过启动命令来设定该变量，目的是区分不同服务器
     */
    @Value("${sid:sms}")
    private String sid;

    @RequestMapping(path = "/sms/send", method = RequestMethod.POST)
    public Object rest(@RequestBody SmsRequest lombokRequest) {

        Map<String, String> result = new HashMap<>();
        result.put("service", "this is:" + sid);
        result.put("companyNo", lombokRequest.getCompanyNo());
        return result;
    }


}
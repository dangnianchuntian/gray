/*
 * Copyright (c) 2019. zhanghan_java@163.com All Rights Reserved.
 * 项目名称：灰度实战
 * 类名称：GrayZuulFilter.java
 * 创建人：张晗
 * 联系方式：zhanghan_java@163.com
 * 开源地址: https://github.com/dangnianchuntian/springboot
 * 博客地址: https://blog.csdn.net/zhanghan18333611647
 */

package com.zhanghan.zuul.filter;


import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.zhanghan.zuul.bean.GrayBean;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.util.StreamUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.nio.charset.Charset;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;

public class GrayZuulFilter extends ZuulFilter {


    @Autowired
    private GrayBean grayBean;

    @Override
    public String filterType() {
        return PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return grayBean.isEnabled();
    }

    @Override
    public Object run() {

        if (shouldBeRedirected()) {
            RequestContext.getCurrentContext().setSendZuulResponse(false);
            String redirectUrl = generateRedirectUrl(grayBean.getSuffix());

            sendRedirect(redirectUrl);
        }

        return null;
    }

    /**
     * 是否为灰度请求 依据条件同时满足1和2: 1.请求在灰度列表中 2.companyNo为配置的灰度companyNo
     *
     * @return
     */
    private boolean shouldBeRedirected() {

        HttpServletRequest request = RequestContext.getCurrentContext().getRequest();


        if (grayBean.isEnabled()) {

            if (grayBean.getGraylist().isEmpty()) {
                return false;
            }

            String requestUrl = request.getRequestURI();

            if (grayBean.getGraylist().contains(requestUrl.split("/")[1])) {

                Object companyNo = getParment(request, "companyNo");

                if (null != companyNo && companyNo.toString().equals(grayBean.getCompanyNo())) {
                    return true;
                }

            }

        }
        return false;
    }

    /**
     * 获取post请求的body
     *
     * @param request
     * @param parmentKey
     * @return
     */
    private static Object getParment(HttpServletRequest request, String parmentKey) {

        try {

            String charset = request.getCharacterEncoding();

            InputStream inputStream = request.getInputStream();

            String body = StreamUtils.copyToString(inputStream, Charset.forName(charset));

            if (StringUtils.isEmpty(body)) {
                return null;
            }

            JSONObject json = JSONObject.fromObject(body);

            return json.get(parmentKey);

        } catch (Exception e) {
            return null;
        }

    }

    /**
     * 获取重定向URL
     *
     * @param graySuffix 灰度后缀
     * @return
     */
    private static String generateRedirectUrl(String graySuffix) {

        HttpServletRequest request = RequestContext.getCurrentContext().getRequest();
        String queryParams = request.getQueryString();
        Object originalRequestPath = request.getRequestURI();

        String[] modifiedRequestPathArr = (originalRequestPath.toString().split("/", 3));
        modifiedRequestPathArr[1] = modifiedRequestPathArr[1] + graySuffix;

        StringBuilder stringBuilder = new StringBuilder();
        for (String str : modifiedRequestPathArr) {
            if (StringUtils.isNotEmpty(str)) {
                stringBuilder.append("/");
                stringBuilder.append(str);
            }
        }

        return stringBuilder.toString() + (queryParams == null ? "" : ("?" + queryParams));
    }

    /**
     * 进行重定向
     *
     * @param redirectUrl 重定向URL
     */
    private static void sendRedirect(String redirectUrl) {

        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletResponse response = ctx.getResponse();
        HttpServletRequest request = ctx.getRequest();

        try {
            if (request.getMethod().toUpperCase().equals("POST")) {
                response.setStatus(HttpServletResponse.SC_TEMPORARY_REDIRECT);
            } else {
                response.setStatus(HttpStatus.MOVED_PERMANENTLY.value());
            }
            response.setHeader(HttpHeaders.LOCATION, redirectUrl);
            response.flushBuffer();

        } catch (Exception ex) {
        }

    }


}
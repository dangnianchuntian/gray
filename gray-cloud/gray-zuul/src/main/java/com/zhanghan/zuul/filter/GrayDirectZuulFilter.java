/*
 * Copyright (c) 2019. zhanghan_java@163.com All Rights Reserved.
 * 项目名称：灰度实战
 * 类名称：GrayDirectZuulFilter.java
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
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.util.StreamUtils;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.nio.charset.Charset;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.*;

public class GrayDirectZuulFilter extends ZuulFilter {


    @Autowired
    private GrayBean grayBean;

    @Autowired
    private RouteLocator routeLocator;

    @Autowired
    private UrlPathHelper urlPathHelper;

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

        Boolean isGray = shouldBeRedirected();

        if (isGray) {

            String grayServiceId = getServiceId() + grayBean.getSuffix();

            String url = RequestContext.getCurrentContext().getRequest().getRequestURI();

            url = replaceUrl(url, grayServiceId);

            try {
                RequestContext.getCurrentContext().put(REQUEST_URI_KEY, url);
                RequestContext.getCurrentContext().put(SERVICE_ID_KEY, grayServiceId);
            } catch (Exception e) {
                return null;
            }


        }

        return null;
    }

    /**
     * 是否为灰度请求  依据条件同时满足1和2为灰度请求: 1.请求在灰度列表中 2.companyNo为配置的灰度companyNo
     *
     * @return true灰度 false不灰度
     */
    private Boolean shouldBeRedirected() {

        HttpServletRequest request = RequestContext.getCurrentContext().getRequest();

        if (grayBean.getGraylist().isEmpty()) {
            return false;
        }

        String serviceId = getServiceId();

        if (!grayBean.getGraylist().contains(serviceId)) {
            return false;
        }

        Object companyNo = getParment(request, "companyNo");

        if (null != companyNo && companyNo.toString().equals(grayBean.getCompanyNo())) {
            return true;
        }

        return false;
    }

    /**
     * 转换为灰度url
     *
     * @param url           原url
     * @param grayServiceId 灰度服务id
     * @return
     */
    private String replaceUrl(String url, String grayServiceId) {
        grayServiceId = grayServiceId.replace(grayBean.getSuffix(), "");
        return url.replace("/" + grayServiceId, "");
    }

    /**
     * 获取本次请求的serviceId
     *
     * @return
     */
    private String getServiceId() {

        HttpServletRequest request = RequestContext.getCurrentContext().getRequest();

        Object object = RequestContext.getCurrentContext().get(SERVICE_ID_KEY);

        String serviceId = object == null ? null : object.toString();

        if (StringUtils.isEmpty(serviceId)) {
            String requestURI = urlPathHelper.getPathWithinApplication(request);
            serviceId = routeLocator.getMatchingRoute(requestURI).getLocation();
        }

        return serviceId;
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

}
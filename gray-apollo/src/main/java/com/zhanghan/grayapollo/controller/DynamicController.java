package com.zhanghan.grayapollo.controller;

import com.zhanghan.grayapollo.util.wrapper.WrapMapper;
import com.zhanghan.grayapollo.util.wrapper.Wrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@Api(value = "演示Apollo控制器",tags = {"演示Apollo控制器"})
public class DynamicController {

    @Value("${zh.int}")
    private Integer zhInt;

    @Autowired
    private RedisTemplate<String, String> strRedisTemplate;

    @ApiOperation(value="测试通过@value注入数据",tags = {"演示Apollo控制器"})
    @RequestMapping(value = "/test/value", method = RequestMethod.POST)
    public Wrapper testValue() {
        Map<String, Object> map = new HashMap();
        map.put("zhTest", zhInt);
        return WrapMapper.ok(map);
    }

    @ApiOperation(value="演示向redis中放数据",tags = {"演示Apollo控制器"})
    @RequestMapping(value = "/post/rdb", method = RequestMethod.POST)
    public Wrapper postDB() {
        strRedisTemplate.opsForValue().set("zh-test",zhInt.toString());
        Map<String, Object> map = new HashMap();
        map.put("putZhTest", zhInt.toString());
        return WrapMapper.ok(map);
    }

    @ApiOperation(value="演示从redis中获取数据",tags = {"演示Apollo控制器"})
    @RequestMapping(value = "/get/rdb", method = RequestMethod.POST)
    public Wrapper getDB() {
        String getZhInt = strRedisTemplate.opsForValue().get("zh-test");
        Map<String, Object> map = new HashMap();
        map.put("getZhTest", getZhInt);
        return WrapMapper.ok(map);
    }

}

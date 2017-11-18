package com.wkx.provider.controller;

import com.alibaba.fastjson.JSONObject;
import com.wkx.provider.config.AppConfigurer;
import com.wkx.provider.config.ZkUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class TestController {

    @Autowired
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    @RequestMapping("/hello")
    public String myFirstMethod(){
        return "hello world";
    }

    @RequestMapping("/handler")
    public String urlMap(){
        Map<RequestMappingInfo,HandlerMethod> maps=requestMappingHandlerMapping.getHandlerMethods();
        List<Map<String, Object>> urlList = new ArrayList<>();
        maps.forEach((requestMappingInfo,handlerMethod)->{
            PatternsRequestCondition p=requestMappingInfo.getPatternsCondition();
            Map<String,Object> map=new HashMap<>();
            for (String url : p.getPatterns()) {
                map.put("url", url);
            }
            map.put("className",handlerMethod.getMethod().getDeclaringClass().getName());
            map.put("method",handlerMethod.getMethod().getName());
            RequestMethodsRequestCondition methodsCondition=requestMappingInfo.getMethodsCondition();
            String type = methodsCondition.toString();
            if (type != null && type.startsWith("[") && type.endsWith("]")) {
                //type = type.substring(1, type.length() - 1);
                map.put("type", type); // 方法名
            }
            urlList.add(map);
        });
        return JSONObject.toJSONString(urlList);
    }

    @RequestMapping("/zk")
    public String getRegisterInfo(){
        String applicationName= AppConfigurer.getProperty("spring.application.name");
        Object data=ZkUtils.getData("/"+applicationName);
        return JSONObject.toJSONString(data);
    }

    @RequestMapping("/all")
    public String getAll(){
        String applicationName= AppConfigurer.getProperty("spring.application.name");
        Object data=ZkUtils.getChildData("/"+applicationName);
        return JSONObject.toJSONString(data);
    }

    @RequestMapping("/basePackages")
    public String getBase(){
        return AppConfigurer.getProperty("basePackages");
    }

}

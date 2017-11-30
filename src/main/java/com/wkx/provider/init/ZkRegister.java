package com.wkx.provider.init;

import com.alibaba.fastjson.JSONObject;
import com.wkx.provider.util.BeanFactory;
import com.wkx.provider.env.ServiceEnv;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.net.InetAddress;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
public class ZkRegister implements ApplicationRunner {

    private RequestMappingHandlerMapping requestMappingHandlerMapping= BeanFactory.getBean(RequestMappingHandlerMapping.class);

    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
        String zkServers= ServiceEnv.getProperty("zookeeper.servers");
        if(StringUtils.isEmpty(zkServers)) return;
        String applicationPath="/"+ ServiceEnv.getProperty("spring.application.name");
        String localPort= ServiceEnv.getProperty("server.port");
        if(StringUtils.isEmpty(localPort)) localPort= ServiceEnv.getProperty("local.server.port");
        ZkClient zkClient=new ZkClient(zkServers);
        if(!zkClient.exists(applicationPath)){
            zkClient.createEphemeral(applicationPath);
        }
        String ip= InetAddress.getLocalHost().getHostAddress();
        String servicePath=ip+":"+localPort;
        Set<String> urlSet=getServiceUrl();
        zkClient.createEphemeral(applicationPath+"/"+servicePath, JSONObject.toJSONString(urlSet));
        BeanFactory.registerBean("zkClient",zkClient);
    }

    private Set<String> getServiceUrl(){
        Map<RequestMappingInfo,HandlerMethod> maps=requestMappingHandlerMapping.getHandlerMethods();
        Set<String> urlSet=new HashSet<>();
        maps.forEach((requestMappingInfo,handlerMethod)->{
            PatternsRequestCondition p=requestMappingInfo.getPatternsCondition();
            urlSet.addAll(p.getPatterns());
        });
        return urlSet;
    }



}
package com.wkx.provider.init;

import com.alibaba.fastjson.JSONObject;
import com.wkx.provider.annotation.RpcService;
import com.wkx.provider.env.ServiceEnv;
import com.wkx.provider.util.BeanFactory;
import org.I0Itec.zkclient.ZkClient;
import org.reflections.Reflections;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.ParamsRequestCondition;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.ProducesRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.net.InetAddress;
import java.util.Arrays;
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
        RegisterService();
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

    private void RegisterService(){
        Reflections reflections=new Reflections("");
        Set<Class<?>> classSet=reflections.getTypesAnnotatedWith(RpcService.class);
        classSet.stream().filter(cs->!cs.isInterface()).forEach(cs->{
            RpcService rpcService=cs.getAnnotation(RpcService.class);
            String serviceName=getBeanName(cs);
            String ServiceUrl=StringUtils.isEmpty(rpcService.url())?"/"+serviceName:rpcService.url();
            Arrays.stream(cs.getDeclaredMethods()).forEach(method -> {
                String methodUrl=ServiceUrl+"/"+method.getName();
                PatternsRequestCondition requestCondition=new PatternsRequestCondition(methodUrl);
                ProducesRequestCondition producesCondition=new ProducesRequestCondition("text/plain","application/json");
                ParamsRequestCondition paramsRequestCondition=new ParamsRequestCondition("text/plain","application/json");
                RequestMappingInfo mappingInfo=new RequestMappingInfo(
                        serviceName,requestCondition,null,null,null,null,
                        producesCondition,null);
                requestMappingHandlerMapping.registerMapping(mappingInfo,serviceName,method);
            });
        });
    }

    private static String getBeanName(Class c){
        String names[]=c.getName().split("\\.");
        String className=names[names.length-1];
        return Character.toLowerCase(className.charAt(0))+className.substring(1);
    }



}

package com.wkx.provider.init;

import com.wkx.provider.resolver.RpcParamResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import java.util.ArrayList;
import java.util.List;

@Component
public class ParamResolverRegister implements ApplicationRunner{

    @Autowired
    private RequestMappingHandlerAdapter adapter;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<HandlerMethodArgumentResolver> resolvers=adapter.getArgumentResolvers();
        List<HandlerMethodArgumentResolver> newResolvers=new ArrayList<>();
        newResolvers.add(new RpcParamResolver());
        newResolvers.addAll(resolvers);
        adapter.setArgumentResolvers(newResolvers);
    }
}

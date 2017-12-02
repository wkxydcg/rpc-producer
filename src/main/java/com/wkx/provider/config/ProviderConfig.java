package com.wkx.provider.config;

import com.wkx.provider.init.ParamResolverRegister;
import com.wkx.provider.init.ZkRegister;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProviderConfig {

    @Bean
    public ZkRegister initRegister(){
        return new ZkRegister();
    }

    @Bean
    public ParamResolverRegister initParamResolver(){
        return new ParamResolverRegister();
    }
}

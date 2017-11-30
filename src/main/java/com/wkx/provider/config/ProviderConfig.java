package com.wkx.provider.config;

import com.wkx.provider.init.ZkRegister;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProviderConfig {

    @Bean
    public ZkRegister getRegister(){
        return new ZkRegister();
    }

}

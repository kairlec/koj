package com.baidu.fsg.uid.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;


@Configuration
@ImportResource(locations = {"classpath:config/uid-spring.xml"})
public class UIDConfig {
}

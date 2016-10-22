package com.baren.config;

import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import com.baren.Application;

@Configuration
@PropertySource("classpath:persistence.properties")
@PropertySource("classpath:application.properties")
@ImportResource("classpath:spring/local-context.xml")
@ComponentScan(basePackageClasses = Application.class, basePackages="com.baren.yak")
@EnableAspectJAutoProxy(proxyTargetClass=true)
class ApplicationConfig {

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

}
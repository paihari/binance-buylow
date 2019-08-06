package com.gundi.binance.buylow;

import com.gundi.binance.buylow.service.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BuylowApplication {


    public static void main(String[] args) throws Exception{
        ConfigurableApplicationContext context=  SpringApplication.run(BuylowApplication.class, args);
//        String[] beanNames = context.getBeanDefinitionNames();
//        BuyLowPrimeService buyLowPrimeService = (BuyLowPrimeService)context.getBean("buyLowPrimeService");
//        buyLowPrimeService.invoke();

    }


    @Bean(name = "helloServiceFactory")
    public HelloServiceFactory helloFactory() {
        return new HelloServiceFactory();
    }

    @Bean(name = "helloServicePython")
    public HelloService helloServicePython() throws Exception {
        return helloFactory().getObject();
    }


    }

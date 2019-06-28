package com.gundi.binance.buylow;

import com.gundi.binance.buylow.service.BuyLowPrimeService;
import com.gundi.binance.buylow.service.CalculationService;
import com.gundi.binance.buylow.service.BuyService;
import com.gundi.binance.buylow.service.SellService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BuylowApplication {


    public static void main(String[] args) throws Exception{
        ConfigurableApplicationContext context=  SpringApplication.run(BuylowApplication.class, args);
        String[] beanNames = context.getBeanDefinitionNames();
        BuyLowPrimeService buyLowPrimeService = (BuyLowPrimeService)context.getBean("buyLowPrimeService");
        buyLowPrimeService.invoke();

    }

}

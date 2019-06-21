package com.gundi.binance.buylow;

import com.gundi.binance.buylow.service.TradingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BuylowApplication {



    public static void main(String[] args) {

        ConfigurableApplicationContext context=  SpringApplication.run(BuylowApplication.class, args);
        TradingService tradingService = (TradingService)context.getBean("tradingService");
        tradingService.doIt();

    }

}

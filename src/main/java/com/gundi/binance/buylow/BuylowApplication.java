package com.gundi.binance.buylow;

import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.amazonaws.services.secretsmanager.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gundi.binance.buylow.config.APIKeyAndSecret;
import com.gundi.binance.buylow.service.TradingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Base64;

@SpringBootApplication
@EnableScheduling
public class BuylowApplication {



    public static void main(String[] args) throws Exception{
        ConfigurableApplicationContext context=  SpringApplication.run(BuylowApplication.class, args);
        TradingService tradingService = (TradingService)context.getBean("tradingService");
        tradingService.doIt();
    }

}

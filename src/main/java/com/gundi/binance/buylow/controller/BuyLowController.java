package com.gundi.binance.buylow.controller;

import com.gundi.binance.buylow.config.APIKeyAndSecret;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BuyLowController {

    @Value("${spring.profiles.active}")
    private String env;

    @Value("${msg}")
    private String msg;

    @Autowired
    private APIKeyAndSecret apiKeyAndSecret;




    @RequestMapping("/")
    public String home() {

        return  msg + " from " + env + " API " + apiKeyAndSecret.getApiKey()  + " Secret " + apiKeyAndSecret.getApiSecret();
    }

}

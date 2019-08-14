package com.gundi.binance.buylow.runner;

import com.gundi.binance.buylow.service.BuyLowPrimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CommandLineAppStartupRunner implements CommandLineRunner {

    @Autowired
    BuyLowPrimeService buyLowPrimeService;

    @Override
    public void run(String... args) throws Exception {
        //buyLowPrimeService.invoke();

    }
}


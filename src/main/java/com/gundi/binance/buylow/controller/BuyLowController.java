package com.gundi.binance.buylow.controller;

import com.gundi.binance.buylow.service.CalculationService;
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


    private CalculationService calculationService;

    @Autowired
    public BuyLowController(CalculationService calculationService) {
        this.calculationService = calculationService;
    }


    @RequestMapping("/")
    public String home() {
        calculationService.invoke("XRPUSDT");
        return  msg + " from " + env
                + " Number of Trades " + calculationService.getNoOfTrades()
                + " Average Price " + calculationService.getAveragePrice()
                + " Total Qty " + calculationService.getTotalQty()
                + " Version 4";
    }
}

package com.gundi.binance.buylow.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BuyLowController {

    @RequestMapping("/")
    public String home() {
        return "Hello World";
    }

}

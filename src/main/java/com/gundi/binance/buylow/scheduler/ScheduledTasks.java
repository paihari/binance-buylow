package com.gundi.binance.buylow.scheduler;

import com.gundi.binance.buylow.config.CryptoPair;
import com.gundi.binance.buylow.service.AnalyticsService;
import com.gundi.binance.buylow.service.BuyService;
import com.gundi.binance.buylow.service.SellService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {

    Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);

    AnalyticsService analyticsService;

    BuyService buyService;

    SellService sellService;

    @Autowired
    public ScheduledTasks(AnalyticsService analyticsService,
                          BuyService buyService,
                          SellService sellService) {
        this.analyticsService = analyticsService;
        this.buyService = buyService;
        this.sellService = sellService;
    }



    @Scheduled(cron = "${cron.expression}")
    //@Scheduled(fixedRate = 5000)
    public void doIt() {

        for(CryptoPair cryptoPair : CryptoPair.values()) {
            analyticsService.invoke(cryptoPair.getPair());
            Boolean idealSituationForBuy = analyticsService.getIsIdealSituationForBuy(cryptoPair.getPair());
            logger.info("Ideal Situation for Buy " + idealSituationForBuy + " For " + cryptoPair.getPair());
            if(idealSituationForBuy) {
                buyService.tradeIt(cryptoPair.getPair());
            }
            Boolean idealSituationForSell = analyticsService.getIsIdealSituationForSell(cryptoPair.getPair());
            logger.info("Ideal Situation for Sell " + idealSituationForSell + " For " + cryptoPair.getPair());
            if(idealSituationForSell) {
                sellService.tradeIt(cryptoPair.getPair());
            }

        }


    }

}

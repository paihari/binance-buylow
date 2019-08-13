package com.gundi.binance.buylow.scheduler;

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



    //@Scheduled(cron = "${cron.expression}")
    @Scheduled(fixedRate = 5000)
    public void doIt() {
        analyticsService.invoke("BTCUSDT");
        Boolean idealSituationForBuy = analyticsService.getIsIdealSituationForBuy("BTCUSDT");
        logger.info("Ideal Situation for Buy " + idealSituationForBuy);
        if(idealSituationForBuy) {
            buyService.tradeIt("BTCUSDT");
        }
        Boolean idealSituationForSell = analyticsService.getIsIdealSituationForSell("BTCUSDT");
        logger.info("Ideal Situation for Sell " + idealSituationForSell);
        if(idealSituationForSell) {
            sellService.tradeIt("BTCUSDT");
        }


    }

}

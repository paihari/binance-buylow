package com.gundi.binance.buylow.scheduler;

import com.gundi.binance.buylow.service.AnalyticsService;
import com.gundi.binance.buylow.service.BuyService;
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

    @Autowired
    public ScheduledTasks(AnalyticsService analyticsService,
                          BuyService buyService) {
        this.analyticsService = analyticsService;
        this.buyService = buyService;
    }



    @Scheduled(cron = "${cron.expression}")
//@Scheduled(fixedRate = 30000)
    public void doIt() {
        analyticsService.invoke("BTCUSDT");
        Boolean idealSituationForBuy = analyticsService.getIsIdealSituationForBuy("BTCUSDT");
        logger.info("Ideal Situation for Buy " + idealSituationForBuy);
        if(idealSituationForBuy) {
            buyService.tradeIt("BTCUSDT");
        }


    }

}

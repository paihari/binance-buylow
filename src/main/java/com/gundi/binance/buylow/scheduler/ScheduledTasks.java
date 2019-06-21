package com.gundi.binance.buylow.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {

    //@Scheduled(fixedRate = 5000)
    public void doIt() {
        System.out.println("Here you Run");
    }

}

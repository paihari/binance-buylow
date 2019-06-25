package com.gundi.binance.buylow.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

//@Component
public class ScheduledTasks {

    private LocalDateTime localBaseDateTime = LocalDateTime.now();

    //@Scheduled(fixedRate = 60000)
    public void doIt() {

        LocalDateTime localDateTime = LocalDateTime.now();

        System.out.println("Time " + localBaseDateTime) ;
        System.out.println("Date " + localDateTime);
    }

}

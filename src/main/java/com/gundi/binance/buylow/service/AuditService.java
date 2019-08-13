package com.gundi.binance.buylow.service;

import com.gundi.binance.buylow.model.TradeLog;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Scope("singleton")
public class AuditService {

    private List<TradeLog> tradeLogs = new ArrayList<TradeLog>();
    private Double averageVolumeOfRedCandles = new Double(0);
    private Double averageDropOfRedCandles = new Double(0);

    public AuditService() {

    }

    public List<TradeLog> getTradeLogs() {

        return tradeLogs;
    }

    public void addTradeLogs(TradeLog tradeLog) {

        this.tradeLogs.add(tradeLog);
    }

    public Double getAverageVolumeOfRedCandles() {
        return averageVolumeOfRedCandles;
    }

    public void setAverageVolumeOfRedCandles(Double averageVolumeOfRedCandles) {
        this.averageVolumeOfRedCandles = averageVolumeOfRedCandles;
    }

    public Double getAverageDropOfRedCandles() {
        return averageDropOfRedCandles;
    }

    public void setAverageDropOfRedCandles(Double averageDropOfRedCandles) {
        this.averageDropOfRedCandles = averageDropOfRedCandles;
    }


}

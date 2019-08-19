package com.gundi.binance.buylow.service;

import com.gundi.binance.buylow.model.TradeLog;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Scope("singleton")
public class AuditService {


    private Map<String, List<TradeLog>> mapOfTradeLogsPerSymbol = new HashMap<String, List<TradeLog>>();

    private Map<String, Double> averageVolumeOfRedCandlesPerSymbol = new HashMap<String, Double>();


    private Map<String, Double> averageDropOfRedCandlesPerSymbol = new HashMap<String, Double>();


    private Map<String, Double> averageVolumeOfGreenCandlesPerSymbol = new HashMap<String, Double>();


    private Map<String, Double> averageRaiseOfGreenCandlesPerSymbol = new HashMap<String, Double>();


    public AuditService() {

    }


    public  Double getAverageVolumeOfRedCandlesPerSymbol(String symbol) {
        return averageVolumeOfRedCandlesPerSymbol.get(symbol);
    }

    public void setAverageVolumeOfRedCandlesPerSymbol(String symbol,
                                                      Double averageVolumeOfRedCandles) {

        averageVolumeOfRedCandlesPerSymbol.put(symbol, averageVolumeOfRedCandles);
    }

    public Double getAverageDropOfRedCandlesPerSymbol(String symbol) {
        return averageDropOfRedCandlesPerSymbol.get(symbol);
    }

    public void setAverageDropOfRedCandlesPerSymbol(String symbol, Double averageDropOfRedCandles) {
        averageDropOfRedCandlesPerSymbol.put(symbol, averageDropOfRedCandles);
    }

    public Double getAverageVolumeOfGreenCandlesPerSymbol(String symbol) {
        return averageVolumeOfGreenCandlesPerSymbol.get(symbol);
    }

    public void setAverageVolumeOfGreenCandlesPerSymbol(String symbol, Double averageVolumeOfGreenCandles) {
        this.averageVolumeOfGreenCandlesPerSymbol.put(symbol, averageVolumeOfGreenCandles);
    }

    public Double getAverageRaiseOfGreenCandlesPerSymbol(String symbol) {
        return averageRaiseOfGreenCandlesPerSymbol.get(symbol);
    }

    public void setAverageRaiseOfGreenCandlesPerSymbol(String symbol, Double averageRaiseOfGreenCandles) {
        this.averageRaiseOfGreenCandlesPerSymbol.put(symbol, averageRaiseOfGreenCandles);
    }

    public List<TradeLog> getMapOfTradeLogsPerSymbol(String symbol) {
        return mapOfTradeLogsPerSymbol.get(symbol);
    }

    public void setMapOfTradeLogsPerSymbol(String symbol, TradeLog tradeLog) {

        if(this.mapOfTradeLogsPerSymbol.get(symbol) == null ||
                this.mapOfTradeLogsPerSymbol.get(symbol).isEmpty()) {
            this.mapOfTradeLogsPerSymbol.put(symbol, new ArrayList<>());
            this.mapOfTradeLogsPerSymbol.put(symbol, Arrays.asList(tradeLog));
        }
        else {
            this.mapOfTradeLogsPerSymbol.get(symbol).add(tradeLog);
        }

    }
}

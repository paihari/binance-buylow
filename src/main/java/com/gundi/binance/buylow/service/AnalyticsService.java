package com.gundi.binance.buylow.service;

import com.binance.api.client.domain.event.AggTradeEvent;
import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.domain.market.CandlestickInterval;
import com.binance.api.client.domain.market.TickerStatistics;
import com.gundi.binance.buylow.api.APIClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class AnalyticsService {

    Logger logger = LoggerFactory.getLogger(AnalyticsService.class);

    private APIClient apiClient;

    private AuditService auditService;

    private Map<String, Boolean> isIdealSituationForBuy = new HashMap<String, Boolean>();

    @Autowired
    public AnalyticsService(APIClient apiClient,
                            AuditService auditService) {

        this.apiClient = apiClient;
        this.auditService = auditService;
    }

    public void invoke(String symbol) {

        List<Candlestick> candlesticks = apiClient.getPastFiveDaysCandlestickBars(symbol,
                CandlestickInterval.FIFTEEN_MINUTES);
        Collections.reverse(candlesticks);
        // Remove the first unfilled Current Candle
        candlesticks.remove(0);

        // Get the Last Candle Stick In Focus
        Candlestick lastCandlestick = candlesticks.get(0);


        // Get filtered 20 Candle sticks with Top Drops
        List<Candlestick> filteredCandleList = candlesticks.stream().filter(candlestick -> {
            return Double.parseDouble(candlestick.getClose()) < Double.parseDouble(candlestick.getOpen());
        }).sorted(new Comparator<Candlestick>() {
            @Override
            public int compare(Candlestick o1, Candlestick o2) {
                Double diff1 = Double.parseDouble(o1.getOpen()) -
                        Double.parseDouble(o1.getClose());
                Double diff2 = Double.parseDouble(o2.getOpen()) -
                        Double.parseDouble(o2.getClose());

                return diff2.compareTo(diff1);
            }
        }).limit(20).collect(Collectors.toList());




        Double averageVolumeOfRedCandles =  filteredCandleList.stream().
                mapToDouble(s -> new Double(s.getVolume())).average().getAsDouble();

        Double averageDropPriceOfRedCandles = filteredCandleList.stream().
                mapToDouble(s -> {
                    Double openPrice = Double.parseDouble(s.getOpen());
                    Double closePrice = Double.parseDouble(s.getClose());
                    return openPrice - closePrice;
                }).average().getAsDouble();

        auditService.setAverageDropOfRedCandles(averageDropPriceOfRedCandles);
        auditService.setAverageVolumeOfRedCandles(averageVolumeOfRedCandles);

        isIdealSituationForBuy.put(symbol, false);

        // Ideal situation to buy when lastCandle Stick is Red and
        // Volume of the Last Candle Stick exceeds the average
        // Drop of the last Candle Stick exceeds the average drop
        if(Double.parseDouble(lastCandlestick.getOpen()) > Double.parseDouble(lastCandlestick.getClose())
                &&
                Double.parseDouble(lastCandlestick.getVolume()) > averageVolumeOfRedCandles
                &&
                (Double.parseDouble(lastCandlestick.getOpen()) - Double.parseDouble(lastCandlestick.getClose()) > averageDropPriceOfRedCandles)
        ) {
            isIdealSituationForBuy.replace(symbol, true);
        }



    }

    public Boolean getIsIdealSituationForBuy(String symbol) {
        return isIdealSituationForBuy.get(symbol);
    }
}

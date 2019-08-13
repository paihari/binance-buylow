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
import java.util.stream.Stream;

@Service
public class AnalyticsService {

    Logger logger = LoggerFactory.getLogger(AnalyticsService.class);

    private APIClient apiClient;

    private Map<String, Boolean> isIdealSituationForBuy = new HashMap<String, Boolean>();

    @Autowired
    public AnalyticsService(APIClient apiClient) {

        this.apiClient = apiClient;
    }

    public void invoke(String symbol) {

        List<Candlestick> candlesticks = apiClient.getPastFiveDaysCandlestickBars(symbol,
                CandlestickInterval.FIFTEEN_MINUTES);

        Collections.reverse(candlesticks);
        candlesticks.remove(0);

        Candlestick lastCandlestick = candlesticks.get(0);

        logger.info("The Last Candle " + candlesticks.get(0).getVolume());

        logger.info("The Size of Candle Stick Bars " + candlesticks.size());


        Double averageVolumeOfRedCandles = candlesticks.stream().filter(candlestick -> {
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
        }).limit(10).mapToDouble(s -> new Double(s.getVolume())).average().getAsDouble();

        isIdealSituationForBuy.put(symbol, false);
        if(Double.parseDouble(lastCandlestick.getOpen()) > Double.parseDouble(lastCandlestick.getClose()) &&
        Double.parseDouble(lastCandlestick.getVolume()) > averageVolumeOfRedCandles) {
            isIdealSituationForBuy.replace(symbol, true);
        }



    }

    public Boolean getIsIdealSituationForBuy(String symbol) {
        return isIdealSituationForBuy.get(symbol);
    }
}

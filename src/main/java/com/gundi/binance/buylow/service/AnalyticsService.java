package com.gundi.binance.buylow.service;

import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.domain.market.CandlestickInterval;
import com.gundi.binance.buylow.api.APIClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    Logger logger = LoggerFactory.getLogger(AnalyticsService.class);

    private APIClient apiClient;

    private AuditService auditService;

    private Map<String, Boolean> isIdealSituationForBuy = new HashMap<String, Boolean>();

    private Map<String, Boolean> isIdealSituationForSell = new HashMap<String, Boolean>();


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

        invoke(symbol, candlesticks);
    }

    private void invoke(String symbol, List<Candlestick> candlesticks) {

        // Get the Last Candle Stick In Focus
        Candlestick lastCandlestick = candlesticks.get(0);
        // Get filtered 20 Candle sticks with Top Drops
        List<Candlestick> filteredGreenCandleList = candlesticks.stream().filter(candlestick -> {
            return Double.parseDouble(candlestick.getClose()) > Double.parseDouble(candlestick.getOpen());
        }).sorted(new SellComparator<Candlestick>()).limit(20).collect(Collectors.toList());

        Double averageVolumeOfGreenCandles =  filteredGreenCandleList.stream().
                mapToDouble(s -> new Double(s.getVolume())).average().getAsDouble();

        Double averageRaisePriceOfGreenCandles = filteredGreenCandleList.stream().
                mapToDouble(s -> {
                    Double openPrice = Double.parseDouble(s.getOpen());
                    Double closePrice = Double.parseDouble(s.getClose());
                    return closePrice - openPrice;
                }).average().getAsDouble();

        auditService.setAverageRaiseOfGreenCandlesPerSymbol(symbol, averageRaisePriceOfGreenCandles);
        auditService.setAverageVolumeOfGreenCandlesPerSymbol(symbol, averageVolumeOfGreenCandles);
        logger.trace("AverageRaiseOfGreenCandles " + averageRaisePriceOfGreenCandles + " Symbol " + symbol);
        logger.trace("AverageVolumeOfGreenCandles " + averageVolumeOfGreenCandles + " Symbol " + symbol) ;
        isIdealSituationForSell.put(symbol, false);


        // Get filtered 20 Candle sticks with Top Drops
        List<Candlestick> filteredRedCandleList = candlesticks.stream().filter(candlestick -> {
            return Double.parseDouble(candlestick.getClose()) < Double.parseDouble(candlestick.getOpen());
        }).sorted(new BuyComparator<Candlestick>()).limit(20).collect(Collectors.toList());

        Double averageVolumeOfRedCandles =  filteredRedCandleList.stream().
                mapToDouble(s -> new Double(s.getVolume())).average().getAsDouble();

        Double averageDropPriceOfRedCandles = filteredRedCandleList.stream().
                mapToDouble(s -> {
                    Double openPrice = Double.parseDouble(s.getOpen());
                    Double closePrice = Double.parseDouble(s.getClose());
                    return openPrice - closePrice;
                }).average().getAsDouble();

        auditService.setAverageDropOfRedCandlesPerSymbol(symbol, averageDropPriceOfRedCandles);
        auditService.setAverageVolumeOfRedCandlesPerSymbol(symbol, averageVolumeOfRedCandles);
        logger.trace("AverageDropPriceOfRedCandles " + averageDropPriceOfRedCandles + " Symbol " + symbol);
        logger.trace("AverageVolumeOfRedCandles " + averageVolumeOfRedCandles + " Symbol " + symbol) ;



        isIdealSituationForBuy.put(symbol, false);

        // Ideal situation to Sell when lastCandle Stick is Green and
        // Volume of the Last Candle Stick exceeds the average
        // Raise of the last Candle Stick exceeds the average raise
        // Average Raise of Green Candles should be higher than Average Drop of Red Candles
        if(Double.parseDouble(lastCandlestick.getClose()) > Double.parseDouble(lastCandlestick.getOpen())
                &&
                Double.parseDouble(lastCandlestick.getVolume()) > averageVolumeOfGreenCandles
                &&
                (Double.parseDouble(lastCandlestick.getClose()) - Double.parseDouble(lastCandlestick.getOpen()) > averageRaisePriceOfGreenCandles)
                &&
                (averageRaisePriceOfGreenCandles > averageDropPriceOfRedCandles && averageVolumeOfGreenCandles > averageVolumeOfRedCandles)
        )
        {
            isIdealSituationForSell.replace(symbol, true);
        }


        // Ideal situation to buy when lastCandle Stick is Red and
        // Volume of the Last Candle Stick exceeds the average
        // Drop of the last Candle Stick exceeds the average drop
        // Average drop of Red Candles is higher than Average raise to Green Candles
        if(Double.parseDouble(lastCandlestick.getOpen()) > Double.parseDouble(lastCandlestick.getClose())
                &&
                Double.parseDouble(lastCandlestick.getVolume()) > averageVolumeOfRedCandles
                &&
                (Double.parseDouble(lastCandlestick.getOpen()) - Double.parseDouble(lastCandlestick.getClose()) > averageDropPriceOfRedCandles)
                &&
                (averageDropPriceOfRedCandles > averageRaisePriceOfGreenCandles && averageVolumeOfRedCandles > averageVolumeOfGreenCandles)

        )
        {
            isIdealSituationForBuy.replace(symbol, true);
        }




    }


    public Boolean getIsIdealSituationForBuy(String symbol) {
        return isIdealSituationForBuy.get(symbol);
    }

    public Boolean getIsIdealSituationForSell(String symbol) {
        return isIdealSituationForSell.get(symbol);
    }


    class BuyComparator<CandleStick> implements Comparator<Candlestick> {
        @Override
        public int compare(Candlestick o1, Candlestick o2) {
            Double diff1 = Double.parseDouble(o1.getOpen()) -
                    Double.parseDouble(o1.getClose());
            Double diff2 = Double.parseDouble(o2.getOpen()) -
                    Double.parseDouble(o2.getClose());

            return diff2.compareTo(diff1);
        }
    }

    class SellComparator<CandleStick> implements Comparator<Candlestick> {
        @Override
        public int compare(Candlestick o1, Candlestick o2) {
            Double diff1 = Double.parseDouble(o1.getClose()) -
                    Double.parseDouble(o1.getOpen());
            Double diff2 = Double.parseDouble(o2.getClose()) -
                    Double.parseDouble(o2.getOpen());

            return diff2.compareTo(diff1);
        }
    }

}

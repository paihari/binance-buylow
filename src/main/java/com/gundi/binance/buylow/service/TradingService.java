package com.gundi.binance.buylow.service;

import com.binance.api.client.BinanceApiCallback;
import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.BinanceApiWebSocketClient;
import com.binance.api.client.domain.account.NewOrder;
import com.binance.api.client.domain.event.AggTradeEvent;
import com.gundi.binance.buylow.config.APIKeyAndSecret;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class TradingService {


    @Autowired
    private APIKeyAndSecret apiKeyAndSecret;

    public Integer getNumberOfTrades() {

        return numberOfTrades;
    }

    private Integer numberOfTrades = 0;

    public Integer getNumberOfEvents() {
        return numberOfEvents;
    }

    private Integer numberOfEvents = 0;

    public void doIt() {

        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance(
                apiKeyAndSecret.getApiKey(), apiKeyAndSecret.getApiSecret());
        BinanceApiWebSocketClient binanceApiWebSocketClient = factory.newWebSocketClient();
        BinanceApiRestClient apiRestClient = factory.newRestClient();


        String pair = CryptoPair.getPairsAsString();

        binanceApiWebSocketClient.onAggTradeEvent(pair.toLowerCase(), new BinanceApiCallback<AggTradeEvent>() {
                    @Override
                    public void onResponse(final AggTradeEvent aggTradeEvent) {
                        CryptoPair cryptoPair = CryptoPair.valueOf(aggTradeEvent.getSymbol());

                        String lastPrice = apiRestClient.get24HrPriceStatistics(aggTradeEvent.getSymbol()).getLastPrice();
                        String lowPrice = apiRestClient.get24HrPriceStatistics(aggTradeEvent.getSymbol()).getLowPrice();
                        //System.out.println("The Last Price " + lastPrice + " The Low Price " + lowPrice );
                        if (lastPrice.equals(lowPrice)) {
                            numberOfEvents++;
                            BigDecimal d_lastPrice = new BigDecimal(lastPrice);
                            BigDecimal amountNeededForBuyTrade = d_lastPrice.multiply(new BigDecimal(cryptoPair.getQuantity()));

                            String baseCurrencyAssetBalanceStr = apiRestClient.getAccount().getAssetBalance(cryptoPair.getBaseCurrency()).getFree();
                            BigDecimal amountInTheAccount = new BigDecimal(baseCurrencyAssetBalanceStr);
                            if(amountInTheAccount.compareTo(amountNeededForBuyTrade) == 1) {
                                apiRestClient.newOrder(NewOrder.marketBuy(aggTradeEvent.getSymbol(), cryptoPair.getQuantity()));
                                numberOfTrades ++;
                            }


                        }
                    }
                   public void onFailure(final Throwable cause) {
                        System.err.println("Web Socket Failed");
                        cause.printStackTrace();
                        doIt();
                    }
                });
    }

}

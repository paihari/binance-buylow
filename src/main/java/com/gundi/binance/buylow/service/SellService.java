package com.gundi.binance.buylow.service;

import com.binance.api.client.domain.OrderSide;
import com.binance.api.client.domain.OrderType;
import com.binance.api.client.domain.TimeInForce;
import com.binance.api.client.domain.account.NewOrder;
import com.binance.api.client.domain.account.Order;
import com.binance.api.client.domain.account.request.CancelOrderRequest;
import com.binance.api.client.domain.account.request.OrderRequest;
import com.binance.api.client.domain.event.AggTradeEvent;
import com.binance.api.client.domain.market.TickerStatistics;
import com.gundi.binance.buylow.api.APIClient;
import org.decimal4j.util.DoubleRounder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SellService {

    private CalculationService calculationService;
    private APIClient apiClient;

    Logger logger = LoggerFactory.getLogger(SellService.class);

    @Value("${throttle.profit.percentage}")
    Double throttleProfitPercentage;

    @Value("${throttle.stop.loss.percentage}")
    Double throttleLossPercentage;

    @Value("${throttle.stop.limit.percentage}")
    Double throttleStopLimitPercentage;

    @Autowired
    public SellService(APIClient apiClient, CalculationService calculationService) {
        this.apiClient = apiClient;
        this.calculationService = calculationService;
    }


    public void invoke(AggTradeEvent aggTradeEvent) {

        calculationService.invoke(aggTradeEvent.getSymbol());
        deleteAndCreateOrCreateStopOrder(aggTradeEvent.getSymbol());

    }

    private void deleteAndCreateOrCreateStopOrder(String symbol) {
        int roundDecimalForSymbol = calculationService.getRoundDecimalPerSymbol(symbol);

        TickerStatistics tickerStatistics =  apiClient.get24HrPriceStatistics(
                symbol);

        Double averagePrice = calculationService.getAveragePricePerSymbol(symbol);

        Double currentPrice = DoubleRounder.round(Double.parseDouble(tickerStatistics.getLastPrice()),
                calculationService.getRoundDecimalPerSymbol(symbol));

        logger.trace("Average Price " + averagePrice);
        logger.trace("Current Price " + currentPrice);

        Double percentageChange = (currentPrice - averagePrice)/ averagePrice * 100;

        logger.trace("Percentage Price " + percentageChange);

        logger.trace("Throttle profit percentage " + throttleProfitPercentage );


        List<Order> openOrders = apiClient.
                getOpenOrders(new OrderRequest(symbol));


        if(percentageChange.compareTo(throttleProfitPercentage) == 1) {


            if(openOrders.isEmpty()) {
                createProfitMarketSellOrder(symbol);

            } else {
                openOrders.stream().forEach( order -> {
                    apiClient.cancelOrder(new CancelOrderRequest(symbol,
                            order.getOrderId()));

                });
                createProfitMarketSellOrder(symbol);
            }

        } else {
            if(openOrders.isEmpty()) {
                createStopLossOrder(symbol);

            } else {
                openOrders.stream().forEach(order -> {
                    Double stopPrice = DoubleRounder.round(averagePrice * throttleLossPercentage, roundDecimalForSymbol);

                    Double orderStopPrice = DoubleRounder.round(Double.parseDouble(order.getStopPrice()),
                            roundDecimalForSymbol);


                    if(orderStopPrice.compareTo(stopPrice) != 0) {
                        apiClient.cancelOrder(new CancelOrderRequest(symbol,
                                order.getOrderId()));
                        createStopLossOrder(symbol);
                    }
                });

            }

        }

    }

    private void createProfitMarketSellOrder(String symbol) {
        String quantity = calculationService.getTotalQtyPerSymbol(symbol).toString();
        apiClient.newOrder(NewOrder.marketSell(symbol, quantity));
    }



    private void createStopLossOrder(String symbol) {

        Double averagePrice = calculationService.getAveragePricePerSymbol(symbol);
        if(averagePrice.compareTo(Double.NaN) == 0)
            return;

        int roundDecimalForSymbol = calculationService.getRoundDecimalPerSymbol(symbol);

        Double stopPrice = DoubleRounder.round(averagePrice * throttleLossPercentage, roundDecimalForSymbol);
        Double stopLimitPrice = DoubleRounder.round(averagePrice * throttleStopLimitPercentage, roundDecimalForSymbol);

        String quantity = calculationService.getTotalQtyPerSymbol(symbol).toString();

        NewOrder order = new NewOrder(symbol,
                OrderSide.SELL, OrderType.STOP_LOSS_LIMIT, TimeInForce.GTC,
                quantity, stopLimitPrice.toString());

        order.stopPrice(stopPrice.toString());
        apiClient.newOrder(order);

    }

}

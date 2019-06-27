package com.gundi.binance.buylow.service;

import com.binance.api.client.domain.OrderSide;
import com.binance.api.client.domain.OrderType;
import com.binance.api.client.domain.TimeInForce;
import com.binance.api.client.domain.account.NewOrder;
import com.binance.api.client.domain.account.Order;
import com.binance.api.client.domain.account.request.CancelOrderRequest;
import com.binance.api.client.domain.account.request.OrderRequest;
import com.binance.api.client.domain.event.AggTradeEvent;
import org.decimal4j.util.DoubleRounder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SellService {

    private AuthenticationService authenticationService;
    private CalculationService calculationService;

    @Autowired
    public SellService(AuthenticationService authenticationService, CalculationService calculationService) {
        this.authenticationService = authenticationService;
        this.calculationService = calculationService;
    }

    public void invoke(AggTradeEvent aggTradeEvent) {

        calculationService.invoke(aggTradeEvent.getSymbol());

        int roundDecimalForSymbol = calculationService.getRoundDecimalForSymbol(aggTradeEvent.getSymbol());
        Double averagePrice = calculationService.getAveragePrice();

        Double stopPrice = DoubleRounder.round(averagePrice * 0.83, roundDecimalForSymbol);
        Double stopLimitPrice = DoubleRounder.round(averagePrice * 0.81, roundDecimalForSymbol);

        List<Order> openOrders = authenticationService.getApiRestClient().
                getOpenOrders(new OrderRequest(aggTradeEvent.getSymbol()));

        if(openOrders.isEmpty()) {
            createStopLossOrder(aggTradeEvent.getSymbol());
        } else {
            openOrders.stream().forEach(order -> {

                Double orderStopPrice = DoubleRounder.round(Double.parseDouble(order.getStopPrice()),
                        roundDecimalForSymbol);


                if(orderStopPrice.compareTo(stopPrice) != 0) {
                    authenticationService.getApiRestClient().cancelOrder(new CancelOrderRequest(aggTradeEvent.getSymbol(),
                            order.getOrderId()));
                    createStopLossOrder(aggTradeEvent.getSymbol());
                }
            });

        }
    }

    private void createStopLossOrder(String symbol) {

        Double averagePrice = calculationService.getAveragePrice();
        int roundDecimalForSymbol = calculationService.getRoundDecimalForSymbol(symbol);

        Double stopPrice = DoubleRounder.round(averagePrice * 0.83, roundDecimalForSymbol);
        Double stopLimitPrice = DoubleRounder.round(averagePrice * 0.81, roundDecimalForSymbol);

        String quantity = calculationService.getTotalQty().toString();

        NewOrder order = new NewOrder(symbol,
                OrderSide.SELL, OrderType.STOP_LOSS_LIMIT, TimeInForce.GTC,
                quantity, stopLimitPrice.toString());

        order.stopPrice(stopPrice.toString());
        authenticationService.getApiRestClient().newOrder(order);

    }

}

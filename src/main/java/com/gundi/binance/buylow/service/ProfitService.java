package com.gundi.binance.buylow.service;

import com.binance.api.client.domain.OrderSide;
import com.binance.api.client.domain.account.NewOrder;
import com.binance.api.client.domain.account.Order;
import com.binance.api.client.domain.account.Trade;
import com.gundi.binance.buylow.api.APIClient;
import com.gundi.binance.buylow.config.CryptoPair;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

@Service
@Slf4j
public class ProfitService {


    private APIClient apiClient;

    @Value("${profit.scrip}")
    String profitScrip;

    @Value("${start.time}")
    String startTime;


    @Autowired
    public ProfitService(APIClient apiClient) {
        this.apiClient = apiClient;
    }

    public void transferProfit(String symbol) {


        // 1566359220912L
        List<Order> orderList = apiClient.getExecutedOrders(symbol).stream().filter(order -> {
                return order.getTime() > Long.valueOf(startTime);

        }).sorted(Comparator.comparing(Order::getTime).reversed()).collect(Collectors.toList());

        List<Trade> allTrades = apiClient.getMyTrades(symbol);

        Order dummyOrder = new Order();
        dummyOrder.setOrderId(0L);

        Optional<Order> opt = Optional.of(dummyOrder);

        List<Trade> filteredBuyTrades = allTrades.stream().filter(
                trade -> orderList.stream().filter(order ->  order.getSide().equals(OrderSide.BUY)).
                        findFirst().orElse(opt.get())
                .getOrderId().toString().equals(trade.getOrderId())).collect(Collectors.toList());


        Double buyAmount = filteredBuyTrades.stream().mapToDouble(trade -> new Double(trade.getQty()) * new Double(trade.getPrice())).sum();


        List<Trade> filteredSellTrades = allTrades.stream().filter(
                trade -> orderList.stream().filter(order ->  order.getSide().equals(OrderSide.SELL)).
                        findFirst().orElse(opt.get())
                        .getOrderId().toString().equals(trade.getOrderId())).collect(Collectors.toList());

        Double sellAmount = filteredSellTrades.stream().mapToDouble(trade -> new Double(trade.getQty()) * new Double(trade.getPrice())).sum();


        log.info("The Buy Amount " + buyAmount);
        log.info("The Sell Amount " + sellAmount);
        Double profitLoss = BigDecimal.valueOf(sellAmount - buyAmount).setScale(2, BigDecimal.ROUND_CEILING).doubleValue();

        if(profitLoss > 0) {
            apiClient.newOrder(NewOrder.marketBuy(profitScrip,
                    String.valueOf(profitLoss)));

        } else {
            Math.abs(profitLoss);
            apiClient.newOrder(NewOrder.marketSell(profitScrip,
                    String.valueOf(Math.abs(profitLoss))));

        }

    }
}

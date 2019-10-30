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


        Optional<List<Order>> o_orderList = Optional.ofNullable(apiClient.getExecutedOrders(symbol).stream().filter(order -> {
                    return order.getTime() > Long.valueOf(startTime);

                }).collect(Collectors.toList()));


        o_orderList.ifPresent(orders -> {
            Map<OrderSide, List<Order>> mapOfOrdersPerSide = Optional.ofNullable(o_orderList.get().stream()).orElseGet(Stream::empty).collect(Collectors.groupingBy(Order::getSide));

            Optional<Order> buyOrder = mapOfOrdersPerSide.get(OrderSide.BUY) == null ? Optional.empty() :
                    Optional.ofNullable(
                    mapOfOrdersPerSide.get(OrderSide.BUY).get(mapOfOrdersPerSide.get(OrderSide.SELL).size() -1));

            Optional<Order> sellOrder = mapOfOrdersPerSide.get(OrderSide.SELL) == null ? Optional.empty() :
                    Optional.ofNullable(
                            mapOfOrdersPerSide.get(OrderSide.SELL).get(mapOfOrdersPerSide.get(OrderSide.SELL).size() -1));




            if(buyOrder.isPresent() && sellOrder.isPresent()) {
                List<Trade> allTrades = apiClient.getMyTrades(symbol);

                Double buyAmount = allTrades.stream().filter(trade -> trade.getOrderId().equals(buyOrder.get().getOrderId().toString())).
                                   mapToDouble(trade -> new Double(trade.getQty()) * new Double(trade.getPrice())).sum();

                Double sellAmount = allTrades.stream().filter(
                        trade -> trade.getOrderId().equals(sellOrder.get().getOrderId().toString())).mapToDouble(trade -> new Double(trade.getQty()) * new Double(trade.getPrice())).sum();

                log.info("The Buy Amount " + buyAmount);
                log.info("The Sell Amount " + sellAmount);
                Double profitLoss = BigDecimal.valueOf(sellAmount - buyAmount).setScale(2, BigDecimal.ROUND_CEILING).doubleValue();
                log.info("Profit " + profitLoss);
                if(profitLoss > 0) {
                    apiClient.newOrder(NewOrder.marketBuy(profitScrip,
                            String.valueOf(profitLoss)));

                } else {
                    Math.abs(profitLoss);
                    apiClient.newOrder(NewOrder.marketSell(profitScrip,
                            String.valueOf(Math.abs(profitLoss))));

                }


            }
        });



 /*       List<Order> t_orderList = apiClient.getExecutedOrders(symbol).stream().filter(order -> {
            return order.getTime() > Long.valueOf(startTime);

        }).collect(Collectors.toList());

        Map<OrderSide, List<Order>> mapOfOrdersPerSide = Optional.ofNullable(apiClient.getExecutedOrders(symbol).stream().filter(order -> {
            return order.getTime() > Long.valueOf(startTime);

        })).orElseGet(Stream::empty).collect(Collectors.groupingBy(Order::getSide));



        //Map<OrderSide, List<Order>> mapOfOrdersPerSide = t_orderList.stream().collect(Collectors.groupingBy(Order::getSide));

        int buySideSize = mapOfOrdersPerSide.get(OrderSide.BUY).size();
        int sellSideSize = mapOfOrdersPerSide.get(OrderSide.SELL).size();

        Optional<Order> buyOrder = mapOfOrdersPerSide.get(OrderSide.BUY) == null ? Optional.empty() :
                mapOfOrdersPerSide.get(OrderSide.BUY).get(sellSideSize -1);


        Order buyOrder = mapOfOrdersPerSide.get(OrderSide.BUY).get(sellSideSize -1);
        Order sellOrder = mapOfOrdersPerSide.get(OrderSide.SELL).get(sellSideSize -1);

        List<Trade> allTrades = apiClient.getMyTrades(symbol);


        List<Trade> filteredBuyTrades = allTrades.stream().filter(
                trade -> trade.getOrderId().equals(buyOrder.getOrderId())).collect(Collectors.toList());

        Double buyAmount = filteredBuyTrades.stream().mapToDouble(trade -> new Double(trade.getQty()) * new Double(trade.getPrice())).sum();


        List<Trade> filteredSellTrades = allTrades.stream().filter(
                trade -> trade.getOrderId().equals(sellOrder.getOrderId())).collect(Collectors.toList());

        Double sellAmount = filteredSellTrades.stream().mapToDouble(trade -> new Double(trade.getQty()) * new Double(trade.getPrice())).sum();



        // Get all the orders, from specific time
        // Sort it decreasing

        // 1566359220912L
        List<Order> orderList = apiClient.getExecutedOrders(symbol).stream().filter(order -> {
                return order.getTime() > Long.valueOf(startTime);

        }).sorted(Comparator.comparing(Order::getTime).reversed()).collect(Collectors.toList());



//        List<Trade> allTrades = apiClient.getMyTrades(symbol);
//
//        Order dummyOrder = new Order();
//        dummyOrder.setOrderId(0L);
//
//        Optional<Order> opt = Optional.of(dummyOrder);
//
//        List<Trade> filteredBuyTrades = allTrades.stream().filter(
//                trade -> orderList.stream().filter(order ->  order.getSide().equals(OrderSide.BUY)).
//                        findFirst().orElse(opt.get())
//                .getOrderId().toString().equals(trade.getOrderId())).collect(Collectors.toList());
//
//
//        Double buyAmount = filteredBuyTrades.stream().mapToDouble(trade -> new Double(trade.getQty()) * new Double(trade.getPrice())).sum();
//
//
//        List<Trade> filteredSellTrades = allTrades.stream().filter(
//                trade -> orderList.stream().filter(order ->  order.getSide().equals(OrderSide.SELL)).
//                        findFirst().orElse(opt.get())
//                        .getOrderId().toString().equals(trade.getOrderId())).collect(Collectors.toList());
//
//        Double sellAmount = filteredSellTrades.stream().mapToDouble(trade -> new Double(trade.getQty()) * new Double(trade.getPrice())).sum();


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

        } */

    }
}

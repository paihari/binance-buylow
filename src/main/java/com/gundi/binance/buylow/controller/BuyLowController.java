package com.gundi.binance.buylow.controller;

import com.binance.api.client.domain.account.Order;
import com.binance.api.client.domain.account.Trade;
import com.gundi.binance.buylow.api.APIClient;
import com.gundi.binance.buylow.config.CryptoPair;
import com.gundi.binance.buylow.model.TradeLog;
import com.gundi.binance.buylow.service.AuditService;
import com.gundi.binance.buylow.service.CalculationService;
import com.gundi.binance.buylow.service.ProfitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class BuyLowController {

    @Value("${spring.profiles.active}")
    private String env;

    @Value("${msg}")
    private String msg;


    private CalculationService calculationService;

    private AuditService auditService;

    private APIClient apiClient;

    private ProfitService profitService;

    @Value("${start.time}")
    String startTime;


    @Autowired
    public BuyLowController(CalculationService calculationService,
                            AuditService auditService,
                            APIClient apiClient,
                            ProfitService profitService

    ) {
        this.calculationService = calculationService;
        this.auditService = auditService;
        this.apiClient = apiClient;
        this.profitService = profitService;

    }


    @RequestMapping("/invoke")
    public String invoke() {
        calculationService.invoke();
        String message = msg + " from the system" + env + System.lineSeparator();
        for (CryptoPair cryptoPair : CryptoPair.values()) {
            message = message + " Symbol " + cryptoPair.getPair() +

                    " Average Price " + calculationService.getAveragePricePerSymbol(cryptoPair.getPair()) +
                    " Total Qty " + calculationService.getTotalQtyPerSymbol(cryptoPair.getPair()) +
                    System.lineSeparator();


        }
        return message;
    }

    @RequestMapping("/audit")
    public String audit() {
        String message = "";


        for(CryptoPair pair: CryptoPair.values()) {

            message = message.concat("<br>");
            message = message.concat("Average Drop of Red Candles for " + pair.getPair() + "   " + auditService.getAverageDropOfRedCandlesPerSymbol(pair.getPair()) + System.lineSeparator());
            message = message.concat("<br>");
            message = message.concat("Average Volume of Red Candles for " + pair.getPair() + "   " + auditService.getAverageVolumeOfRedCandlesPerSymbol(pair.getPair()) + System.lineSeparator());
            message = message.concat("<br>");
            message = message.concat("Average Raise of Green Candles " + pair.getPair() + "   " + auditService.getAverageRaiseOfGreenCandlesPerSymbol(pair.getPair()) + System.lineSeparator());
            message = message.concat("<br>");
            message = message.concat("Average Volume of Green Candles " + pair.getPair() + "   " + auditService.getAverageVolumeOfGreenCandlesPerSymbol(pair.getPair()) + System.lineSeparator());
            message = message.concat("<br>");
            // 1572054422281
            // 1566359220912L

            List<Trade> allTrades = apiClient.getMyTrades(pair.getPair()).stream().filter(trade -> {
                return trade.getTime() > Long.valueOf(startTime);
            }).collect(Collectors.toList());

            for (Trade trade : allTrades) {
                LocalDateTime lastTradeTime =
                        LocalDateTime.ofInstant(Instant.ofEpochMilli(trade.getTime()),
                                ZoneId.systemDefault());
                String type = trade.isBuyer() ? "BUY" : "SELL";
                message = message.concat("<br>");
                message = message.concat("Symbol " + pair.getPair() + " Type " + type  + " Price " + trade.getPrice() + " Time " + lastTradeTime  + " Qty " + trade.getQty() + System.lineSeparator());

            }
            message = message.concat(System.lineSeparator());

            Double buyValue = allTrades.stream().filter(trade -> {
                return trade.isBuyer();
            }).collect(Collectors.summingDouble(trade -> {
                return Double.parseDouble(trade.getQty()) * Double.parseDouble(trade.getPrice());
            }));

            Double buyQuantity = allTrades.stream().filter(trade -> {
                return trade.isBuyer();
            }).collect(Collectors.summingDouble(trade -> {
                return Double.parseDouble(trade.getQty());
            }));

            Double averageBuyPrice = allTrades.stream().filter(trade -> {
                return trade.isBuyer();
            }).mapToDouble(trade -> {
                        return Double.parseDouble(trade.getPrice());
            }).average().orElse(0);


            Long noOfBuyTrades = allTrades.stream().filter(trade -> {
                return trade.isBuyer();
            }).count();




            Double sellValue = allTrades.stream().filter(trade -> {
                return !trade.isBuyer();
            }).collect(Collectors.summingDouble(trade -> {
                return Double.parseDouble(trade.getQty()) * Double.parseDouble(trade.getPrice());
            }));

            Double sellQuantity = allTrades.stream().filter(trade -> {
                return !trade.isBuyer();
            }).collect(Collectors.summingDouble(trade -> {
                return Double.parseDouble(trade.getQty());
            }));

            Double averageSellPrice = allTrades.stream().filter(trade -> {
                return !trade.isBuyer();
            }).mapToDouble(trade -> {
                return Double.parseDouble(trade.getPrice());
            }).average().orElse(0);

            Long noOfSellTrades = allTrades.stream().filter(trade -> {
                return !trade.isBuyer();
            }).count();

            message = message.concat("<br>");
            message = message.concat("Symbol " + pair.getPair() + " Average Buy Price " + averageBuyPrice + " Average Sell Price " + averageSellPrice) + System.lineSeparator();
            message = message.concat("<br>");

            message = message.concat("Symbol " + pair.getPair() + " Number Of Buy Trades " + noOfBuyTrades + " No Of Sell Trades  " + noOfSellTrades + System.lineSeparator());
            message = message.concat("<br>");
            message = message.concat("Symbol " + pair.getPair() + " Buy Value " + buyValue + " Sell Value " + sellValue + System.lineSeparator());
            message = message.concat("<br>");
            message = message.concat("Symbol " + pair.getPair() + " Buy Quantity " + buyQuantity + " Sell Quantity " + sellQuantity);
            message = message.concat("<br>");

            message = message.concat("Symbol " + pair.getPair() + " Profit/Loss " + + (sellValue - buyValue));
            message = message.concat("<br>");


        }
        message = message.concat("<br>");
        return message;
    }

    @RequestMapping("/calculate")
    public String calculate() {


        //profitService.transferProfit("ETHUSDT");
        log.info(apiClient.getServerTime().toString());


//        for (CryptoPair pair: CryptoPair.values()) {
//            List<Order> orderList = apiClient.getExecutedOrders(pair.getPair()).stream().filter(order -> {
//                return order.getTime() > 1566359220912L;
//            }).collect(Collectors.toList());
//
//
//            for(Order order : orderList) {
//                //log.info("Order Info " + order.getOrderId());
//            }
//
////            Map<String, Trade> mapOfTrades = apiClient.getMyTrades(pair.getPair()).stream().filter(trade -> {
////                return trade.getTime() > 1566359220912L;
////            }).collect(Collectors.toMap(Trade::getOrderId, trade -> trade));
//
//
////            Map<String, Set<Trade>> mapOfTrades = apiClient.getMyTrades(pair.getPair()).stream().filter(trade -> {
////                return trade.getTime() > 1566359220912L;
////            }).collect(Collectors.groupingBy(Trade::getOrderId, Collectors.mapping(Trade::getOrderId, Collectors.toSet())));
//
//            //mapOfTrades.forEach((k,v) -> log.info("Key " + k));
//
//            List<Trade> tradeList = apiClient.getMyTrades(pair.getPair()).stream().filter(trade -> {
//                return trade.getTime() > 1566359220912L;
//            }).collect(Collectors.toList());
//
//            tradeList.forEach(k -> log.info(k.getOrderId() + " Order Id " + k.getId()));
//
//            Map<Boolean, List<Trade>> mapOfBuyAndSellTrades = tradeList.stream().collect(Collectors.groupingBy(Trade::isBuyer));
//
//
//            //mapOfBuyAndSellTrades.forEach((k,v) -> log.info(" Key " + k + " Value " + v));
//
//            Map<Boolean, Map<String, List<Trade>>> mapOfBuySellTradesPerOrderId = tradeList.stream().collect(Collectors.groupingBy(Trade::isBuyer, Collectors.groupingBy(Trade::getOrderId)));
//
//            mapOfBuySellTradesPerOrderId.forEach((k,v) -> {
//                    if(k) {
//                        log.info("Buy Trades");
//                        v.forEach((key, value) -> {
//                            log.info("Key " + key + " Value " + value);
//                        });
//                    }
//            });
//
//            //Map<Boolean, Map<String, Long>> mapOf



 //       }

//        for(CryptoPair pair: CryptoPair.values()) {
//            List<Trade> allTrades = apiClient.getMyTrades(pair.getPair()).stream().filter(trade -> {
//                return trade.getTime() > 1566359220912L;
//            }).collect(Collectors.toList());
//
//            for (Trade trade : allTrades) {
//
//                LocalDateTime lastTradeTime =
//                        LocalDateTime.ofInstant(Instant.ofEpochMilli(trade.getTime()),
//                                ZoneId.systemDefault());
//                System.out.println("Local Time " + lastTradeTime + " Epoch Time " + trade.getTime() + " Symbol " + trade.getSymbol());
//
//            }
//
//
//
//        }
        return "";

    }

}

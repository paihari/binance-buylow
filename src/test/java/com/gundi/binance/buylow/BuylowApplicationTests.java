package com.gundi.binance.buylow;

import com.binance.api.client.domain.OrderSide;
import com.binance.api.client.domain.OrderType;
import com.binance.api.client.domain.TimeInForce;
import com.binance.api.client.domain.account.NewOrder;
import com.binance.api.client.domain.account.Trade;
import com.gundi.binance.buylow.service.AuthenticationService;
import com.gundi.binance.buylow.service.CalculationService;
import org.decimal4j.util.DoubleRounder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BuylowApplicationTests {

    @Autowired
    CalculationService calculationService;

    @Autowired
    AuthenticationService authenticationService;




    @Test
    public void contextLoads() {
    }


    //@Test
    public void testCalculateAveragePrice() {


        String symobol = "BTCUSDT";

        Trade trade_1 = new Trade();
        trade_1.setPrice("11951.99");
        trade_1.setQty("0.004");

        Trade trade_2 = new Trade();
        trade_2.setPrice("11751.99");
        trade_2.setQty("0.004");

        List<Trade> tradeList = new ArrayList<>();
        tradeList.add(trade_1);
        tradeList.add(trade_2);





        double totalPrice = tradeList.stream().mapToDouble(s -> {
            System.out.println("S.get Proce " + s.getPrice());
            double price = Double.parseDouble(s.getPrice());
            double qty = Double.parseDouble(s.getQty());
            return  price * qty;
        }).sum();

        Double totalQty = tradeList.stream().mapToDouble(s -> Double.parseDouble(s.getQty())).sum();


        System.out.println("The Total price " + totalPrice);
        Double averagePricePerQty = totalPrice/totalQty;

        System.out.println("Average Price " + averagePricePerQty + " Total Qty " + totalQty);
        int round = calculationService.getRoundDecimalPerSymbol(symobol);

        Double avgRounded = DoubleRounder.round(averagePricePerQty, round);
        System.out.println("Avg Rounded " + avgRounded);

        Double stopPrice = DoubleRounder.round(avgRounded * 0.83, round);
        System.out.println("Stop Price " + stopPrice);

        Double stopLimitPrice = DoubleRounder.round(avgRounded * 0.81, round);
        System.out.println("Stop Limit Price " + stopLimitPrice);









//        Double averagePricePerQty = totalPrice/totalQtyPerSymbol.get(symbol);
//        return DoubleRounder.round(averagePricePerQty, roundDecimalPerSymbol.get(symbol));
    }

}

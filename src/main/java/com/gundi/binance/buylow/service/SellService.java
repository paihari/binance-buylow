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
import com.gundi.binance.buylow.config.CryptoPair;
import com.gundi.binance.buylow.model.TradeLog;
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
    private AuditService auditService;
    private APIClient apiClient;

    Logger logger = LoggerFactory.getLogger(SellService.class);

    @Autowired
    public SellService(APIClient apiClient,
                       CalculationService calculationService,
                       AuditService auditService) {
        this.apiClient = apiClient;
        this.calculationService = calculationService;
        this.auditService = auditService;
    }

    public boolean tradeIt(String symbol) {

        boolean tradeDone = false;
        CryptoPair cryptoPair = CryptoPair.valueOf(symbol);

        Double sellPrice = DoubleRounder.round(Double.parseDouble(apiClient.get24HrPriceStatistics(symbol).getBidPrice()),
                calculationService.getRoundDecimalPerSymbol(symbol));


        String tradeCurrencyAssetBalanceStr =
                apiClient.getAssetBalance(
                        cryptoPair.getTradeCurrency()).getFree();
        Double amountNeededForSellTrade = Double.parseDouble(cryptoPair.getQuantity());

        Double amountInTheAccount = Double.parseDouble(tradeCurrencyAssetBalanceStr);
        if(amountInTheAccount.compareTo(amountNeededForSellTrade) == 1) {
            apiClient.newOrder(NewOrder.marketSell(symbol, cryptoPair.getQuantity()));
            tradeDone = true;

        }
        return tradeDone;
    }
}

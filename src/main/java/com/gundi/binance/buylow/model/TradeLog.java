package com.gundi.binance.buylow.model;

public class TradeLog {

    Long tradeTime;
    Boolean isBuyTrade;
    Double tradePrice;
    String symbol;

    public TradeLog(Long tradeTime, Boolean isBuyTrade, Double tradePrice,
    String symbol) {
        this.tradeTime = tradeTime;
        this.isBuyTrade = isBuyTrade;
        this.tradePrice = tradePrice;
        this.symbol = symbol;
    }

    public Long getTradeTime() {
        return tradeTime;
    }

    public void setTradeTime(Long tradeTime) {
        this.tradeTime = tradeTime;
    }

    public Boolean getBuyTrade() {
        return isBuyTrade;
    }

    public void setBuyTrade(Boolean buyTrade) {
        isBuyTrade = buyTrade;
    }

    public Double getTradePrice() {
        return tradePrice;
    }

    public void setTradePrice(Double tradePrice) {
        this.tradePrice = tradePrice;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return "TradeLog{" +
                "tradeTime=" + tradeTime +
                ", isBuyTrade=" + isBuyTrade +
                ", tradePrice=" + tradePrice +
                ", symbol=" + symbol+
                '}';
    }
}

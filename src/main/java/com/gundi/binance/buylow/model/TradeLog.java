package com.gundi.binance.buylow.model;

public class TradeLog {

    Long tradeTime;
    Boolean isBuyTrade;
    Double tradePrice;

    public TradeLog(Long tradeTime, Boolean isBuyTrade, Double tradePrice) {
        this.tradeTime = tradeTime;
        this.isBuyTrade = isBuyTrade;
        this.tradePrice = tradePrice;
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

    @Override
    public String toString() {
        return "TradeLog{" +
                "tradeTime=" + tradeTime +
                ", isBuyTrade=" + isBuyTrade +
                ", tradePrice=" + tradePrice +
                '}';
    }
}

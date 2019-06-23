package com.gundi.binance.buylow.service;

import java.io.Serializable;

/**
 * Created by pai on 14.12.18.
 */
public enum CryptoPair implements Serializable{


    TRXUSDT("TRXUSDT", "100", "USDT", "TRX", true);



    private String pair;
    private String quantity;
    private String baseCurrency;
    private String tradeCurrency;
    private boolean keepOnBuying;





    CryptoPair(String pair, String quantity,
               String baseCurrency, String tradeCurrency,

               boolean keepOnBuying) {
        this.pair = pair;
        this.quantity = quantity;
        this.baseCurrency = baseCurrency;
        this.tradeCurrency = tradeCurrency;
        this.keepOnBuying = keepOnBuying;

    }

    public String getPair() {
        return pair;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getBaseCurrency() {
        return baseCurrency;
    }

    public String getTradeCurrency() {
        return tradeCurrency;
    }


    public boolean isKeepOnBuying() {
        return keepOnBuying;
    }

    public static CryptoPair fromPair(String pair) {

        return valueOf(pair);
    }

    public static String getPairsAsString() {
        String pairs = "";
        for(CryptoPair cryptoPair : CryptoPair.values()) {
            pairs = pairs.concat(",").concat(cryptoPair.getPair());
        }
        return pairs.replaceFirst(",", "");
    }



}

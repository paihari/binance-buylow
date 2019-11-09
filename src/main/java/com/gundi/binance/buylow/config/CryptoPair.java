package com.gundi.binance.buylow.config;

import java.io.Serializable;

/**
 * Created by pai on 14.12.18.
 */
public enum CryptoPair implements Serializable{


    //XRPUSDT("XRPUSDT", "100", "USDT", "XRP", false),
    BTCUSDT("BTCUSDT", "0.08", "USDT", "BTC", false, false),
    //BNBUSDT("BNBUSDT", "1.2", "USDT", "BNB", true, true),
    ETHUSDT("ETHUSDT", "4", "USDT", "ETH", true, true);




    private String pair;
    private String quantity;
    private String baseCurrency;
    private String tradeCurrency;
    private boolean keepBuying;
    private boolean keepSelling;





    CryptoPair(String pair, String quantity,
               String baseCurrency, String tradeCurrency,

               boolean keepBuying,
                boolean keepSelling) {
        this.pair = pair;
        this.quantity = quantity;
        this.baseCurrency = baseCurrency;
        this.tradeCurrency = tradeCurrency;
        this.keepBuying = keepBuying;
        this.keepSelling = keepSelling;

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


    public boolean isKeepBuying() {
        return keepBuying;
    }

    public boolean isKeepSelling() {
        return keepSelling;
    }



    public static String getPairsAsString() {
        String pairs = "";
        for(CryptoPair cryptoPair : CryptoPair.values()) {
            pairs = pairs.concat(",").concat(cryptoPair.getPair());
        }
        return pairs.replaceFirst(",", "");
    }



}

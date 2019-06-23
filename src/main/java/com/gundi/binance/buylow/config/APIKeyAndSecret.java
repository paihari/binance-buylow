package com.gundi.binance.buylow.config;

import java.util.Objects;

public class APIKeyAndSecret {

    private String apiKey;
    private String apiSecret;

    public APIKeyAndSecret(String apiKey, String apiSecret) {
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
    }

    public APIKeyAndSecret() {
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getApiSecret() {
        return apiSecret;
    }

    public void setApiSecret(String apiSecret) {
        this.apiSecret = apiSecret;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        APIKeyAndSecret that = (APIKeyAndSecret) o;
        return Objects.equals(apiKey, that.apiKey) &&
                Objects.equals(apiSecret, that.apiSecret);
    }

    @Override
    public int hashCode() {
        return Objects.hash(apiKey, apiSecret);
    }


}

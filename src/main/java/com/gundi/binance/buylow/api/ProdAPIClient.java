package com.gundi.binance.buylow.api;

import com.binance.api.client.domain.account.NewOrder;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("prod")
public class ProdAPIClient extends BaseAPIClient {
    @Override
    public void newOrder(NewOrder newOrder) {
        this.apiRestClient.newOrder(newOrder);
    }
}

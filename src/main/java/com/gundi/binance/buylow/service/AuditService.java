package com.gundi.binance.buylow.service;

import com.gundi.binance.buylow.model.TradeInfo;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Scope("singleton")
public class AuditService {


    private List<String> auditLogs = new ArrayList<String>();

    private List<TradeInfo> tradeLogs = new ArrayList<TradeInfo>();

    public AuditService() {

    }

    public void addAuditLogs(String auditLog) {
        this.auditLogs.add(auditLog);
    }

    public List<TradeInfo> getTradeLogs() {
        return tradeLogs;
    }

    public void addTradeLogs(TradeInfo tradeLog) {
        this.tradeLogs.add(tradeLog);
    }

    public List<String> getAuditLogs () {
        return auditLogs;
    }

}

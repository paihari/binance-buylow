package com.gundi.binance.buylow.service;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Scope("singleton")
public class AuditService {


    private List<String> auditLogs = new ArrayList<String>();

    public AuditService() {

    }

    public void addAuditLogs(String auditLog) {
        this.auditLogs.add(auditLog);
    }

    public List<String> getAuditLogs () {
        return auditLogs;
    }

}

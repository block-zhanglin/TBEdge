package com.zl.blockchain.dto;

import java.util.List;

/**
 *get获取未确认交易 response
 */
public class GetUnconfirmedTransactionsResponse {

    private List<TransactionDto> transactions;

    public List<TransactionDto> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<TransactionDto> transactions) {
        this.transactions = transactions;
    }
}

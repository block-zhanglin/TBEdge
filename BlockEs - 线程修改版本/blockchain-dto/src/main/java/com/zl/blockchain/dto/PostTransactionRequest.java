package com.zl.blockchain.dto;

/**
 *post 接收其它节点提交的交易
 */
public class PostTransactionRequest {

    private TransactionDto transaction;

    public TransactionDto getTransaction() {
        return transaction;
    }

    public void setTransaction(TransactionDto transaction) {
        this.transaction = transaction;
    }
}

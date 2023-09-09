package com.zl.blockchain.core;

import com.zl.blockchain.dto.*;


import java.util.List;

/**
 * 未确认交易数据库
 *
 */
public abstract class UnconfirmedTransactionDatabase {

    //配置
    protected CoreConfiguration coreConfiguration;


    /**
     * 新增交易
     */
    public abstract boolean insertTransaction(TransactionDto transaction) ;

    /**
     * 批量提取交易
     */
    public abstract List<TransactionDto> selectTransactions(long from, long size) ;

    /**
     * 删除交易（通过交易hash）
     */
    public abstract void deleteByTransactionHash(String transactionHash) ;

    public abstract  void deleByTransaction(TransactionDto transaction);


    /**
     * 查询交易
     */
    public abstract TransactionDto selectTransactionByTransactionHash(String transactionHash);
}

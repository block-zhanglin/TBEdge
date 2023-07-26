package com.zl.blockchain.core;

import com.zl.blockchain.dto.*;


import java.util.List;

/**
 * 未确认交易数据库
 * 所有没有持久化到区块链的交易，都应该尽可能的被收集起来。
 * 其它对象可以从这里获取未确认交易数据，然后进行自己的活动。例如矿工可以从这里获取挖矿的原材料(未确认交易数据)进行挖矿活动。
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

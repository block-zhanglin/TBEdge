package com.zl.blockchain.core;

import com.zl.blockchain.crypto.model.Account;
import com.zl.blockchain.dto.ToCaresponse;

/**
 * 矿工:挖矿、将挖取的区块放入区块链
 *
 */
public abstract class Miner {

    //配置
    protected CoreConfiguration coreConfiguration;

    //矿工挖矿所在的区块链
    protected BlockchainDatabase blockchainDatabase;

    //未确认交易数据库：矿工从未确认交易数据库里获取挖矿的原材料(未确认交易数据)
    protected com.zl.blockchain.core.UnconfirmedTransactionDatabase unconfirmedTransactionDatabase;

    public Miner(CoreConfiguration coreConfiguration, BlockchainDatabase blockchainDatabase, com.zl.blockchain.core.UnconfirmedTransactionDatabase unconfirmedTransactionDatabase) {
        this.coreConfiguration = coreConfiguration;
        this.blockchainDatabase = blockchainDatabase;
        this.unconfirmedTransactionDatabase = unconfirmedTransactionDatabase;
    }


    //region 挖矿相关
    /**
     * 启用矿工。
     * 矿工有两种状态：活动状态与非活动状态。
     * 若矿工处于活动作态，矿工会进行挖矿劳作。
     * 若矿工处于非活动状态，矿工不会进行任何工作。
     */
    public abstract void start(Account account, ToCaresponse toCaresponse) ;

    /**
     * 矿工是否处于活动状态。
     */
    public abstract boolean isActive() ;

    /**
     * 激活矿工：设置矿工为活动状态。
     */
    public abstract void active() ;

    /**
     * 停用矿工：设置矿工为非活动状态。
     */
    public abstract void deactive() ;

    /**
     * 设置矿工可挖掘的最高区块高度
     */
    public abstract void setMinerMineMaxBlockHeight(long maxHeight) ;
    /**
     * 获取矿工可挖掘的最高区块高度
     */
    public abstract long getMinerMineMaxBlockHeight( ) ;

    //endregion



    //region get set

    public BlockchainDatabase getBlockchainDatabase() {
        return blockchainDatabase;
    }

    public com.zl.blockchain.core.UnconfirmedTransactionDatabase getUnconfirmedTransactionDatabase() {
        return unconfirmedTransactionDatabase;
    }

    public CoreConfiguration getCoreConfiguration() {
        return coreConfiguration;
    }

    //endregion
}
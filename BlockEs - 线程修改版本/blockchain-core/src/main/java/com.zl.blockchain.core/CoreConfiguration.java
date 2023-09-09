package com.zl.blockchain.core;

/**
 * Core配置: BlockchainCore的配置。
 *
 */
public abstract class CoreConfiguration {

    //BlockchainCore数据存放路径
    protected String corePath;

    /**
     * BlockchainCore数据存放路径
     */
    public abstract String getCorePath();

    /**
     * 矿工是否处于激活状态？
     */
    public abstract boolean isMinerActive();

    /**
     * 激活矿工
     */
    public abstract void activeMiner() ;

    /**
     * 停用矿工
     */
    public abstract void deactiveMiner() ;

    /**
     * 设置矿工能挖掘的最高区块高度
     */
    public abstract void setMinerMineMaxBlockHeight(long maxHeight) ;

    /**
     * 获取矿工能挖掘的最高区块高度
     */
    public abstract long getMinerMineMaxBlockHeight() ;

    /**
     * 矿工挖矿时间周期
     */

    public abstract long getMinerMineTimeInterval();

}

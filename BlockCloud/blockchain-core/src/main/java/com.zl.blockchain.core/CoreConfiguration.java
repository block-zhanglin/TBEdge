package com.zl.blockchain.core;

/**
 * Core配置: BlockchainCore的配置。
 *  数据存放的地址
 */
public abstract class CoreConfiguration {

    //BlockchainCore数据存放路径
    protected String corePath;

    /**
     * BlockchainCore数据存放路径
     */
    public abstract String getCorePath();

}

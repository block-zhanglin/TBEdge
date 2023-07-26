package com.zl.blockchain.netcore.configuration;


/**
 * NetCore 配置：
 *
 */
public interface NetcoreConfiguration {

    /**
     * 获取服务器端口号
     * @return
     */
    int getport();

    /**
     * 获取服务器层设备编号
     * @return
     */
    String getEsNumber();


    /**
     * BlockchainNetCore数据的存储路径
     */
    String getNetCorePath();

    /**
     * 种子节点初始化时间间隔
     */
    long getSeedNodeInitializeTimeInterval();

    /**
     * 节点搜索时间间隔
     */
    long getNodeSearchTimeInterval();
    /**
     * 节点广播时间间隔
     */
    long getNodeBroadcastTimeInterval();

    /**
     * 节点清理时间间隔
     */
    long getNodeCleanTimeInterval();

    /**
     * 是否"自动搜索新区块"
     */
    boolean isAutoSearchBlock();

    /**
     * 开启"自动搜索新区块"选项
     */
    void activeAutoSearchBlock() ;

    /**
     * 关闭"自动搜索新区块"选项
     */
    void deactiveAutoSearchBlock() ;

    /**
     * 是否自动搜索节点
     */
    boolean isAutoSearchNode();

    /**
     * 开启自动搜索节点
     */
    void activeAutoSearchNode();

    /**
     * 关闭自动搜索节点
     */
    void deactiveAutoSearchNode();


    /**
     * 区块搜索时间间隔
     */
    long getBlockSearchTimeInterval();

    /**
     * 区块广播时间间隔
     */
    long getBlockBroadcastTimeInterval();


    /**
     * 区块链高度搜索时间间隔
     */
    long getBlockchainHeightSearchTimeInterval();

    /**
     * 区块链高度广播时间间隔
     */
    long getBlockchainHeightBroadcastTimeInterval();


    /**
     * 硬分叉区块数量：两个区块链有分叉时，区块差异数量大于这个值，则真的分叉了。
     */
    long getHardForkBlockCount();


    /**
     * 未确认交易的搜索时间间隔
     */
    long getUnconfirmedTransactionsSearchTimeInterval();


}


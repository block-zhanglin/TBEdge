package com.zl.blockchain.core;

import com.zl.blockchain.core.impl.*;
import com.zl.blockchain.core.tool.ResourcePathTool;

/**
 *
 * 创建BlockchainCore的工厂
 *
 */
public class BlockchainCoreFactory {

    /**
     * 创建BlockchainCore实例
     */
    public static BlockchainCore createDefaultBlockchainCore(){
        return createBlockchainCore(ResourcePathTool.getDataRootPath());
    }

    /**
     * 创建BlockchainCore实例
     *
     * @param corePath BlockchainCore 配置文件数据存放位置
     */
    public static BlockchainCore createBlockchainCore(String corePath) {

        CoreConfiguration coreConfiguration = new CoreConfigurationDefaultImpl(corePath); //调用配置文件数据默认实现
        Consensus consensus = new ProofOfWorkConsensusImpl(); // 调用共识算法 默认实现

        UnconfirmedTransactionDatabase unconfirmedTransactionDatabase = new UnconfirmedTransactionDatabaseDefaultImpl(coreConfiguration); //区块链未确认交易数据库默认实

        BlockchainDatabase blockchainDatabase = new BlockchainDatabaseDefaultImpl(coreConfiguration,consensus,unconfirmedTransactionDatabase); // 区块链数据库默认实现
//
//        blockchainDatabase.deleteBlocks(1);
//        blockchainDatabase.deleteBlocks(2);


        Miner miner = new MinerDefaultImpl(coreConfiguration,blockchainDatabase,unconfirmedTransactionDatabase); //区块链矿工默认实现
        miner.active();
        return new BlockchainCoreImpl(coreConfiguration,blockchainDatabase,unconfirmedTransactionDatabase,miner); //调用区块链核心默认实现
    }

}

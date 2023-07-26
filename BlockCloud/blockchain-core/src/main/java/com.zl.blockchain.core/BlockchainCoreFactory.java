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

        BlockchainDatabase blockchainDatabase = new BlockchainDatabaseDefaultImpl(coreConfiguration); // 区块链数据库默认实现
//
//        blockchainDatabase.deleteBlocks(1);
//        blockchainDatabase.deleteBlocks(2);

        return new BlockchainCoreImpl(coreConfiguration,blockchainDatabase); //调用区块链核心默认实现
    }

}

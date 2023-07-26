package com.zl.blockchain.core;

import com.zl.blockchain.core.model.Block;
import com.zl.blockchain.core.model.transaction.Transaction;
import com.zl.blockchain.crypto.model.Account;
import com.zl.blockchain.dto.*;

import java.util.List;

/**
 *
 *
 * 区块链核心：单机版[没有网络交互功能的]区块链核心，代表一个完整的单机版区块链核心系统，它在底层维护着一条区块链的完整数据。
 * 设计之初，为了精简，它被设计为不含有网络模块。
 * 除了不含有网络模块外

 *
 *
 */


public abstract class BlockchainCore {

    //配置
    protected CoreConfiguration coreConfiguration;

    //区块链数据库
    protected com.zl.blockchain.core.BlockchainDatabase blockchainDatabase ;


    public BlockchainCore(CoreConfiguration coreConfiguration, com.zl.blockchain.core.BlockchainDatabase blockchainDatabase) {
        this.coreConfiguration = coreConfiguration;
        this.blockchainDatabase = blockchainDatabase;
    }

    /**
     * 将一个区块添加到区块链
     */
    public abstract boolean addBlockDto(BlockDto blockDto);

    /**
     * 将一个区块添加到区块链
     */
    public abstract boolean addBlock(Block block);

    /**
     * 删除区块链的尾巴区块(最后一个区块)
     */
    public abstract void deleteTailBlock();

    /**
     * 删除区块高度大于等于@blockHeight@的区块
     */
    public abstract void deleteBlocks(long blockHeight) ;

    /**
     * 获取区块链高度
     */
    public abstract long queryBlockchainHeight() ;

    /**
     * 根据区块高度查询区块
     */
    public abstract Block queryBlockByBlockHeight(long blockHeight);

    /**
     * 根据区块哈希查询区块
     */
    public abstract Block queryBlockByBlockHash(String blockHash);

    /**
     * 查询区块链尾巴区块
     */
    public abstract Block queryTailBlock();

    /**
     * 根据交易哈希获取交易
     */
    public abstract Transaction queryTransactionByTransactionHash(String transactionHash) ;

    /**
     * 根据交易高度获取交易
     * @param transactionHeight 交易高度。注意：区块高度从1开始。
     */
    public abstract Transaction queryTransactionByTransactionHeight(long transactionHeight) ;



    //region
    /**
     * block dto to block model
     */
    public abstract Block blockDto2Block(BlockDto blockDto) ;

    /**
     * transaction dto to transaction model
     */
    public abstract Transaction transactionDto2Transaction(TransactionDto transactionDto) ;
    //endregion


    //region get set
    public com.zl.blockchain.core.BlockchainDatabase getBlockchainDatabase() {
        return blockchainDatabase;
    }

    public CoreConfiguration getCoreConfiguration() {
        return coreConfiguration;
    }
    //endregion
}
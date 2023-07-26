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
 * 除了不含有网络模块外，它包含了一个区块链系统应有的所有功能，包含
 * 1.区块链账户生成 2.转账 3.提交交易至区块链
 *  5.新增区块到区块链 6.数据校验：区块验证、交易验证
 * 7.链上区块回滚 8.链上区块查询、交易查询、账户资金查询...... 等等。
 *
 * 单机版区块链核心系统，由以下几部分组成：
 * 区块链数据库：用于持久化本地区块链的数据
 *
 * 未确认交易数据库：存放未确认的交易数据
 *
 * 矿工：挖矿，将新挖取的矿放进区块链数据库
 *
 *
 *
 */


public abstract class BlockchainCore {

    //配置
    protected CoreConfiguration coreConfiguration;

    //区块链数据库
    protected com.zl.blockchain.core.BlockchainDatabase blockchainDatabase ;

    //未确认交易数据库
    protected com.zl.blockchain.core.UnconfirmedTransactionDatabase unconfirmedTransactionDatabase;

    //矿工
    protected com.zl.blockchain.core.Miner miner ;

    public BlockchainCore(CoreConfiguration coreConfiguration, com.zl.blockchain.core.BlockchainDatabase blockchainDatabase, com.zl.blockchain.core.UnconfirmedTransactionDatabase unconfirmedTransactionDatabase, com.zl.blockchain.core.Miner miner) {
        this.coreConfiguration = coreConfiguration;
        this.blockchainDatabase = blockchainDatabase;
        this.unconfirmedTransactionDatabase = unconfirmedTransactionDatabase;
        this.miner = miner;
    }


    /**
     * 激活区块链核心。包含激活矿工等操作。
     */
    public abstract void start(Account account, ToCaresponse toCaresponse);

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

    /**
     * 提交交易到区块链
     */
    public abstract void postTransaction(TransactionDto transactionDto) ;

    /**
     * 查询未确认的交易
     */
    public abstract List<TransactionDto> queryUnconfirmedTransactions(long from, long size) ;

    /**
     * 根据交易哈希查询未确认交易
     */
    public abstract TransactionDto queryUnconfirmedTransactionByTransactionHash(String transactionHash) ;


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

    public com.zl.blockchain.core.UnconfirmedTransactionDatabase getUnconfirmedTransactionDatabase() {
        return unconfirmedTransactionDatabase;
    }


    public com.zl.blockchain.core.Miner getMiner() {
        return miner;
    }

    public CoreConfiguration getCoreConfiguration() {
        return coreConfiguration;
    }
    //endregion
}
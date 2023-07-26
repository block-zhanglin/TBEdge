package com.zl.blockchain.core;

import com.zl.blockchain.core.model.Block;
import com.zl.blockchain.core.model.transaction.Transaction;
import com.zl.blockchain.dto.*;



/**
 * 区块链数据库：该类用于区块链数据的持久化。
 *
 *
 *
 */
public abstract class BlockchainDatabase {


    //region 变量与构造函数
    //配置
    protected CoreConfiguration coreConfiguration;


    public BlockchainDatabase() {

    }
    //endregion

    //region 区块增加与删除
    /**
     * 将一个区块添加到区块链的尾部。
     */
    public abstract boolean addBlockDto(BlockDto blockDto) ;




    /**
     * 删除区块链的尾巴区块(最后一个区块)
     */
    public abstract void deleteTailBlock() ;

    /**
     * 删除区块高度大于等于@blockHeight@的区块
     */
    public abstract void deleteBlocks(long blockHeight) ;
    //endregion



    //region 校验区块、交易
    /**
     * 检测区块是否可以被添加到区块链上
     * 只有一种情况，区块可以被添加到区块链，即: 区块是区块链上的下一个区块。
     */
    public abstract boolean checkBlock(Block block) ;

    /**
     * 校验交易是否可以被添加进下一个区块之中。
     * 注意，如果是创世交易，则会跳过激励金额的校验，但除了不校验激励金额外，仍然会校验创世交易的方方面面，如交易大小、交易的结构等。
     * 为什么会跳过创世交易的激励金额的校验？
     * 因为激励金额的校验需要整个区块的信息，因此激励校验是区块层面的校验，而不是在交易层面校验激励金额。
     */
    public abstract boolean checkTransaction(Transaction transaction) ;
    //endregion


    //region 区块链查询
    /**
     * 查询区块链的长度
     */
    public abstract long queryBlockchainHeight() ;

    /**
     * 查询区块链中总的交易数量
     */
    public abstract long queryBlockchainTransactionHeight() ;

    //endregion



    //region 区块查询
    /**
     * 查询区块链上的最后一个区块
     */
    public abstract Block queryTailBlock() ;

    /**
     * 在区块链中根据区块高度查找区块
     */
    public abstract Block queryBlockByBlockHeight(long blockHeight) ;

    /**
     * 在区块链中根据区块哈希查找区块
     */
    public abstract Block queryBlockByBlockHash(String blockHash) ;
    //endregion



    //region 交易查询
    /**
     * 根据交易高度查询交易。交易高度从1开始。
     */
    public abstract Transaction queryTransactionByTransactionHeight(long transactionHeight) ;


    /**
     * 在区块链中根据交易哈希查找交易
     */
    public abstract Transaction queryTransactionByTransactionHash(String transactionHash) ;

    //endregion


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


}
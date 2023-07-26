package com.zl.blockchain.core.impl;

import com.zl.blockchain.core.*;
import com.zl.blockchain.core.model.Block;
import com.zl.blockchain.core.model.transaction.Transaction;
import com.zl.blockchain.core.tool.Model2DtoTool;
import com.zl.blockchain.crypto.model.Account;
import com.zl.blockchain.dto.BlockDto;
import com.zl.blockchain.dto.ToCaresponse;
import com.zl.blockchain.dto.TransactionDto;
import com.zl.blockchain.util.LogUtil;

import java.util.List;


/**
 * 区块链默认实现
 *
 */
public class BlockchainCoreImpl extends BlockchainCore {

    public BlockchainCoreImpl(CoreConfiguration coreConfiguration, BlockchainDatabase blockchainDatabase, UnconfirmedTransactionDatabase unconfirmedTransactionDatabase, Miner miner) {
        super(coreConfiguration,blockchainDatabase,unconfirmedTransactionDatabase,miner);
    }

    @Override
    public void start(Account account,ToCaresponse toCaresponse) {
        //启动矿工线程
        new Thread(
                ()->{
                    try {
                        //LogUtil.debug("矿工启动");
                        miner.start(account,toCaresponse);

                    } catch (Exception e) {
                        LogUtil.error("矿工在运行中发生异常，请检查修复异常！",e);
                    }
                }
        ).start();
    }

    @Override
    public long queryBlockchainHeight() {
        return blockchainDatabase.queryBlockchainHeight();
    }


    @Override
    public Transaction queryTransactionByTransactionHash(String transactionHash) {
        return blockchainDatabase.queryTransactionByTransactionHash(transactionHash);
    }

    @Override
    public Transaction queryTransactionByTransactionHeight(long transactionHeight) {
        return blockchainDatabase.queryTransactionByTransactionHeight(transactionHeight);
    }

    @Override
    public Block queryBlockByBlockHeight(long blockHeight) {
        return blockchainDatabase.queryBlockByBlockHeight(blockHeight);
    }

    @Override
    public Block queryBlockByBlockHash(String blockHash) {
        return blockchainDatabase.queryBlockByBlockHash(blockHash);
    }

    @Override
    public Block queryTailBlock() {
        return blockchainDatabase.queryTailBlock();
    }

    @Override
    public void deleteTailBlock() {
        blockchainDatabase.deleteTailBlock();
    }

    //添加区块dto
    @Override
    public boolean addBlockDto(BlockDto blockDto) {
        return blockchainDatabase.addBlockDto(blockDto);
    }

    //添加区块
    @Override
    public boolean addBlock(Block block) {
        BlockDto blockDto = Model2DtoTool.block2BlockDto(block);
        return addBlockDto(blockDto);
    }


    @Override
    public void deleteBlocks(long blockHeight) {
        blockchainDatabase.deleteBlocks(blockHeight);
    }


    @Override
    public void postTransaction(TransactionDto transactionDto) {
        unconfirmedTransactionDatabase.insertTransaction(transactionDto);
    }

    @Override
    public List<TransactionDto> queryUnconfirmedTransactions(long from, long size) {
        return unconfirmedTransactionDatabase.selectTransactions(from,size);
    }

    @Override
    public TransactionDto queryUnconfirmedTransactionByTransactionHash(String transactionHash) {
        return unconfirmedTransactionDatabase.selectTransactionByTransactionHash(transactionHash);
    }

    //region
    /**
     * block dto to block model
     */
    public Block blockDto2Block(BlockDto blockDto) {
        return blockchainDatabase.blockDto2Block(blockDto);
    }
    /**
     * transaction dto to transaction model
     */
    public Transaction transactionDto2Transaction(TransactionDto transactionDto) {
        return blockchainDatabase.transactionDto2Transaction(transactionDto);
    }
    //endregion
}
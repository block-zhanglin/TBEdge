package com.zl.blockchain.core;

import com.zl.blockchain.dto.BlockDto;

import java.io.Serializable;

/**
 * 挖矿共识
 * 区块链是一个分布式的数据库。任何节点都可以产生下一个区块，如果同时有多个节点都产生了下一个区块
 *
 */

public abstract class Consensus implements Serializable {

    /**
     * 校验区块是否满足共识
     */
    public abstract boolean checkConsensus(BlockchainDatabase blockchainDatabase, BlockDto blockDto) ;

}


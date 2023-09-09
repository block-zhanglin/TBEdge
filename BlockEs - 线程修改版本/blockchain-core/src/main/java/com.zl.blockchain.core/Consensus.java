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
     * 如果区块满足共识的要求，这个区块就可能(为什么说是可能呢？因为还要进一步校验区块的结构、大小等信息)是一个合格的区块
     * ，如果进一步校验通过的话，那么这个区块就被允许添加进区块链了。
     * 如果区块不满足共识的要求，那么这个区块一定是一个非法的区块，非法的区块一定不能被添加进区块链。
     */
    public abstract boolean checkConsensus(BlockchainDatabase blockchainDatabase, BlockDto blockDto) ;

}


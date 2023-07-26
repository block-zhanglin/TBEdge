package com.zl.blockchain.dto;


/**
 * 获取区块链高度
 */
public class GetBlockchainHeightResponse {

    private long blockchainHeight;

    public long getBlockchainHeight() {
        return blockchainHeight;
    }

    public void setBlockchainHeight(long blockchainHeight) {
        this.blockchainHeight = blockchainHeight;
    }
}

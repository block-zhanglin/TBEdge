package com.zl.blockchain.dto;

/**
 *提交区块链高度至节点
 */
public class PostBlockchainHeightRequest {

    private long blockchainHeight;

    public long getBlockchainHeight() {
        return blockchainHeight;
    }

    public void setBlockchainHeight(long blockchainHeight) {
        this.blockchainHeight = blockchainHeight;
    }
}

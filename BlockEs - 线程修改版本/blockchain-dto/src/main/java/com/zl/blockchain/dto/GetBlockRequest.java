package com.zl.blockchain.dto;

/**
 *根据区块高度获取区块
 */
public class GetBlockRequest {

    private long blockHeight;

    public long getBlockHeight() {
        return blockHeight;
    }

    public void setBlockHeight(long blockHeight) {
        this.blockHeight = blockHeight;
    }
}

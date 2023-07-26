package com.zl.blockchain.dto;

/**
 *提交区块至其他节点
 */
public class PostBlockRequest {

    private BlockDto block;

    public BlockDto getBlock() {
        return block;
    }

    public void setBlock(BlockDto block) {
        this.block = block;
    }
}

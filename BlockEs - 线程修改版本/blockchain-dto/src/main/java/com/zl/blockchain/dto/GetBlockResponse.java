package com.zl.blockchain.dto;

/**
 *get 根据区块高度查询区块
 */
public class GetBlockResponse {

    private BlockDto block;

    public BlockDto getBlock() {
        return block;
    }

    public void setBlock(BlockDto block) {
        this.block = block;
    }
}

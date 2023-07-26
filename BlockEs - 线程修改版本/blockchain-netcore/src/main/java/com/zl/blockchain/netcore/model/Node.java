package com.zl.blockchain.netcore.model;


/**
 * 节点model
 */
public class Node {
    private  String ip;//ip地址
    private long blockchainHeight;//区块高度


    //region get set

    /**
     *获取到节点的ip
     * @return
     */
    public String getIp() {
        return ip;
    }

    /**
     * 设置节点的ip
     * @param ip
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * 获取区块链的高度
     * @return
     */
    public long getBlockchainHeight() {
        return blockchainHeight;
    }

    /**
     * 设置区块链的高度
     * @param blockchainHeight
     */
    public void setBlockchainHeight(long blockchainHeight) {
        this.blockchainHeight = blockchainHeight;
    }

    //endregion
}

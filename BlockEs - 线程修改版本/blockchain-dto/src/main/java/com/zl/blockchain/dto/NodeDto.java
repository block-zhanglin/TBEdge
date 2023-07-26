package com.zl.blockchain.dto;

/**
 *节点 dto
 */
public class NodeDto {

    private String ip;

    public NodeDto() {
    }
    public NodeDto(String ip) {
        this.ip = ip;
    }

    //region get set

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    //endregion
}

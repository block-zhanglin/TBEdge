package com.zl.blockchain.dto;

import java.util.List;

/**
 *get获取节点 response
 */
public class GetNodesResponse {

    private List<NodeDto> nodes;

    public List<NodeDto> getNodes() {
        return nodes;
    }

    public void setNodes(List<NodeDto> nodes) {
        this.nodes = nodes;
    }
}

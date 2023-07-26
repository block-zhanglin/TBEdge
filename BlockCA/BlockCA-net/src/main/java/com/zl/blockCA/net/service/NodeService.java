package com.zl.blockCA.net.service;

import com.zl.blockCA.net.model.Node;

import java.util.List;

/**
 * 节点服务
 * 供外部使用，等于对dao、model进行包装
 *
 */
public interface NodeService {

    /**
     * 查询node
     */
    Node queryNode(String ip);

    /**
     * 获取所有节点
     */
    List<Node> queryAllNodes();

    /**
     * 删除节点
     */
    void deleteNode(String ip);

    /**
     * 新增节点
     */
    void addNode(Node node);

    /**
     * 更新节点
     */
    void updateNode(Node node);
}

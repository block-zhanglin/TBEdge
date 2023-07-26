package com.zl.blockCA.net.dao;

import com.zl.blockCA.net.model.Node;

import java.util.List;

public interface NodeDao {

    /**
     * 查询固定ip节点
     */
    Node queryNode(String ip);

    /**
     * 查询所有节点
     */
    List<Node> queryAllNodes();


    /**
     * 添加节点
     */
    void addNode(Node node);

    /**
     * 更新节点信息
     */
    void updateNode(Node node);

    /**
     * 删除节点
     */
    void deleteNode(String ip);

}

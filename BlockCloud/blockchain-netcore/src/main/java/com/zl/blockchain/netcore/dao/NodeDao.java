package com.zl.blockchain.netcore.dao;

import com.zl.blockchain.netcore.model.Node;

import java.util.List;

/**
 * 节点dao
 * 管理（增删改查）已知的网络节点。
 *（dao层主要是封装对于实体类的数据库的访问，只是增删查看，不增加逻辑）
 */
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

    /**
     * 增删查改ES层节点
     */

    Node queryESNode(String ip);

    List<Node> queryAllESNodes();

    void addESNode(Node node);

    void updateESNode(Node node);

    void deleteESNode(String ip);


}

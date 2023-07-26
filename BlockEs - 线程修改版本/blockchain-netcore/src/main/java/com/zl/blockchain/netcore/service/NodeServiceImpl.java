package com.zl.blockchain.netcore.service;

import com.zl.blockchain.netcore.dao.NodeDao;
import com.zl.blockchain.netcore.model.Node;

import java.util.List;

/**
 * 节点服务实现类
 *
 */
public class NodeServiceImpl implements NodeService {

    private NodeDao nodeDao; //创建一个nodeDao

    public NodeServiceImpl(NodeDao nodeDao) { //构造函数
        this.nodeDao = nodeDao;
    }

    /**
     * 删除节点
     * @param ip
     */
    @Override
    public void deleteNode(String ip){
        nodeDao.deleteNode(ip); //调用nodedao的delete
    }

    /**
     * 查询所有的节点
     * @return
     */
    @Override
    public List<Node> queryAllNodes(){
        List<Node> Nodes = nodeDao.queryAllNodes(); //调用nodedap的queryAllNodes（）
        return Nodes; //返回node列表
    }

    /**
     * 添加node
     * @param node ndoe类
     */
    @Override
    public void addNode(Node node){
        if(nodeDao.queryNode(node.getIp()) != null){ //如果查询到节点已存在，则不添加
            return;
        }
        nodeDao.addNode(node); //nodedap添加Node
    }

    /**
     * 更新node
     * @param node node类
     */
    @Override
    public void updateNode(Node node){
        Node Node = nodeDao.queryNode(node.getIp());
        if(Node == null){ //如果查询到节点为空，则不更新
            return;
        }
        nodeDao.updateNode(node);
    }

    /**
     * 通过ip查询node
     * @param ip
     * @return
     */
    @Override
    public Node queryNode(String ip){
        Node Node = nodeDao.queryNode(ip);
        if(Node == null){
            return null;
        }
        return Node;
    }

}

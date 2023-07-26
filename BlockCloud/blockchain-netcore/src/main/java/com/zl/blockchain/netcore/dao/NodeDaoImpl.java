package com.zl.blockchain.netcore.dao;


import com.zl.blockchain.netcore.configuration.NetcoreConfiguration;
import com.zl.blockchain.netcore.model.Node;
import com.zl.blockchain.util.ByteUtil;
import com.zl.blockchain.util.EncodeDecodeTool;
import com.zl.blockchain.util.FileUtil;
import com.zl.blockchain.util.KvDbUtil;

import java.util.ArrayList;
import java.util.List;

/**
 *节点dao实现类=====对数据进行CURD操作的代码
 *
 */
public class NodeDaoImpl implements NodeDao {

    private static final String NODE_DATABASE_NAME = "NodeDatabase"; //云中心节点数据库名字
    private static final String ESNODE_DATABASE_NAME="EsNodeDatabase"; //Es层节点数据库名字
    private NetcoreConfiguration netcoreConfiguration; //节点配置类

    /**
     * 节点dto构造函数
     *
     */
    public NodeDaoImpl(NetcoreConfiguration netcoreConfiguration) {
        this.netcoreConfiguration=netcoreConfiguration;
    }

    /**
     * 通过ip查询节点
     * @param ip
     * @return
     */
    @Override
    public Node queryNode(String ip){
        byte[] bytesNode = KvDbUtil.get(getNodeDatabasePath(),getKeyByIp(ip)); //
        if(bytesNode == null){
            return null;
        }
        return EncodeDecodeTool.decode(bytesNode,Node.class); //解码字节数组到对象
    }

    /**
     *查询所有的节点
     * @return
     */
    @Override
    public List<Node> queryAllNodes(){
        List<Node> Nodes = new ArrayList<>(); //创建Node列表
        //获取所有
        List<byte[]> bytesNodes = KvDbUtil.gets(getNodeDatabasePath(),1,100000000);
        if(bytesNodes != null){
            for(byte[] bytesNode:bytesNodes){
                Node Node = EncodeDecodeTool.decode(bytesNode,Node.class);
                Nodes.add(Node);
            }
        }
        return Nodes;
    }


    /**
     * 添加节点
     * @param node 节点
     */
    @Override
    public void addNode(Node node){
        KvDbUtil.put(getNodeDatabasePath(),getKeyByNode(node), EncodeDecodeTool.encode(node)); //向nodedatabase数据库中利用kv操作添加节点<key,node>（node对象编码成字节数组）
    }

    /**
     * 更新节点
     * @param node 节点
     */
    @Override
    public void updateNode(Node node){
        KvDbUtil.put(getNodeDatabasePath(),getKeyByNode(node),EncodeDecodeTool.encode(node)); //向nodedatabase数据库中利用kv操作更新节点<key,node>
    }

    /**
     * 删除节点
     * @param ip ip地址
     */
    @Override
    public void deleteNode(String ip){
        KvDbUtil.delete(getNodeDatabasePath(),getKeyByIp(ip)); //向nodedatabase中利用kv操作删除<key,node>
    }

    /**
     * begain
     * @param ip
     * @return
     */
    @Override
    public Node queryESNode(String ip) {
        byte[] bytesNode = KvDbUtil.get(getEsNodeDatabasePath(),getKeyByIp(ip)); //
        if(bytesNode == null){
            return null;
        }
        return EncodeDecodeTool.decode(bytesNode,Node.class); //解码字节数组到对象
    }

    @Override
    public List<Node> queryAllESNodes() {
        List<Node> Nodes = new ArrayList<>(); //创建Node列表
        //获取所有
        List<byte[]> bytesNodes = KvDbUtil.gets(getEsNodeDatabasePath(),1,100000000);
        if(bytesNodes != null){
            for(byte[] bytesNode:bytesNodes){
                Node Node = EncodeDecodeTool.decode(bytesNode,Node.class);
                Nodes.add(Node);
            }
        }
        return Nodes;
    }

    @Override
    public void addESNode(Node node) {
        KvDbUtil.put(getEsNodeDatabasePath(),getKeyByNode(node), EncodeDecodeTool.encode(node)); //向nodedatabase数据库中利用kv操作添加节点<key,node>（node对象编码成字节数组）
    }

    @Override
    public void updateESNode(Node node) {
        KvDbUtil.put(getEsNodeDatabasePath(),getKeyByNode(node),EncodeDecodeTool.encode(node)); //向nodedatabase数据库中利用kv操作更新节点<key,node>
    }

    @Override
    public void deleteESNode(String ip) {
        KvDbUtil.delete(getEsNodeDatabasePath(),getKeyByIp(ip)); //向nodedatabase中利用kv操作删除<key,node>
    }


    /**
     * 获取netcore总目录下的ESnode数据库
     * @return
     */
    private String getEsNodeDatabasePath(){
        return FileUtil.newPath(netcoreConfiguration.getNetCorePath(), ESNODE_DATABASE_NAME); //在netcore的目录下创建node数据库地址
    }


    /**
     * 获取netcore总目录下的node数据库
     * @return
     */
    private String getNodeDatabasePath(){
        return FileUtil.newPath(netcoreConfiguration.getNetCorePath(), NODE_DATABASE_NAME); //在netcore的目录下创建node数据库地址
    }



    /**
     * 通过节点获取key
     * @param node 节点
     * @return
     */
    private byte[] getKeyByNode(Node node){
        return getKeyByIp(node.getIp());
    }

    /**
     * 通过ip获取key
     * @param ip ip地址
     * @return
     */
    private byte[] getKeyByIp(String ip){
        return ByteUtil.stringToUtf8Bytes(ip); //将ip由string类型转换为byte数组
    }
}

package com.zl.blockCA.net.dao;


import com.zl.blockCA.net.*;
import com.zl.blockCA.net.configuration.NetcoreConfiguration;
import com.zl.blockCA.net.model.Node;
import util.*;


import java.util.ArrayList;
import java.util.List;

/**
 *节点dao实现类=====对数据进行CURD操作的代码
 *
 */
public class NodeDaoImpl implements NodeDao {

    private static final String NODE_DATABASE_NAME = "NodeDatabase"; //节点数据库名字


    /**
     * 节点dto构造函数
     *
     */
    public NodeDaoImpl() {
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
     * 获取netcore总目录下的node数据库
     * @return
     */
    private String getNodeDatabasePath(){
        return FileUtil.newPath(ResourcePathTool.getDataRootPath(), NODE_DATABASE_NAME); //在netcore的目录下创建node数据库地址
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

package com.zl.blockchain.netcore;

import com.zl.blockchain.dto.API;
import com.zl.blockchain.dto.PingRequest;
import com.zl.blockchain.netcore.client.HttpClient;
import com.zl.blockchain.netcore.client.HttpClientImpl;
import com.zl.blockchain.netcore.configuration.NetcoreConfiguration;
import com.zl.blockchain.netcore.model.Node;
import com.zl.blockchain.netcore.service.NodeService;
import com.zl.blockchain.util.LogUtil;
import com.zl.blockchain.util.ThreadUtil;

import java.util.List;


/**
 * 节点广播器：在区块链网络中，广播自身这个节点。
 *
 * 为什么要广播自身这个节点？
 * 例如，系统启动后，广播自身，让区块链网络的其它节点知道自己已经上线了。
 * 再例如，由于未知原因，部分节点与自己中断了联系，自己已经不在它们的节点列表中了，
 * 而自己的列表中有它们，这时候可以广播一下自己，这些中断联系的节点将会恢复与自己联系。
 *
 *
 */
public class NodeBroadcaster {


    private NetcoreConfiguration netCoreConfiguration;
    private NodeService nodeService;

    public NodeBroadcaster(NetcoreConfiguration netCoreConfiguration, NodeService nodeService) {
        this.netCoreConfiguration = netCoreConfiguration;
        this.nodeService = nodeService;
    }

    public void start() {
        try {
            while (true){
                broadcastNode();
                ThreadUtil.millisecondSleep(netCoreConfiguration.getNodeBroadcastTimeInterval());
            }
        } catch (Exception e) {
            LogUtil.error("在区块链网络中广播自己出现异常",e);
        }
    }

    /**
     * 广播自己
     */
    private void broadcastNode() {

        List<Node> nodes = nodeService.queryAllNodes();
        if(nodes == null || nodes.size()==0){
            return;
        }

        for(Node node:nodes){

            //不广播给自身节点
            if(node.getIp().equals(API.Esip)){
                continue;
            }

            new Thread(()->nb(node)).start();
//            HttpClient nodeClient = new HttpClientImpl(node.getIp());
//            PingRequest pingRequest = new PingRequest();
//            nodeClient.pingNode(pingRequest);

        }
    }



    private  void nb(Node node){
        try{
            HttpClient nodeClient = new HttpClientImpl(node.getIp());
            PingRequest pingRequest = new PingRequest();
            nodeClient.pingNode(pingRequest);
        }catch (Exception e){
            LogUtil.error("节点广播错误",e);
        }

    }


}

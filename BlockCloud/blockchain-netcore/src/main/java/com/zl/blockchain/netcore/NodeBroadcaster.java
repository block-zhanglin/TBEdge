package com.zl.blockchain.netcore;

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
 *  节点广播器：在区块链网络中，广播自身这个节点。
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
            HttpClient nodeClient = new HttpClientImpl(node.getIp());
            PingRequest pingRequest = new PingRequest();
            nodeClient.pingNode(pingRequest,0);
        }

    }

}

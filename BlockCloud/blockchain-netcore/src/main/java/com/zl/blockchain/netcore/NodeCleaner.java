package com.zl.blockchain.netcore;

import com.zl.blockchain.dto.*;
import com.zl.blockchain.netcore.client.HttpClient;
import com.zl.blockchain.netcore.client.HttpClientImpl;
import com.zl.blockchain.netcore.configuration.NetcoreConfiguration;
import com.zl.blockchain.netcore.model.Node;
import com.zl.blockchain.netcore.service.NodeService;
import com.zl.blockchain.util.LogUtil;
import com.zl.blockchain.util.ThreadUtil;

import java.util.List;


/**
 *  节点清理器：清除死亡节点。
 * 所谓死亡节点就是无法联系的节点。
 *
 *
 */
public class NodeCleaner {

    private NetcoreConfiguration netCoreConfiguration;
    private NodeService nodeService;

    public NodeCleaner(NetcoreConfiguration netCoreConfiguration, NodeService nodeService) {
        this.netCoreConfiguration = netCoreConfiguration;
        this.nodeService = nodeService;
    }

    public void start() {
        try {
            while (true){
                cleanDeadNodes();
                ThreadUtil.millisecondSleep(netCoreConfiguration.getNodeCleanTimeInterval());
            }
        } catch (Exception e) {
            LogUtil.error("清理死亡节点出现异常",e);
        }
    }

    private void cleanDeadNodes() {
        List<Node> nodes = nodeService.queryAllNodes();
        if(nodes == null || nodes.size()==0){
            return;
        }

        for(Node node:nodes){
            HttpClient nodeClient = new HttpClientImpl(node.getIp());
            PingRequest pingRequest = new PingRequest();
            PingResponse pingResponse = nodeClient.pingNode(pingRequest,0);
            if(pingResponse == null){
                nodeService.deleteNode(node.getIp());
                LogUtil.debug("节点清理器发现死亡节点["+node.getIp()+"]，已在云节点数据库中将该节点删除了。");
            }
        }


        /**
         * ES层
         */
        List<Node> esnodes = nodeService.queryAllESNodes();
        if(esnodes == null || esnodes.size()==0){
            return;
        }
        for(Node node:esnodes){
            HttpClient nodeClient = new HttpClientImpl(node.getIp());
            PingRequest pingRequest = new PingRequest();
            PingResponse pingResponse = nodeClient.pingNode(pingRequest,1);
            if(pingResponse == null){
                nodeService.deleteNode(node.getIp());
                LogUtil.debug("节点清理器发现死亡节点["+node.getIp()+"]，已在Es节点数据库中将该节点删除了。");
            }
        }


    }

}

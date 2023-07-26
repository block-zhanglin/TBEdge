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
 * 节点搜索器：在区块链网络中搜寻新的节点。
 *
 */
public class NodeSearcher {

    private NetcoreConfiguration netCoreConfiguration;
    private NodeService nodeService;

    public NodeSearcher(NetcoreConfiguration netCoreConfiguration, NodeService nodeService) {
        this.netCoreConfiguration = netCoreConfiguration;
        this.nodeService = nodeService;
    }

    public void start() {
        try {
            while (true){
                searchNodes();
                ThreadUtil.millisecondSleep(netCoreConfiguration.getNodeSearchTimeInterval());
            }
        } catch (Exception e) {
            LogUtil.error("在区块链网络中搜索新的节点出现异常",e);
        }
    }

    private void searchNodes() {

        /**
         * 云中心
         */
        if(!netCoreConfiguration.isAutoSearchNode()){
            return;
        }
        List<Node> nodes = nodeService.queryAllNodes();
        if(nodes!=null && nodes.size()!=0){
            for(Node node:nodes){
                if(!netCoreConfiguration.isAutoSearchNode()){
                    return;
                }
                HttpClient nodeClient = new HttpClientImpl(node.getIp());
                GetNodesRequest getNodesRequest = new GetNodesRequest();
                GetNodesResponse getNodesResponse = nodeClient.getNodes(getNodesRequest,0);
                handleGetNodesResponse(getNodesResponse);
            }
        }

        /**
         * Es层
         */
        List<Node> Esnodes=nodeService.queryAllESNodes();
        if(nodes == null || nodes.size()==0){
            return;
        }
        for(Node node:Esnodes){
            System.out.println("Es层节点"+node.getIp());
            if(!netCoreConfiguration.isAutoSearchNode()){
                return;
            }
            HttpClient nodeClient = new HttpClientImpl(node.getIp());
            GetNodesRequest getNodesRequest = new GetNodesRequest();
            GetNodesResponse getNodesResponse = nodeClient.getNodes(getNodesRequest,1);
            System.out.println("Es层节点"+node.getIp()+"包含的节点数"+getNodesResponse.getNodes().size());
            handleESGetNodesResponse(getNodesResponse);
        }

    }

    private void handleGetNodesResponse(GetNodesResponse getNodesResponse) {
        if(getNodesResponse == null){
            return;
        }
        List<NodeDto> nodes = getNodesResponse.getNodes();
        if(nodes == null || nodes.size()==0){
            return;
        }
        for(NodeDto node:nodes){
            if(!netCoreConfiguration.isAutoSearchNode()){
                return;
            }
            HttpClient nodeClient = new HttpClientImpl(node.getIp());
            PingRequest pingRequest = new PingRequest();
            PingResponse pingResponse = nodeClient.pingNode(pingRequest,0);
            if(pingResponse != null){
                Node n = new Node();
                n.setIp(node.getIp());
                n.setBlockchainHeight(0);
                nodeService.addNode(n);
                LogUtil.debug("自动机制发现节点["+node.getIp()+"]，已在云节点数据库中添加了该节点。");
            }
        }
    }

    private void handleESGetNodesResponse(GetNodesResponse getNodesResponse) {
        if(getNodesResponse == null){
            return;
        }
        List<NodeDto> nodes = getNodesResponse.getNodes();
        if(nodes == null || nodes.size()==0){
            return;
        }
        for(NodeDto node:nodes){
            if(!netCoreConfiguration.isAutoSearchNode()){
                return;
            }
            HttpClient nodeClient = new HttpClientImpl(node.getIp());
            PingRequest pingRequest = new PingRequest();
            PingResponse pingResponse = nodeClient.pingNode(pingRequest,1);
            if(pingResponse != null){
                Node n = new Node();
                n.setIp(node.getIp());
                n.setBlockchainHeight(0);
                nodeService.addESNode(n);
                LogUtil.debug("自动机制发现节点["+node.getIp()+"]，已在ES节点数据库中添加了该节点。");
            }
        }
    }

}

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
                LogUtil.debug("节点数量"+nodeService.queryAllNodes().size());
                ThreadUtil.millisecondSleep(netCoreConfiguration.getNodeSearchTimeInterval());
            }
        } catch (Exception e) {
            LogUtil.error("在区块链网络中搜索新的节点出现异常",e);
        }
    }

    private void searchNodes() {

        if(!netCoreConfiguration.isAutoSearchNode()){
            return;
        }
        List<Node> nodes = nodeService.queryAllNodes();
        if(nodes == null || nodes.size()==0){
            return;
        }

        for(Node node:nodes){
            //不寻找自身节点
            if(node.getIp().equals(API.Esip)){
                continue;
            }

            if(!netCoreConfiguration.isAutoSearchNode()){
                return;
            }
              new Thread(()->ns(node)).start();
//            HttpClient nodeClient = new HttpClientImpl(node.getIp());
//            GetNodesRequest getNodesRequest = new GetNodesRequest();
//            GetNodesResponse getNodesResponse = nodeClient.getNodes(getNodesRequest);
//            handleGetNodesResponse(getNodesResponse);
        }

    }

    private  void ns(Node node){

        try{
            HttpClient nodeClient = new HttpClientImpl(node.getIp());
            GetNodesRequest getNodesRequest = new GetNodesRequest();
            GetNodesResponse getNodesResponse = nodeClient.getNodes(getNodesRequest);
            handleGetNodesResponse(getNodesResponse);
        }catch (Exception e){
            LogUtil.error("节点搜索错误",e);
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
            PingResponse pingResponse = nodeClient.pingNode(pingRequest);
            if(pingResponse != null){
                Node n = new Node();
                n.setIp(node.getIp());
                n.setBlockchainHeight(0);
                nodeService.addNode(n);
//                LogUtil.debug("自动机制发现节点["+node.getIp()+"]，已在节点数据库中添加了该节点。");
            }
        }
    }

}

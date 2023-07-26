package com.zl.blockchain.netcore;

import com.zl.blockchain.dto.API;
import com.zl.blockchain.dto.GetBlockchainHeightRequest;
import com.zl.blockchain.dto.GetBlockchainHeightResponse;
import com.zl.blockchain.netcore.client.HttpClient;
import com.zl.blockchain.netcore.client.HttpClientImpl;
import com.zl.blockchain.netcore.configuration.NetcoreConfiguration;
import com.zl.blockchain.netcore.model.Node;
import com.zl.blockchain.netcore.service.NodeService;
import com.zl.blockchain.util.LogUtil;
import com.zl.blockchain.util.ThreadUtil;

import java.util.List;


/**
 *  区块链高度搜索器
 * 为什么要搜索节点的高度？
 * 在我的设计之中，本节点已知的所有节点的信息(ip、区块链高度等)都持久化在本地，区块链高度搜索器定时的更新已知节点的高度。
 * 区块搜寻器BlockSearcher定时的用本地区块链高度与已知节点的区块链高度(存储在本地的高度)作比较
 * ，若本地区块链高度较小，本地区块链则去同步远程节点的区块。
 *
 *
 */
public class BlockchainHeightSearcher {

    private NetcoreConfiguration netCoreConfiguration;
    private NodeService nodeService;

    public BlockchainHeightSearcher(NetcoreConfiguration netCoreConfiguration, NodeService nodeService) {
        this.netCoreConfiguration = netCoreConfiguration;
        this.nodeService = nodeService;
    }

    public void start() {
        try {
            while (true){
                searchBlockchainHeight();
                ThreadUtil.millisecondSleep(netCoreConfiguration.getBlockchainHeightSearchTimeInterval());
            }
        } catch (Exception e) {
            LogUtil.error("在区块链网络中搜索节点的高度异常",e);
        }
    }

    /**
     * 收索节点高度
     */
    private void searchBlockchainHeight() {
        List<Node> nodes = nodeService.queryAllNodes(); //查询到本地的所有节点
        if(nodes == null || nodes.size()==0){
            return;
        }

        for(Node node:nodes){
            if(node.getIp().equals(API.Esip)){
                continue;
            }

            new Thread(()->bh(node)).start();

//            HttpClient nodeClient = new HttpClientImpl(node.getIp()); //对于每个node构建nodecclient
//            GetBlockchainHeightRequest getBlockchainHeightRequest = new GetBlockchainHeightRequest();
//            GetBlockchainHeightResponse getBlockchainHeightResponse = nodeClient.getBlockchainHeight(getBlockchainHeightRequest);  //nodeclient获取节点的高度
//            if(getBlockchainHeightResponse != null){
//                node.setBlockchainHeight(getBlockchainHeightResponse.getBlockchainHeight());
//                nodeService.updateNode(node);
////                System.out.println("节点高度搜索完成"+nodeService.queryNode(node.getIp()).getBlockchainHeight());
//            }


        }
    }


    private  void  bh(Node node){
        try{
            HttpClient nodeClient = new HttpClientImpl(node.getIp()); //对于每个node构建nodecclient
            GetBlockchainHeightRequest getBlockchainHeightRequest = new GetBlockchainHeightRequest();
            GetBlockchainHeightResponse getBlockchainHeightResponse = nodeClient.getBlockchainHeight(getBlockchainHeightRequest);  //nodeclient获取节点的高度
            if(getBlockchainHeightResponse != null){
                node.setBlockchainHeight(getBlockchainHeightResponse.getBlockchainHeight());
                nodeService.updateNode(node);
//                System.out.println("节点高度搜索完成"+nodeService.queryNode(node.getIp()).getBlockchainHeight());
            }


        }catch (Exception e){
            LogUtil.error("获取区块高度错误",e);
        }
    }


}

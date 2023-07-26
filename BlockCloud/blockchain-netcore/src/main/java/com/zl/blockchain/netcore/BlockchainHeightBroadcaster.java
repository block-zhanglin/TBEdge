package com.zl.blockchain.netcore;

import com.zl.blockchain.core.BlockchainCore;
import com.zl.blockchain.dto.PostBlockchainHeightRequest;
import com.zl.blockchain.netcore.client.HttpClient;
import com.zl.blockchain.netcore.client.HttpClientImpl;
import com.zl.blockchain.netcore.configuration.NetcoreConfiguration;
import com.zl.blockchain.netcore.model.Node;
import com.zl.blockchain.netcore.service.NodeService;
import com.zl.blockchain.util.LogUtil;
import com.zl.blockchain.util.ThreadUtil;

import java.util.List;

/**
 *  区块链高度广播器：将区块链高度传播至全网。
 * 如果本地区块链的高度高于全网，那么就应该(通过在区块链网络中广播自己的高度的方式)通知其它节点
 * ，好让其它节点知道可以来同步自己的区块数据了。
 * 至于其它节点什么时候来同步自己的区块，应该由其它节点来决定。
 *
 * 顺便说一句，矿工把区块放入区块链后，当区块广播器广播区块链高度时，
 * 也就相当于通知了其它节点"自己挖出了新的区块"这件事。
 *


 */
public class BlockchainHeightBroadcaster {

    private NetcoreConfiguration netCoreConfiguration;
    private BlockchainCore blockchainCore;
    private NodeService nodeService;

    public BlockchainHeightBroadcaster(NetcoreConfiguration netCoreConfiguration, BlockchainCore blockchainCore, NodeService nodeService) {
        this.netCoreConfiguration = netCoreConfiguration;
        this.blockchainCore = blockchainCore;
        this.nodeService = nodeService;
    }

    public void start() {
        try {
            while (true){
                broadcastBlockchainHeight();
                ThreadUtil.millisecondSleep(netCoreConfiguration.getBlockchainHeightBroadcastTimeInterval());
            }
        } catch (Exception e) {
            LogUtil.error("在区块链网络中广播自身区块链高度异常",e);
        }
    }

    /**
     * 广播区块链高度
     */
    private void broadcastBlockchainHeight() {
        List<Node> nodes = nodeService.queryAllNodes();  //查询到所有的云中心节点
        if(nodes == null || nodes.size()==0){
            return;
        }

        for(Node node:nodes){
            long blockchainHeight = blockchainCore.queryBlockchainHeight();
            if(blockchainHeight <= node.getBlockchainHeight()){//如果本地的区块链高度低于其他节点高度
                continue;
            }
            /**
             * 如果本地的区块链高度高于其他节点高度
             */
            HttpClient nodeClient = new HttpClientImpl(node.getIp());
            PostBlockchainHeightRequest postBlockchainHeightRequest = new PostBlockchainHeightRequest();
            postBlockchainHeightRequest.setBlockchainHeight(blockchainHeight);
            nodeClient.postBlockchainHeight(postBlockchainHeightRequest); //nodeclient 提交区块链高度
        }
    }

}

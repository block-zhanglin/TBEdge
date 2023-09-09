package com.zl.blockchain.netcore;

import com.zl.blockchain.core.BlockchainCore;
import com.zl.blockchain.core.model.Block;
import com.zl.blockchain.core.tool.Model2DtoTool;
import com.zl.blockchain.dto.API;
import com.zl.blockchain.dto.BlockDto;
import com.zl.blockchain.dto.PostBlockRequest;
import com.zl.blockchain.netcore.client.HttpClient;
import com.zl.blockchain.netcore.client.HttpClientImpl;
import com.zl.blockchain.netcore.configuration.NetcoreConfiguration;
import com.zl.blockchain.netcore.model.Node;
import com.zl.blockchain.netcore.service.NodeService;
import com.zl.blockchain.util.LogUtil;
import com.zl.blockchain.util.ThreadUtil;

import java.util.ArrayList;
import java.util.List;

/**
 *   区块广播器：主动将自己最新的区块广播至全网。（防止其他节点由于这样那样的原因没来同步）
 *
 */
public class BlockBroadcaster {

    private NetcoreConfiguration netCoreConfiguration; //网络核心配置类
    private BlockchainCore blockchainCore; //区块链核心（单机）
    private NodeService nodeService; //节点服务类

    public BlockBroadcaster(NetcoreConfiguration netCoreConfiguration, BlockchainCore blockchainCore, NodeService nodeService) {
        this.netCoreConfiguration = netCoreConfiguration;
        this.blockchainCore = blockchainCore;
        this.nodeService = nodeService;
    }

    public void start() {
        try {
            while (true){
                broadcastBlock(); //广播区块
                ThreadUtil.millisecondSleep(netCoreConfiguration.getBlockBroadcastTimeInterval()); //线程休息广播区块休息时间
            }
        } catch (Exception e) {
            LogUtil.error("在区块链网络中广播自己的区块出现异常",e);
        }
    }

    /**
     * 广播区块
     * 云中心之间可以广播区块
     */
    private void broadcastBlock() {

        List<Node> nodes = nodeService.queryAllNodes();
        if(nodes == null || nodes.size()==0){
            return;
        }

        for(Node node:nodes){
            Block block = blockchainCore.queryTailBlock(); //查询当前最后的区块
            if(block == null){
                return;
            }
            if(block.getHeight() <= node.getBlockchainHeight()){ //如果当前区块的高度小于该云中心的区块链高度，则无法向他提交区块
                continue;
            }
            /**
             * 当前区块的高度大于节点的区块链高度时
             */
            BlockDto blockDto = Model2DtoTool.block2BlockDto(block); //获取blockdto
            HttpClient nodeClient = new HttpClientImpl(node.getIp()); //获取客户端
            PostBlockRequest postBlockRequest = new PostBlockRequest(); //构造广播区块request
            postBlockRequest.setBlock(blockDto);
            nodeClient.postBlock(postBlockRequest);
        }

    }
} 

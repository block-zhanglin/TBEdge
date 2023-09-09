package com.zl.blockchain.netcore;

import com.zl.blockchain.core.BlockchainCore;
import com.zl.blockchain.dto.API;
import com.zl.blockchain.dto.BlockDto;
import com.zl.blockchain.dto.Committee;
import com.zl.blockchain.dto.PostBlockRequest;
import com.zl.blockchain.netcore.client.HttpClient;
import com.zl.blockchain.netcore.client.HttpClientImpl;
import com.zl.blockchain.netcore.configuration.NetcoreConfiguration;
import com.zl.blockchain.netcore.model.Node;
import com.zl.blockchain.netcore.service.NodeService;
import com.zl.blockchain.util.LogUtil;
import com.zl.blockchain.util.ThreadUtil;

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
                if(API.Master&&API.NewBlock){
                    broadcastBlock(); //广播区块
                   // API.NewBlock=false;
                }
                ThreadUtil.millisecondSleep(netCoreConfiguration.getBlockBroadcastTimeInterval()); //线程休息广播区块休息时间
            }
        } catch (Exception e) {
            LogUtil.error("在区块链网络中广播自己的区块出现异常",e);
        }
    }

    /**
     * 广播区块
     */
    private void broadcastBlock() {
        //获取当前的委员会
        Committee committee=API.committee;
        if(committee==null){
            return;
        }
        List<Node> nodes = nodeService.queryAllNodes();
        if(nodes == null || nodes.size()==0){
            return;
        }

        for(Node node:nodes){

            //不广播自身节点
            if(node.getIp().equals(API.Esip)){
                continue;
            }

            BlockDto blockDto=API.blockDto;
            if(blockDto==null){
                return;
            }

            new Thread(()->bb(node,blockDto)).start();

//            /**
//             * 当前区块的高度大于节点的区块链高度时
//             */
//            HttpClient nodeClient = new HttpClientImpl(node.getIp()); //获取客户端
//            PostBlockRequest postBlockRequest = new PostBlockRequest(); //构造广播区块request
//            postBlockRequest.setBlock(blockDto);
//            nodeClient.postBlock(postBlockRequest);

        }
        return;
    }

    private void bb(Node node,BlockDto blockDto){

        try{
            HttpClient nodeClient = new HttpClientImpl(node.getIp()); //获取客户端
            PostBlockRequest postBlockRequest = new PostBlockRequest(); //构造广播区块request
            postBlockRequest.setBlock(blockDto);
            nodeClient.postBlock(postBlockRequest);
        }catch (Exception e){
            LogUtil.error("发送区块错误",e);
        }

    }



    //如果本身是委员会中的节点且收到了来自领导者的区块
    public void committeeBroadcastBlock(BlockDto blockDto){

        List<Node>nodes=nodeService.queryAllNodes();

        for(Node node:nodes){

            //不广播自身节点
            if(node.getIp().equals(API.Esip)){
                continue;
            }

             new  Thread(()->cb(blockDto,node)).start();

//            HttpClient nodeClient = new HttpClientImpl(node.getIp()); //获取客户端
//            PostBlockRequest postBlockRequest = new PostBlockRequest(); //构造广播区块request
//            postBlockRequest.setBlock(blockDto);
//            nodeClient.postBlock(postBlockRequest);
        }

    }


    private void cb(BlockDto blockDto,Node node){
        try{
            HttpClient nodeClient = new HttpClientImpl(node.getIp()); //获取客户端
            PostBlockRequest postBlockRequest = new PostBlockRequest(); //构造广播区块request
            postBlockRequest.setBlock(blockDto);
            nodeClient.postBlock(postBlockRequest);
        }catch (Exception e){
            LogUtil.error("委员会收到区块后，广播错误",e);
        }
    }
}

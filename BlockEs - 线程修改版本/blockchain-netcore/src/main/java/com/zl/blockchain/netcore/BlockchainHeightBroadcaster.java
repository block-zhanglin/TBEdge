package com.zl.blockchain.netcore;

import com.zl.blockchain.core.BlockchainCore;
import com.zl.blockchain.crypto.model.Account;
import com.zl.blockchain.dto.API;
import com.zl.blockchain.dto.PostBlockchainHeightRequest;
import com.zl.blockchain.dto.ToCaresponse;
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
 *
 */
public class BlockchainHeightBroadcaster {

    private NetcoreConfiguration netCoreConfiguration;
    private BlockchainCore blockchainCore;
    private NodeService nodeService;
    private Account account;
    private ToCaresponse toCaresponse;

    public BlockchainHeightBroadcaster(NetcoreConfiguration netCoreConfiguration, BlockchainCore blockchainCore, NodeService nodeService,Account account,ToCaresponse toCaresponse) {
        this.netCoreConfiguration = netCoreConfiguration;
        this.blockchainCore = blockchainCore;
        this.nodeService = nodeService;
        this.account=account;
        this.toCaresponse=toCaresponse;
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

        //如果本地的区块链高度已经高于最大容纳高度
//        if(blockchainCore.queryBlockchainHeight()> BlockSetting.MaxBlock){
//            System.out.println("提出卸载");
//            HttpClient httpClient = new HttpClientImpl();
//            Esfullrequest esfullrequest=new Esfullrequest();
//            esfullrequest.setTimestamp(TimeUtil.millisecondTimestamp());
//            esfullrequest.setToCaresponse(toCaresponse);
//            esfullrequest.setSig(AccountUtil.signature(account.getPrivateKey(),String.valueOf(esfullrequest.getTimestamp()).getBytes(StandardCharsets.UTF_8)));
//            Esfullresponse esfullresponse=httpClient.Esfull(esfullrequest);
//        }

        List<Node> nodes = nodeService.queryAllNodes();  //查询到本地的所有节点
        if(nodes == null || nodes.size()==0){
            return;
        }

        for(Node node:nodes){
//            System.out.println(node.getIp());

            if(node.getIp().equals(API.Esip)){
                continue;
            }
            long blockchainHeight = blockchainCore.queryBlockchainHeight();
            if(blockchainHeight <= node.getBlockchainHeight()){//如果本地的区块链高度低于其他节点高度
                continue;
            }

            new Thread(()->bh(node,blockchainHeight)).start();

//            /**
//             * 如果本地的区块链高度高于其他节点高度
//             */
//            HttpClient nodeClient = new HttpClientImpl(node.getIp());
//            PostBlockchainHeightRequest postBlockchainHeightRequest = new PostBlockchainHeightRequest();
//            postBlockchainHeightRequest.setBlockchainHeight(blockchainHeight);
//            nodeClient.postBlockchainHeight(postBlockchainHeightRequest); //nodeclient 提交区块链高度
        }



    }

    private void bh(Node node,long blockchainHeight){
        try{
            HttpClient nodeClient = new HttpClientImpl(node.getIp());
            PostBlockchainHeightRequest postBlockchainHeightRequest = new PostBlockchainHeightRequest();
            postBlockchainHeightRequest.setBlockchainHeight(blockchainHeight);
            nodeClient.postBlockchainHeight(postBlockchainHeightRequest); //nodeclient 提交区块链高度
        }catch (Exception e){
            LogUtil.error("发送区块高度错误",e);
        }

    }


}

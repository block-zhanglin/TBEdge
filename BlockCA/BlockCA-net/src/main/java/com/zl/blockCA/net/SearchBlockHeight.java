package com.zl.blockCA.net;

import com.zl.blockCA.net.client.HttpClient;
import com.zl.blockCA.net.client.HttpClientImpl;
import com.zl.blockCA.net.dao.GetBlockchainHeightRequest;
import com.zl.blockCA.net.dao.GetBlockchainHeightResponse;
import com.zl.blockCA.net.dao.NodeDao;
import com.zl.blockCA.net.dao.NodeDaoImpl;
import com.zl.blockCA.net.model.Node;
import com.zl.blockCA.net.service.NodeService;
import com.zl.blockchain.blockCA.crypto.model.Account;
import util.BlockSetting;
import util.ThreadUtil;

import java.util.List;

public class SearchBlockHeight {

    private NodeService nodeService;


    public SearchBlockHeight(NodeService nodeService){
        this.nodeService=nodeService;
    }

    public void start(){
        while (true){
            searchBlockchainHeight();
            ThreadUtil.millisecondSleep(60*10*10);
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
            HttpClient nodeClient = new HttpClientImpl(node.getIp()); //对于每个node构建nodecclient
            GetBlockchainHeightRequest getBlockchainHeightRequest = new GetBlockchainHeightRequest();
            GetBlockchainHeightResponse getBlockchainHeightResponse = nodeClient.getBlockchainHeight(getBlockchainHeightRequest);  //nodeclient获取节点的高度
            if(getBlockchainHeightResponse != null){
                node.setBlockchainHeight(getBlockchainHeightResponse.getBlockchainHeight());
                nodeService.updateNode(node);
                if(node.getBlockchainHeight()> BlockSetting.getHEIGHT()){
                    BlockSetting.setHEIGHT(node.getBlockchainHeight());
                }
            }
        }
    }
}

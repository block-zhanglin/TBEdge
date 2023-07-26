package com.zl.blockchain.netcore;

import com.zl.blockchain.core.BlockchainCore;
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
 *  未确认交易搜索器
 * 搜索区块链网络中的未确认交易，放入未确认交易池，等待矿工用于挖矿。
 *
 *
 */
public class UnconfirmedTransactionsSearcher {

    private NetcoreConfiguration netCoreConfiguration;
    private NodeService nodeService;
    private BlockchainCore blockchainCore;


    public UnconfirmedTransactionsSearcher(NetcoreConfiguration netCoreConfiguration, BlockchainCore blockchainCore, NodeService nodeService) {
        this.netCoreConfiguration = netCoreConfiguration;
        this.nodeService = nodeService;
        this.blockchainCore = blockchainCore;
    }

    public void start() {
        try {
            while (true){
//                System.out.println("未确认数据库包含数据量"+ blockchainCore.getUnconfirmedTransactionDatabase().selectTransactions(1,100).size());
                searchUnconfirmedTransactions();
                ThreadUtil.millisecondSleep(netCoreConfiguration.getUnconfirmedTransactionsSearchTimeInterval());
            }
        } catch (Exception e) {
            LogUtil.error("在区块链网络中搜寻未确认交易出现异常",e);
        }
    }

    private void searchUnconfirmedTransactions() {

        List<Node> nodes = nodeService.queryAllNodes();
        if(nodes == null || nodes.size()==0){
            return;
        }
        for(Node node:nodes){
            //不寻找自身节点
            if(node.getIp().equals(API.Esip)){
                continue;
            }

            HttpClient nodeClient = new HttpClientImpl(node.getIp());
            GetUnconfirmedTransactionsRequest getUnconfirmedTransactionsRequest = new GetUnconfirmedTransactionsRequest();
            GetUnconfirmedTransactionsResponse getUnconfirmedTransactionsResponse = nodeClient.getUnconfirmedTransactions(getUnconfirmedTransactionsRequest);
            if(getUnconfirmedTransactionsResponse == null){
                continue;
            }
            List<TransactionDto> transactions = getUnconfirmedTransactionsResponse.getTransactions();
            if(transactions == null){
                continue;
            }
            for(TransactionDto transaction:transactions){
                blockchainCore.getUnconfirmedTransactionDatabase().insertTransaction(transaction);
            }
        }
    }

}

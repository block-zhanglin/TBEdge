package com.zl.blockchain.netcore;

import com.zl.blockchain.core.BlockchainCore;
import com.zl.blockchain.crypto.model.Account;
import com.zl.blockchain.dto.ToCaresponse;
import com.zl.blockchain.netcore.Server.EsServer;
import com.zl.blockchain.netcore.configuration.NetcoreConfiguration;
import com.zl.blockchain.netcore.service.NodeService;

/**
 * 区块链网络版核心，代表一个完整的区块链网络版核心系统，它就是所谓的节点。
 *  区块链网络版核心底层依赖单机版区块链核心BlockchainCore，在单机版区块链系统的基础上
 * ，新增了网络功能：自动的在整个区块链网络中寻找/发布：节点、区块、交易。
 *
 */
public class BlockchainNetCore {

    private Account account;

    private ToCaresponse EsCA;

    private NetcoreConfiguration netCoreConfiguration;
    private NodeService nodeService;

    private BlockchainCore blockchainCore;
    private EsServer EsServer;

    private SeedNodeInitializer seedNodeInitializer;
    private NodeSearcher nodeSearcher;
    private NodeBroadcaster nodeBroadcaster;
    private NodeCleaner nodeCleaner;

    private BlockchainHeightSearcher blockchainHeightSearcher;
    private BlockchainHeightBroadcaster blockchainHeightBroadcaster;

    private BlockSearcher blockSearcher;
    private BlockBroadcaster blockBroadcaster;

    private UnconfirmedTransactionsSearcher unconfirmedTransactionsSearcher;


    public BlockchainNetCore(NetcoreConfiguration netCoreConfiguration, BlockchainCore blockchainCore, EsServer EsServer
            , NodeService nodeService
            , SeedNodeInitializer seedNodeInitializer, NodeSearcher nodeSearcher, NodeBroadcaster nodeBroadcaster, NodeCleaner nodeCleaner
            , BlockchainHeightSearcher blockchainHeightSearcher, BlockchainHeightBroadcaster blockchainHeightBroadcaster
            , BlockSearcher blockSearcher, BlockBroadcaster blockBroadcaster
            , UnconfirmedTransactionsSearcher unconfirmedTransactionsSearcher
            , Account account,ToCaresponse EsCA
        ) {
        this.netCoreConfiguration = netCoreConfiguration;

        this.blockchainCore = blockchainCore;
        this.EsServer = EsServer;
        this.nodeService = nodeService;
        this.nodeCleaner = nodeCleaner;

        this.seedNodeInitializer = seedNodeInitializer;
        this.nodeBroadcaster = nodeBroadcaster;
        this.nodeSearcher = nodeSearcher;

        this.blockchainHeightSearcher = blockchainHeightSearcher;
        this.blockchainHeightBroadcaster = blockchainHeightBroadcaster;

        this.blockBroadcaster = blockBroadcaster;
        this.blockSearcher = blockSearcher;

        this.unconfirmedTransactionsSearcher = unconfirmedTransactionsSearcher;

        this.account=account;
        this.EsCA=EsCA;
    }

    public void  start() {

        //启动本地的单机区块链
        new Thread(()->blockchainCore.start(account,EsCA)).start();

        //启动区块链节点服务器
        new Thread(()->EsServer.start()).start();

        //种子节点初始化器
        new Thread(()->seedNodeInitializer.start()).start();
        //启动节点广播器
        new Thread(()->nodeBroadcaster.start()).start();

        //启动节点搜寻器
        new Thread(()->nodeSearcher.start()).start();


//        //启动节点清理器
        new Thread(()->nodeCleaner.start()).start();


        //启动区块链高度广播器
        new Thread(()->blockchainHeightBroadcaster.start()).start();
        //启动区块链高度搜索器
        new Thread(()->blockchainHeightSearcher.start()).start();

        //启动区块广播器
        new Thread(()->blockBroadcaster.start()).start();

        //启动区块搜寻器
        new Thread(()->blockSearcher.start()).start();//当收到第一次的委员会时候，开始调用

//
        //未确认交易搜索器
//        new Thread(()->unconfirmedTransactionsSearcher.start()).start();


    }

    //region get set
    public NetcoreConfiguration getNetCoreConfiguration() {
        return netCoreConfiguration;
    }
    public BlockchainCore getBlockchainCore() {
        return blockchainCore;
    }
    public EsServer getEsServer() {
        return EsServer;
    }
    public NodeService getNodeService() {
        return nodeService;
    }
    public SeedNodeInitializer getSeedNodeInitializer() {
        return seedNodeInitializer;
    }
    public NodeSearcher getNodeSearcher() {
        return nodeSearcher;
    }
    public NodeBroadcaster getNodeBroadcaster() {
        return nodeBroadcaster;
    }
    public NodeCleaner getNodeCleaner() {
        return nodeCleaner;
    }
    public BlockchainHeightSearcher getBlockchainHeightSearcher() {
        return blockchainHeightSearcher;
    }
    public BlockchainHeightBroadcaster getBlockchainHeightBroadcaster() {
        return blockchainHeightBroadcaster;
    }
    public BlockSearcher getBlockSearcher() {
        return blockSearcher;
    }
    public BlockBroadcaster getBlockBroadcaster() {
        return blockBroadcaster;
    }
    public UnconfirmedTransactionsSearcher getUnconfirmedTransactionsSearcher() {
        return unconfirmedTransactionsSearcher;
    }

    public Account getAccount() {
        return account;
    }

    public ToCaresponse getEsCA() {
        return EsCA;
    }
    //end
}

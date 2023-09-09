package com.zl.blockchain.netcore;

import com.zl.blockchain.core.BlockchainCore;
import com.zl.blockchain.crypto.model.Account;
import com.zl.blockchain.dto.ToCaresponse;
import com.zl.blockchain.netcore.Server.EsServer;
import com.zl.blockchain.netcore.configuration.NetcoreConfiguration;
import com.zl.blockchain.netcore.service.NodeService;

/**
 * 区块链网络版核心，代表一个完整的区块链网络版核心系统，它就是所谓的节点。
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

    private BlockUnload blockUnload;



    public BlockchainNetCore(NetcoreConfiguration netCoreConfiguration, BlockchainCore blockchainCore, EsServer EsServer
            , NodeService nodeService
            , SeedNodeInitializer seedNodeInitializer, NodeSearcher nodeSearcher, NodeBroadcaster nodeBroadcaster, NodeCleaner nodeCleaner
            , BlockchainHeightSearcher blockchainHeightSearcher, BlockchainHeightBroadcaster blockchainHeightBroadcaster
            , BlockSearcher blockSearcher, BlockBroadcaster blockBroadcaster
            , Account account,ToCaresponse EsCA,BlockUnload blockUnload
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
        this.account=account;
        this.EsCA=EsCA;

        this.blockUnload=blockUnload;
    }

    public void start() {

        //启动区块链节点服务器
        new Thread(()->EsServer.start()).start();

        //种子节点初始化器
        new Thread(()->seedNodeInitializer.start()).start();
        //启动节点广播器
        new Thread(()->nodeBroadcaster.start()).start();
        //启动节点搜寻器
        new Thread(()->nodeSearcher.start()).start();
        //启动节点清理器
        new Thread(()->nodeCleaner.start()).start();

        //启动区块链高度广播器
        new Thread(()->blockchainHeightBroadcaster.start()).start();
        //启动区块链高度搜索器
        new Thread(()->blockchainHeightSearcher.start()).start();

        //启动区块广播器
        new Thread(()->blockBroadcaster.start()).start();

        //启动区块搜寻器
        new Thread(()->blockSearcher.start()).start();

        //启动卸载区块任务
        new Thread(()->blockUnload.start()).start();

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

    public Account getAccount() {
        return account;
    }

    public ToCaresponse getEsCA() {
        return EsCA;
    }
    //end
}

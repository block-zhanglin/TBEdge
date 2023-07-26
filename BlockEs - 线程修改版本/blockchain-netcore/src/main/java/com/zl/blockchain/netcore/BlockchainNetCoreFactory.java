package com.zl.blockchain.netcore;

import com.zl.blockchain.core.BlockchainCore;
import com.zl.blockchain.core.BlockchainCoreFactory;
import com.zl.blockchain.core.tool.ResourcePathTool;
import com.zl.blockchain.crypto.model.Account;
import com.zl.blockchain.dto.ToCaresponse;
import com.zl.blockchain.netcore.Server.EsServer;
import com.zl.blockchain.netcore.Server.HttpServerHandlerResolver;
import com.zl.blockchain.netcore.configuration.NetcoreConfiguration;
import com.zl.blockchain.netcore.configuration.NetcoreConfigurationImpl;
import com.zl.blockchain.netcore.dao.NodeDao;
import com.zl.blockchain.netcore.dao.NodeDaoImpl;
import com.zl.blockchain.netcore.service.NodeService;
import com.zl.blockchain.netcore.service.NodeServiceImpl;
import com.zl.blockchain.util.FileUtil;

/**
 * 区块链网络版核心工厂
 *
 *
 */
public class BlockchainNetCoreFactory {

    /**
     * 创建[区块链网络版核心]实例  (账户，证书)
     */
    public static BlockchainNetCore createDefaultBlockchainNetCore(Account account, ToCaresponse EsCA){
        return createBlockchainNetCore(ResourcePathTool.getDataRootPath(),account,EsCA);
    }

    /**
     * 创建[区块链网络版核心]实例
     *
     *  @param netcorePath 区块链数据存放位置
     */
    public static BlockchainNetCore createBlockchainNetCore(String netcorePath,Account account,ToCaresponse EsCA){

        NetcoreConfiguration netCoreConfiguration = new NetcoreConfigurationImpl(netcorePath);

        String blockchainCorePath = FileUtil.newPath(netcorePath,"BlockchainCore");  //创建blockchaincore数据库
        BlockchainCore blockchainCore = BlockchainCoreFactory.createBlockchainCore(blockchainCorePath);//创建blockchain实例
        String slaveBlockchainCorePath = FileUtil.newPath(netcorePath,"SlaveBlockchainCore"); //创建备份blockchaincore数据库
        BlockchainCore slaveBlockchainCore = BlockchainCoreFactory.createBlockchainCore(slaveBlockchainCorePath); //创建备份blockchain实例


        NodeDao nodeDao = new NodeDaoImpl(netCoreConfiguration); //创建节点dao
        NodeService nodeService = new NodeServiceImpl(nodeDao); //创建节点服务


        SeedNodeInitializer seedNodeInitializer = new SeedNodeInitializer(netCoreConfiguration,nodeService); //种子节点初始化
        NodeSearcher nodeSearcher = new NodeSearcher(netCoreConfiguration,nodeService); //节点搜索器
        NodeBroadcaster nodeBroadcaster = new NodeBroadcaster(netCoreConfiguration,nodeService); //节点广播器
        NodeCleaner nodeCleaner = new NodeCleaner(netCoreConfiguration,nodeService);

        BlockchainHeightSearcher blockchainHeightSearcher = new BlockchainHeightSearcher(netCoreConfiguration,nodeService);
        BlockchainHeightBroadcaster blockchainHeightBroadcaster = new BlockchainHeightBroadcaster(netCoreConfiguration,blockchainCore,nodeService,account,EsCA);

        BlockSearcher blockSearcher = new BlockSearcher(netCoreConfiguration,blockchainCore,slaveBlockchainCore,nodeService);
        BlockBroadcaster blockBroadcaster = new BlockBroadcaster(netCoreConfiguration,blockchainCore,nodeService);

        UnconfirmedTransactionsSearcher unconfirmedTransactionsSearcher = new UnconfirmedTransactionsSearcher(netCoreConfiguration,blockchainCore,nodeService);

        HttpServerHandlerResolver httpServerHandlerResolver=new HttpServerHandlerResolver(account,EsCA,netCoreConfiguration,blockchainCore,nodeService,blockBroadcaster);//创建节点服务处理器
        EsServer nodeServer = new EsServer(netCoreConfiguration,httpServerHandlerResolver); //创建节点服务器

        BlockchainNetCore blockchainNetCore
                = new BlockchainNetCore(netCoreConfiguration, blockchainCore, nodeServer, nodeService
                , seedNodeInitializer, nodeSearcher, nodeBroadcaster, nodeCleaner
                , blockchainHeightSearcher, blockchainHeightBroadcaster
                , blockSearcher, blockBroadcaster
                , unconfirmedTransactionsSearcher
                ,account,EsCA
        );
        return blockchainNetCore;
    }

}

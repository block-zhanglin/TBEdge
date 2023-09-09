package com.zl.blockchain.netcore;

import com.zl.blockchain.netcore.configuration.NetcoreConfiguration;
import com.zl.blockchain.netcore.model.Node;
import com.zl.blockchain.netcore.service.NodeService;
import com.zl.blockchain.setting.NetworkSetting;
import com.zl.blockchain.util.LogUtil;
import com.zl.blockchain.util.ThreadUtil;


/**
 *  种子节点初始化器
 *
 *
 */
public class SeedNodeInitializer {

    private NetcoreConfiguration netCoreConfiguration;
    private NodeService nodeService;

    public SeedNodeInitializer(NetcoreConfiguration netCoreConfiguration, NodeService nodeService) {
        this.netCoreConfiguration = netCoreConfiguration;
        this.nodeService = nodeService;
    }

    //TODO 提供两个方法 start and run ？
    public void start() {
        try {
            while (true){
                addSeedNodes();
                ThreadUtil.millisecondSleep(netCoreConfiguration.getSeedNodeInitializeTimeInterval());
            }
        } catch (Exception e) {
            LogUtil.error("定时将种子节点加入区块链网络出现异常",e);
        }
    }

    /**
     * 添加种子节点
     */
    private void addSeedNodes() {
        if(!netCoreConfiguration.isAutoSearchNode()){
            return;
        }

        for(String seedNode: NetworkSetting.SEED_NODES){
            Node node = new Node();
            node.setIp(seedNode);
            node.setBlockchainHeight(0);
            nodeService.addNode(node);
            LogUtil.debug("种子节点初始化器提示您:云种子节点["+node.getIp()+"]加入了区块链网络。");
        }

        for(String seedNode: NetworkSetting.Es_SEED_NODES){
            Node node = new Node();
            node.setIp(seedNode);
            node.setBlockchainHeight(0);
            nodeService.addESNode(node);
            LogUtil.debug("种子节点初始化器提示您:Es种子节点["+node.getIp()+"]加入了区块链网络。");
        }

    }

}

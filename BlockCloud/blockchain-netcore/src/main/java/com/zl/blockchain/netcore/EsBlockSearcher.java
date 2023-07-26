package com.zl.blockchain.netcore;

import com.zl.blockchain.core.BlockchainCore;
import com.zl.blockchain.core.model.Block;
import com.zl.blockchain.core.tool.BlockDtoTool;
import com.zl.blockchain.core.tool.BlockTool;
import com.zl.blockchain.core.tool.Model2DtoTool;
import com.zl.blockchain.dto.BlockDto;
import com.zl.blockchain.dto.GetBlockRequest;
import com.zl.blockchain.dto.GetBlockResponse;
import com.zl.blockchain.netcore.client.HttpClient;
import com.zl.blockchain.netcore.client.HttpClientImpl;
import com.zl.blockchain.netcore.configuration.NetcoreConfiguration;
import com.zl.blockchain.netcore.model.Node;
import com.zl.blockchain.netcore.service.NodeService;
import com.zl.blockchain.setting.GenesisBlockSetting;
import com.zl.blockchain.util.StringUtil;
import com.zl.blockchain.util.SystemUtil;

import java.util.List;

public class EsBlockSearcher {

    private NetcoreConfiguration netCoreConfiguration;
    private NodeService nodeService;
    private BlockchainCore blockchainCore;
    private BlockchainCore slaveBlockchainCore;


    public EsBlockSearcher(NetcoreConfiguration netCoreConfiguration, BlockchainCore blockchainCore
            , BlockchainCore slaveBlockchainCore, NodeService nodeService) {
        this.netCoreConfiguration = netCoreConfiguration;
        this.nodeService = nodeService;
        this.blockchainCore = blockchainCore;
        this.slaveBlockchainCore = slaveBlockchainCore;
    }

    /**
     * 搜索新的区块，并同步这些区块到本地区块链系统
     */
    public void searchNodesBlocks(Node n,long s,long send) {

        System.out.println(s+":"+send);
        //从云中心的本地区块 高度+1开始从Es卸载区块
        for(long i=s;i<send;i++){
            HttpClient httpClient=new HttpClientImpl(n.getIp());
            GetBlockRequest getBlockRequest=new GetBlockRequest();
            getBlockRequest.setBlockHeight(i);
            GetBlockResponse getBlockResponse=httpClient.getBlock(getBlockRequest,1);
            //如果查询到区块为空
            if(getBlockResponse==null){
                System.out.println("空");
                break;
            }else {
                BlockDto blockDto=getBlockResponse.getBlock(); //获取到blockdto
                blockchainCore.addBlockDto(blockDto);
            }
        }
        System.out.println("云中心卸载任务完成后，云中心具有的区块高度："+blockchainCore.getBlockchainDatabase().queryTailBlock().getHeight());

    }
}

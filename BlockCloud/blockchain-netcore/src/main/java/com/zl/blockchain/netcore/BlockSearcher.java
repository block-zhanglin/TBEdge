package com.zl.blockchain.netcore;

import com.zl.blockchain.core.BlockchainCore;
import com.zl.blockchain.core.model.Block;
import com.zl.blockchain.core.tool.BlockDtoTool;
import com.zl.blockchain.core.tool.BlockTool;
import com.zl.blockchain.core.tool.Model2DtoTool;
import com.zl.blockchain.netcore.client.HttpClient;
import com.zl.blockchain.netcore.client.HttpClientImpl;
import com.zl.blockchain.netcore.configuration.NetcoreConfiguration;
import com.zl.blockchain.netcore.model.Node;
import com.zl.blockchain.netcore.service.NodeService;
import com.zl.blockchain.setting.GenesisBlockSetting;
import com.zl.blockchain.util.LogUtil;
import com.zl.blockchain.util.StringUtil;
import com.zl.blockchain.util.ThreadUtil;
import com.zl.blockchain.dto.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 区块搜索器
 * 尝试发现区块链网络中是否有区块链高度比自身区块链高度高的节点，若发现，则尝试同步区块到本地区块链。
 */
public class BlockSearcher {

    private NetcoreConfiguration netCoreConfiguration;
    private NodeService nodeService;
    private BlockchainCore blockchainCore;
    private BlockchainCore slaveBlockchainCore;


    public BlockSearcher(NetcoreConfiguration netCoreConfiguration, BlockchainCore blockchainCore
            , BlockchainCore slaveBlockchainCore, NodeService nodeService) {
        this.netCoreConfiguration = netCoreConfiguration;
        this.nodeService = nodeService;
        this.blockchainCore = blockchainCore;
        this.slaveBlockchainCore = slaveBlockchainCore;
    }

    public void start() {
        try {
            while (true){
                searchNodesBlocks();
                ThreadUtil.millisecondSleep(netCoreConfiguration.getBlockSearchTimeInterval());
            }
        } catch (Exception e) {
            LogUtil.error("在区块链网络中同步节点的区块出现异常",e);
        }
    }

    /**
     * 搜索新的区块，并同步这些区块到本地区块链系统
     */
    private void searchNodesBlocks() {

        List<Node> nodes = nodeService.queryAllNodes();
        for(Node node:nodes){
            searchNodeBlocks(blockchainCore,slaveBlockchainCore,node);
        }
    }

    /**
     * 搜索新的区块，并同步这些区块到本地区块链系统
     */
    public void searchNodeBlocks(BlockchainCore masterBlockchainCore, BlockchainCore slaveBlockchainCore, Node node) {
        if(!netCoreConfiguration.isAutoSearchBlock()){
            return;
        }
        long masterBlockchainHeight = masterBlockchainCore.queryBlockchainHeight();
        //本地区块链高度大于等于远程节点区块链高度，此时远程节点没有可以同步到本地区块链的区块。
        if(masterBlockchainHeight >= node.getBlockchainHeight()){
            return;
        }
        //本地区块链与node区块链是否分叉？
        boolean fork = isForkNode(masterBlockchainCore,node);
        if(fork){
            boolean isHardFork = isHardForkNode(masterBlockchainCore,node);
            if(!isHardFork){
                //求分叉区块的高度
                long forkBlockHeight = getForkBlockHeight(masterBlockchainCore,node);
                //复制"主区块链核心"的区块至"从区块链核心"
                duplicateBlockchainCore(masterBlockchainCore, slaveBlockchainCore);
                //删除"从区块链核心"已分叉区块
                slaveBlockchainCore.deleteBlocks(forkBlockHeight);
                //同步远程节点区块至从区块链核心
                synchronizeBlocks(slaveBlockchainCore,node,forkBlockHeight);
                //同步从区块链核心的区块至主区块链核心
                promoteBlockchainCore(slaveBlockchainCore, masterBlockchainCore);
            }
        } else {
            //未分叉，同步远程节点区块至主区块链核心
            long nextBlockHeight = masterBlockchainCore.queryBlockchainHeight()+1;
            synchronizeBlocks(masterBlockchainCore,node,nextBlockHeight);
        }
    }

    /**
     * 复制区块链核心的区块，操作完成后，'来源区块链核心'区块数据不发生变化，'去向区块链核心'的区块数据与'来源区块链核心'的区块数据保持一致。
     * @param fromBlockchainCore 来源区块链核心
     * @param toBlockchainCore 去向区块链核心
     */
    private void duplicateBlockchainCore(BlockchainCore fromBlockchainCore,BlockchainCore toBlockchainCore) {
        //删除'去向区块链核心'区块
        while (true){
            Block toBlockchainTailBlock = toBlockchainCore.queryTailBlock() ;
            if(toBlockchainTailBlock == null){
                break;
            }
            Block fromBlockchainBlock = fromBlockchainCore.queryBlockByBlockHeight(toBlockchainTailBlock.getHeight());
            if(BlockTool.isBlockEquals(fromBlockchainBlock,toBlockchainTailBlock)){
                break;
            }
            toBlockchainCore.deleteTailBlock();
        }
        //增加'去向区块链核心'区块
        while (true){
            long toBlockchainHeight = toBlockchainCore.queryBlockchainHeight();
            Block nextBlock = fromBlockchainCore.queryBlockByBlockHeight(toBlockchainHeight+1) ;
            if(nextBlock == null){
                break;
            }
            toBlockchainCore.addBlock(nextBlock);
        }
    }

    /**
     * 增加"去向区块链核心"的区块，操作完成后，"来源区块链核心"的区块不发生变化，"去向区块链核心"的高度不变或者增长。
     * @param fromBlockchainCore "来源区块链核心"
     * @param toBlockchainCore "去向区块链核心"
     */
    private void promoteBlockchainCore(BlockchainCore fromBlockchainCore, BlockchainCore toBlockchainCore) {
        //此时，"去向区块链核心高度"大于"来源区块链核心高度"，"去向区块链核心高度"不能增加，结束逻辑。
        if(toBlockchainCore.queryBlockchainHeight() >= fromBlockchainCore.queryBlockchainHeight()){
            return;
        }
        //硬分叉
        if(isHardFork(toBlockchainCore,fromBlockchainCore)){
            return;
        }
        //此时，"去向区块链核心高度"小于"来源区块链核心高度"，且未硬分叉，可以增加"去向区块链核心高度"
        duplicateBlockchainCore(fromBlockchainCore,toBlockchainCore);
    }


    /**
     * 获取分叉区块的高度
     * @param blockchainCore
     * @param node
     * @return
     */
    private long getForkBlockHeight(BlockchainCore blockchainCore, Node node) {
        //求分叉区块的高度，此时已知分叉了，从当前高度依次递减1，判断高度相同的区块的是否相等，若相等，(高度+1)即开始分叉高度。
        long masterBlockchainHeight = blockchainCore.queryBlockchainHeight();
        long forkBlockHeight = masterBlockchainHeight;
        while (true) {
            if (forkBlockHeight <= GenesisBlockSetting.HEIGHT) {
                break;
            }
            GetBlockRequest getBlockRequest = new GetBlockRequest();
            getBlockRequest.setBlockHeight(forkBlockHeight);
            HttpClient nodeClient = new HttpClientImpl(node.getIp());
            GetBlockResponse getBlockResponse = nodeClient.getBlock(getBlockRequest,0);
            if(getBlockResponse == null){
                break;
            }
            BlockDto remoteBlock = getBlockResponse.getBlock();
            if(remoteBlock == null){
                break;
            }
            Block localBlock = blockchainCore.queryBlockByBlockHeight(forkBlockHeight);
            if(BlockDtoTool.isBlockEquals(Model2DtoTool.block2BlockDto(localBlock),remoteBlock)){
                break;
            }
            forkBlockHeight--;
        }
        forkBlockHeight++;
        return forkBlockHeight;
    }

    //同步远程节点区块至从区块链核心
    private void synchronizeBlocks(BlockchainCore blockchainCore, Node node, long startBlockHeight) {
        while (true){
            GetBlockRequest getBlockRequest = new GetBlockRequest();
            getBlockRequest.setBlockHeight(startBlockHeight);
            HttpClient nodeClient = new HttpClientImpl(node.getIp());
            GetBlockResponse getBlockResponse = nodeClient.getBlock(getBlockRequest,0);
            if(getBlockResponse == null){
                break;
            }
            BlockDto remoteBlock = getBlockResponse.getBlock();
            if(remoteBlock == null){
                break;
            }
            boolean isAddBlockSuccess = blockchainCore.addBlockDto(remoteBlock);
            if(!isAddBlockSuccess){
                break;
            }
            startBlockHeight++;
        }
    }

    /**
     * 本地区块链与node区块链是否分叉？
     * @param blockchainCore
     * @param node
     * @return
     */
    private boolean isForkNode(BlockchainCore blockchainCore, Node node) {
        Block block = blockchainCore.queryTailBlock();
        if(block == null){
            return false;
        }
        GetBlockRequest getBlockRequest = new GetBlockRequest();
        getBlockRequest.setBlockHeight(block.getHeight());
        HttpClient nodeClient = new HttpClientImpl(node.getIp());
        GetBlockResponse getBlockResponse = nodeClient.getBlock(getBlockRequest,0);
        //没有查询到区块，这里则认为远程节点没有该高度的区块存在，远程节点的高度没有本地区块链高度高，所以不分叉。
        if(getBlockResponse == null){
            return false;
        }
        BlockDto blockDto = getBlockResponse.getBlock();
        if(blockDto == null){
            return false;
        }
        String blockHash = BlockDtoTool.calculateBlockHash(blockDto);
        return !StringUtil.equals(block.getHash(), blockHash);
    }


    private boolean isHardFork(BlockchainCore blockchainCore1, BlockchainCore blockchainCore2) {
        BlockchainCore longer;
        BlockchainCore shorter;
        if(blockchainCore1.queryBlockchainHeight()>=blockchainCore2.queryBlockchainHeight()){
            longer = blockchainCore1;
            shorter = blockchainCore2;
        }else {
            longer = blockchainCore2;
            shorter = blockchainCore1;
        }

        long shorterBlockchainHeight = shorter.queryBlockchainHeight();
        if(shorterBlockchainHeight < netCoreConfiguration.getHardForkBlockCount()){
            return false;
        }

        long criticalPointBlocHeight = shorterBlockchainHeight-netCoreConfiguration.getHardForkBlockCount()+1;
        Block longerBlock = longer.queryBlockByBlockHeight(criticalPointBlocHeight);
        Block shorterBlock = shorter.queryBlockByBlockHeight(criticalPointBlocHeight);
        return !BlockTool.isBlockEquals(longerBlock, shorterBlock);
    }

    /**
     * 是否是硬分叉
     * @param blockchainCore
     * @param node
     * @return
     */
    private boolean isHardForkNode(BlockchainCore blockchainCore, Node node) {
        long blockchainHeight = blockchainCore.queryBlockchainHeight();
        if (blockchainHeight < netCoreConfiguration.getHardForkBlockCount()) {
            return false;
        }
        long criticalPointBlocHeight = blockchainHeight-netCoreConfiguration.getHardForkBlockCount()+1;
        if(criticalPointBlocHeight <= GenesisBlockSetting.HEIGHT){
            return false;
        }
        GetBlockRequest getBlockRequest = new GetBlockRequest();
        getBlockRequest.setBlockHeight(criticalPointBlocHeight);
        HttpClient nodeClient = new HttpClientImpl(node.getIp());
        GetBlockResponse getBlockResponse = nodeClient.getBlock(getBlockRequest,0);
        if(getBlockResponse == null){
            return false;
        }
        BlockDto remoteBlock = getBlockResponse.getBlock();
        if(remoteBlock == null){
            return false;
        }
        Block localBlock = blockchainCore.queryBlockByBlockHeight(criticalPointBlocHeight);
        return !BlockDtoTool.isBlockEquals(Model2DtoTool.block2BlockDto(localBlock),remoteBlock);
    }
}

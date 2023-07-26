package com.zl.blockchain.netcore.Server;


import com.zl.blockchain.core.BlockchainCore;
import com.zl.blockchain.core.CoreConfiguration;
import com.zl.blockchain.core.impl.CoreConfigurationDefaultImpl;
import com.zl.blockchain.core.model.Block;
import com.zl.blockchain.core.tool.Model2DtoTool;
import com.zl.blockchain.core.tool.ResourcePathTool;
import com.zl.blockchain.crypto.AccountUtil;
import com.zl.blockchain.crypto.model.Account;
import com.zl.blockchain.dto.*;
import com.zl.blockchain.netcore.client.HttpClientImpl;
import com.zl.blockchain.netcore.configuration.NetcoreConfiguration;
import com.zl.blockchain.netcore.model.Node;
import com.zl.blockchain.netcore.service.NodeService;
import com.zl.blockchain.setting.BlockSetting;
import com.zl.blockchain.util.*;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 请求处理分解类
 *
 */
public class HttpServerHandlerResolver {

    private Account account; //账户
    private ToCaresponse tocaresponse; //账户的证书

    private BlockchainCore blockchainCore;//区块链核心
    private NodeService nodeService;//节点服务
    private NetcoreConfiguration netcoreConfiguration;//netcore配置


    public HttpServerHandlerResolver(Account account, ToCaresponse tocaresponse,NetcoreConfiguration netcoreConfiguration,BlockchainCore blockchainCore,NodeService nodeService){
        this.account=account;
        this.tocaresponse=tocaresponse;
        this.netcoreConfiguration=netcoreConfiguration;
        this.blockchainCore=blockchainCore;
        this.nodeService=nodeService;
    }


    /**
     * Ping节点
     */
    public PingResponse ping(String requestIp, PingRequest request){  //<请求的ip地址，请求的request类>
        try {

            //将ping的来路作为区块链节点
            if(netcoreConfiguration.isAutoSearchNode()){  //从配置类中获取是否自动搜寻节点
                Node node = new Node(); //创建一个节点node
                node.setIp(requestIp); //节点设置ip为requestip
                node.setBlockchainHeight(0); //节点设置区块链高度为0
                nodeService.addNode(node); //节点添加节点
                LogUtil.debug("发现节点["+requestIp+"]在Ping本地节点，已将发现的节点放入了节点数据库。");
            }
            PingResponse response = new PingResponse(); //创建一个返回类
            return response;
        } catch (Exception e){
            String message = "ping node failed";
            LogUtil.error(message,e);
            return null;
        }
    }


    /**
     * 根据区块高度查询区块
     */
    public GetBlockResponse getBlock(GetBlockRequest request){
        try {
            Block blockByBlockHeight = blockchainCore.queryBlockByBlockHeight(request.getBlockHeight()); //通过区块高度，获取区块
            System.out.println(blockByBlockHeight.getHeight());
            BlockDto block = Model2DtoTool.block2BlockDto(blockByBlockHeight);  //blockdto转换为block
            GetBlockResponse response = new GetBlockResponse(); //获取区块的response类
            response.setBlock(block); //response设置区块block
            return response;
        } catch (Exception e){
            String message = "get block failed";
            LogUtil.error(message,e);
            return null;
        }
    }


    /**
     * 接收其他节点提交的区块
     * @param request
     * @return
     */
    public PostBlockResponse postBlock(PostBlockRequest request) {  //<提交的区块>
        try {
            blockchainCore.addBlockDto(request.getBlock());  //区块链数据库添加区块
            PostBlockResponse response = new PostBlockResponse();
            return response;
        } catch (Exception e){
            String message = "post block failed";
            LogUtil.error(message,e);
            return null;
        }
    }

    /**
     * 获取所有的节点列表
     * @param request
     * @return
     */
    public GetNodesResponse getNodes(GetNodesRequest request) {
        try {
            List<Node> allNodes = nodeService.queryAllNodes();
            List<NodeDto> nodes = new ArrayList<>();
            if(allNodes != null){
                for (Node node:allNodes) {
                    NodeDto n = new NodeDto();
                    n.setIp(node.getIp());
                    nodes.add(n);
                }
            }
            GetNodesResponse response = new GetNodesResponse();
            response.setNodes(nodes);
            return response;
        }catch (Exception e){
            String message = "get nodes failed";
            LogUtil.error(message,e);
            return null;
        }
    }

    /**
     * 更新其他节点的区块链高度
     * @param requestIp
     * @param request
     * @return
     */
    public PostBlockchainHeightResponse postBlockchainHeight(String requestIp, PostBlockchainHeightRequest request) {
        try {
            Node node = new Node();
            node.setIp(requestIp);
            node.setBlockchainHeight(request.getBlockchainHeight());
            nodeService.updateNode(node);
            PostBlockchainHeightResponse response = new PostBlockchainHeightResponse();
            return response;
        } catch (Exception e){
            String message = "post blockchain height failed";
            LogUtil.error(message,e);
            return null;
        }
    }

    /**
     * 获取区块链高度
     * @param request
     * @return
     */
    public GetBlockchainHeightResponse getBlockchainHeight(GetBlockchainHeightRequest request) {
        try {
            long blockchainHeight = blockchainCore.queryBlockchainHeight(); //查询区块链长度
            GetBlockchainHeightResponse response = new GetBlockchainHeightResponse();
            response.setBlockchainHeight(blockchainHeight);
            return response;
        } catch (Exception e){
            String message = "get blockchain height failed";
            LogUtil.error(message,e);
            return null;
        }
    }

    /**
     * 收到Es节点传来的卸载请求
     */
    public Esfullresponse esfull(String  requestip,Esfullrequest esfullrequest){
        try {
            ToCaresponse toCaresponse=esfullrequest.getToCaresponse();
            Boolean b=AccountUtil.verifySignature(API.CAPublicKey,(toCaresponse.getPublickkey()+toCaresponse.getName()).getBytes(StandardCharsets.UTF_8),ByteUtil.hexStringToBytes(toCaresponse.getSig()));
            if(b){
                byte[]bb=String.valueOf(esfullrequest.getTimestamp()).getBytes(StandardCharsets.UTF_8);
                Boolean c=AccountUtil.verifySignature(toCaresponse.getPublickkey(),bb,ByteUtil.hexStringToBytes(esfullrequest.getSig()));
                if(c){
                    /**
                     * 确定是近期发出的卸载请求
                     */
                    if(esfullrequest.getTimestamp()+60*1000>TimeUtil.millisecondTimestamp()){
                        /**
                         * 已经有了该ip地址的卸载请求
                         */
                        if(!API.Esfullip.contains(requestip)){
                            API.Esfullip.add(requestip);
                            BlockSetting.unloadcount+=1;
                            Esfullresponse esfullresponse=new Esfullresponse();

                            System.out.println("更新有几个节点提出卸载区块请求"+BlockSetting.unloadcount);
                            return esfullresponse;
                        }
                    }
                }
            }
            return null;
        }catch (Exception e){
            String message = "Es卸载请求失败";
            LogUtil.error(message,e);
            return null;
        }

    }

}

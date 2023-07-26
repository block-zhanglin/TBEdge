package com.zl.blockchain.netcore.client;


import com.zl.blockchain.dto.*;

/**
 * Es层的客户端
 *  1.与网关层、Iot层进行通信
 *  2.向目标节点请求、提交数据
 */
public interface HttpClient {


    /**
     * Es向CA提出证书请求
     * @param iottoCarequest
     * @return
     */
    public ToCaresponse IOTTO_CARESPONSE(ToCarequest iottoCarequest);


    /**
     * ping节点 ping的request
     * @param request
     * @return
     */
    PingResponse pingNode(PingRequest request,int i);

    /**
     * 根据区块高度，获取区块
     * @param request 获取区块请求
     * @return
     */
    GetBlockResponse getBlock(GetBlockRequest request,int i);

    /**
     * 获取节点列表
     * @param request 获取节点请求
     * @return
     */
    GetNodesResponse getNodes(GetNodesRequest request,int i);

    /**
     * 提交区块至节点
     * @param request 提交区块请求
     * @return
     */
    PostBlockResponse postBlock(PostBlockRequest request);


    /**
     * 提交区块链高度至节点
     * @param request 提交区块链高度请求
     * @return
     */
    PostBlockchainHeightResponse postBlockchainHeight(PostBlockchainHeightRequest request);

    /**
     * 获取区块链高度
     * @param request 获取区块链高度请求
     * @return
     */
    GetBlockchainHeightResponse getBlockchainHeight(GetBlockchainHeightRequest request,int i);


    /**
     * 云中心发出卸载命令
     * @param blockunloadrequest
     * @return
     */
    blockunloadresponse  blockunload(blockunloadrequest blockunloadrequest);

}

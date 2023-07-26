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
     * Es向Iot发出数据请求
     */
    public EstoIotResponse estoiotrequest(EstoIotrequest estoIotrequest);

    /**
     * Es处理完任务之后，向Iot设备返回确认信息
     */
    public EstoIotdataResponse estoiotdatarequest(EstoIotdatarequest estoIotdatarequest);

    /**
     * 提交交易至节点
     * @param request  交易的request
     * @return
     */
    PostTransactionResponse postTransaction(PostTransactionRequest request);


    /**
     * ping节点 ping的request
     * @param request
     * @return
     */
    PingResponse pingNode(PingRequest request);

    /**
     * 根据区块高度，获取区块
     * @param request 获取区块请求
     * @return
     */
    GetBlockResponse getBlock(GetBlockRequest request);

    /**
     * 获取节点列表
     * @param request 获取节点请求
     * @return
     */
    GetNodesResponse getNodes(GetNodesRequest request);

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
    GetBlockchainHeightResponse getBlockchainHeight(GetBlockchainHeightRequest request);

    /**
     * 根据交易高度，获取交易
     * @param getUnconfirmedTransactionsRequest get获取未确认交易请求
     * @return
     */
    GetUnconfirmedTransactionsResponse getUnconfirmedTransactions(GetUnconfirmedTransactionsRequest getUnconfirmedTransactionsRequest);


    /**
     * Es节点储存空间满了
     * @param esfullrequest
     * @return
     */
    Esfullresponse Esfull(Esfullrequest esfullrequest);
}

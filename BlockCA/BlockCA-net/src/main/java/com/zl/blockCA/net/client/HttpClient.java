package com.zl.blockCA.net.client;


import com.zl.blockCA.net.dao.*;

/**
 * CA层的客户端
 *  1.与ES层进行通信
 *  2.向目标节点请求、提交数据
 */
public interface HttpClient {


    /**
     * ping节点
     */
    PingResponse pingNode(PingRequest request);

    /**
     * 获取节点的区块链高度
     */
    GetBlockchainHeightResponse getBlockchainHeight(GetBlockchainHeightRequest request);


    /**
     *  向ES节点发布委员会
     */
    PostCommitteeResponse postcommittee(PostCommitteeRequest request);

}

package com.zl.blockgateway.net.client;


import com.zl.blockgateway.net.dao.*;

/**
 * 网关层的客户端
 *  1.与Es层、Iot层进行通信
 */
public interface HttpClient {

    public ToCaresponse IOTTO_CARESPONSE(ToCarequest iottoCarequest);

    public GatewaytoEsMessageResponse GATEWAYTO_ES_MESSAGE_REQUEST(GatewaytoEsMessageRequest gatewaytoEsMessageRequest);

}

package com.zl.blockIot.net.client;

import com.zl.blockIot.net.dao.*;

/**
 * Iot层的客户端
 *  1. 网关层进行通信
 */
public interface HttpClient {

    public ToCaresponse IOTTO_CARESPONSE(ToCarequest iottoCarequest);

    public IottoGatewayMessageResponse IOTTO_GATEWAY_MESSAGE_RESPONSE(IottoGatewayMessageRequest iottoGatewayMessageRequest);

}

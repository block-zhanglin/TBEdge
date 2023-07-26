package com.zl.blockgateway.net.Server;


import com.zl.blockchain.blockgateway.crypto.AccountUtil;
import com.zl.blockchain.blockgateway.crypto.model.Account;
import com.zl.blockgateway.net.client.HttpClientImpl;
import com.zl.blockgateway.net.dao.*;

import java.nio.charset.StandardCharsets;

public class HttpServerHandlerResolver {

    private Account account; //账户

    private ToCaresponse tocaresponse; //账户的证书

   public HttpServerHandlerResolver(Account account, ToCaresponse tocaresponse){
       this.account=account;
       this.tocaresponse=tocaresponse;
   }


    /**
     * 收到Iot的请求信息【任务编号，任务大小】，发送【任务编号，任务大小，Es编号】给Es
     * @param iottoGatewayMessageRequest
     * @return
     */
    public IottoGatewayMessageResponse iottoGatewayMessage(IottoGatewayMessageRequest iottoGatewayMessageRequest){

        //获取到iot的证书
        ToCaresponse iottoCaresponse=iottoGatewayMessageRequest.getIottoCaresponse();
        //验证证书
        Boolean b= AccountUtil.verifySignature(API.CAPublicKey,(iottoCaresponse.getPublickkey()+iottoCaresponse.getName()).getBytes(StandardCharsets.UTF_8),iottoCaresponse.getSig());
        if(b){
            //通过证书中的公钥验证签名
            Boolean c=AccountUtil.verifySignature(iottoCaresponse.getPublickkey(),(iottoGatewayMessageRequest.getTaskNumber()+String.valueOf(iottoGatewayMessageRequest.getTaskSize())).getBytes(StandardCharsets.UTF_8),iottoGatewayMessageRequest.getSig());
            if(c){
                HttpClientImpl httpClient=new HttpClientImpl();
                httpClient.setEsIp(API.Esip);
                GatewaySendTask(iottoGatewayMessageRequest,httpClient,account,tocaresponse); //分配任务到Es
                IottoGatewayMessageResponse iottoGatewayMessageResponse=new IottoGatewayMessageResponse();
                return  iottoGatewayMessageResponse;
            }
        }
        return  null;
    }


    /**
     * Gateway分配任务给Es
     * @param iottoGatewayMessageRequest
     * @param httpClient
     */
    private static void GatewaySendTask(IottoGatewayMessageRequest iottoGatewayMessageRequest, HttpClientImpl httpClient, Account account, ToCaresponse tocaresponse){
        GatewaytoEsMessageRequest gatewaytoEsMessageRequest=new GatewaytoEsMessageRequest();
        gatewaytoEsMessageRequest.setTaskNumber(iottoGatewayMessageRequest.getTaskNumber());
        gatewaytoEsMessageRequest.setTaskSize(iottoGatewayMessageRequest.getTaskSize());
        gatewaytoEsMessageRequest.setEsNumber(API.EsName);
        //对[任务编号、任务大小、ES编号]签名
        gatewaytoEsMessageRequest.setSig(AccountUtil.signature(account.getPrivateKey(),(gatewaytoEsMessageRequest.getTaskNumber()+String.valueOf(gatewaytoEsMessageRequest.getTaskSize())+gatewaytoEsMessageRequest.getEsNumber()).getBytes(StandardCharsets.UTF_8)));
        gatewaytoEsMessageRequest.setGatewaytoCaresponse(tocaresponse);
        GatewaytoEsMessageResponse gatewaytoEsMessageResponse=httpClient.GATEWAYTO_ES_MESSAGE_REQUEST(gatewaytoEsMessageRequest);
    }

}

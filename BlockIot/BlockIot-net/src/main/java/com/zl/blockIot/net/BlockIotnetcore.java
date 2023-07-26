package com.zl.blockIot.net;

import com.zl.blockIot.net.Server.API;
import com.zl.blockIot.net.Server.HttpServerHandlerResolver;
import com.zl.blockIot.net.Server.IotServer;
import com.zl.blockIot.net.client.HttpClientImpl;
import com.zl.blockIot.net.configuration.NetcoreConfigurationImpl;
import com.zl.blockIot.net.dao.ToCarequest;
import com.zl.blockIot.net.dao.ToCaresponse;
import com.zl.blockIot.net.dao.IottoGatewayMessageRequest;
import com.zl.blockIot.net.dao.IottoGatewayMessageResponse;
import com.zl.blockchain.blockIot.crypto.model.Account;
import util.CustomConfigurationUtil;
import util.MathUtil;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import com.zl.blockchain.blockIot.crypto.*;
public class BlockIotnetcore {

    public static void main(String [] args){

        CustomConfigurationUtil.run(); //读取本地的配置文件

        Account account=AccountUtil.accountFromPrivateKey(ByteUtil.bytesToHexString(API.IotName.getBytes(StandardCharsets.UTF_8)));
        ToCaresponse iottoCaresponse=TakeCa(account); //向CA获取得到证书
        NetcoreConfigurationImpl netcoreConfiguration=new NetcoreConfigurationImpl(Integer.valueOf(API.IotPort),API.IotName);
        HttpServerHandlerResolver httpServerHandlerResolver=new HttpServerHandlerResolver(account,iottoCaresponse);
        IotServer iotServer=new IotServer(netcoreConfiguration,httpServerHandlerResolver);
        /**
         * 开启服务器端，并接受请求
         */
        new  Thread(()->iotServer.start()).start();

        /**
         * 每间隔10秒，Iot设备发送一次任务
         */
        new Thread(()->BlockIotnetcore.SendTask(account,iottoCaresponse)).start();


    }
    private static  void SendTask(Account account, ToCaresponse iottoCaresponse){
        HttpClientImpl httpClient=new HttpClientImpl();
        httpClient.setGatewayIp(API.GateWayip); //设置Iot客户端所对应的gateway的ip地址
        httpClient.setMaxTaskNum(1000000); //设置该Iot客户端发送任务最大值
        for(int i=14105;i<httpClient.getMaxTaskNum();i++){
            IottoGatewayMessageRequest iottoGatewayMessageRequest=new IottoGatewayMessageRequest(); //创建iot设备到网关层的请求
            iottoGatewayMessageRequest.setTaskNumber(API.IotName+String.valueOf(i)); //设置任务编号
            iottoGatewayMessageRequest.setTaskSize(MathUtil.randx()); //设置任务的大小（这里是随机生成）
            iottoGatewayMessageRequest.setSig(AccountUtil.signature(account.getPrivateKey(),(iottoGatewayMessageRequest.getTaskNumber()+String.valueOf(iottoGatewayMessageRequest.getTaskSize())).getBytes(StandardCharsets.UTF_8)));
            iottoGatewayMessageRequest.setIottoCaresponse(iottoCaresponse);
            IottoGatewayMessageResponse iottoGatewayMessageResponse=httpClient.IOTTO_GATEWAY_MESSAGE_RESPONSE(iottoGatewayMessageRequest);
            try {
                TimeUnit.SECONDS.sleep(1); //程序睡眠1秒
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private  static ToCaresponse TakeCa(Account account){
        HttpClientImpl httpClient=new HttpClientImpl();
        httpClient.setCaIp(API.Caip); //设置Iot客户端所对应的gateway的ip地址
        ToCarequest iottoCarequest=new ToCarequest();
        iottoCarequest.setName(API.IotName);
        iottoCarequest.setPublicKey(account.getPublicKey());
        ToCaresponse iottoCaresponse=httpClient.IOTTO_CARESPONSE(iottoCarequest);
        Boolean b=AccountUtil.verifySignature(API.CAPublicKey,(iottoCaresponse.getPublickkey()+iottoCaresponse.getName()).getBytes(StandardCharsets.UTF_8),iottoCaresponse.getSig());
        if(b){
            return  iottoCaresponse;
        }else {
            return null;
        }

    }



}

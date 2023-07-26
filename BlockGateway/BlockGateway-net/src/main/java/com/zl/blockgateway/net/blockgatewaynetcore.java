package com.zl.blockgateway.net;
import com.zl.blockchain.blockgateway.crypto.AccountUtil;
import com.zl.blockchain.blockgateway.crypto.ByteUtil;
import com.zl.blockchain.blockgateway.crypto.model.Account;
import com.zl.blockgateway.net.Server.API;
import com.zl.blockgateway.net.Server.GatewayServer;
import com.zl.blockgateway.net.Server.HttpServerHandlerResolver;
import com.zl.blockgateway.net.client.HttpClientImpl;
import com.zl.blockgateway.net.configuration.NetcoreConfigurationImpl;
import com.zl.blockgateway.net.dao.ToCarequest;
import com.zl.blockgateway.net.dao.ToCaresponse;
import util.CustomConfigurationUtil;

import java.nio.charset.StandardCharsets;

public class blockgatewaynetcore {
    public static void  main(String[]args){

        CustomConfigurationUtil.run();

        Account account= AccountUtil.accountFromPrivateKey(ByteUtil.bytesToHexString(API.GateWayName.getBytes(StandardCharsets.UTF_8)));
        ToCaresponse toCaresponse=TakeCa(account); //向CA获取得到证书

        NetcoreConfigurationImpl netcoreConfiguration=new NetcoreConfigurationImpl(Integer.valueOf(API.GateWayPort),API.GateWayName);
        HttpServerHandlerResolver httpServerHandlerResolver=new HttpServerHandlerResolver(account,toCaresponse);
        GatewayServer gatewayServer=new GatewayServer(netcoreConfiguration,httpServerHandlerResolver);
        /**
         * 启动服务器端
         */
        new Thread(()->gatewayServer.start()).start();


    }

    private  static ToCaresponse TakeCa(Account account){
        HttpClientImpl httpClient=new HttpClientImpl();
        httpClient.setCaIp(API.Caip); //CA的ip地址
        ToCarequest GatewaytoCarequest =new ToCarequest();
        GatewaytoCarequest.setName(API.GateWayName);
        GatewaytoCarequest.setPublicKey(account.getPublicKey());
        ToCaresponse toCaresponse=httpClient.IOTTO_CARESPONSE(GatewaytoCarequest);
        Boolean b=AccountUtil.verifySignature(API.CAPublicKey,(toCaresponse.getPublickkey()+toCaresponse.getName()).getBytes(StandardCharsets.UTF_8),toCaresponse.getSig());
        if(b){
            return  toCaresponse;
        }else {
            return null;
        }

    }


}

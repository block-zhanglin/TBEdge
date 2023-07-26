package com.zl.blockchain.netcore;

import com.zl.blockchain.crypto.AccountUtil;
import com.zl.blockchain.crypto.model.Account;
import com.zl.blockchain.dto.API;
import com.zl.blockchain.dto.ToCarequest;
import com.zl.blockchain.dto.ToCaresponse;
import com.zl.blockchain.netcore.client.HttpClientImpl;
import com.zl.blockchain.util.ByteUtil;

import java.nio.charset.StandardCharsets;

public class BlockEsnetcore {

    public static  void main(String[]args){

        CustomConfigurationUtil.run(); //读入本地配置文件
        Account account= AccountUtil.accountFromPrivateKey(ByteUtil.bytesToHexString(API.EsNumber.getBytes(StandardCharsets.UTF_8)));
        ToCaresponse toCaresponse=TakeCa(account); //向CA获取得到证书

//        BlockchainCore blockchainCore= BlockchainCoreFactory. createDefaultBlockchainCore();
//        blockchainCore.start(account,toCaresponse);
//        /**
//         * 开启服务器端，并接收请求
//         */
//        NetcoreConfigurationImpl netcoreConfiguration=new NetcoreConfigurationImpl(ResourcePathTool.getDataRootPath());
//        NodeDao nodeDao = new NodeDaoImpl(netcoreConfiguration); //创建节点dao
//        NodeService nodeService = new NodeServiceImpl(nodeDao); //创建节点服务
//        HttpServerHandlerResolver httpServerHandlerResolver=new HttpServe  ver(account,toCaresponse,netcoreConfiguration,blockchainCore,nodeService);
//        EsServer esServer=new EsServer(netcoreConfiguration,httpServerHan  dlerResolver);
//        new Thread(()->esServer.start()).start();
//        System.out.println("服务器正常启动");
        BlockchainNetCore blockchainNetCore=BlockchainNetCoreFactory.createDefaultBlockchainNetCore(account,toCaresponse);
        blockchainNetCore.start();
    }

    private  static ToCaresponse TakeCa(Account account){

        HttpClientImpl httpClient=new HttpClientImpl();
        httpClient.setCaIp(API.Caip); //设置Iot客户端所对应的gateway的ip地址
        ToCarequest toCarequest=new ToCarequest();
        toCarequest.setName(API.EsNumber);
        toCarequest.setPublicKey(account.getPublicKey());
        ToCaresponse toCaresponse=httpClient.IOTTO_CARESPONSE(toCarequest);
        Boolean b=AccountUtil.verifySignature(API.CAPublicKey,(toCaresponse.getPublickkey()+toCaresponse.getName()).getBytes(StandardCharsets.UTF_8),ByteUtil.hexStringToBytes(toCaresponse.getSig()));
        if(b){
            return  toCaresponse;
        }else {
            return null;
        }

    }
}

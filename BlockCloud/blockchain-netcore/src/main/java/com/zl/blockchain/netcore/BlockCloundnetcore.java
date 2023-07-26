package com.zl.blockchain.netcore;

import com.zl.blockchain.crypto.AccountUtil;
import com.zl.blockchain.crypto.model.Account;
import com.zl.blockchain.dto.API;
import com.zl.blockchain.dto.ToCarequest;
import com.zl.blockchain.dto.ToCaresponse;
import com.zl.blockchain.netcore.client.HttpClientImpl;
import com.zl.blockchain.util.ByteUtil;

import java.nio.charset.StandardCharsets;

public class BlockCloundnetcore {

    public static  void main(String[]args){

        CustomConfigurationUtil.run();

        Account account= AccountUtil.accountFromPrivateKey(ByteUtil.bytesToHexString(API.CloundName.getBytes(StandardCharsets.UTF_8)));
        ToCaresponse toCaresponse=TakeCa(account); //向CA获取得到证书


        BlockchainNetCore blockchainNetCore=BlockchainNetCoreFactory.createDefaultBlockchainNetCore(account,toCaresponse);
        blockchainNetCore.start();


    }


    private  static ToCaresponse TakeCa(Account account){
        HttpClientImpl httpClient=new HttpClientImpl();
        httpClient.setCaIp(API.Caip); //设置Iot客户端所对应的gateway的ip地址
        ToCarequest toCarequest=new ToCarequest();
        toCarequest.setName(API.CloundName);
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

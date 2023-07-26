package com.zl.blockIot.net.Server;

import com.zl.blockIot.net.client.HttpClientImpl;
import com.zl.blockIot.net.dao.*;
import com.zl.blockchain.blockIot.crypto.AccountUtil;
import com.zl.blockchain.blockIot.crypto.model.Account;

import java.nio.charset.StandardCharsets;

public class HttpServerHandlerResolver {


    private Account account; //账户

    private ToCaresponse tocaresponse; //账户的证书


    public HttpServerHandlerResolver(Account account, ToCaresponse tocaresponse){
        this.account=account;
        this.tocaresponse=tocaresponse;
    }


    public EstoIotResponse estoIot(EstoIotrequest estoIotrequest){

            //验证证书
            ToCaresponse toCaresponse=estoIotrequest.getGatewaytoCaresponse();
            Boolean b= AccountUtil.verifySignature(API.CAPublicKey,(toCaresponse.getPublickkey()+toCaresponse.getName()).getBytes(StandardCharsets.UTF_8),toCaresponse.getSig());
            if(b){
                //通过证书中的公钥验证签名
                byte[] bb=(estoIotrequest.getTaskNumber()+String.valueOf(estoIotrequest.getTaskSize())+estoIotrequest.getEsNumber()).getBytes(StandardCharsets.UTF_8);
                Boolean c=AccountUtil.verifySignature(toCaresponse.getPublickkey(),bb,estoIotrequest.getSig());
                if(c){

                    EstoIotResponse estoIotResponse=new EstoIotResponse();
                    estoIotResponse.setData(estoIotrequest.getTaskNumber()+estoIotrequest.getEsNumber());
                    estoIotResponse.setEsNumber(estoIotrequest.getEsNumber());
                    estoIotResponse.setTaskNumber(estoIotrequest.getTaskNumber());
                    estoIotResponse.setTaskSize(estoIotrequest.getTaskSize());
                    estoIotResponse.setSig(AccountUtil.signature(account.getPrivateKey(),(estoIotResponse.getTaskNumber()+estoIotResponse.getEsNumber()+String.valueOf(estoIotResponse.getTaskSize())+estoIotResponse.getData()).getBytes(StandardCharsets.UTF_8)));
                    estoIotResponse.setToCaresponse(tocaresponse);
                    return estoIotResponse;
                }
                else{
                    System.out.println("es来的数据请求验证失败");
                }
            }
            return null;
        }

        public EstoIotdataResponse estoIotdata(EstoIotdatarequest estoIotdatarequest){
            //验证证书
            ToCaresponse toCaresponse=estoIotdatarequest.getToCaresponse();
            Boolean b= AccountUtil.verifySignature(API.CAPublicKey,(toCaresponse.getPublickkey()+toCaresponse.getName()).getBytes(StandardCharsets.UTF_8),toCaresponse.getSig());
            if(b) {
                //通过证书中的公钥验证签名
                byte[]bb=(estoIotdatarequest.getTaskNumber()+estoIotdatarequest.getEsNumber()+new String(estoIotdatarequest.getDatahash())+estoIotdatarequest.getDatalocation()).getBytes(StandardCharsets.UTF_8);
                Boolean c = AccountUtil.verifySignature(toCaresponse.getPublickkey(),bb,estoIotdatarequest.getSig());
                if(c){
                    EstoIotdataResponse estoIotdataResponse=new EstoIotdataResponse();
                    estoIotdataResponse.setTaskNumber(estoIotdatarequest.getTaskNumber());
                    estoIotdataResponse.setEsNumber(estoIotdatarequest.getEsNumber());
                    estoIotdataResponse.setaBoolean(Boolean.TRUE);
                    estoIotdataResponse.setSig(AccountUtil.signature(account.getPrivateKey(),(estoIotdataResponse.getTaskNumber()+estoIotdataResponse.getEsNumber()+estoIotdataResponse.getaBoolean()).getBytes(StandardCharsets.UTF_8)));
                    estoIotdataResponse.setToCaresponse(tocaresponse);
                    return estoIotdataResponse;
                }else {
                    System.out.println("err");
                }

            }
            return null;
        }


}

package com.zl.blockchain.core.tool;

import com.zl.blockchain.dto.*;
import com.zl.blockchain.core.model.transaction.Transaction;
import com.zl.blockchain.crypto.AccountUtil;
import com.zl.blockchain.dto.ToCaresponse;
import com.zl.blockchain.dto.TransactionDto;
import com.zl.blockchain.util.ByteUtil;
import com.zl.blockchain.util.StringUtil;

import java.nio.charset.StandardCharsets;


/**
 * Transaction工具类
 *
 */
public class TransactionTool {
    /**
     * 计算交易哈希
     */
    public static String calculateTransactionHash(Transaction transaction){
        TransactionDto transactionDto = Model2DtoTool.transaction2TransactionDto(transaction);
        return TransactionDtoTool.calculateTransactionHash(transactionDto);
    }

    /**
     * 验证交易的hash是否正确
     * @param transaction
     * @return
     */
    public static  boolean checkTransactionHash(Transaction transaction){
        String transhash=calculateTransactionHash(transaction);
        return StringUtil.equals(transhash,transaction.getTransactionHash());

    }


    /**
     * 验证交易的签名
     * @param transaction
     * @return
     */
    public static boolean checkTransactionSig(Transaction transaction){
        //验证Es和Iot的证书是否合法
        ToCaresponse EsCA=transaction.getEsCA();
        ToCaresponse IotCA=transaction.getIotCA();

        boolean b,b1,c,c1=false;

         b= AccountUtil.verifySignature(API.CAPublicKey,(EsCA.getPublickkey()+EsCA.getName()).getBytes(StandardCharsets.UTF_8), ByteUtil.hexStringToBytes(EsCA.getSig()));
         b1= AccountUtil.verifySignature(API.CAPublicKey,(IotCA.getPublickkey()+IotCA.getName()).getBytes(StandardCharsets.UTF_8), ByteUtil.hexStringToBytes(IotCA.getSig()));


        if(b){
            if(b1){
                byte[]bb=(transaction.getTaskNumber()+transaction.getEsNumber()+new String(transaction.getDatahash())+transaction.getDatalocation()).getBytes(StandardCharsets.UTF_8);
                 c=AccountUtil.verifySignature(EsCA.getPublickkey(),bb,ByteUtil.hexStringToBytes(transaction.getEsSig()));

                byte[]bb1=(transaction.getTaskNumber()+transaction.getEsNumber()+transaction.getaBoolean()).getBytes(StandardCharsets.UTF_8);
                 c1=AccountUtil.verifySignature(IotCA.getPublickkey(),bb1,ByteUtil.hexStringToBytes(transaction.getIotSig()));


                if(c){
                    if(c1){
                        return true;
                    }
                    return false;
                }
                return false;

            }
            return false;
        }
        return false;
    }

}

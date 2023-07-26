package com.zl.blockchain.core.tool;

import com.zl.blockchain.crypto.AccountUtil;
import com.zl.blockchain.crypto.Sha256Util;
import com.zl.blockchain.dto.*;

import com.zl.blockchain.util.ByteUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * TransactionDto工具类
 *
 */
public class TransactionDtoTool {


    /**
     * 利用交易[任务编号、es编号、数据hash、数据地址]计算交易的hash
     * @param transactionDto
     * @return
     */
    public static String calculateTransactionHash(TransactionDto transactionDto){
        byte[] bytesTransaction = bytesTransaction(transactionDto);
        byte[] bytesTransactionHash = Sha256Util.doubleDigest(bytesTransaction);
        return ByteUtil.bytesToHexString(bytesTransactionHash);
    }

    //region 序列化与反序列化
    /**
     * 序列化。将交易[任务编号、es编号、数据hash、数据地址]转换为字节数组
     */
    public static byte[] bytesTransaction(TransactionDto transactionDto) {
        byte[] TaskNumber=ByteUtil.stringToUtf8Bytes(transactionDto.getTaskNumber());
        byte[] EsNumber=ByteUtil.stringToUtf8Bytes(transactionDto.getEsNumber());
        byte[] datahash=transactionDto.getDatahash();
        byte[] datalocation=ByteUtil.stringToUtf8Bytes(transactionDto.getDatalocation());
        byte[] data=ByteUtil.concatenate4(TaskNumber,EsNumber,datahash,datalocation);
        return data;
    }
    //endregion

}

package com.zl.blockchain.core.impl;

import com.zl.blockchain.core.CoreConfiguration;
import com.zl.blockchain.core.UnconfirmedTransactionDatabase;
import com.zl.blockchain.core.tool.TransactionDtoTool;
import  com.zl.blockchain.dto.*;
import com.zl.blockchain.util.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 默认实现
 * 未确认交易数据库默认实现类
 */
public class UnconfirmedTransactionDatabaseDefaultImpl extends UnconfirmedTransactionDatabase {

    private static final String UNCONFIRMED_TRANSACTION_DATABASE_NAME = "UnconfirmedTransactionDatabase";  //未确认交易数据库

    public UnconfirmedTransactionDatabaseDefaultImpl(CoreConfiguration coreConfiguration) {  //配置类设置
        this.coreConfiguration = coreConfiguration;
    }

    /**
     * 插入交易
     * @param transaction
     * @return
     */
    @Override
    public boolean insertTransaction(TransactionDto transaction) {
        try {
            String transactionHash = TransactionDtoTool.calculateTransactionHash(transaction);//计算交易的hash
            KvDbUtil.put(getUnconfirmedTransactionDatabasePath(), getKey(transactionHash), EncodeDecodeTool.encode(transaction));//未确认数据库添加数据<交易hash、交易编码到字节数组>
            return true;
        }catch (Exception e){
            LogUtil.error("交易["+ JsonUtil.toString(transaction)+"]放入交易池异常。",e);  //log打印错误信息 “交易放入交易池异常”
            return false;
        }
    }

    /**
     * 批量提取交易 （从哪里开始，大小）
     * @param from
     * @param size
     * @return
     */
    @Override
    public List<TransactionDto> selectTransactions(long from, long size) {
        List<TransactionDto> transactionDtos = new ArrayList<>(); //创建交易列表
        List<byte[]> bytesTransactionDtos = KvDbUtil.gets(getUnconfirmedTransactionDatabasePath(),from,size);//从未确认交易数据库获取交易（from、size）
        if(bytesTransactionDtos != null){ //如果交易不为空
            for(byte[] bytesTransactionDto:bytesTransactionDtos){ //交易字节数组
                TransactionDto transactionDto = EncodeDecodeTool.decode(bytesTransactionDto,TransactionDto.class);//将交易字节数组转换为交易对象
                transactionDtos.add(transactionDto);//交易列表添加该交易对象
            }
        }
        return transactionDtos;
    }

    /**
     *删除交易（通过交易hash）
     * @param transactionHash
     */
    @Override
    public void deleteByTransactionHash(String transactionHash) {
        KvDbUtil.delete(getUnconfirmedTransactionDatabasePath(), getKey(transactionHash));//从未确认交易数据库中删除该key值的交易
    }

    @Override
    public void deleByTransaction(TransactionDto transaction) {
        String transactionHash = TransactionDtoTool.calculateTransactionHash(transaction);//计算交易的hash
        deleteByTransactionHash(transactionHash);
    }


    /**
     * 选择交易（通过交易hash）
     * @param transactionHash
     * @return
     */
    @Override
    public TransactionDto selectTransactionByTransactionHash(String transactionHash) {
        byte[] byteTransactionDto = KvDbUtil.get(getUnconfirmedTransactionDatabasePath(), getKey(transactionHash));//从未确认数据库中通过交易hash获取交易字节数组
        if(byteTransactionDto == null){
            return null;
        }
        return EncodeDecodeTool.decode(byteTransactionDto,TransactionDto.class); //解码交易字节数组到交易
    }

    /**
     * 创建未确认交易数据库
     * @return
     */
    private String getUnconfirmedTransactionDatabasePath() {
        return FileUtil.newPath(coreConfiguration.getCorePath(), UNCONFIRMED_TRANSACTION_DATABASE_NAME);//在corepath下，创建未确认数据库
    }

    /**
     * 获取key，16进制字符串转为bytes
     * @param transactionHash
     * @return
     */
    private byte[] getKey(String transactionHash){
        return ByteUtil.hexStringToBytes(transactionHash);
    }
}

package com.zl.blockchain.core.model.transaction;

import com.sun.org.apache.xpath.internal.operations.Bool;
import  com.zl.blockchain.dto.*;

import java.io.Serializable;
import java.util.List;



public class Transaction implements Serializable {

    private  String TaskNumber; //任务编号

    private String EsNumber; //Es编号

    private byte[] datahash; //数据hash

    private  String datalocation; // 数据地址

    private Boolean aBoolean;//确认信号

    /**
     * Es对【任务编号、Es编号、数据hash、数据地址】 进行签名
     */
    private  String EsSig;
    /**
     * Es的证书
     */
    private ToCaresponse EsCA;

    /**
     * IOT对【任务编号、Es编号、确认信息】进行签名
     */
    private String IotSig;

    /**
     * IOT的证书
     */
    private ToCaresponse IotCA;

    /**
     * 交易哈希
     * 交易哈希用来表示一个独一无二的交易编号。
     * 交易哈希是交易的摘要。可以认为交易哈希和交易一一对应。交易确定了，交易哈希也就确定了。交易哈希确定了，交易也就确定了。
     * 这里要求区块链系统不允许同一个哈希被使用两次或是两次以上(一个哈希同时被交易哈希、区块哈希使用也不行)。
     *
     * 冗余字段，这个值可以由区块链系统推算出来
     */
    private String transactionHash;


    /**
     * 交易在区块中的序列号，每个区块的第一笔交易的序列号都是从1开始计算，其后交易的序列号依次递增1。
     *
     * 冗余字段，这个值可以由区块链系统推算出来
     */
    private long transactionIndex;


    /**
     * 交易在区块链中的高度，这是一个全局高度，区块链系统中的第一笔交易，交易高度为1，其后交易的高度依次递增1。
     *
     * 冗余字段，这个值可以由区块链系统推算出来
     */
    private long transactionHeight;


    /**
     *
     * 交易所在区块的区块高度。
     *
     * 冗余字段，这个值可以由区块链系统推算出来
     */
    private long blockHeight;


    //region get set



    public String getTransactionHash() {
        return transactionHash;
    }

    public void setTransactionHash(String transactionHash) {
        this.transactionHash = transactionHash;
    }

    public long getTransactionIndex() {
        return transactionIndex;
    }

    public void setTransactionIndex(long transactionIndex) {
        this.transactionIndex = transactionIndex;
    }

    public long getTransactionHeight() {
        return transactionHeight;
    }

    public void setTransactionHeight(long transactionHeight) {
        this.transactionHeight = transactionHeight;
    }

    public long getBlockHeight() {
        return blockHeight;
    }

    public void setBlockHeight(long blockHeight) {
        this.blockHeight = blockHeight;
    }

    public String getTaskNumber() {
        return TaskNumber;
    }

    public String getEsNumber() {
        return EsNumber;
    }

    public byte[] getDatahash() {
        return datahash;
    }

    public String getDatalocation() {
        return datalocation;
    }

    public Boolean getaBoolean() {
        return aBoolean;
    }

    public void setaBoolean(Boolean aBoolean) {
        this.aBoolean = aBoolean;
    }

    public String getEsSig() {
        return EsSig;
    }

    public String getIotSig() {
        return IotSig;
    }

    public ToCaresponse getEsCA() {
        return EsCA;
    }

    public ToCaresponse getIotCA() {
        return IotCA;
    }

    public void setTaskNumber(String taskNumber) {
        TaskNumber = taskNumber;
    }

    public void setEsNumber(String esNumber) {
        EsNumber = esNumber;
    }

    public void setDatahash(byte[] datahash) {
        this.datahash = datahash;
    }

    public void setDatalocation(String datalocation) {
        this.datalocation = datalocation;
    }

    public void setEsCA(ToCaresponse esCA) {
        EsCA = esCA;
    }

    public void setEsSig(String esSig) {
        EsSig = esSig;
    }

    public void setIotCA(ToCaresponse iotCA) {
        IotCA = iotCA;
    }

    public void setIotSig(String iotSig) {
        IotSig = iotSig;
    }

    //endregion
}

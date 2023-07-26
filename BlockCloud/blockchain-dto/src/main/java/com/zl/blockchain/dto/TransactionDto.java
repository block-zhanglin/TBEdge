package com.zl.blockchain.dto;


import java.io.Serializable;

/**
 * 交易
 * 属性含义参考
 *
 */
public class TransactionDto implements Serializable {

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






    //region get set


    public Boolean getaBoolean() {
        return aBoolean;
    }

    public void setaBoolean(Boolean aBoolean) {
        this.aBoolean = aBoolean;
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

}
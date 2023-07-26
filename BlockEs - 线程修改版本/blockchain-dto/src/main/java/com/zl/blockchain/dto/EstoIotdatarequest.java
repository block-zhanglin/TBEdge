package com.zl.blockchain.dto;

/**
 * Es层处理完Iot的数据之后，返回Iot设备确认信息
 */
public class EstoIotdatarequest {

    private String  TaskNumber; // 十六进制字符串（前4位代表设备编号，后8位代表任务序号）

    private String EsNumber; //Es编号

    private byte[] datahash; //数据hash

    private String datalocation;//数据地址

    private String sig; //签名

    private ToCaresponse toCaresponse; //Es的证书

    public void setToCaresponse(ToCaresponse toCaresponse) {
        this.toCaresponse = toCaresponse;
    }

    public void setSig(String sig) {
        this.sig = sig;
    }

    public ToCaresponse getToCaresponse() {
        return toCaresponse;
    }

    public String getSig() {
        return sig;
    }

    public void setDatahash(byte[] datahash) {
        this.datahash = datahash;
    }

    public void setEsNumber(String esNumber) {
        EsNumber = esNumber;
    }

    public void setDatalocation(String datalocation) {
        this.datalocation = datalocation;
    }

    public void setTaskNumber(String taskNumber) {
        TaskNumber = taskNumber;
    }


    public String getTaskNumber() {
        return TaskNumber;
    }

    public String getDatalocation() {
        return datalocation;
    }

    public String getEsNumber() {
        return EsNumber;
    }

    public byte[] getDatahash() {
        return datahash;
    }
}

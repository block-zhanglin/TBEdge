package com.zl.blockchain.dto;


/**
 * Es层向Iot层发送数据请求 ---response
 */
public class EstoIotResponse {

    private  String TaskNumber; //任务编号

    private float TaskSize; //任务大小

    private String EsNumber; //Es编号

    private String data; //数据

    private String sig;//IOT的签名

    private ToCaresponse toCaresponse; //IOT的证书

    public String getSig() {
        return sig;
    }

    public ToCaresponse getToCaresponse() {
        return toCaresponse;
    }

    public void setSig(String sig) {
        this.sig = sig;
    }

    public void setToCaresponse(ToCaresponse toCaresponse) {
        this.toCaresponse = toCaresponse;
    }

    public String getData() {
        return data;
    }

    public String getTaskNumber() {
        return TaskNumber;
    }

    public String getEsNumber() {
        return EsNumber;
    }

    public float getTaskSize() {
        return TaskSize;
    }

    public void setTaskSize(float taskSize) {
        TaskSize = taskSize;
    }

    public void setTaskNumber(String taskNumber) {
        TaskNumber = taskNumber;
    }

    public void setEsNumber(String esNumber) {
        EsNumber = esNumber;
    }

    public void setData(String data) {
        this.data = data;
    }
}

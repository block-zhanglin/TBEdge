package com.zl.blockchain.dto;

/**
 * Es层向Iot层发送数据请求
 */
public class EstoIotrequest {

    private String  TaskNumber; // 十六进制字符串（前4位代表设备编号，后8位代表任务序号）

    private float TaskSize; //任务大小

    private String EsNumber; //Es编号

    private String sig;//签名

    private ToCaresponse gatewaytoCaresponse;//证书


    public String getSig() {
        return sig;
    }

    public ToCaresponse getGatewaytoCaresponse() {
        return gatewaytoCaresponse;
    }

    public void setSig(String sig) {
        this.sig = sig;
    }

    public void setGatewaytoCaresponse(ToCaresponse gatewaytoCaresponse) {
        this.gatewaytoCaresponse = gatewaytoCaresponse;
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

    public String getTaskNumber() {
        return TaskNumber;
    }

    public String getEsNumber() {
        return EsNumber;
    }

    public float getTaskSize() {
        return TaskSize;
    }
}

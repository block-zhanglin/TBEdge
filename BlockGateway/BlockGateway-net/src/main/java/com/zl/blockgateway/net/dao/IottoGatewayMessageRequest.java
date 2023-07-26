package com.zl.blockgateway.net.dao;


/**
 * Iot设备向网关层请求的任务信息【任务编号、数据大小】
 */
public class IottoGatewayMessageRequest {

    private String  TaskNumber; // 十六进制字符串（前4位代表设备编号，后8位代表任务序号）

    private float TaskSize; //任务大小

    private String sig;//签名

    private ToCaresponse iottoCaresponse;//证书


    public String getSig() {
        return sig;
    }

    public void setSig(String sig) {
        this.sig = sig;
    }

    public ToCaresponse getIottoCaresponse() {
        return iottoCaresponse;
    }

    public void setIottoCaresponse(ToCaresponse iottoCaresponse) {
        this.iottoCaresponse = iottoCaresponse;
    }

    public void setTaskNumber(String taskNumber) {
        TaskNumber = taskNumber;
    }

    public void setTaskSize(float taskSize) {
        TaskSize = taskSize;
    }

    public String getTaskNumber() {
        return TaskNumber;
    }

    public float getTaskSize() {
        return TaskSize;
    }
}

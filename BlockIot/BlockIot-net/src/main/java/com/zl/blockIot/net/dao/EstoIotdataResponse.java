package com.zl.blockIot.net.dao;

/**
 * Es层处理完Iot的数据之后，返回Iot设备确认信息 -----response
 */

public class EstoIotdataResponse {


    private String  TaskNumber; // 十六进制字符串（前4位代表设备编号，后8位代表任务序号）

    private String EsNumber; //Es编号

    private Boolean aBoolean;//确认信号

    private String sig;//IOT的私钥

    private ToCaresponse toCaresponse;//IOT证书

    public String getSig() {
        return sig;
    }

    public ToCaresponse getToCaresponse() {
        return toCaresponse;
    }

    public String getEsNumber() {
        return EsNumber;
    }

    public String getTaskNumber() {
        return TaskNumber;
    }

    public Boolean getaBoolean() {
        return aBoolean;
    }

    public void setSig(String sig) {
        this.sig = sig;
    }

    public void setToCaresponse(ToCaresponse toCaresponse) {
        this.toCaresponse = toCaresponse;
    }

    public void setEsNumber(String esNumber) {
        EsNumber = esNumber;
    }

    public void setTaskNumber(String taskNumber) {
        TaskNumber = taskNumber;
    }

    public void setaBoolean(Boolean aBoolean) {
        this.aBoolean = aBoolean;
    }

}

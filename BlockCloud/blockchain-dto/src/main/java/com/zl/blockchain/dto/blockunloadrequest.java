package com.zl.blockchain.dto;

public class blockunloadrequest {

    private long originheight;// 卸载区块的起点
    private long endheight;// 卸载区块的终点

    private  long timestap; //当前时间戳

    private  String sig;  //云中心的签名

    private ToCaresponse toCaresponse;//证书

    public String getSig() {
        return sig;
    }

    public void setSig(String sig) {
        this.sig = sig;
    }

    public long getTimestap() {
        return timestap;
    }

    public void setTimestap(long timestap) {
        this.timestap = timestap;
    }

    public ToCaresponse getToCaresponse() {
        return toCaresponse;
    }

    public void setToCaresponse(ToCaresponse toCaresponse) {
        this.toCaresponse = toCaresponse;
    }

    public long getEndheight() {
        return endheight;
    }

    public void setEndheight(long endheight) {
        this.endheight = endheight;
    }

    public long getOriginheight() {
        return originheight;
    }

    public void setOriginheight(long originheight) {
        this.originheight = originheight;
    }
}

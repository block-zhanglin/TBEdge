package com.zl.blockchain.dto;


/**
 * 节点的存储空间已经满了
 */
public class Esfullrequest {

    private long timestamp;

    private String Sig;

    private ToCaresponse toCaresponse;

    public void setToCaresponse(ToCaresponse toCaresponse) {
        this.toCaresponse = toCaresponse;
    }

    public void setSig(String sig) {
        Sig = sig;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public ToCaresponse getToCaresponse() {
        return toCaresponse;
    }

    public String getSig() {
        return Sig;
    }

    public long getTimestamp() {
        return timestamp;
    }

}

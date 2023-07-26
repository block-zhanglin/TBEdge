package com.zl.blockchain.dto;

import java.io.Serializable;
import java.util.List;

/**
 * 区块
 * 属性含义参考
 */
public class BlockDto implements Serializable {

    //区块产生的时间戳
    private long timestamp;

    //上一个区块的哈希
    private String previousHash;

    //区块里的交易
    private List<TransactionDto> transactions;

    private Committee committee;


    /**
     * 区块的签名：区块发布者对区块的hash进行签名
     */
    private String sig;

    private ToCaresponse EsCA;




    //region get set


    public Committee getCommittee() {
        return committee;
    }

    public void setCommittee(Committee committee) {
        this.committee = committee;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public List<TransactionDto> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<TransactionDto> transactions) {
        this.transactions = transactions;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }

    public ToCaresponse getEsCA() {
        return EsCA;
    }

    public String getSig() {
        return sig;
    }

    public void setEsCA(ToCaresponse esCA) {
        EsCA = esCA;
    }

    public void setSig(String sig) {
        this.sig = sig;
    }

    //endregion
}

package com.zl.blockchain.core.model;

import com.zl.blockchain.core.model.transaction.Transaction;
import com.zl.blockchain.dto.ToCaresponse;

import java.io.Serializable;
import java.util.List;

/**
 * 区块
 *
 */
public class Block implements Serializable {

    /**
     * 区块产生的时间戳
     * 这里约定：区块的时间戳一定比前一个区块的时间戳大，一定比当前时间小。
     */
    private long timestamp;


    /**
     * 上一个区块的哈希
     */
    private String previousHash;

    /**
     * 区块里的交易
     */
    private List<Transaction> transactions;

    /**
     * 区块的签名：区块发布者对区块的hash进行签名
     */
    private String sig;

    private ToCaresponse EsCA;



    /**
     * 区块高度，创世区块的高度是0，标准区块的高度从1开始，依次递增加1。
     *
     * 冗余字段，这个值可以由区块链系统推算出来
     */
    private long height;

    /**
     * 默克尔树根
     * 由transactions生成。
     * 既然这个字段是由交易列表生成的，这个字段每次需要时完全可以自己生成？为什么需要这个字段？
     * 第一：这个值是区块里所有交易的摘要。这个值确定了，交易也就固定了。
     * 第二：请参考SPV。
     *
     * 冗余字段，这个值可以由区块链系统推算出来
     */
    private String merkleTreeRoot;

    /**
     * 区块哈希：由timestamp、previousBlockHash、merkleRoot共同作用使用Hash算法生成。
     *
     *  冗余字段，这个值可以由区块链系统推算出来
     */
    private String hash;

    /**
     * 区块中的交易总笔数
     *
     * 冗余字段，这个值可以由区块链系统推算出来
     */
    private long transactionCount;

    /**
     * 上一个区块最后一笔交易在所有交易中的高度。
     * 这个序列号是站在整个区块链的角度而产生的，而不是站在这个区块的角度而产生的。
     * 它的值等于：(高度低于当前区块的所有区块中包含的)交易数量之和
     *
     * 冗余字段，这个值可以由区块链系统推算出来
     */
    private long previousTransactionHeight;


    //region get set

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }

    public long getHeight() {
        return height;
    }

    public void setHeight(long height) {
        this.height = height;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public String getMerkleTreeRoot() {
        return merkleTreeRoot;
    }

    public void setMerkleTreeRoot(String merkleTreeRoot) {
        this.merkleTreeRoot = merkleTreeRoot;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }


    public long getTransactionCount() {
        return transactionCount;
    }

    public void setTransactionCount(long transactionCount) {
        this.transactionCount = transactionCount;
    }

    public String getSig() {
        return sig;
    }

    public void setSig(String sig) {
        this.sig = sig;
    }

    public ToCaresponse getToCaresponse() {
        return EsCA;
    }

    public void setToCaresponse(ToCaresponse EsCA) {
        this.EsCA = EsCA;
    }

    public long getPreviousTransactionHeight() {
        return previousTransactionHeight;
    }

    public void setEsCA(ToCaresponse esCA) {
        EsCA = esCA;
    }

    public void setPreviousTransactionHeight(long previousTransactionHeight) {
        this.previousTransactionHeight = previousTransactionHeight;
    }
    //endregion
}

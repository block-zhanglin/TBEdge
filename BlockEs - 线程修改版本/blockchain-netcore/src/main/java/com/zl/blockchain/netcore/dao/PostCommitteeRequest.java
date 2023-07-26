package com.zl.blockchain.netcore.dao;


import com.zl.blockchain.netcore.model.Node;

import java.util.List;

public class PostCommitteeRequest {


    private long index;//委员会索引

    private List<Node> nodes;//{节点、节点、节点、节点·····}
    private long begintime; //发布委员会的开始时间
    private String sig;//CA的签名

    public void setIndex(long index) {
        this.index = index;
    }

    public long getIndex() {
        return index;
    }
    public List<Node> getNodes() {
        return nodes;
    }

    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }

    public String getSig() {
        return sig;
    }

    public void setSig(String sig) {
        this.sig = sig;
    }

    public long getBegintime() {
        return begintime;
    }

    public void setBegintime(long begintime) {
        this.begintime = begintime;
    }
}

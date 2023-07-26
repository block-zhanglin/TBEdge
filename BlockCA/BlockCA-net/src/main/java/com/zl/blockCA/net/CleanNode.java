package com.zl.blockCA.net;

import com.zl.blockCA.net.client.HttpClient;
import com.zl.blockCA.net.client.HttpClientImpl;
import com.zl.blockCA.net.dao.PingRequest;
import com.zl.blockCA.net.dao.PingResponse;
import com.zl.blockCA.net.model.Node;
import com.zl.blockCA.net.service.NodeService;
import util.ThreadUtil;

import java.util.List;

public class CleanNode {

    private NodeService nodeService;

    public CleanNode(NodeService nodeService){
        this.nodeService=nodeService;
    }
    public void start(){
        while (true){
            cleanDeadNodes();
            ThreadUtil.millisecondSleep(10*60*1000);
        }
    }


    private void cleanDeadNodes() {
        List<Node> nodes = nodeService.queryAllNodes();
        if(nodes == null || nodes.size()==0){
            return;
        }

        for(Node node:nodes){
//            System.out.println(node.getIp());
            HttpClient nodeClient = new HttpClientImpl(node.getIp());
            PingRequest pingRequest = new PingRequest();
            PingResponse pingResponse = nodeClient.pingNode(pingRequest);
            if(pingResponse == null){
                nodeService.deleteNode(node.getIp());
                System.out.println("节点清理器发现死亡节点["+node.getIp()+"]，已在节点数据库中将该节点删除了。");
            }
        }


    }
}

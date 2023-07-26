package com.zl.blockCA.net;

import com.zl.blockCA.net.Server.API;
import com.zl.blockCA.net.client.HttpClient;
import com.zl.blockCA.net.client.HttpClientImpl;
import com.zl.blockCA.net.dao.PostCommitteeRequest;
import com.zl.blockCA.net.dao.PostCommitteeResponse;
import com.zl.blockCA.net.model.Node;
import com.zl.blockCA.net.service.NodeService;
import com.zl.blockchain.blockCA.crypto.AccountUtil;
import com.zl.blockchain.blockCA.crypto.model.Account;
import util.*;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PostCommittee {

    private  NodeService nodeService;
    private Account account;

    public  PostCommittee(NodeService nodeService,Account account){
        this.nodeService=nodeService;
        this.account=account;
    }

    public void start(){
        System.out.println(Long.valueOf(API.init));
        ThreadUtil.millisecondSleep(Long.valueOf(API.init)); //等待Es节点相互通信成功,等待节点生成交易
        while (true){
            CommitteePost();
            ThreadUtil.millisecondSleep(Long.valueOf(BlockSetting.CommitteeTime)); // 每隔X分钟重新发布一次委员会
        }
    }
    public void CommitteePost(){
        List<Node>nodes=nodeService.queryAllNodes();
        List<Node> CommitteeNode = new ArrayList<>(); //委员会中成员
        System.out.println(nodes.size());
        if(nodes.size()<Integer.valueOf(BlockSetting.CommitteeCount)){
            return;
        }else {
            Random random=new Random();
            for(int i = 0; i<Integer.valueOf(BlockSetting.CommitteeCount); i++){
                int intRandom;
                if(nodes.size()==1){
                    intRandom=0;
                }else {
                    intRandom=random.nextInt(nodes.size()); //从【0，size-1】随机选出一个值
                }
                CommitteeNode.add(nodes.get(intRandom));
                nodes.remove(nodes.get(intRandom));
            }
        }
        List<Node>nodeList=nodeService.queryAllNodes();
        PostCommitteeRequest postCommitteeRequest=new PostCommitteeRequest();
        postCommitteeRequest.setNodes(CommitteeNode);
        postCommitteeRequest.setIndex(BlockSetting.getIndex()+1);
        postCommitteeRequest.setBegintime(TimeUtil.millisecondTimestamp());
        postCommitteeRequest.setSig(AccountUtil.signature(account.getPrivateKey(),ByteUtil.concatenate(String.valueOf(postCommitteeRequest.getIndex()+String.valueOf(postCommitteeRequest.getBegintime())).getBytes(StandardCharsets.UTF_8),EncodeDecodeTool.encode(postCommitteeRequest.getNodes()))));

        for(Node node:nodeList){
            HttpClient httpClient=new HttpClientImpl(node.getIp()); //对于每个node构建httpclient
            PostCommitteeResponse postCommitteeResponse=httpClient.postcommittee(postCommitteeRequest);
        }
        BlockSetting.setIndex(BlockSetting.getIndex()+1); //改变委员会迭代数
        for(Node node:CommitteeNode){
            System.out.println(node.getIp());
        }
        System.out.println("发布了委员会"+BlockSetting.getIndex());
    }
}

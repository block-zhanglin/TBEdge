package com.zl.blockCA.net;

import com.zl.blockCA.net.Server.API;
import com.zl.blockCA.net.Server.CAServer;
import com.zl.blockCA.net.Server.HttpServerHandlerResolver;
import com.zl.blockCA.net.client.HttpClient;
import com.zl.blockCA.net.client.HttpClientImpl;
import com.zl.blockCA.net.configuration.NetcoreConfigurationImpl;
import com.zl.blockCA.net.dao.*;
import com.zl.blockCA.net.model.Node;
import com.zl.blockCA.net.service.NodeService;
import com.zl.blockCA.net.service.NodeServiceImpl;
import com.zl.blockchain.blockCA.crypto.AccountUtil;
import com.zl.blockchain.blockCA.crypto.ByteUtil;
import com.zl.blockchain.blockCA.crypto.LogUtil;
import com.zl.blockchain.blockCA.crypto.model.Account;
import util.BlockSetting;
import util.ResourcePathTool;
import java.nio.charset.StandardCharsets;
import java.util.List;

import java.util.Random;

public class BlockCAnetcore {

    public static  void  main(String []args){


        CustomConfigurationUtil.run();

        Account account= AccountUtil.accountFromPrivateKey(ByteUtil.bytesToHexString("CA".getBytes(StandardCharsets.UTF_8)));

        NetcoreConfigurationImpl netcoreConfiguration=new NetcoreConfigurationImpl(Integer.valueOf(API.CAPort));
        HttpServerHandlerResolver httpServerHandlerResolver=new HttpServerHandlerResolver();
        CAServer caServer=new CAServer(netcoreConfiguration,httpServerHandlerResolver);
        /**
         * 开启服务器端，并接受请求
         */
        new Thread(()->caServer.start()).start();


        /**
         * 定时清理无法ping通节点
         */
        NodeDao nodeDao=new NodeDaoImpl();
        NodeService nodeService=new NodeServiceImpl(nodeDao);
        CleanNode cleanNode=new CleanNode(nodeService);
//        new Thread(()->cleanNode.start()).start();

        /**
         * 查询区块链高度
         */
        SearchBlockHeight searchBlockHeight=new SearchBlockHeight(nodeService);
        new Thread(()->searchBlockHeight.start()).start();

        /**
         * 发布委员会
         */
        PostCommittee postCommittee=new PostCommittee(nodeService,account);
        new Thread(()->postCommittee.start()).start();


    }

}

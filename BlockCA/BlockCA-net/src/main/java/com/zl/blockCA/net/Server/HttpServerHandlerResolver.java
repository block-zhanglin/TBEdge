package com.zl.blockCA.net.Server;

import com.zl.blockCA.net.dao.*;
import com.zl.blockCA.net.model.Node;
import com.zl.blockCA.net.service.NodeService;
import com.zl.blockCA.net.service.NodeServiceImpl;
import com.zl.blockchain.blockCA.crypto.AccountUtil;
import com.zl.blockchain.blockCA.crypto.ByteUtil;
import com.zl.blockchain.blockCA.crypto.model.Account;

import java.nio.charset.StandardCharsets;

public class HttpServerHandlerResolver {


    /**
     * 返回证书
     * @param toCarequest
     * @return
     */
        public toCaresponse toCA(String requestip,toCarequest toCarequest){

            /**
             * CA构建返回证书
             */
            Account account= AccountUtil.accountFromPrivateKey(ByteUtil.bytesToHexString("CA".getBytes(StandardCharsets.UTF_8)));
            toCaresponse toCaresponse=new toCaresponse();
            toCaresponse.setIp(requestip);
            toCaresponse.setName(toCarequest.getName());
            toCaresponse.setPublickkey(toCarequest.getPublicKey());
            toCaresponse.setSig(AccountUtil.signature(account.getPrivateKey(),(toCaresponse.getPublickkey()+toCaresponse.getName()).getBytes(StandardCharsets.UTF_8))); //签名公钥+信息

            String name=toCarequest.getName();
            if(name.contains("Es")){ ///是Es服务器的请求
                /**
                 * 将节点添加到节点数据库中
                 */
                Node node=new Node();
                node.setIp(requestip);
                node.setBlockchainHeight(0);
                NodeDao nodeDao=new NodeDaoImpl();
                NodeService nodeService=new NodeServiceImpl(nodeDao);
                nodeService.addNode(node);
                System.out.println("添加节点:"+node.getIp());
            }

            return toCaresponse;
        }

}

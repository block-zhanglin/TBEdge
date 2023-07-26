package com.zl.blockchain.netcore.Server;


import com.zl.blockchain.core.BlockchainCore;
import com.zl.blockchain.core.CoreConfiguration;
import com.zl.blockchain.core.UnconfirmedTransactionDatabase;
import com.zl.blockchain.core.impl.CoreConfigurationDefaultImpl;
import com.zl.blockchain.core.impl.UnconfirmedTransactionDatabaseDefaultImpl;
import com.zl.blockchain.core.model.Block;
import com.zl.blockchain.core.tool.Model2DtoTool;
import com.zl.blockchain.core.tool.ResourcePathTool;
import com.zl.blockchain.crypto.AccountUtil;
import com.zl.blockchain.crypto.model.Account;
import com.zl.blockchain.dto.*;
import com.zl.blockchain.netcore.BlockBroadcaster;
import com.zl.blockchain.netcore.client.HttpClientImpl;
import com.zl.blockchain.netcore.configuration.NetcoreConfiguration;
import com.zl.blockchain.netcore.dao.PostCommitteeRequest;
import com.zl.blockchain.netcore.dao.PostCommitteeResponse;
import com.zl.blockchain.netcore.model.Node;
import com.zl.blockchain.netcore.service.NodeService;
import com.zl.blockchain.setting.BlockSetting;
import com.zl.blockchain.util.*;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 请求处理分解类
 *
 */
public class HttpServerHandlerResolver {


    private Account account; //账户

    private ToCaresponse tocaresponse; //账户的证书

    private BlockchainCore blockchainCore;//区块链核心
    private NodeService nodeService;//节点服务
    private NetcoreConfiguration netcoreConfiguration;//netcore配置
    private BlockBroadcaster blockBroadcaster;


    public HttpServerHandlerResolver(Account account, ToCaresponse tocaresponse,NetcoreConfiguration netcoreConfiguration,BlockchainCore blockchainCore,NodeService nodeService,BlockBroadcaster blockBroadcaster){
        this.account=account;
        this.tocaresponse=tocaresponse;
        this.netcoreConfiguration=netcoreConfiguration;
        this.blockchainCore=blockchainCore;
        this.nodeService=nodeService;
        this.blockBroadcaster=blockBroadcaster;
    }


    /**
     *收到网关的分配信息【任务编号，数据大小，Es编号】，向指定的IOT设备发出数据请求【任务编号、数据大小，Es编号】；处理完成后，向
     * Iot设备发出确认信息
     * @param gatewaytoEsMessageRequest
     * @return
     */
    public GatewaytoEsMessageResponse gatewaytoEsMessage(GatewaytoEsMessageRequest gatewaytoEsMessageRequest){
        //验证证书
        ToCaresponse toCaresponse=gatewaytoEsMessageRequest.getGatewaytoCaresponse();
        Boolean b= AccountUtil.verifySignature(API.CAPublicKey,(toCaresponse.getPublickkey()+toCaresponse.getName()).getBytes(StandardCharsets.UTF_8), ByteUtil.hexStringToBytes(toCaresponse.getSig()));
        if(b){
            byte[]bb=(gatewaytoEsMessageRequest.getTaskNumber()+String.valueOf(gatewaytoEsMessageRequest.getTaskSize())+gatewaytoEsMessageRequest.getEsNumber()).getBytes(StandardCharsets.UTF_8);
            //通过证书中的公钥验证签名
            Boolean c=AccountUtil.verifySignature(toCaresponse.getPublickkey(),bb,ByteUtil.hexStringToBytes(gatewaytoEsMessageRequest.getSig()));
            if(c){
                HttpClientImpl httpClient=new HttpClientImpl();
                httpClient.setIotIp(API.Iotip);
                EstoIotResponse estoIotResponse=EsSendTask(gatewaytoEsMessageRequest,httpClient); //Es向Iot设备发出任务请求

                //如果es向iot发出任务请求未收到正确回复
                if(estoIotResponse==null){
                    return null;
                }

                try {
                    TimeUnit.SECONDS.sleep(20); //程序睡眠20秒,进行数据处理
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                EsGoBlockchainData esGoBlockchainData=EsSendConfirm(estoIotResponse,httpClient,account,tocaresponse);//Es向Iot设备发出确认

//                System.out.println(esGoBlockchainData.getEstoIotdataResponse().getTaskNumber());
                /**
                 * 构建需要上链的交易
                 */
                TransactionDto transactionDto=new TransactionDto();
                transactionDto.setTaskNumber(esGoBlockchainData.getEstoIotdataResponse().getTaskNumber());
                transactionDto.setEsNumber(esGoBlockchainData.getEstoIotdataResponse().getEsNumber());
                transactionDto.setDatahash(esGoBlockchainData.getEstoIotdatarequest().getDatahash());
                transactionDto.setDatalocation(esGoBlockchainData.getEstoIotdatarequest().getDatalocation());
                transactionDto.setaBoolean(esGoBlockchainData.getEstoIotdataResponse().getaBoolean());
                transactionDto.setEsSig(esGoBlockchainData.getEstoIotdatarequest().getSig());
                transactionDto.setEsCA(esGoBlockchainData.getEstoIotdatarequest().getToCaresponse());
                transactionDto.setIotSig(esGoBlockchainData.getEstoIotdataResponse().getSig());
                transactionDto.setIotCA(esGoBlockchainData.getEstoIotdataResponse().getToCaresponse());


                String blockchainCorePath = FileUtil.newPath(ResourcePathTool.getDataRootPath(),"BlockchainCore");
                CoreConfiguration coreConfiguration= new CoreConfigurationDefaultImpl(blockchainCorePath);
                UnconfirmedTransactionDatabase unconfirmedTransactionDatabase=new UnconfirmedTransactionDatabaseDefaultImpl(coreConfiguration);
                unconfirmedTransactionDatabase.insertTransaction(transactionDto);

                GatewaytoEsMessageResponse gatewaytoEsMessageResponse=new GatewaytoEsMessageResponse();
                return gatewaytoEsMessageResponse;
            }else {
                System.out.println("网关的分配信息确认失败");
            }
        }
        return  null;
    }

    /**
     * Es向Iot发出数据请求客户端
     * @param gatewaytoEsMessageRequest
     * @return
     */
    private static EstoIotResponse EsSendTask(GatewaytoEsMessageRequest gatewaytoEsMessageRequest,HttpClientImpl httpClient){
        EstoIotrequest estoIotrequest=new EstoIotrequest();
        estoIotrequest.setTaskNumber(gatewaytoEsMessageRequest.getTaskNumber());
        estoIotrequest.setTaskSize(gatewaytoEsMessageRequest.getTaskSize());
        estoIotrequest.setEsNumber(gatewaytoEsMessageRequest.getEsNumber());
        estoIotrequest.setSig(gatewaytoEsMessageRequest.getSig());
        estoIotrequest.setGatewaytoCaresponse(gatewaytoEsMessageRequest.getGatewaytoCaresponse());
        EstoIotResponse estoIotResponse=httpClient.estoiotrequest(estoIotrequest); //向指定的Iot设备获取到数据后

        //验证证书
        ToCaresponse toCaresponse=estoIotResponse.getToCaresponse();
        Boolean b= AccountUtil.verifySignature(API.CAPublicKey,(toCaresponse.getPublickkey()+toCaresponse.getName()).getBytes(StandardCharsets.UTF_8), ByteUtil.hexStringToBytes(toCaresponse.getSig()));
        if(b){
            //通过证书中的公钥验证签名
            Boolean c=AccountUtil.verifySignature(toCaresponse.getPublickkey(),(estoIotResponse.getTaskNumber()+estoIotResponse.getEsNumber()+String.valueOf(estoIotResponse.getTaskSize())+estoIotResponse.getData()).getBytes(StandardCharsets.UTF_8),ByteUtil.hexStringToBytes(estoIotResponse.getSig()));
            if(c){
                return estoIotResponse;
            }else {
                System.out.println("err");
            }
        }
        return null;
    }

    /**
     * Es向Iot发出确认
     * @param estoIotResponse
     * @param httpClient
     */
    private static  EsGoBlockchainData EsSendConfirm(EstoIotResponse estoIotResponse,HttpClientImpl httpClient,Account account,ToCaresponse tocaresponse){

        EstoIotdatarequest estoIotdatarequest=new EstoIotdatarequest();
        estoIotdatarequest.setTaskNumber(estoIotResponse.getTaskNumber());
        estoIotdatarequest.setEsNumber(estoIotResponse.getEsNumber());
        estoIotdatarequest.setDatahash(estoIotResponse.getData().getBytes(StandardCharsets.UTF_8));
        estoIotdatarequest.setDatalocation("Adress");
        //签名[任务标号、Es编号]
        byte[]bb=(estoIotdatarequest.getTaskNumber()+estoIotdatarequest.getEsNumber()+new String(estoIotdatarequest.getDatahash())+estoIotdatarequest.getDatalocation()).getBytes(StandardCharsets.UTF_8);
        estoIotdatarequest.setSig(AccountUtil.signature(account.getPrivateKey(),bb));
        estoIotdatarequest.setToCaresponse(tocaresponse);

        EstoIotdataResponse estoIotdataResponse=httpClient.estoiotdatarequest(estoIotdatarequest);

        //验证返回Iot返回的确认信息的证书
        ToCaresponse toCaresponse=estoIotdataResponse.getToCaresponse();
        Boolean b=AccountUtil.verifySignature(API.CAPublicKey,(toCaresponse.getPublickkey()+toCaresponse.getName()).getBytes(StandardCharsets.UTF_8),ByteUtil.hexStringToBytes(toCaresponse.getSig()));
        if(b){
            //验证签名
            byte[]bb1=(estoIotdataResponse.getTaskNumber()+estoIotdataResponse.getEsNumber()+estoIotdataResponse.getaBoolean()).getBytes(StandardCharsets.UTF_8);
            Boolean c=AccountUtil.verifySignature(toCaresponse.getPublickkey(),bb1,ByteUtil.hexStringToBytes(estoIotdataResponse.getSig()));
            if(c){
                EsGoBlockchainData esGoBlockchainData=new EsGoBlockchainData();
                esGoBlockchainData.setEstoIotdatarequest(estoIotdatarequest);
                esGoBlockchainData.setEstoIotdataResponse(estoIotdataResponse);
                return esGoBlockchainData;
            }
            return null;
        }
        return null;

    }


    /**
     * Ping节点
     */
    public PingResponse ping(String requestIp, PingRequest request){  //<请求的ip地址，请求的request类>
        try {

            //将ping的来路作为区块链节点
            if(!requestIp.equals(API.Caip) && !requestIp.equals(API.CloundIp)){
                Node node = new Node(); //创建一个节点node
                node.setIp(requestIp); //节点设置ip为requestip
                node.setBlockchainHeight(0); //节点设置区块链高度为0
                nodeService.addNode(node); //节点添加节点
//                LogUtil.debug("发现节点["+requestIp+"]在Ping本地节点，已将发现的节点放入了节点数据库。");
            }
//            Node node = new Node(); //创建一个节点node
//            node.setIp(requestIp); //节点设置ip为requestip
//            node.setBlockchainHeight(0); //节点设置区块链高度为0
//            nodeService.addNode(node); //节点添加节点
//            LogUtil.debug("发现节点["+requestIp+"]在Ping本地节点，已将发现的节点放入了节点数据库。");
            PingResponse response = new PingResponse(); //创建一个返回类
            return response;
        } catch (Exception e){
            String message = "ping node failed";
            LogUtil.error(message,e);
            return null;
        }
    }


    /**
     * 根据区块高度查询区块
     */
    public GetBlockResponse getBlock(GetBlockRequest request){
        try {
            Block blockByBlockHeight = blockchainCore.queryBlockByBlockHeight(request.getBlockHeight()); //通过区块高度，获取区块
            BlockDto block = Model2DtoTool.block2BlockDto(blockByBlockHeight);  //blockdto转换为block
            GetBlockResponse response = new GetBlockResponse(); //获取区块的response类
            response.setBlock(block); //response设置区块block
            return response;
        } catch (Exception e){
            String message = "get block failed";
            LogUtil.error(message,e);
            return null;
        }
    }


    /**
     * 接收其它节点提交的交易
     */
    public PostTransactionResponse postTransaction(PostTransactionRequest request){ //<提交的交易>
        try {
            blockchainCore.postTransaction(request.getTransaction()); //提交交易至未确认数据库
            PostTransactionResponse response = new PostTransactionResponse();
            return response;
        } catch (Exception e){
            String message = "post transaction failed";
            LogUtil.error(message,e);
            return null;
        }
    }

    /**
     * 接收其他节点提交的区块
     * （如果本身是委员会中的节点则需要将区块广播出去）
     * @param request
     * @return
     */
    public PostBlockResponse postBlock(String requestip,PostBlockRequest request) { //<提交的区块>
        try {
            BlockDto blockDto=request.getBlock();
            //区块凭证+来源的区块
            String s=blockDto.getPreviousHash()+requestip;
            Committee committee=blockDto.getCommittee();
            List<com.zl.blockchain.dto.Node> nodeList=committee.getNodes();//委员会中的节点
            List<String>ips=new ArrayList<>();//委员会节点ip列表
            for(com.zl.blockchain.dto.Node node:nodeList){
                ips.add(node.getIp());
            }

            //只有该区块--委员会节点才可以广播
            if(!ips.contains(requestip)){
                PostBlockResponse response = new PostBlockResponse();
                return response;
            }

            //如果已经收到过该 “区块凭证+来源的区块”
            if(API.ipandblock.contains(s)){
//                System.out.println("已经收到该区块"+blockDto.getPreviousHash()+requestip);
            }else {

               // System.out.println("接收区块"+blockDto.getPreviousHash()+requestip);
                API.ipandblock.add(s);


                //区块中的CA证书
                ToCaresponse toCaresponse=blockDto.getEsCA();
                //如果委员会中包括该节点
                if(ips.contains(toCaresponse.getIp())){
                    //判断区块中的时间戳是否在该ip地址的规定 委员会区块时间中
                    int position=ips.indexOf(toCaresponse.getIp());
                    long mintime=committee.getBegintime()+position* Long.valueOf(BlockSetting.CommitteeTime)/Integer.valueOf(BlockSetting.CommitteeCount);  //发区块时间下界
                    long maxtime=committee.getBegintime()+(position+1)*Long.valueOf(BlockSetting.CommitteeTime)/Integer.valueOf(BlockSetting.CommitteeCount); //发区块时间上界
                    //如果区块在时间合法的范围内
                    if(blockDto.getTimestamp()>=mintime&&blockDto.getTimestamp()<=maxtime){
                        //如果当前节点也是委员会中成员，则需要广播
                        if(ips.contains(API.Esip)){
                            //System.out.println("我是委员会节点:收到一正确区块,马上广播");
                            new Thread(()->blockBroadcaster.committeeBroadcastBlock(blockDto)).start();
                            //blockBroadcaster.committeeBroadcastBlock(blockDto);
                        }else {
                            //System.out.println("我不是委员会节点:不广播");
                            //System.out.println("本节点ip："+API.Esip);
                        }
                        boolean b=blockchainCore.addBlockDto(blockDto);  //区块链数据库添加区块
                        if(b){
                            //如果区块添加到了本地区块链中，则需要更新未确认数据库
                            List<TransactionDto> transactionDtos=blockDto.getTransactions();
                            for(TransactionDto transactionDto:transactionDtos){

                                //获取未确认数据库
                                String blockchainCorePath = FileUtil.newPath(ResourcePathTool.getDataRootPath(),"BlockchainCore");
                                CoreConfiguration coreConfiguration= new CoreConfigurationDefaultImpl(blockchainCorePath);
                                UnconfirmedTransactionDatabase unconfirmedTransactionDatabase=new UnconfirmedTransactionDatabaseDefaultImpl(coreConfiguration);
                                //从未确认数据中删除交易
                                unconfirmedTransactionDatabase.deleByTransaction(transactionDto);
                            }
                        }
                    }else {
                        System.out.println("该区块不在合法的时间范围内");
                    }
                }

            }
            PostBlockResponse response = new PostBlockResponse();
            return response;
        } catch (Exception e){
            String message = "post block failed";
            LogUtil.error(message,e);
            return null;
        }
    }

    /**
     * 获取所有的节点列表
     * @param request
     * @return
     */
    public GetNodesResponse getNodes(GetNodesRequest request) {
        try {
            List<Node> allNodes = nodeService.queryAllNodes();
            List<NodeDto> nodes = new ArrayList<>();
            if(allNodes != null){
                for (Node node:allNodes) {
                    NodeDto n = new NodeDto();
                    n.setIp(node.getIp());
                    nodes.add(n);
                }
            }
            GetNodesResponse response = new GetNodesResponse();
            response.setNodes(nodes);
            return response;
        }catch (Exception e){
            String message = "get nodes failed";
            LogUtil.error(message,e);
            return null;
        }
    }

    /**
     * 更新其他节点的区块链高度
     * @param requestIp
     * @param request
     * @return
     */
    public PostBlockchainHeightResponse postBlockchainHeight(String requestIp, PostBlockchainHeightRequest request) {
        try {
            Node node = new Node();
            node.setIp(requestIp);
            node.setBlockchainHeight(request.getBlockchainHeight());
            nodeService.updateNode(node);
            PostBlockchainHeightResponse response = new PostBlockchainHeightResponse();
            return response;
        } catch (Exception e){
            String message = "post blockchain height failed";
            LogUtil.error(message,e);
            return null;
        }
    }

    /**
     * 获取区块链高度
     * @param request
     * @return
     */
    public GetBlockchainHeightResponse getBlockchainHeight(GetBlockchainHeightRequest request) {
        try {
            long blockchainHeight = blockchainCore.queryBlockchainHeight(); //查询区块链长度
            GetBlockchainHeightResponse response = new GetBlockchainHeightResponse();
            response.setBlockchainHeight(blockchainHeight);
            return response;
        } catch (Exception e){
            String message = "get blockchain height failed";
            LogUtil.error(message,e);
            return null;
        }
    }

    /**
     * 获取未确认交易
     * @param request
     * @return
     */
    public GetUnconfirmedTransactionsResponse getUnconfirmedTransactions(GetUnconfirmedTransactionsRequest request) {
        try {
            UnconfirmedTransactionDatabase unconfirmedTransactionDatabase = blockchainCore.getUnconfirmedTransactionDatabase(); //获取未确认数据库
            List<TransactionDto> transactions = unconfirmedTransactionDatabase.selectTransactions(1, BlockSetting.BLOCK_MAX_TRANSACTION_COUNT); //从未确认数据库中提取所有的交易
            GetUnconfirmedTransactionsResponse response = new GetUnconfirmedTransactionsResponse();
            response.setTransactions(transactions);
            return response;
        } catch (Exception e){
            String message = "get transaction failed";
            LogUtil.error(message,e);
            return null;
        }
    }


    /**
     * 收到委员会
     * @param postCommitteeRequest
     * @return
     */

    public PostCommitteeResponse postCommittee(PostCommitteeRequest postCommitteeRequest){

        //验证是否为CA发出的
        byte[] b=ByteUtil.concatenate(String.valueOf(postCommitteeRequest.getIndex()+String.valueOf(postCommitteeRequest.getBegintime())).getBytes(StandardCharsets.UTF_8), EncodeDecodeTool.encode(postCommitteeRequest.getNodes()));
        Boolean c=AccountUtil.verifySignature(API.CAPublicKey,b,ByteUtil.hexStringToBytes(postCommitteeRequest.getSig()));
        if(c){
            /**
             * 将网络层的委员会凭证转化为dto层,存放到
             */
            Committee comm=new Committee();
            comm.setIndex(postCommitteeRequest.getIndex());
            comm.setBegintime(postCommitteeRequest.getBegintime());
            List<com.zl.blockchain.dto.Node> nodeList=new ArrayList<>();
            List<Node>nodes=postCommitteeRequest.getNodes();
            for(Node node:nodes){
                com.zl.blockchain.dto.Node dtonode=new com.zl.blockchain.dto.Node();
                dtonode.setBlockchainHeight(node.getBlockchainHeight());
                dtonode.setIp(node.getIp());
                nodeList.add(dtonode);
                System.out.println(dtonode.getIp());
            }
            comm.setNodes(nodeList);
            comm.setSig(postCommitteeRequest.getSig());
            API.committee=comm;

            PostCommitteeResponse postCommitteeResponse=new PostCommitteeResponse();
            postCommitteeResponse.setFlag(true);
            return postCommitteeResponse;
        }else {
            System.out.println("错误");
        }
        return null;

    }

    public blockunloadresponse blockunload(blockunloadrequest request){

        /**
         * 验证证书
         */
        ToCaresponse toCaresponse= request.getToCaresponse();
        Boolean b=AccountUtil.verifySignature(API.CAPublicKey,(toCaresponse.getPublickkey()+toCaresponse.getName()).getBytes(StandardCharsets.UTF_8), ByteUtil.hexStringToBytes(toCaresponse.getSig()));
        if(b){
            //验证卸载命令
            byte[] data=String.valueOf(request.getTimestap()+request.getOriginheight()+request.getEndheight()).getBytes(StandardCharsets.UTF_8);
            Boolean c=AccountUtil.verifySignature(toCaresponse.getPublickkey(),data,ByteUtil.hexStringToBytes( request.getSig()));
            if(c){

                //当前时间与卸载任务中的时间不大于1分钟--防止重放攻击
                if(TimeUtil.millisecondTimestamp()- request.getTimestap()<=1000*60*1){

                    System.out.println("开始卸载");


                    //保留高度大于request.getEndheight的所有区块
                    List<Block>blocks=new ArrayList<>();
                    long nowheight=blockchainCore.getBlockchainDatabase().queryBlockchainHeight();


                    /**
                     * 当云中心卸载高度>本地的区块链高度
                     * 需要暂停，等到本地区块链向云中心发出错误请求并删除本地所有区块----云中心会记录错误，并把当前云区块链中最后一个区块返回给你
                     */
                    while (request.getEndheight()>nowheight){
                        ThreadUtil.millisecondSleep(60*1000);
                    }


                    for(long i=request.getEndheight();i<=nowheight;i++){
                        Block block=blockchainCore.getBlockchainDatabase().queryBlockByBlockHeight(i);
                        blocks.add(block);
                    }

                    System.out.println("保留的区块数量"+blocks.size());
                    blockchainCore.deleteBlocks(request.getOriginheight()+1);//删除所有区块

                    //将保留的区块加上
                    for(int i=0;i<blocks.size();i++){
                        blockchainCore.getBlockchainDatabase().unloadaddBlock(blocks.get(i));
                    }


                    System.out.println("删除后的区块链高度"+blockchainCore.getBlockchainDatabase().queryBlockchainHeight());


                }

                blockunloadresponse blockunloadresponse=new blockunloadresponse();
                return blockunloadresponse;
            }
        }
        return null;
    }

}

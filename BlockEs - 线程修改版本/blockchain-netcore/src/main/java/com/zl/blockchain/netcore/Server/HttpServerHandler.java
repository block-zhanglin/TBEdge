package com.zl.blockchain.netcore.Server;

import com.zl.blockchain.dto.*;
import com.zl.blockchain.netcore.dao.PostCommitteeRequest;
import com.zl.blockchain.netcore.dao.PostCommitteeResponse;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import com.zl.blockchain.util.JsonUtil;

import java.net.InetSocketAddress;


public class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private HttpServerHandlerResolver httpServerHandlerResolver;

    public HttpServerHandler(HttpServerHandlerResolver httpServerHandlerResolver){
        super();
        this.httpServerHandlerResolver=httpServerHandlerResolver;

    }
    /**
     * channelRead0当接收到客户端消息后调用
     * @param channelHandlerContext
     * @param fullHttpRequest
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpRequest fullHttpRequest) {


        String requestIp = parseRequestIp(channelHandlerContext); //获取请求ip
        String requestApi = parseRequestApi(fullHttpRequest);  //获取请求api
        String requestBody = parseRequestBody(fullHttpRequest); //获取请求体
        String responseMessage; //服务器响应客户端的请求
        /*
         * 任何节点都可以访问这里的接口，请不要在这里写任何能泄露用户私钥的代码。
         * 因为有的节点没有公网IP，所以为了照顾这些节点，新增了一系列的接口。
         * 但是我们假设所有节点都有公网IP，我们只需要写五个接口就可以了。
         */
        if("/".equals(requestApi)){ //如果请求api为“/”
            responseMessage = " "; //返回信息为空
        } else if(requestApi.equals(API.GatewaytoEsRequest)){
            GatewaytoEsMessageRequest gatewaytoEsMessageRequest=JsonUtil.toObject(requestBody,GatewaytoEsMessageRequest.class);
            GatewaytoEsMessageResponse gatewaytoEsMessageResponse=httpServerHandlerResolver.gatewaytoEsMessage(gatewaytoEsMessageRequest);
            responseMessage=JsonUtil.toString(gatewaytoEsMessageResponse);
        }else if(API.PING.equals(requestApi)){ //如果请求api为“/ping”
            PingRequest request = JsonUtil.toObject(requestBody, PingRequest.class); //构建request
            PingResponse response = httpServerHandlerResolver.ping(requestIp,request); //构建response
            responseMessage = JsonUtil.toString(response);
        }else if(API.GET_NODES.equals(requestApi)){//如果请求api为“/get_nodes”
//            System.out.println("有节点想获取节点列表"+requestIp);
            GetNodesRequest request = JsonUtil.toObject(requestBody, GetNodesRequest.class);//构建request
            GetNodesResponse getBlockResponse = httpServerHandlerResolver.getNodes(request);//构建response
            responseMessage = JsonUtil.toString(getBlockResponse);
        }else if(API.GET_BLOCK.equals(requestApi)){//如果请求api为“/get_block”
            GetBlockRequest request = JsonUtil.toObject(requestBody, GetBlockRequest.class);//构建request
            if(request!=null){
                //System.out.println("获取特定区块高度的区块"+request.getBlockHeight());
                GetBlockResponse getBlockResponse = httpServerHandlerResolver.getBlock(request);//构建response
                responseMessage = JsonUtil.toString(getBlockResponse);
            }else {
                responseMessage = "";
            }
        }else if(API.GET_UNCONFIRMED_TRANSACTIONS.equals(requestApi)){//如果请求api为“/get_unconfirmed_transactions”
            GetUnconfirmedTransactionsRequest request = JsonUtil.toObject(requestBody, GetUnconfirmedTransactionsRequest.class);//构建request
            GetUnconfirmedTransactionsResponse response = httpServerHandlerResolver.getUnconfirmedTransactions(request);//构建response
            responseMessage = JsonUtil.toString(response);
        }else if(API.POST_TRANSACTION.equals(requestApi)){//如果请求api为“/post_transaction”
            PostTransactionRequest request = JsonUtil.toObject(requestBody, PostTransactionRequest.class);//构建request
            PostTransactionResponse response = httpServerHandlerResolver.postTransaction(request);//构建response
            responseMessage = JsonUtil.toString(response);
        }else if(API.POST_BLOCK.equals(requestApi)){ //如果请求api为“/post_block”
            PostBlockRequest request = JsonUtil.toObject(requestBody, PostBlockRequest.class);//构建request
            PostBlockResponse response = httpServerHandlerResolver.postBlock(requestIp,request);//构建response
            responseMessage = JsonUtil.toString(response);
        }else if(API.POST_BLOCKCHAIN_HEIGHT.equals(requestApi)){//如果请求api为“/post_blockchain_height”
            PostBlockchainHeightRequest request = JsonUtil.toObject(requestBody, PostBlockchainHeightRequest.class);//构建request
            PostBlockchainHeightResponse response = httpServerHandlerResolver.postBlockchainHeight(requestIp,request);//构建response
            responseMessage = JsonUtil.toString(response);
        }else if(API.GET_BLOCKCHAIN_HEIGHT.equals(requestApi)){//如果请求api为“/get_blockchain_height"”
             GetBlockchainHeightRequest request = JsonUtil.toObject(requestBody, GetBlockchainHeightRequest.class);//构建request
            GetBlockchainHeightResponse response = httpServerHandlerResolver.getBlockchainHeight(request);//构建response
            responseMessage = JsonUtil.toString(response);
        }else  if(API.POST_COMMITTEE.equals(requestApi)){
            //System.out.println("发布委员会");
            PostCommitteeRequest postCommitteeRequest=JsonUtil.toObject(requestBody,PostCommitteeRequest.class);
            PostCommitteeResponse postCommitteeResponse =httpServerHandlerResolver.postCommittee(postCommitteeRequest);
            responseMessage=JsonUtil.toString(postCommitteeResponse);
        }else if(API.BLOCK_UNLOAD.equals(requestApi)){
            blockunloadrequest blockunloadrequest=JsonUtil.toObject(requestBody, blockunloadrequest.class);
            blockunloadresponse blockunloadresponse=httpServerHandlerResolver.blockunload(blockunloadrequest);
            responseMessage=JsonUtil.toString(blockunloadresponse);
        }
        else {
            responseMessage = "404 page not found";
        }
        writeResponse(channelHandlerContext,responseMessage);
    }

//    private  void mc(ChannelHandlerContext channelHandlerContext, FullHttpRequest fullHttpRequest){
//
//        try{
//            String requestIp = parseRequestIp(channelHandlerContext); //获取请求ip
//            String requestApi = parseRequestApi(fullHttpRequest);  //获取请求api
//            String requestBody = parseRequestBody(fullHttpRequest); //获取请求体
//            String responseMessage; //服务器响应客户端的请求
//            /*
//             * 任何节点都可以访问这里的接口，请不要在这里写任何能泄露用户私钥的代码。
//             * 因为有的节点没有公网IP，所以为了照顾这些节点，新增了一系列的接口。
//             * 但是我们假设所有节点都有公网IP，我们只需要写五个接口就可以了。
//             */
//            if("/".equals(requestApi)){ //如果请求api为“/”
//                responseMessage = " "; //返回信息为空
//            } else if(requestApi.equals(API.GatewaytoEsRequest)){
//                GatewaytoEsMessageRequest gatewaytoEsMessageRequest=JsonUtil.toObject(requestBody,GatewaytoEsMessageRequest.class);
//                GatewaytoEsMessageResponse gatewaytoEsMessageResponse=httpServerHandlerResolver.gatewaytoEsMessage(gatewaytoEsMessageRequest);
//                responseMessage=JsonUtil.toString(gatewaytoEsMessageResponse);
//            }else if(API.PING.equals(requestApi)){ //如果请求api为“/ping”
//                PingRequest request = JsonUtil.toObject(requestBody, PingRequest.class); //构建request
//                PingResponse response = httpServerHandlerResolver.ping(requestIp,request); //构建response
//                responseMessage = JsonUtil.toString(response);
//            }else if(API.GET_NODES.equals(requestApi)){//如果请求api为“/get_nodes”
////            System.out.println("有节点想获取节点列表"+requestIp);
//                GetNodesRequest request = JsonUtil.toObject(requestBody, GetNodesRequest.class);//构建request
//                GetNodesResponse getBlockResponse = httpServerHandlerResolver.getNodes(request);//构建response
//                responseMessage = JsonUtil.toString(getBlockResponse);
//            }else if(API.GET_BLOCK.equals(requestApi)){//如果请求api为“/get_block”
//                GetBlockRequest request = JsonUtil.toObject(requestBody, GetBlockRequest.class);//构建request
//                if(request!=null){
//                    //System.out.println("获取特定区块高度的区块"+request.getBlockHeight());
//                    GetBlockResponse getBlockResponse = httpServerHandlerResolver.getBlock(request);//构建response
//                    responseMessage = JsonUtil.toString(getBlockResponse);
//                }else {
//                    responseMessage = "";
//                }
//            }else if(API.GET_UNCONFIRMED_TRANSACTIONS.equals(requestApi)){//如果请求api为“/get_unconfirmed_transactions”
//                GetUnconfirmedTransactionsRequest request = JsonUtil.toObject(requestBody, GetUnconfirmedTransactionsRequest.class);//构建request
//                GetUnconfirmedTransactionsResponse response = httpServerHandlerResolver.getUnconfirmedTransactions(request);//构建response
//                responseMessage = JsonUtil.toString(response);
//            }else if(API.POST_TRANSACTION.equals(requestApi)){//如果请求api为“/post_transaction”
//                PostTransactionRequest request = JsonUtil.toObject(requestBody, PostTransactionRequest.class);//构建request
//                PostTransactionResponse response = httpServerHandlerResolver.postTransaction(request);//构建response
//                responseMessage = JsonUtil.toString(response);
//            }else if(API.POST_BLOCK.equals(requestApi)){ //如果请求api为“/post_block”
//                PostBlockRequest request = JsonUtil.toObject(requestBody, PostBlockRequest.class);//构建request
//                PostBlockResponse response = httpServerHandlerResolver.postBlock(requestIp,request);//构建response
//                responseMessage = JsonUtil.toString(response);
//            }else if(API.POST_BLOCKCHAIN_HEIGHT.equals(requestApi)){//如果请求api为“/post_blockchain_height”
//                PostBlockchainHeightRequest request = JsonUtil.toObject(requestBody, PostBlockchainHeightRequest.class);//构建request
//                PostBlockchainHeightResponse response = httpServerHandlerResolver.postBlockchainHeight(requestIp,request);//构建response
//                responseMessage = JsonUtil.toString(response);
//            }else if(API.GET_BLOCKCHAIN_HEIGHT.equals(requestApi)){//如果请求api为“/get_blockchain_height"”
//                GetBlockchainHeightRequest request = JsonUtil.toObject(requestBody, GetBlockchainHeightRequest.class);//构建request
//                GetBlockchainHeightResponse response = httpServerHandlerResolver.getBlockchainHeight(request);//构建response
//                responseMessage = JsonUtil.toString(response);
//            }else  if(API.POST_COMMITTEE.equals(requestApi)){
//                //System.out.println("发布委员会");
//                PostCommitteeRequest postCommitteeRequest=JsonUtil.toObject(requestBody,PostCommitteeRequest.class);
//                PostCommitteeResponse postCommitteeResponse =httpServerHandlerResolver.postCommittee(postCommitteeRequest);
//                responseMessage=JsonUtil.toString(postCommitteeResponse);
//            }else if(API.BLOCK_UNLOAD.equals(requestApi)){
//                blockunloadrequest blockunloadrequest=JsonUtil.toObject(requestBody, blockunloadrequest.class);
//                blockunloadresponse blockunloadresponse=httpServerHandlerResolver.blockunload(blockunloadrequest);
//                responseMessage=JsonUtil.toString(blockunloadresponse);
//            }
//            else {
//                responseMessage = "404 page not found";
//            }
//            writeResponse(channelHandlerContext,responseMessage);
//
//        }catch (Exception e){
//            LogUtil.error("服务器接收信息错误",e);
//        }
//
//    }

    /**
     * 获取请求体
     * @param fullHttpRequest 请求
     * @return
     */
    private String parseRequestBody(FullHttpRequest fullHttpRequest) {
        return fullHttpRequest.content().toString(CharsetUtil.UTF_8);
    }

    /**
     * 获取请求ip
     * @param channelHandlerContext 传输业务数据
     * @return
     */
    private String parseRequestIp(ChannelHandlerContext channelHandlerContext) {
        InetSocketAddress inetSocketAddress = (InetSocketAddress) channelHandlerContext.channel().remoteAddress();
        String ip = inetSocketAddress.getAddress().getHostAddress();
        return ip;
    }

    /**
     * 获取请求api
     * @param fullHttpRequest 请求
     * @return
     */
    private String parseRequestApi(FullHttpRequest fullHttpRequest) {
        String uri = fullHttpRequest.uri();
        if(uri.contains("?")){
            return uri.split("\\?")[0];
        }else {
            return uri;
        }
    }

    /**
     * 构建返回
     * @param channelHandlerContext
     * @param msg
     */
    private void writeResponse(ChannelHandlerContext channelHandlerContext, String msg) {
        ByteBuf bf = Unpooled.copiedBuffer(msg, CharsetUtil.UTF_8); //将msg转换位bytebuf
        FullHttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, bf); //创建response
        res.headers().set(HttpHeaderNames.CONTENT_LENGTH, msg.length()); //设置头部：消息长度
        res.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json");//设置头部：消息类型
        channelHandlerContext.writeAndFlush(res).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);//数据发送到客户端
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {

        onUnhandledInboundException(cause);

    }
    protected void onUnhandledInboundException(Throwable cause) {
        try {

        } finally {
            ReferenceCountUtil.release(cause);
        }
    }

}

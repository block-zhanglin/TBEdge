package com.zl.blockCA.net.Server;

import com.zl.blockCA.net.dao.*;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import util.JsonUtil;

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
        try{
            if(fullHttpRequest instanceof  FullHttpRequest){
                String requestIp = parseRequestIp(channelHandlerContext); //获取请求ip
                String requestApi = parseRequestApi(fullHttpRequest);  //获取请求api
                String requestBody = parseRequestBody(fullHttpRequest); //获取请求体

                String responseMessage;

                if("/".equals(requestApi)) { //如果请求api为“/”
                    responseMessage = "Blockchain"; //返回信息为blockchain
                }else if(requestApi.equals(API.IottoCA)){
                   System.out.println(requestBody);
                   toCarequest toCarequest=JsonUtil.toObject(requestBody, com.zl.blockCA.net.dao.toCarequest.class);
                   toCaresponse toCaresponse=httpServerHandlerResolver.toCA(requestIp,toCarequest);
                   responseMessage=JsonUtil.toString(toCaresponse);
                }
                else {
                    responseMessage = "404 page not found";
                }
                writeResponse(channelHandlerContext, responseMessage); //传输response到客户端
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

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

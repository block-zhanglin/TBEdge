package com.zl.blockchain.netcore.Server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

public class HttpServerChannelInitializer extends ChannelInitializer<SocketChannel> {

    private HttpServerHandlerResolver httpServerHandlerResolver;

    public HttpServerChannelInitializer(HttpServerHandlerResolver httpServerHandlerResolver) {
        super();
        this.httpServerHandlerResolver = httpServerHandlerResolver;
    }

    @Override
    protected void initChannel(SocketChannel ch) {
        ch.pipeline().addLast("codec", new HttpServerCodec()); //是decoder和encoder的结合
        ch.pipeline().addLast("aggregator", new HttpObjectAggregator(10*1024*1024));  //消息聚合器 参数是消息合并的数据大小，聚合的消息内容长度不超过521kb
        ch.pipeline().addLast("serverHandler", new HttpServerHandler(httpServerHandlerResolver)); //添加业务接口
    }
}

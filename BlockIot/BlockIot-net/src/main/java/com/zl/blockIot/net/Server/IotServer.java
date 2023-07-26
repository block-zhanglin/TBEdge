package com.zl.blockIot.net.Server;

import com.zl.blockIot.net.client.HttpClientImpl;
import com.zl.blockIot.net.configuration.NetcoreConfiguration;
import com.zl.blockIot.net.dao.IottoGatewayMessageRequest;
import com.zl.blockIot.net.dao.IottoGatewayMessageResponse;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class IotServer {

    private  HttpServerHandlerResolver httpServerHandlerResolver; //创建节点请求处理类
    private NetcoreConfiguration netcoreConfiguration;

    public IotServer(NetcoreConfiguration netcoreConfiguration,HttpServerHandlerResolver httpServerHandlerResolver){
        super();
        this.httpServerHandlerResolver=httpServerHandlerResolver;
        this.netcoreConfiguration=netcoreConfiguration;
    }

    public void start() {

        EventLoopGroup bossGroup = new NioEventLoopGroup(1); //创建线程组：用于Accetpt连接建立事件并分发请求
        EventLoopGroup workerGroup = new NioEventLoopGroup();//创建线程组：用于处理i/o读写事件和业务逻辑
        try {
            ServerBootstrap b = new ServerBootstrap();//引导类，引导服务器工作
            b.group(bossGroup, workerGroup) //配置线程组角色
                    .channel(NioServerSocketChannel.class) //channel配置为nio
                    .childHandler(new HttpServerChannelInitializer(httpServerHandlerResolver)) //初始化channel
                    .option(ChannelOption.SO_BACKLOG, 128) //确定排队的连接数
                    .childOption(ChannelOption.SO_KEEPALIVE, true); //开启TCP底层心态机制

            ChannelFuture f = b.bind(netcoreConfiguration.getport()).sync(); //绑定端口
            f.channel().closeFuture().sync(); //是等待服务端监听端口关闭,用来监听客户端连接服务端的结果反馈，Netty是异步操作，无法知道什么时候执行完成，因此可以通过channelfuture来进行执行结果的监听
        } catch (Exception e) {
            System.out.println("启动失败");
        } finally {
//            workerGroup.shutdownGracefully();
//            bossGroup.shutdownGracefully();
        }
    }
}

package com.xunle.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Inet4Address;
import java.net.InetSocketAddress;

/**
 * @author xunle
 * @date 2021/12/16 14:48
 */
public class EchoServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(EchoServer.class);
    private final int PORT;

    public EchoServer(int port) {
        this.PORT = port;
    }

    public void start() {
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(group)
                    //指定传输Channel
                    .channel(NioServerSocketChannel.class)
                    //socket地址使用所选的端口
                    .localAddress(new InetSocketAddress(PORT))
                    //添加 EchoServerHandler到 Channel的 ChannelPipeline
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new EchoServerHandler());
                        }
                    });

            //绑定的服务器，sync 等待服务器关闭
            ChannelFuture future = bootstrap.bind().sync();
            LOGGER.info(EchoServer.class.getName() + " started and listen on " +
                    future.channel().localAddress());
            //关闭 Channel和块直到它被关闭
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            LOGGER.error("服务端出现异常：{}", e);
            try {
                group.shutdownGracefully().sync();
            } catch (InterruptedException interruptedException) {
                LOGGER.error("服务端关闭出现异常：{}", interruptedException);
            }
        }
    }

    public static void main(String[] args) {

        // 设置端口值
        int port = Integer.parseInt("8000");
        new EchoServer(port).start();
    }
}

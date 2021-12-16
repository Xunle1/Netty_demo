package com.xunle.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * @author xunle
 * @date 2021/12/16 15:28
 */
public class EchoClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(EchoClient.class);

    private final String HOST;
    private final int PORT;

    public EchoClient(String host, int port) {
        this.HOST = host;
        this.PORT = port;
    }

    public void start() {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .remoteAddress(new InetSocketAddress(HOST, PORT))
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new EchoClientHandler());
                        }
                    });

            ChannelFuture future = bootstrap.connect().sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            LOGGER.error("客户端关闭出错：{}",e);
            try {
                group.shutdownGracefully().sync();
            } catch (InterruptedException exception) {
                LOGGER.error("客户端关闭出错：{}",exception);
            }
        }
    }

    public static void main(String[] args) {


        final String host = "0:0:0:0:0:0:0:0";
        final int port = Integer.parseInt("8000");

        new EchoClient(host,port).start();
    }
}

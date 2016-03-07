package com.server.protocol.intializers;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;

/**
 * Created by tahirmacbook on 29/01/2016.
 */
public class GenericBootstrap {

    private ArrayList<Pair<Integer, ChannelInitializer<SocketChannel>>> initializers;
    private ArrayList<ChannelFuture> channels = new ArrayList<>();

    public GenericBootstrap(ArrayList<Pair<Integer, ChannelInitializer<SocketChannel>>> initializers) {
        this.initializers = initializers;
    }

    public void initialize() throws Exception {

        EventLoopGroup bossGroup = new NioEventLoopGroup(1000);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        for (Pair<Integer, ChannelInitializer<SocketChannel>> pair : initializers) {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.DEBUG))
                    .childHandler(pair.getRight());

            // Start the server.
            channels.add(bootstrap.bind(pair.getLeft()).sync());

            // Wait until the server socket is closed.
            doSync();
        }
    }

    private void doSync() throws InterruptedException {
        for (ChannelFuture ch : channels) {
            ch.channel().closeFuture();
        }
    }
}

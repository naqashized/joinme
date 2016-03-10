package com.server.protocol.intializers;

import com.server.protocol.handlers.ActorHandler;
import com.server.protocol.handlers.WebSocketHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslContext;

/**
 * Created by tahirmacbook on 29/01/2016.
 */
public class WebSocketInitializer extends ChannelInitializer<SocketChannel> {

    private final SslContext sslCtx;

    public WebSocketInitializer(SslContext sslCtx) {
        this.sslCtx = sslCtx;
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        if (sslCtx != null) {
            pipeline.addLast(sslCtx.newHandler(ch.alloc()));
        }
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(100000));
        pipeline.addLast(new WebSocketHandler());
        pipeline.addLast(new ActorHandler(WebSocketHandler.class));

    }
}

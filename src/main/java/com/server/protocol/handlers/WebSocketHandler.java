package com.server.protocol.handlers;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActorContext;
import akka.pattern.Patterns;
import akka.util.Timeout;
import com.joinme.actors.ActorSystemContainer;
import com.joinme.actors.UnauthorizedAccess;
import com.joinme.actors.UserActor;
import com.server.app.App;
import com.server.protocol.connectors.WebSocketConnector;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.concurrent.Await;
import scala.concurrent.Future;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.FORBIDDEN;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * Created by tahirmacbook on 29/01/2016.
 */
public class WebSocketHandler extends SimpleChannelInboundHandler<Object> implements ClientForwarding {

    private static final String WEBSOCKET_PATH = "/websocket";
    private final Logger log = LoggerFactory.getLogger(WebSocketHandler.class);

    private ChannelHandlerContext ctx;

    private WebSocketServerHandshaker handshaker;


    public WebSocketHandler(){
        //log.info("WebSocketHandler constructor called =>"+userId);
        //sys = ActorSystem.apply("joinme");
        //log.info("sys =>"+sys);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        this.ctx = ctx;
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof FullHttpRequest) {
            handleHttpRequest(ctx, (FullHttpRequest) msg);
        } else if (msg instanceof WebSocketFrame) {
            handleWebSocketFrame(ctx, (WebSocketFrame) msg);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) {
        // Handle a bad request.
        if (!req.decoderResult().isSuccess()) {
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HTTP_1_1, BAD_REQUEST));
            return;
        }

        // Allow only GET methods.
        if (req.method() != GET) {
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HTTP_1_1, FORBIDDEN));
            return;
        }

        if ("/test".equals(req.uri())) {
            ByteBuf content = Unpooled.copiedBuffer("Yo! This shit working for you. I am very happy for you :)", CharsetUtil.US_ASCII);
            FullHttpResponse res = new DefaultFullHttpResponse(HTTP_1_1, OK, content);

            res.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");
            HttpUtil.setContentLength(res, content.readableBytes());

            sendHttpResponse(ctx, req, res);
            return;
        }

        // Handshake
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
                getWebSocketLocation(req), null, true);
        handshaker = wsFactory.newHandshaker(req);
        if (handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        } else {
            handshaker.handshake(ctx.channel(), req);
        }
    }

    private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame)  {

        // Check for closing frame
        if (frame instanceof CloseWebSocketFrame) {
            handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            return;
        }
        if (frame instanceof PingWebSocketFrame) {
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
            return;
        }
        if (!(frame instanceof TextWebSocketFrame)) {
            throw new UnsupportedOperationException(String.format("%s frame types not supported", frame.getClass()
                    .getName()));
        }

        String request = ((TextWebSocketFrame) frame).text();
        log.debug("Received from client => " + request);
        processRequest(request);

        //sendToClient(response);
    }

    private void processRequest(String message)  {

             ctx.pipeline().get(ActorHandler.class).processRequest(message);

    }



    public void sendToClient(String message) {
        log.debug("Forwarding to client => " + message);
        //add event here
        //ctx.fireChannelRegistered().writeAndFlush(new TextWebSocketFrame("message fired from server"));
        ctx.channel().writeAndFlush(new TextWebSocketFrame(message));
    }

    private static void sendHttpResponse(
            ChannelHandlerContext ctx, FullHttpRequest req, FullHttpResponse res) {
        // Generate an error page if response getStatus code is not OK (200).
        if (res.status().code() != 200) {
            ByteBuf buf = Unpooled.copiedBuffer(res.status().toString(), CharsetUtil.UTF_8);
            res.content().writeBytes(buf);
            buf.release();
            HttpUtil.setContentLength(res, res.content().readableBytes());
        }

        // Send the response and close the connection if necessary.
        ChannelFuture f = ctx.channel().writeAndFlush(res);
        if (!HttpUtil.isKeepAlive(req) || res.status().code() != 200) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    private static String getWebSocketLocation(FullHttpRequest req) {
        String location = req.headers().get(HttpHeaderNames.HOST) + WEBSOCKET_PATH;
        if (WebSocketConnector.SSL) {
            return "wss://" + location;
        } else {
            return "ws://" + location;
        }
    }

    private UntypedActorContext getContext(){

        return null;
    }
}


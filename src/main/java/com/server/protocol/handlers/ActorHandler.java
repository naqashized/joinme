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
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.concurrent.Await;
import scala.concurrent.Future;

import java.util.concurrent.TimeUnit;

/**
 * Created by tahirmacbook on 07/03/2016.
 */
public class ActorHandler extends ChannelDuplexHandler {

    private UntypedActorContext context;
    private Long userId;
    private final Logger log = LoggerFactory.getLogger(ActorHandler.class);
    private Class<ClientForwarding> clientForwardingClass;
    private ChannelHandlerContext ctx;


    public ActorHandler(Class clientForwardingClass){

        this.clientForwardingClass = clientForwardingClass;

    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        this.ctx = ctx;
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);

    }

    @Override
    public void close(ChannelHandlerContext ctx, ChannelPromise future) throws Exception {

        super.close(ctx, future);
        log.debug("User logged out closing all handlers...");


    }

    public void processRequest(String request){

        log.info("userId =>"+userId);
        String response = null;
        if(userId == null)
            response = processUnauthorizedRequest(request);
        else
            response = processAuthorizedMessage(request);
        sendToClient(response);
    }

    private String processAuthorizedMessage(String request){

        String response = null;
        ActorSystem sys = ActorSystemContainer.getInstance().getSystem();
        ActorRef actor = sys.actorFor(UserActor.PATH+userId);
        //ActorRef actor = sys.actorOf(Props.create(UserActor.class, userId), userId.toString());
        if(!actor.isTerminated()) {
            Timeout t = new Timeout(5, TimeUnit.SECONDS);
            Future<Object> fut = Patterns.ask(actor, request, t);

            try {
                response = (String) Await.result(fut, t.duration());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        log.info("response =>" + response);
        return response;

    }

    private String processUnauthorizedRequest(String request){


        log.info("processing unauthorized access =>"+request);
        ActorSystem sys = ActorSystemContainer.getInstance().getSystem();
        ActorRef ref = sys.actorFor(UnauthorizedAccess.PATH);
        Timeout t = new Timeout(10, TimeUnit.SECONDS);
        Future<Object> fut = Patterns.ask(ref, request, t);
        String response = null;
        try {
            response = (String) Await.result(fut, t.duration());
            JSONObject jsonObject = new JSONObject(response);
            userId = jsonObject.getLong("userId");
            ActorRef actor = sys.actorOf(Props.create(UserActor.class, userId,this), userId.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("response =>" + response);
        return response;
    }

    public void sendToClient(String message){

        this.ctx.pipeline().get(clientForwardingClass).sendToClient(message);
    }

}

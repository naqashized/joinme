package com.server.app;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.joinme.actors.UnauthorizedAccess;
import com.server.constants.Configurations;
import com.server.protocol.intializers.GenericBootstrap;
import com.server.protocol.intializers.WebSocketInitializer;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * Created by tahirmacbook on 29/01/2016.
 */
public class App {

    private static final Logger log = LoggerFactory.getLogger(App.class);
    public static final boolean SSL = System.getProperty("ssl") != null;
    private static ActorSystem sys;

    public static void main(String[] args) throws Exception {
        log.info("Booting system in " + Configurations.getEnvironment());


        ArrayList<Pair<Integer, ChannelInitializer<SocketChannel>>> initializers = new ArrayList<>();

        final SslContext sslCtx;
        if (SSL) {
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
        } else {
            sslCtx = null;
        }

        //initializers.add(Pair.of(Configurations.getNetworkServerTcpPort(), new TcpInitializer()));
        initializers.add(Pair.of(Configurations.getNetworkServerWsPort(), new WebSocketInitializer(sslCtx)));

        GenericBootstrap bootstrap = new GenericBootstrap(initializers);
        bootstrap.initialize();

        sys = ActorSystem.apply("joinme");

        ActorRef ref = sys.actorOf(Props.create(UnauthorizedAccess.class),UnauthorizedAccess.NAME);

    }

    public static ActorSystem getActorSystem(){
         return sys;
    }
}

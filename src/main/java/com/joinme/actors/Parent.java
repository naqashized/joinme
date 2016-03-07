package com.joinme.actors;

import akka.actor.Props;
import akka.actor.UntypedActor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by tahirmacbook on 19/02/2016.
 */
public class Parent extends UntypedActor {

    private final Logger log = LoggerFactory.getLogger(Parent.class);
    public static final String NAME = "Supervisor";
    public static final String PATH = "/actors/" + NAME;

    @Override
    public void preStart(){


        log.debug("starting Parent Actor");
        context().actorOf(Props.create(UserActor.class), UserActor.NAME);

    }

    @Override
    public void postStop(){

    }
    @Override
    public void onReceive(Object message) throws Exception {

    }
}

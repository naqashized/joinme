package com.joinme.actors;

import akka.actor.ActorSystem;

/**
 * Created by tahirmacbook on 08/03/2016.
 */
public class ActorSystemContainer {


    private ActorSystem sys;
    private ActorSystemContainer() {
        sys = ActorSystem.create("joinme");
    }

    public ActorSystem getSystem() {
        return sys;
    }

    private static ActorSystemContainer instance = null;

    public static synchronized ActorSystemContainer getInstance() {
        if (instance == null) {
            instance = new ActorSystemContainer();
        }
        return instance;
    }
}

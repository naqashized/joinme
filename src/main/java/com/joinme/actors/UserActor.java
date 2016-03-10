package com.joinme.actors;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import com.server.protocol.handlers.ActorHandler;
import org.json.JSONObject;

/**
 * Created by tahirmacbook on 29/01/2016.
 */
public class UserActor extends UntypedActor {

    public static final String NAME = "User";
    public static final String PATH ="/user/";
    private Long userId;
    private ActorHandler actorHandler;

    public UserActor(Long userId, ActorHandler actorHandler){

        this.userId = userId;
        this.actorHandler = actorHandler;
    }

    @Override
    public void preStart(){


    }

    @Override
    public void postStop(){

    }
    @Override
    public void onReceive(Object message) throws Exception {

        System.out.println("message is "+message);
        JSONObject response = new JSONObject(message.toString());
        response.put("response","here is response from "+userId+" actor");

        if(response.has("actorId"))
            sendPushMessage(response.getLong("actorId"));
        else if(response.has("pushMessage"))
            actorHandler.sendToClient(response.toString());
        else
            getContext().sender().tell(response.toString(), getContext().self());
    }

    public void sendPushMessage(Long userId){

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("pushMessage", "This is a push message ");
        ActorRef actor = getContext().actorFor(UserActor.PATH+userId);
        actor.tell(jsonObject,ActorRef.noSender());
    }


}

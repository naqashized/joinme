package com.joinme.actors;

import akka.actor.UntypedActor;
import org.json.JSONObject;

/**
 * Created by tahirmacbook on 29/01/2016.
 */
public class UserActor extends UntypedActor {

    public static final String NAME = "User";
    public static final String PATH ="/user/"+NAME+"/";
    private Long userId;

    public UserActor(Long userId){

        this.userId = userId;
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
        getContext().sender().tell(response.toString(), getContext().self());
    }
}

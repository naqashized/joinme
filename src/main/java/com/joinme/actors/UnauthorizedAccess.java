package com.joinme.actors;

import akka.actor.UntypedActor;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

/**
 * Created by tahirmacbook on 29/01/2016.
 */
public class UnauthorizedAccess extends UntypedActor {

    private final Logger log = LoggerFactory.getLogger(UnauthorizedAccess.class);
    public static final String NAME = "Unauthorized";
    public static String PATH ="/user/"+NAME+"/";
    @Override
    public void onReceive(Object message) throws Exception {

        log.info("Message received =>"+message);
        JSONObject response = new JSONObject(message.toString());
        //Long userId = response.getLong("userId");
        response.put("success", true);
        log.info("response =>"+response);
        getContext().sender().tell(response.toString(), getContext().self());
    }

    private Long validateAuthorization(String request){
        Random random = new Random();
        long userId = random.nextLong();
        return userId;
    }
}

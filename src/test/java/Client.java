import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Random;

/**
 * Created by tahirmacbook on 01/03/2016.
 */
public class Client {

    private Logger log = LoggerFactory.getLogger(Client.class);
    Long userId;


    protected  WSClient client = new WSClient();



    public Client() throws Exception {

        client.openConnection();

    }

    public static void main(String args[]) throws Exception {



        Client pushClient = new Client();

        Random random = new Random();
        //pushClient.userId = random.nextLong();
        pushClient.userId =11L;
        JSONObject request = new JSONObject()
                .put("userId", pushClient.userId);

        JSONObject response = pushClient.client.ask(request);

        pushClient.log.info("response =>"+response);

        JSONObject request2 = new JSONObject()
                .put("request", "request from client")
                .put("actorId",10L);


        JSONObject response2 = pushClient.client.ask(request2);

        pushClient.log.info("response2  =>"+response2);

        BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            String msg = console.readLine();
            System.out.println("mesg "+msg);


        }

    }


}
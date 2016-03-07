/**
 * Created by tahirmacbook on 01/03/2016.
 */

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_10;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.UUID;



public final class WSClient {

    private WebSocketClient webSocketClient;
    private JSONObject lastResponse;
    private Logger log = LoggerFactory.getLogger(WSClient.class);
    Object lock = new Object();

    public void openConnection() throws Exception {

        final String url = String.format("ws://%s:%s", "localhost", 9091);
        //final String url = String.format("ws://%s:%s", "fbomb.cloudapp.net", networkServerWSPort());
        webSocketClient = new WebSocketClient(new URI(url), new Draft_10()) {

            @Override
            public void onMessage(String message) {
                log.info("message in WS is "+message);
                JSONObject obj = new JSONObject(message);


                    handleResponse(obj);
//                } else {
//                    handlePushMessage(obj);
//                }
            }

            private void handlePushMessage(JSONObject obj) {
                log.info("received push message :" + obj.toString());
                JSONObject request = new JSONObject()
                        .put("action", 123);
                try {
                    ask2(request);
                    log.debug("res :" );
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            private void handleResponse(JSONObject response) {
                log.info("received response from server :" + response.toString());
                lastResponse = response;

                synchronized (lock) {
                    lock.notify();
                }
            }

            @Override
            public void onOpen(ServerHandshake handshake) {
                log.info("opened connection");
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                log.info("connection closed");
            }

            @Override
            public void onError(Exception ex) {
                ex.printStackTrace();
            }

        };
        webSocketClient.connectBlocking();
    }

    public JSONObject ask(JSONObject request) throws Exception {
        String correlationId = UUID.randomUUID().toString();
        //request.put(ActionParams.CORRELATION_ID, correlationId);

        log.info("sending message : " + request);

        webSocketClient.send(request.toString());

        synchronized (lock) {
            while (true) {
                lock.wait();
                if (lastResponse != null) {
                    break;
                } else {
                    log.info("skipping message");
                }

            }
        }

        return lastResponse;
    }
    public void ask2(JSONObject request) throws Exception {
        String correlationId = UUID.randomUUID().toString();
        //request.put(ActionParams.CORRELATION_ID, correlationId);

        log.info("sending message : " + request);

        webSocketClient.send(request.toString());


    }

    public void closeConnection() {
        webSocketClient.close();
    }
}

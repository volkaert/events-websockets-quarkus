package fr.volkaert.events.websockets.quarkus.subscriber;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.*;

@ClientEndpoint
public class SubscriberWebSocketClient {

    private static final Logger LOG = LoggerFactory.getLogger(SubscriberWebSocketClient.class);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        LOG.info("Subscription {} joined", session.getId());
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) {
        LOG.info("Subscription {} left for reason {}", session.getId(), reason.getReasonPhrase());
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        LOG.error("Subscription {} thrown error {}", session.getId(), throwable.getMessage(), throwable);
    }

    @OnMessage
    void onMessage(Session session, String message) {
        LOG.info("(onMessage) Subscription {} received: {}", session.getId(), message);
        // Event event = GSON.fromJson(message, Event.class);
    }
}

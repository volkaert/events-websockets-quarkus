package fr.volkaert.events.websockets.quarkus.subscriber;

import org.jboss.logging.Logger;

import javax.websocket.*;

@ClientEndpoint
public class SubscriberWebSocketClient {

    private static final Logger LOG = Logger.getLogger(SubscriberWebSocketClient.class);

    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        LOG.info("Subscription " + session.getId() + " joined");
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) {
        LOG.info("Subscription " + session.getId() + " left for reason " + reason.getReasonPhrase());
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        LOG.error("Subscription " + session.getId() + " thrown error " + throwable.getMessage(), throwable);
    }

    @OnMessage
    void onMessage(Session session, String message) {
        LOG.info("(onMessage) Subscription " + session.getId() + " received: " + message);
    }

}

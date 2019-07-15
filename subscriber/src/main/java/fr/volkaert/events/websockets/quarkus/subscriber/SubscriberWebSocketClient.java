package fr.volkaert.events.websockets.quarkus.subscriber;

import org.jboss.logging.Logger;

import javax.websocket.*;

@ClientEndpoint
public class SubscriberWebSocketClient {

    private static final Logger LOG = Logger.getLogger(SubscriberWebSocketClient.class);

    @OnOpen
    public void onOpen(Session session) {
        LOG.info("SubscriberWebSocketClient.open() for session " + session.getId());
    }

    @OnClose
    public void onClose(Session session) {
        LOG.info("SubscriberWebSocketClient.close() for session " + session.getId());
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        LOG.error("SubscriberWebSocketClient.error() for session " + session.getId(), throwable);
    }

    @OnMessage
    void message(Session session, String message) {
        LOG.info("SubscriberWebSocketClient.message(" + message + ") for session " + session.getId());
    }
}

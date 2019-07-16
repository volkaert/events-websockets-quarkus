package fr.volkaert.events.websockets.quarkus.publisher;

import org.jboss.logging.Logger;

import javax.websocket.*;

@ClientEndpoint
public class PublisherWebSocketClient {

    private static final Logger LOG = Logger.getLogger(PublisherWebSocketClient.class);

    @OnOpen
    public void onOpen(Session session) {
        LOG.info("Publication " + session.getId() + " joined");
    }

    @OnClose
    public void onClose(Session session) {
        LOG.info("Publication " + session.getId() + " left");
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        LOG.error("Publication " + session.getId() + " thrown error " + throwable.getMessage(), throwable);
    }

    @OnMessage
    void onMessage(Session session, String message) {
        LOG.info("(onMessage) Publication " + session.getId() + " received: " + message);
    }
}

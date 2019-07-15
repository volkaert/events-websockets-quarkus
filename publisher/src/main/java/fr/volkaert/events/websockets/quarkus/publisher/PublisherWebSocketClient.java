package fr.volkaert.events.websockets.quarkus.publisher;

import org.jboss.logging.Logger;

import javax.websocket.*;

@ClientEndpoint
public class PublisherWebSocketClient {

    private static final Logger LOG = Logger.getLogger(PublisherWebSocketClient.class);

    @OnOpen
    public void onOpen(Session session) {
        LOG.info("PublisherWebSocketClient.open() for session " + session.getId());
    }

    @OnClose
    public void onClose(Session session) {
        LOG.info("PublisherWebSocketClient.close() for session " + session.getId());
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        LOG.error("PublisherWebSocketClient.error() for session " + session.getId(), throwable);
    }

    @OnMessage
    void message(Session session, String message) {
        LOG.info("PublisherWebSocketClient.message(" + message + ") for session " + session.getId());
    }
}

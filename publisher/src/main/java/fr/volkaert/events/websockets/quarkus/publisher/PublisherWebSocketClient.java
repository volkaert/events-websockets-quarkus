package fr.volkaert.events.websockets.quarkus.publisher;

import org.jboss.logging.Logger;

import javax.websocket.*;

@ClientEndpoint
public class PublisherWebSocketClient {

    private static final Logger LOG = Logger.getLogger(PublisherWebSocketClient.class);

    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        LOG.info("Publication " + session.getId() + " joined");
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) {
        LOG.info("Publication " + session.getId() + " left for reason " + reason.getReasonPhrase());
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

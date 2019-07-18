package fr.volkaert.events.websockets.quarkus.publisher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.*;

@ClientEndpoint
public class PublisherWebSocketClient {

    private static final Logger LOG = LoggerFactory.getLogger(PublisherWebSocketClient.class);

    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        LOG.info("Publication {} joined", session.getId());
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) {
        LOG.info("Publication {} left for reason {}", session.getId(), reason.getReasonPhrase());
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        LOG.error("Publication {} thrown error {}", session.getId(), throwable);
    }

    @OnMessage
    void onMessage(Session session, String message) {
        LOG.info("(onMessage) Publication {} received: {}", session.getId(), message);
    }
}

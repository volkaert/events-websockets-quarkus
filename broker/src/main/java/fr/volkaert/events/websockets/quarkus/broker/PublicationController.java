package fr.volkaert.events.websockets.quarkus.broker;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/events/{eventCode}/publications")
@ApplicationScoped
public class PublicationController {

    private static final Logger LOG = LoggerFactory.getLogger(PublicationController.class);

    @Inject
    SubscriptionController subscriptionController;

    @OnOpen
    public void onOpen(Session session, EndpointConfig config, @PathParam("eventCode") String eventCode) {
        LOG.info("Publication {} for event {} joined", session.getId(), eventCode);
    }

    @OnClose
    public void onClose(Session session, CloseReason reason, @PathParam("eventCode") String eventCode) {
        LOG.info("Publication {} for event {} left for reason {}", session.getId(), eventCode, reason.getReasonPhrase());
    }

    @OnError
    public void onError(Session session, Throwable throwable, @PathParam("eventCode") String eventCode) {
        LOG.error("Publication {} for event {} thrown error {}", session.getId(), eventCode, throwable);
    }

    @OnMessage
    public void onMessage(Session session, String message, @PathParam("eventCode") String eventCode) {
        LOG.info("(onMessage) Publication {} for event {} published message: {}", session.getId(), eventCode, message);
        broadcastToSubscriptionsOfThisEventCode(message, eventCode);
    }

    private void broadcastToSubscriptionsOfThisEventCode(String message, String eventCode) {
        subscriptionController.broadcastToSubscriptionsOfThisEventCode(message, eventCode);
    }
}
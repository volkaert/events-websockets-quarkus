package fr.volkaert.events.websockets.quarkus.broker;

import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/events/{eventCode}/publications")
@ApplicationScoped
public class PublicationController {

    private static final Logger LOG = Logger.getLogger(PublicationController.class);

    @Inject
    SubscriptionController subscriptionController;

    @OnOpen
    public void onOpen(Session session, EndpointConfig config, @PathParam("eventCode") String eventCode) {
        LOG.info("Publication " + session.getId() + " for event " + eventCode + " joined");
    }

    @OnClose
    public void onClose(Session session, CloseReason reason, @PathParam("eventCode") String eventCode) {
        LOG.info("Publication " + session.getId() + " for event " + eventCode + " left for reason " + reason.getReasonPhrase());
    }

    @OnError
    public void onError(Session session, Throwable throwable, @PathParam("eventCode") String eventCode) {
        LOG.error("Publication " + session.getId() + " for event " + eventCode + " thrown error " + throwable.getMessage(), throwable);
    }

    @OnMessage
    public void onMessage(Session session, String message, @PathParam("eventCode") String eventCode) {
        LOG.info("(onMessage) Publication " + session.getId() + " for event " + eventCode + " published: " + message);
        broadcastToSubscriptionsOfThisEventCode(message, eventCode);
    }

    private void broadcastToSubscriptionsOfThisEventCode(String message, String eventCode) {
        subscriptionController.broadcastToSubscriptionsOfThisEventCode(message, eventCode);
    }
}
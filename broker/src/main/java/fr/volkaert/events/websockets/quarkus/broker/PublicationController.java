package fr.volkaert.events.websockets.quarkus.broker;

import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/events/{eventCode}/publications/{publicationCode}")
@ApplicationScoped
public class PublicationController {

    private static final Logger LOG = Logger.getLogger(PublicationController.class);

    @Inject
    SubscriptionController subscriptionController;

    @OnOpen
    public void onOpen(Session session, @PathParam("eventCode") String eventCode, @PathParam("publicationCode") String publicationCode) {
        LOG.info("Publication " + publicationCode + " for event " + eventCode + " joined");
    }

    @OnClose
    public void onClose(Session session, @PathParam("eventCode") String eventCode, @PathParam("publicationCode") String publicationCode) {
        LOG.info("Publication " + publicationCode + " for event " + eventCode + " left");
    }

    @OnError
    public void onError(Session session, Throwable throwable, @PathParam("eventCode") String eventCode, @PathParam("publicationCode") String publicationCode) {
        LOG.error("Publication " + publicationCode + " for event " + eventCode + " thrown error " + throwable.getMessage(), throwable);
    }

    @OnMessage
    public void onMessage(Session session, String message, @PathParam("eventCode") String eventCode, @PathParam("publicationCode") String publicationCode) {
        LOG.info(">> " + publicationCode + "(" + eventCode + "): " + message);
        broadcastToSubscriptionsOfThisEventCode(">> " + publicationCode + "(" + eventCode + "): " + message, eventCode);
    }

    private void broadcastToSubscriptionsOfThisEventCode(String message, String eventCode) {
        subscriptionController.broadcastToSubscriptionsOfThisEventCode(message, eventCode);
    }
}
package fr.volkaert.events.websockets.quarkus.broker;

import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/events/{eventCode}/subscriptions/{subscriptionCode}")
@ApplicationScoped
public class SubscriptionController {

    private static final Logger LOG = Logger.getLogger(SubscriptionController.class);

    // key is Session Id, value is Session
    Map<String, Session> idToSessionMap = new ConcurrentHashMap<>();

    // key is eventCode, value is a list of Session Id
    Map<String, List<String>> eventCodeToSessionIdsMap = new ConcurrentHashMap<>();

    Object lock = new Object();

    @OnOpen
    public void onOpen(Session session, @PathParam("eventCode") String eventCode, @PathParam("subscriptionCode") String subscriptionCode) {
        synchronized (lock) {
            idToSessionMap.put(session.getId(), session);
            List<String> sessionIdsForThisEventCode = getSessionIdsForThisEventCode(eventCode);
            sessionIdsForThisEventCode.add(session.getId());
        }
        LOG.info("Subscription " + subscriptionCode + " for event " + eventCode + " joined");
    }

    @OnClose
    public void onClose(Session session, @PathParam("eventCode") String eventCode, @PathParam("subscriptionCode") String subscriptionCode) {
        synchronized (lock) {
            idToSessionMap.remove(session.getId());
            List<String> sessionIdsForThisEventCode = getSessionIdsForThisEventCode(eventCode);
            sessionIdsForThisEventCode.remove(session.getId());
        }
        LOG.info("Subscription " + subscriptionCode + " for event " + eventCode + " left");
    }

    @OnError
    public void onError(Session session, Throwable throwable, @PathParam("eventCode") String eventCode, @PathParam("subscriptionCode") String subscriptionCode) {
        synchronized (lock) {
            idToSessionMap.remove(session.getId());
            List<String> sessionIdsForThisEventCode = getSessionIdsForThisEventCode(eventCode);
            sessionIdsForThisEventCode.remove(session.getId());
        }
        LOG.error("Subscription " + subscriptionCode + " for event " + eventCode + " thrown error " + throwable.getMessage(), throwable);
    }

    @OnMessage
    public void onMessage(Session session, String message, @PathParam("eventCode") String eventCode, @PathParam("subscriptionCode") String subscriptionCode) {
        LOG.error("Subscription " + subscriptionCode + " cannot publish event " + eventCode + " because it is a subscription and not a publication !");
    }

    public void broadcastToSubscriptionsOfThisEventCode(String message, String eventCode) {
        List<Session> sessionsOfTheSubscriptionsOfThisEventCode = new ArrayList<>();

        synchronized (lock) {
            List<String> sessionIdsForThisEventCode = getSessionIdsForThisEventCode(eventCode);
            sessionIdsForThisEventCode.forEach(sessionId -> {
                Session session = idToSessionMap.get(sessionId);
                sessionsOfTheSubscriptionsOfThisEventCode.add(session);
            });
        }

        sessionsOfTheSubscriptionsOfThisEventCode.forEach(session -> {
            session.getAsyncRemote().sendObject(message, result -> {
                if (result.getException() != null) {
                    LOG.error("Unable to send message: " + result.getException(), result.getException());
                }
            });
        });
    }

    private List<String> getSessionIdsForThisEventCode(String eventCode) {
        synchronized (lock) {
            List<String> sessionIdsForThisEventCode = eventCodeToSessionIdsMap.get(eventCode);
            if (sessionIdsForThisEventCode == null) {
                sessionIdsForThisEventCode = new ArrayList<>();
                eventCodeToSessionIdsMap.put(eventCode, sessionIdsForThisEventCode);
            }
            return sessionIdsForThisEventCode;
        }
    }
}
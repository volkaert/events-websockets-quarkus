package fr.volkaert.events.websockets.quarkus.subscriber;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.ContainerProvider;
import javax.websocket.Session;
import javax.ws.rs.*;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Path("/events")
@Produces("application/json")
@Consumes("application/json")
@ApplicationScoped
public class SubscriberController {

    private static final Logger LOG = LoggerFactory.getLogger(SubscriberController.class);

    @ConfigProperty(name = "ws.address")
    String wsAddress;

    Map<String, Session> sessionsMap = new ConcurrentHashMap<>();

    @POST
    @Path("/{eventCode}/subscriptions")
    // Example using curl: curl -X POST http://localhost:8082/events/eventA/subscriptions
    public void subscribe(@PathParam("eventCode") String eventCode) {
        String sessionKey = eventCode;
        Session session = sessionsMap.get(sessionKey);
        if (session == null) {
            String wsURL = wsAddress + "/events/" + eventCode + "/subscriptions";
            try {
                session = ContainerProvider.getWebSocketContainer().connectToServer(SubscriberWebSocketClient.class, URI.create(wsURL));
                LOG.info("(subscribe) Subscription {} for event {} joined", session.getId(), eventCode);
            } catch (Exception ex) {
                LOG.error("Error while connecting to the WebSockets Server at {}: {}", wsURL, ex);
                throw new RuntimeException(ex);
            }
            sessionsMap.put(sessionKey, session);
        }
    }

    @GET // For tests only (typically to ease subscription from the navigation bar of a browser)
    @Path("/{eventCode}/subscriptions")
    // Example (from the navigation bar of a browser): http://localhost:8082/events/eventA/subscriptions
    public void subscribeUsingGETForTestsOnly(@PathParam("eventCode") String eventCode) {
        subscribe(eventCode);
    }
}



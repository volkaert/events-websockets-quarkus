package fr.volkaert.events.websockets.quarkus.publisher;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.ContainerProvider;
import javax.websocket.Session;
import javax.ws.rs.*;
import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Path("/events")
@Produces("application/json")
@Consumes("application/json")
@ApplicationScoped
public class PublisherController {

    private static final Logger LOG = Logger.getLogger(PublisherController.class);

    @ConfigProperty(name = "ws.address")
    String wsAddress;

    Map<String, Session> sessionsMap = new ConcurrentHashMap<>();

    @POST
    // Example using curl: curl -d '{"eventCode":"eventA", "publicationCode":"pubA1", "payload":"helloA"}' -H "Content-Type: application/json" -X POST http://localhost:8081/events
    public Event publish(Event eventToPublish) {
        if (eventToPublish.id == null) {    // do not overwrite existing id explicitly set by the publisher
            eventToPublish.id = UUID.randomUUID().toString();
        }

        String sessionKey = eventToPublish.eventCode + "-" + eventToPublish.publicationCode;
        Session session = sessionsMap.get(sessionKey);
        if (session == null) {
            String wsURL = wsAddress + "/events/" + eventToPublish.eventCode + "/publications/" + eventToPublish.publicationCode;
            try {
                session = ContainerProvider.getWebSocketContainer().connectToServer(PublisherWebSocketClient.class, URI.create(wsURL));
            } catch (Exception ex) {
                LOG.error("Error while connecting to the WebSockets Server at " + wsURL + ": " + ex.getMessage(), ex);
                throw new RuntimeException(ex);
            }
            sessionsMap.put(sessionKey, session);
        }

        LOG.info("Publish event to the websocket (payload: " + eventToPublish.payload + ")");
        session.getAsyncRemote().sendObject(eventToPublish);

        Event publishedEvent = new Event(eventToPublish);
        publishedEvent.payload = null;  // remove the payload from the response to preserve bandwidth
        return publishedEvent;
    }

    @GET // For tests only (typically to ease publication from the navigation bar of a browser)
    @Path("/{eventCode}/publications/{publicationCode}")
    // Example (from the navigation bar of a browser): http://localhost:8081/events/eventA/publications/pubA1?payload=helloA
    public Event publishUsingGETForTestsOnly(@PathParam("eventCode") String eventCode, @PathParam("publicationCode") String publicationCode,  @QueryParam("payload") String payload) {
        Event eventToPublish = new Event(eventCode, publicationCode, payload);
        Event publishedEvent = publish(eventToPublish);
        return publishedEvent;
    }
}



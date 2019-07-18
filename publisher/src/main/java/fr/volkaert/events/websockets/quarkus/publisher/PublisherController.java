package fr.volkaert.events.websockets.quarkus.publisher;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.ContainerProvider;
import javax.websocket.Session;
import javax.ws.rs.*;
import java.net.URI;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Path("/events")
@Produces("application/json")
@Consumes("application/json")
@ApplicationScoped
public class PublisherController {

    private static final Logger LOG = LoggerFactory.getLogger(PublisherController.class);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @ConfigProperty(name = "ws.address")
    String wsAddress;

    Map<String, Session> sessionsMap = new ConcurrentHashMap<>();

    @POST
    // Example using curl: curl -d '{"eventCode":"eventA", "payload":"helloA"}' -H "Content-Type: application/json" -X POST http://localhost:8081/events
    public Event publish(Event eventToPublish) {
        if (eventToPublish.id == null) {    // do not overwrite existing id explicitly set by the publisher
            eventToPublish.id = UUID.randomUUID().toString();
        }

        String sessionKey = eventToPublish.eventCode;
        Session session = sessionsMap.get(sessionKey);
        if (session == null) {
            String wsURL = wsAddress + "/events/" + eventToPublish.eventCode + "/publications";
            try {
                session = ContainerProvider.getWebSocketContainer().connectToServer(PublisherWebSocketClient.class, URI.create(wsURL));
                LOG.info("(publish) Publication {} for event {} joined", session.getId(), eventToPublish.eventCode);
            } catch (Exception ex) {
                LOG.error("Error while connecting to the WebSockets Server at {}: {}", wsURL, ex.getMessage(), ex);
                throw new RuntimeException(ex);
            }
            sessionsMap.put(sessionKey, session);
        }

        LOG.info("(publish) Publication {} for event {} published: {}", session.getId(), eventToPublish.eventCode, eventToPublish.payload);
        String eventToPublishAsJSONString = GSON.toJson(eventToPublish);
        session.getAsyncRemote().sendText(eventToPublishAsJSONString);

        Event publishedEvent = new Event(eventToPublish);
        publishedEvent.payload = null;  // remove the payload from the response to preserve bandwidth
        return publishedEvent;
    }

    @GET // For tests only (typically to ease publication from the navigation bar of a browser)
    @Path("/{eventCode}/publications")
    // Example (from the navigation bar of a browser): http://localhost:8081/events/eventA/publications?payload=helloA
    public Event publishUsingGETForTestsOnly(@PathParam("eventCode") String eventCode, @QueryParam("payload") String payload) {
        Event eventToPublish = new Event(eventCode, payload);
        Event publishedEvent = publish(eventToPublish);
        return publishedEvent;
    }
}



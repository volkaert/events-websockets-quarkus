package fr.volkaert.events.websockets.quarkus.subscriber;

import java.util.UUID;

public class Event {
    public String id;
    public String eventCode;
    public String publicationCode;
    public String payload;

    public Event() {
        this.id = UUID.randomUUID().toString();
    }

    public Event(String id, String eventCode, String publicationCode, String payload) {
        this.id = id;
        this.eventCode = eventCode;
        this.publicationCode = publicationCode;
        this.payload = payload;
    }

    public Event(String eventCode, String publicationCode, String payload) {
        this.id = UUID.randomUUID().toString();
        this.eventCode = eventCode;
        this.publicationCode = publicationCode;
        this.payload = payload;
    }

    public Event(Event eventToCopy) {
        this.id = eventToCopy.id;
        this.eventCode = eventToCopy.eventCode;
        this.publicationCode = eventToCopy.publicationCode;
        this.payload = eventToCopy.payload;
    }
}

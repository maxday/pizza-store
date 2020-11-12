package fr.maximedavid.serverless;

public class PizzaEvent {

    private String uuid;
    private String eventId;
    private String name;

    public PizzaEvent() {
    }

    public PizzaEvent(String uuid, String eventId, String name) {
        this.uuid = uuid;
        this.eventId = eventId;
        this.name = name;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

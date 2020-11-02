package fr.maximedavid.serverless;

import java.util.Objects;

public class PubSubEvent {
    private PubSubMessage message;

    public PubSubEvent() {
    }

    public PubSubEvent(PubSubMessage message) {
        this.message = message;
    }

    public PubSubMessage getMessage() {
        return message;
    }

    public void setMessage(PubSubMessage message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PubSubEvent that = (PubSubEvent) o;
        return Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message);
    }
}

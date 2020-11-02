package fr.maximedavid.serverless;

import java.util.Objects;

public class Event {
    private String id;
    private String data;

    public Event() {
    }

    public Event(String id, String data) {
        this.id = id;
        this.data = data;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return Objects.equals(id, event.id) &&
                Objects.equals(data, event.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, data);
    }
}

package fr.maximedavid.serverless;

import java.util.Objects;

public class PubSubMessage {
    private String messageId;
    private String data;

    public PubSubMessage() {
    }

    public PubSubMessage(String messageId, String data) {
        this.messageId = messageId;
        this.data = data;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
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
        PubSubMessage that = (PubSubMessage) o;
        return Objects.equals(data, that.data) &&
                Objects.equals(messageId, that.messageId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(data, messageId);
    }
}

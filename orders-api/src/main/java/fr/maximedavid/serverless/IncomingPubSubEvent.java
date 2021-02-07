package fr.maximedavid.serverless;

public class IncomingPubSubEvent {

    private Message message;
    private String subscription;

    public IncomingPubSubEvent() {
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public String getSubscription() {
        return subscription;
    }

    public void setSubscription(String subscription) {
        this.subscription = subscription;
    }
}

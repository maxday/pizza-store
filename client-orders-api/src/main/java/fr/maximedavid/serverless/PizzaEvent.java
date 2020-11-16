package fr.maximedavid.serverless;

public enum PizzaEvent {
    PIZZA_ORDER_REQUEST("PIZZA_ORDER_REQUEST"),
    PIZZA_STATUS_REQUEST("PIZZA_STATUS_REQUEST");


    private String event;

    PizzaEvent(String event) {
        this.event = event;
    }

    public String getEvent() {
        return event;
    }
}
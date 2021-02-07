package fr.maximedavid.serverless;

public enum PizzaEvent {
    PIZZA_PREPARED_REQUEST("PIZZA_PREPARED_REQUEST"),
    PIZZA_BAKED_REQUEST("PIZZA_BAKED_REQUEST"),
    PIZZA_LEFT_STORE_REQUEST("PIZZA_LEFT_STORE_REQUEST"),
    PIZZA_DELIVERED_REQUEST("PIZZA_DELIVERED_REQUEST"),
    PIZZA_ORDERED("PIZZA_ORDERED"),
    PIZZA_ORDER_REQUEST("PIZZA_ORDER_REQUEST");

    private String event;

    PizzaEvent(String event) {
        this.event = event;
    }

    public String getEvent() {
        return event;
    }
}
package fr.maximedavid.serverless;

import java.util.List;

public class PizzaOrder {

    private String uuid;
    private List<String> toppings;

    public PizzaOrder() {
    }

    public PizzaOrder(String uuid, List<String> toppings) {
        this.uuid = uuid;
        this.toppings = toppings;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public List<String> getToppings() {
        return toppings;
    }

    public void setToppings(List<String> toppings) {
        this.toppings = toppings;
    }
}

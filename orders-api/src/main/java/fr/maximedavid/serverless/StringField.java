package fr.maximedavid.serverless;

public class StringField {

    private String type;
    private String value;

    public StringField(String value) {
        this.type = "stringValue";
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

}

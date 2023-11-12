package moe.protasis.yukimessenger.util;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class JsonObjectBuilder {
    private final JsonObject node;

    public JsonObjectBuilder() {
        node = new JsonObject();
    }

    public JsonObjectBuilder set(String key, JsonElement node) {
        this.node.add(key, node);
        return this;
    }

    public JsonObjectBuilder put(String key, Number value) {
        this.node.addProperty(key, value);
        return this;
    }

    public JsonObjectBuilder put(String key, String value) {
        this.node.addProperty(key, value);
        return this;
    }

    public JsonObjectBuilder put(String key, boolean value) {
        this.node.addProperty(key, value);
        return this;
    }

    public JsonObject finish() {
        return node;
    }

}

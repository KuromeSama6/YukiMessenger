package moe.protasis.yukimessenger.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.org.apache.xpath.internal.operations.Bool;

public class ObjectNodeBuilder {
    private final ObjectNode node;

    public ObjectNodeBuilder() {
        ObjectMapper mapper = new ObjectMapper();
        node = mapper.createObjectNode();
    }

    public ObjectNodeBuilder set(String key, JsonNode node) {
        this.node.set(key, node);
        return this;
    }

    public ObjectNodeBuilder put(String key, Integer value) {
        this.node.put(key, value);
        return this;
    }

    public ObjectNodeBuilder put(String key, Float value) {
        this.node.put(key, value);
        return this;
    }

    public ObjectNodeBuilder put(String key, Double value) {
        this.node.put(key, value);
        return this;
    }

    public ObjectNodeBuilder put(String key, Boolean value) {
        this.node.put(key, value);
        return this;
    }

    public ObjectNodeBuilder put(String key, String value) {
        this.node.put(key, value);
        return this;
    }

    public ObjectNode finish() {
        return node;
    }

}

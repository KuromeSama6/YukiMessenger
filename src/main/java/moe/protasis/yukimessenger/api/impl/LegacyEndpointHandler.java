package moe.protasis.yukimessenger.api.impl;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import moe.protasis.yukimessenger.api.IInboundMessageHandler;
import moe.protasis.yukimessenger.message.InboundMessage;

import java.util.function.Consumer;

@Deprecated
@AllArgsConstructor
public class LegacyEndpointHandler implements IInboundMessageHandler {
    private final Consumer<JsonObject> callback;

    @Override
    public void Handle(InboundMessage msg) {
        callback.accept(msg.getData().getJson());
    }
}

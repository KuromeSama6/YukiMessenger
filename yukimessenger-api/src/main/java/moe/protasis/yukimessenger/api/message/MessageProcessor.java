package moe.protasis.yukimessenger.api.message;

import lombok.Getter;
import moe.protasis.yukimessenger.api.IYukiMessengerAPI;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class MessageProcessor {
    private final IYukiMessengerAPI api = IYukiMessengerAPI.Get();

    @Getter
    private final Map<OutboundMessage, Consumer<MessageResponse>> retryQueue = new HashMap<>();
    @Getter
    private final Map<UUID, Consumer<MessageResponse>> callbacks = new HashMap<>();

    public boolean AddTask(OutboundMessage msg, Consumer<MessageResponse> callback) {
        if (!api.IsReady()) {
            api.GetLogger().warning("Attempted to send a message when the service is not ready. Queued it for retry.");
            retryQueue.put(msg, callback);
            return false;
        }

        if (callback != null) getCallbacks().put(msg.getId(), callback);

        return true;
    }

    public void HandleResponse(UUID id, MessageResponse response) {
        Consumer<MessageResponse> callback = callbacks.get(id);
        if (callback != null) {
            try {
                callback.accept(response);
            } catch (Throwable t) {
                t.printStackTrace();
            }
            callbacks.remove(id);
        }
    }

}

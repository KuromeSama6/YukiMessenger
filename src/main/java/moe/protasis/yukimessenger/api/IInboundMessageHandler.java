package moe.protasis.yukimessenger.api;

import moe.protasis.yukimessenger.message.InboundMessage;
import moe.protasis.yukimessenger.message.ServerInboundMessage;

public interface IInboundMessageHandler {
    @Deprecated
    default void Handle(ServerInboundMessage msg) {}

    void Handle(InboundMessage msg);
}

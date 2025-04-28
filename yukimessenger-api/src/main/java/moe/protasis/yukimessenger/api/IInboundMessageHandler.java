package moe.protasis.yukimessenger.api;

import moe.protasis.yukimessenger.api.message.InboundMessage;

public interface IInboundMessageHandler {
    void Handle(InboundMessage msg);
}

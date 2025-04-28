package moe.protasis.yukimessenger.api.message;

import moe.protasis.yukicommons.api.json.JsonWrapper;

public interface IMessageNode {
    void Send(JsonWrapper data);
}

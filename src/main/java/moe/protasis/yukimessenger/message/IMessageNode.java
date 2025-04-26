package moe.protasis.yukimessenger.message;

import moe.protasis.yukicommons.api.json.JsonWrapper;

public interface IMessageNode {
    void Send(JsonWrapper data);
}

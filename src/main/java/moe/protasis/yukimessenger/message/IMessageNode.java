package moe.protasis.yukimessenger.message;

import moe.protasis.yukicommons.json.JsonWrapper;

public interface IMessageNode {
    void Send(JsonWrapper data);
}
